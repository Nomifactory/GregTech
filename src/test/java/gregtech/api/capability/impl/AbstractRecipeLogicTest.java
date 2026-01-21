package gregtech.api.capability.impl;

import gregtech.api.*;
import gregtech.api.metatileentity.*;
import gregtech.api.recipes.*;
import gregtech.api.recipes.builders.*;
import gregtech.api.render.*;
import gregtech.api.util.world.DummyWorld;
import gregtech.common.metatileentities.electric.MetaTileEntityMacerator;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static gregtech.api.GTValues.*;

public class AbstractRecipeLogicTest {

	@BeforeAll
	public static void init() {
		Bootstrap.register();
	}

	@Test
	public void trySearchNewRecipe() {

		World world = DummyWorld.INSTANCE;

		// Create an empty recipe map to work with
		RecipeMap<SimpleRecipeBuilder> map = new RecipeMap<>("chemical_reactor",
		                                                     0,
		                                                     2,
		                                                     0,
		                                                     2,
		                                                     0,
		                                                     3,
		                                                     0,
		                                                     2,
		                                                     new SimpleRecipeBuilder().EUt(30),
		                                                     false);

		MetaTileEntity at =
			GregTechAPI.registerMetaTileEntity(190,
			                                   new SimpleMachineMetaTileEntity(
				                                   new ResourceLocation(GTValues.MODID, "chemical_reactor.lv"),
				                                   map,
				                                   Textures.CHEMICAL_REACTOR_OVERLAY,
				                                   1));

		MetaTileEntity atte = new MetaTileEntityHolder().setMetaTileEntity(at);
		atte.getHolder().setWorld(world);
		map.recipeBuilder()
		   .inputs(new ItemStack(Blocks.COBBLESTONE))
		   .outputs(new ItemStack(Blocks.STONE))
		   .EUt(1).duration(1)
		   .buildAndRegister();

		AbstractRecipeLogic arl = new AbstractRecipeLogic(atte, map) {
			@Override
			protected long getEnergyStored() {
				return Long.MAX_VALUE;
			}

			@Override
			protected long getEnergyCapacity() {
				return Long.MAX_VALUE;
			}

			@Override
			protected boolean drawEnergy(int recipeEUt) {
				return true;
			}

			@Override
			protected long getMaxVoltage() {
				return 32;
			}
		};

		arl.isOutputsFull = false;
		arl.invalidInputsForRecipes = false;
		arl.trySearchNewRecipe();

		// no recipe found
		assertTrue(arl.invalidInputsForRecipes);
		assertFalse(arl.isActive);
		assertNull(arl.previousRecipe);

		// put an item in the inventory that will trigger recipe recheck
		arl.getInputInventory().insertItem(0, new ItemStack(Blocks.COBBLESTONE, 16), false);
		// Inputs change. did we detect it ?
		assertTrue(arl.hasNotifiedInputs());
		arl.trySearchNewRecipe();
		assertFalse(arl.invalidInputsForRecipes);
		assertNotNull(arl.previousRecipe);
		assertTrue(arl.isActive);
		assertEquals(15, arl.getInputInventory().getStackInSlot(0).getCount());
		//assert the consumption of the inputs did not mark the arl to look for a new recipe
		assertFalse(arl.hasNotifiedInputs());

		// Save a reference to the old recipe so we can make sure it's getting reused
		Recipe prev = arl.previousRecipe;

		// Finish the recipe, the output should generate, and the next iteration should begin
		arl.update();
		assertEquals(prev, arl.previousRecipe);
		assertTrue(AbstractRecipeLogic.areItemStacksEqual(arl.getOutputInventory().getStackInSlot(0),
		                                                  new ItemStack(Blocks.STONE, 1)));
		assertTrue(arl.isActive);

		// Complete the second iteration, but the machine stops because its output is now full
		arl.getOutputInventory().setStackInSlot(0, new ItemStack(Blocks.STONE, 63));
		arl.getOutputInventory().setStackInSlot(1, new ItemStack(Blocks.STONE, 64));
		arl.update();
		assertFalse(arl.isActive);
		assertTrue(arl.isOutputsFull);

		// Try to process again and get failed out because of full buffer.
		arl.update();
		assertFalse(arl.isActive);
		assertTrue(arl.isOutputsFull);

		// Some room is freed in the output bus, so we can continue now.
		arl.getOutputInventory().setStackInSlot(1, ItemStack.EMPTY);
		arl.update();
		assertTrue(arl.isActive);
		assertFalse(arl.isOutputsFull);
		assertTrue(AbstractRecipeLogic.areItemStacksEqual(arl.getOutputInventory().getStackInSlot(0),
		                                                  new ItemStack(Blocks.STONE, 1)));
	}

