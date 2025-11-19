package gregtech.integration.jei.recipe.primitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.*;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.util.GTUtility;
import gregtech.common.metatileentities.MetaTileEntities;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import static gregtech.api.GTValues.LV;
import static gregtech.api.GTValues.ULV;

public class OreByProduct implements IRecipeWrapper {
    private static final List<OrePrefix> ORES = new ArrayList<>();

    private static final int NUM_INPUTS = 21;

    public static void addOreByProductPrefix(OrePrefix orePrefix) {
        if (!ORES.contains(orePrefix)) {
            ORES.add(orePrefix);
        }
    }

    private static final ImmutableList<OrePrefix> IN_PROCESSING_STEPS = ImmutableList.of(
            OrePrefix.crushed,
            OrePrefix.crushedPurified,
            OrePrefix.dustImpure,
            OrePrefix.dustPure,
            OrePrefix.crushedCentrifuged);

    private static ImmutableList<ItemStack> ALWAYS_MACHINES;

    private final Int2ObjectMap<Recipe.ChanceEntry> chances = new Int2ObjectOpenHashMap<>();
    private final List<List<ItemStack>> inputs = new ArrayList<>();
    private final List<List<ItemStack>> outputs = new ArrayList<>();
    private final List<List<FluidStack>> fluidInputs = new ArrayList<>();
    private boolean hasDirectSmelt = false;
    private boolean hasChemBath = false;
    private boolean hasSeparator = false;
    private boolean hasSifter = false;
    private int currentSlot;

	public OreByProduct(DustMaterial material) {
        if (ALWAYS_MACHINES == null) {
            ALWAYS_MACHINES = ImmutableList.of(
                    MetaTileEntities.MACERATOR[ULV].getStackForm(),
                    MetaTileEntities.MACERATOR[ULV].getStackForm(),
                    MetaTileEntities.CENTRIFUGE[ULV].getStackForm(),
                    MetaTileEntities.ORE_WASHER[ULV].getStackForm(),
                    MetaTileEntities.THERMAL_CENTRIFUGE[ULV].getStackForm(),
                    MetaTileEntities.MACERATOR[ULV].getStackForm(),
                    MetaTileEntities.MACERATOR[ULV].getStackForm(),
                    MetaTileEntities.CENTRIFUGE[ULV].getStackForm()
            );
        }
        int oreMultiplier = material.oreMultiplier;
        int byproductMultiplier = material.byProductMultiplier;
        currentSlot = 0;

        List<ItemStack> oreStacks = new ArrayList<>();
        for (OrePrefix prefix : ORES) {
            oreStacks.addAll(OreDictionary.getOres(prefix.name() + material.toCamelCaseString()));
        }
        inputs.add(oreStacks);

        List<ItemStack> simpleWashers = new ArrayList<>();
        simpleWashers.add(new ItemStack(Items.CAULDRON));

        if (!material.disableDirectSmelting) {
            addToInputs(new ItemStack(Blocks.FURNACE));
            hasDirectSmelt = true;
        } else {
            addToInputs(ItemStack.EMPTY);
        }

        for (ItemStack stack : ALWAYS_MACHINES) {
            addToInputs(stack);
        }

        inputs.add(simpleWashers);
        inputs.add(simpleWashers);
        inputs.add(simpleWashers);

        if (material.washedIn != null) {
            hasChemBath = true;
            addToInputs(MetaTileEntities.CHEMICAL_BATH[ULV].getStackForm());
        } else {
            addToInputs(ItemStack.EMPTY);
        }
        if (material.separatedOnto != null) {
            hasSeparator = true;
            addToInputs(MetaTileEntities.ELECTROMAGNETIC_SEPARATOR[ULV].getStackForm());
        } else {
            addToInputs(ItemStack.EMPTY);
        }
        if (material instanceof GemMaterial) {
            hasSifter = true;
            addToInputs(MetaTileEntities.SIFTER[ULV].getStackForm());
        } else {
            addToInputs(ItemStack.EMPTY);
        }

        for (OrePrefix prefix : IN_PROCESSING_STEPS) {
            List<ItemStack> tempList = new ArrayList<>();
            tempList.add(OreDictUnifier.get(prefix, material));
            inputs.add(tempList);
        }

        currentSlot += NUM_INPUTS;

        if (hasDirectSmelt) {
            ItemStack smeltResult;
            Material smeltMaterial = material.directSmelting == null ? material : material.directSmelting;
            if (smeltMaterial instanceof IngotMaterial) {
                smeltResult = OreDictUnifier.get(OrePrefix.ingot, smeltMaterial);
            } else if (smeltMaterial instanceof GemMaterial) {
                smeltResult = OreDictUnifier.get(OrePrefix.gem, smeltMaterial);
            } else {
                smeltResult = OreDictUnifier.get(OrePrefix.dust, smeltMaterial);
            }
            smeltResult.setCount(smeltResult.getCount() * oreMultiplier);
            addToOutputs(smeltResult);
        } else {
            addEmptyOutputs(1);
        }

        addToOutputs(material, OrePrefix.crushed, 2 * oreMultiplier);
        addToOutputs(getByproduct(material, 0), OrePrefix.dust, 1);
        addChance(1400, 850);

        addToOutputs(material, OrePrefix.dustImpure, 1);
        addToOutputs(getByproduct(material, 0), OrePrefix.dust, byproductMultiplier);
        addChance(1400, 850);

        addToOutputs(material, OrePrefix.dust, 1);
        addToOutputs(getByproduct(material, 0), OrePrefix.dust, 1);

        addToOutputs(material, OrePrefix.crushedPurified, 1);
        addToOutputs(getByproduct(material, 0), OrePrefix.dust, 1);

        List<FluidStack> fluidStacks = new ArrayList<>();
        fluidStacks.add(Materials.Water.getFluid(1000));
        fluidStacks.add(Materials.DistilledWater.getFluid(100));
        fluidInputs.add(fluidStacks);

        addToOutputs(material, OrePrefix.crushedCentrifuged, 1);
        addToOutputs(getByproduct(material, 1), OrePrefix.dust, 1);

        addToOutputs(material, OrePrefix.dust, 1);
        addToOutputs(getByproduct(material, 2), OrePrefix.dust, 1);
        addChance(1400, 850);

        addToOutputs(material, OrePrefix.dustPure, 1);
        addToOutputs(getByproduct(material, 1), OrePrefix.dust, 1);
        addChance(1400, 850);

        addToOutputs(material, OrePrefix.dust, 1);
        addToOutputs(getByproduct(material, 1), OrePrefix.dust, 1);

        addToOutputs(material, OrePrefix.crushed, 1);
        addToOutputs(material, OrePrefix.crushedPurified, 1);
        addToOutputs(material, OrePrefix.dustImpure, 1);
        addToOutputs(material, OrePrefix.dust, 1);
        addToOutputs(material, OrePrefix.dustPure, 1);
        addToOutputs(material, OrePrefix.dust, 1);

        List<FluidStack> washedFluid = new ArrayList<>();
        if (hasChemBath) {
            addToOutputs(material, OrePrefix.crushedPurified, 1);
            addToOutputs(getByproduct(material, 3), OrePrefix.dust, byproductMultiplier);
            addChance(7000, 580);
            washedFluid.add(material.washedIn.getFluid(material.washedIn == Materials.SodiumPersulfate ? 100 : 1000));
        } else {
            addEmptyOutputs(2);
        }
        fluidInputs.add(washedFluid);

        if (hasSeparator) {
            OrePrefix prefix = (material.separatedOnto instanceof IngotMaterial) ? OrePrefix.nugget : OrePrefix.dust;
            ItemStack separatedStack = OreDictUnifier.get(prefix, material.separatedOnto, 1);
            addToOutputs(material, OrePrefix.dust, 1);
            addToOutputs(material.separatedOnto, OrePrefix.dust, 1);
            addChance(4000, 900);
            addToOutputs(separatedStack);
            addChance(2000, 600);
        } else {
            addEmptyOutputs(3);
        }

        if (hasSifter) {
            boolean highOutput = material.hasFlag(GemMaterial.MatFlags.HIGH_SIFTER_OUTPUT);

            addToOutputs(material, OrePrefix.gemExquisite, 1);
            addGemChance(100, 30, 300, 60, highOutput);
            addToOutputs(material, OrePrefix.gemFlawless, 1);
            addGemChance(400, 70, 1200, 180, highOutput);
            addToOutputs(material, OrePrefix.gem, 1);
            addGemChance(1500, 300, 4500, 540, highOutput);
            addToOutputs(material, OrePrefix.dustPure, 1);
            addGemChance(5000, 600, 3500, 500, highOutput);
            addToOutputs(material, OrePrefix.gemFlawed, 1);
            addGemChance(2000, 240, 1400, 240, highOutput);
            addToOutputs(material, OrePrefix.gemChipped, 1);
            addGemChance(4000, 320, 2800, 320, highOutput);
        } else {
            addEmptyOutputs(6);
        }
	}