	@Test
	public void ulv_recipe_overclock_test() {

		World world = DummyWorld.INSTANCE;

		// Create an empty recipe map to work with
		RecipeMap<SimpleRecipeBuilder> map = new RecipeMap<>("chemical_reactor_2",
		                                                     0,
		                                                     2,
		                                                     0,
		                                                     2,
		                                                     0,
		                                                     3,
		                                                     0,
		                                                     2,
		                                                     new SimpleRecipeBuilder().EUt(30),
		                                                     false);

		MetaTileEntity at =
			GregTechAPI.registerMetaTileEntity(198,
			                                   new SimpleMachineMetaTileEntity(
				                                   new ResourceLocation(GTValues.MODID, "chemical_reactor.uv"),
				                                   map,
				                                   Textures.CHEMICAL_REACTOR_OVERLAY,
				                                   1));

		MetaTileEntity atte = new MetaTileEntityHolder().setMetaTileEntity(at);
		atte.getHolder().setWorld(world);

		// Recipe that will reach maximum speed (1t) at ZPM
		map.recipeBuilder()
		   .inputs(new ItemStack(Blocks.COBBLESTONE))
		   .outputs(new ItemStack(Blocks.STONE))
		   .EUt(4).duration(128)
		   .buildAndRegister();

		// UV-tier machine
		AbstractRecipeLogic arl = new AbstractRecipeLogic(atte, map) {
			@Override
			protected long getEnergyStored() {
				return Long.MAX_VALUE;
			}

			@Override
			protected long getEnergyCapacity() {
				return Long.MAX_VALUE;
			}

			@Override
			protected boolean drawEnergy(int recipeEUt) {
				return true;
			}

			@Override
			protected long getMaxVoltage() {
				return GTValues.V[GTValues.UV];
			}
		};

		arl.getInputInventory().insertItem(0, new ItemStack(Blocks.COBBLESTONE, 16), false);
		Recipe recipe = arl.findRecipe(arl.getMaxVoltage(), arl.getInputInventory(), arl.getInputTank());
		int[] oc = arl.calculateOverclock(recipe);

		// Expect seven overclocks to a half-amp of ZPM and capped 1t duration, instead of previous behavior where
		// the energy consumption increased as though an eighth overclock was performed
		assertEquals(GTValues.V[GTValues.ZPM] / 2, oc[0]);
		assertEquals(1, oc[1]);
	}

	/** Base chance for standard_chanced_outputs test */
	private static final int BASE_CHANCE = 7500;
	/** Bonus chance per tier for standard_chanced_outputs test */
	private static final int BONUS_CHANCE = 500;

	/** Argument stream for the standard_chanced_outputs test */
	static Stream<Arguments> standardChancedArgs() {
		return Stream.of(
			Arguments.of( LV, BASE_CHANCE + BONUS_CHANCE * 0),
			Arguments.of( MV, BASE_CHANCE + BONUS_CHANCE * 1), // starts to gain bonuses here
			Arguments.of( HV, BASE_CHANCE + BONUS_CHANCE * 2),
			Arguments.of( EV, BASE_CHANCE + BONUS_CHANCE * 3),
			Arguments.of( IV, BASE_CHANCE + BONUS_CHANCE * 4), // hits speed cap here
			Arguments.of(LuV, BASE_CHANCE + BONUS_CHANCE * 5), // hits chance cap here
			Arguments.of(ZPM, BASE_CHANCE + BONUS_CHANCE * 5),
			Arguments.of( UV, BASE_CHANCE + BONUS_CHANCE * 5));
	}

	/**
	 * Test to verify AbstractRecipeLogic handles chanced output computations appropriately.
	 */
	@ParameterizedTest
	@MethodSource("standardChancedArgs")
	public void standard_chanced_outputs(int tier, int chanceExpected) {
		World world = DummyWorld.INSTANCE;

		// Create an empty recipe map to work with
		RecipeMap<SimpleRecipeBuilder> map = new RecipeMap<>("fakemachine",
		                                                     1,
		                                                     1,
		                                                     1,
		                                                     3,
		                                                     0,
		                                                     0,
		                                                     0,
		                                                     0,
		                                                     new SimpleRecipeBuilder());

		var machine = GregTechAPI.registerMetaTileEntity(70+tier,
		                                               new SimpleMachineMetaTileEntity(new ResourceLocation(GTValues.MODID, "fakemachine." + GTValues.VN[tier].toLowerCase()),
		                                                                               map, Textures.CHEMICAL_REACTOR_OVERLAY, tier));

		MetaTileEntity macerTE = new MetaTileEntityHolder().setMetaTileEntity(machine);
		macerTE.getHolder().setWorld(world);

		// stone makes cobblestone and has a chance of gravel
		map.recipeBuilder()
		   .inputs(new ItemStack(Blocks.STONE))
		   .outputs(new ItemStack(Blocks.COBBLESTONE))
		   .chancedOutput(new ItemStack(Blocks.GRAVEL), BASE_CHANCE, BONUS_CHANCE)
		   .EUt(30).duration(150) // LV base recipe
		   .buildAndRegister();

		AbstractRecipeLogic arl = new AbstractRecipeLogic(macerTE, map) {
			@Override
			protected long getEnergyStored() {
				return Long.MAX_VALUE;
			}

			@Override
			protected long getEnergyCapacity() {
				return Long.MAX_VALUE;
			}

			@Override
			protected boolean drawEnergy(int recipeEUt) {
				return true;
			}

			@Override
			protected long getMaxVoltage() {
				return GTValues.V[tier];
			}
		};

		arl.isOutputsFull = false;
		arl.invalidInputsForRecipes = false;

		// put a stack of stone in the machine
		arl.getInputInventory().insertItem(0, new ItemStack(Blocks.STONE, 16), false);
		arl.trySearchNewRecipe();
		// ensure that it found a recipe and consumed inputs
		assertTrue(arl.isActive);

		// get the computed chance of the gravel stack
		var chancedOut = arl.chancedItemOutputs.get(0);
		assertEquals(chanceExpected, chancedOut.getRight());
	}

	/** Base chance for macerator_chanced_outputs test */
	private static final int MACER_BASE_CHANCE = 1400;
	/** Bonus chance per tier for macerator_chanced_outputs test */
	private static final int MACER_BONUS_CHANCE = 850;

	/** Argument stream for the macerator_chanced_outputs test */
	static Stream<Arguments> macerChancedArgs() {
		return Stream.of(
			Arguments.of(1,  LV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 0),
			Arguments.of(1,  MV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 0),
			Arguments.of(3,  HV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 1), // starts to gain bonuses here
			Arguments.of(3,  EV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 2),
			Arguments.of(3,  IV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 3),
			Arguments.of(3, LuV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 4),
			Arguments.of(3, ZPM, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 5),
			Arguments.of(3,  UV, MACER_BASE_CHANCE + MACER_BONUS_CHANCE * 6));
	}

	/**
	 * Test to verify Macerators handle chanced output computations appropriately.
	 */
	@ParameterizedTest
	@MethodSource("macerChancedArgs")
	public void macerator_chanced_outputs(int outputs, int tier, int chanceExpected) throws
	                                                                                 NoSuchMethodException,
	                                                                                 InvocationTargetException,
	                                                                                 IllegalAccessException {
		World world = DummyWorld.INSTANCE;

		// Create an empty recipe map to work with
		RecipeMap<SimpleRecipeBuilder> map = new RecipeMap<>("macerator_2",
		                                                     1,
		                                                     1,
		                                                     1,
		                                                     3,
		                                                     0,
		                                                     0,
		                                                     0,
		                                                     0,
		                                                     new SimpleRecipeBuilder());

		var macer = GregTechAPI.registerMetaTileEntity(60+tier,
		                                               new MetaTileEntityMacerator(new ResourceLocation(GTValues.MODID, "macerator." + GTValues.VN[tier].toLowerCase()),
		                                                                           map, outputs, Textures.MACERATOR_OVERLAY, tier));

		MetaTileEntity macerTE = new MetaTileEntityHolder().setMetaTileEntity(macer);
		macerTE.getHolder().setWorld(world);

		// stone makes cobblestone and has a chance of gravel
		map.recipeBuilder()
		   .inputs(new ItemStack(Blocks.STONE))
		   .outputs(new ItemStack(Blocks.COBBLESTONE))
		   .chancedOutput(new ItemStack(Blocks.GRAVEL), MACER_BASE_CHANCE, MACER_BONUS_CHANCE)
		   .EUt(12).duration(200) // ULV-scaling LV-base-tier recipe, emulating crushedCentrifuged macer recipes
		   .buildAndRegister();

		// get at the Macerator custom workable logic
		var workableFn = MetaTileEntityMacerator.class.getDeclaredMethod("createWorkable", RecipeMap.class);
		workableFn.setAccessible(true);
		RecipeLogicEnergy workable = (RecipeLogicEnergy) workableFn.invoke(macer, map);

		AbstractRecipeLogic arl = new AbstractRecipeLogic(macerTE, map) {

			@Override
			protected int getMachineTierForRecipe(Recipe recipe) {
				return workable.getMachineTierForRecipe(recipe);
			}

			@Override
			protected long getEnergyStored() {
				return Long.MAX_VALUE;
			}

			@Override
			protected long getEnergyCapacity() {
				return Long.MAX_VALUE;
			}

			@Override
			protected boolean drawEnergy(int recipeEUt) {
				return true;
			}

			@Override
			protected long getMaxVoltage() {
				return GTValues.V[tier];
			}
		};

		arl.isOutputsFull = false;
		arl.invalidInputsForRecipes = false;

		// put a stack of stone in the machine
		arl.getInputInventory().insertItem(0, new ItemStack(Blocks.STONE, 16), false);
		arl.trySearchNewRecipe();
		// ensure that it found a recipe and consumed inputs
		assertTrue(arl.isActive);

		// get the computed chance of the gravel stack
		var chancedOut = arl.chancedItemOutputs.get(0);
		assertEquals(chanceExpected, chancedOut.getRight());
	}
}