    public boolean hasByProducts() {
        return !outputs.isEmpty();
    }

    public Recipe.ChanceEntry getChance(int slot) {
        return chances.get(slot);
    }

    public boolean hasDirectSmelt() {
        return hasDirectSmelt;
    }

    public boolean hasChemBath() {
        return hasChemBath;
    }

    public boolean hasSeparator() {
        return hasSeparator;
    }

    public boolean hasSifter() {
        return hasSifter;
    }

    public Material getByproduct(DustMaterial base, int index) {
        if (base.oreByProducts.isEmpty()) return base;
        return base.oreByProducts.get(Math.min(index, base.oreByProducts.size() - 1));
    }

    public void addToOutputs(Material material, OrePrefix prefix, int size) {
        addToOutputs(OreDictUnifier.get(prefix, material, size));
    }

    public void addToOutputs(ItemStack stack) {
        List<ItemStack> tempList = new ArrayList<>();
        tempList.add(stack);
        outputs.add(tempList);
        currentSlot++;
    }

    public void addEmptyOutputs(int amount) {
        for (int i = 0; i < amount; i++) {
            addToOutputs(ItemStack.EMPTY);
        }
    }

    public void addToInputs(ItemStack stack) {
        List<ItemStack> tempList = new ArrayList<>();
        tempList.add(stack);
        inputs.add(tempList);
    }

    public void addChance(int base, int tier) {
        ItemStack stack = outputs.get(currentSlot - 1 - NUM_INPUTS).get(0);
        if (!stack.isEmpty()) {
            chances.put(currentSlot - 1, new Recipe.ChanceEntry(stack, base, tier));
        }
    }

    public void addGemChance(int baseLow, int tierLow, int baseHigh, int tierHigh, boolean high) {
        if (high) addChance(baseHigh, tierHigh);
        else addChance(baseLow, tierLow);
    }

	@Override
	public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setInputLists(VanillaTypes.FLUID, fluidInputs);
		ingredients.setOutputLists(VanillaTypes.ITEM, outputs);
	}

	public void addTooltip(int slotIndex, boolean input, Object ingredient, List<String> tooltip) {
        if (chances.containsKey(slotIndex)) {
            Recipe.ChanceEntry entry = chances.get(slotIndex);
            double chance = entry.getChance() / 100D;
            double boost = entry.getBoostPerTier() / 100D;
            tooltip.add(I18n.format("gregtech.recipe.chance", chance, boost));
        }
	}
}
