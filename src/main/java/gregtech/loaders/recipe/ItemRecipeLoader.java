package gregtech.loaders.recipe;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import static gregtech.api.recipes.RecipeMaps.*;
import static gregtech.common.items.MetaItems.*;

public class ItemRecipeLoader {
    private ItemRecipeLoader() {}

    public static void init() {
        registerMetaItem1Recipes();
        registerMetaItem2Recipes();
    }

    private static void registerMetaItem1Recipes() {
        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(SPRAY_EMPTY.getStackForm())
            .input(OrePrefix.dust, Materials.Redstone)
            .input(OrePrefix.plate, Materials.Tin, 2)
            .duration(200).EUt(8)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(LARGE_FLUID_CELL_STEEL.getStackForm())
            .input(OrePrefix.plateDense, Materials.Steel, 2)
            .input(OrePrefix.ring, Materials.Steel, 8)
            .circuitMeta(1)
            .duration(100).EUt(64)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(LARGE_FLUID_CELL_TUNGSTEN_STEEL.getStackForm())
            .input(OrePrefix.plateDense, Materials.TungstenSteel, 2)
            .input(OrePrefix.ring, Materials.TungstenSteel, 8)
            .circuitMeta(1)
            .duration(200).EUt(256)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(FOAM_SPRAYER.getStackForm())
            .input(OrePrefix.plate, Materials.Tin, 6)
            .inputs(SPRAY_EMPTY.getStackForm())
            .input(OrePrefix.paneGlass.name(), 1)
            .duration(200).EUt(8)
            .buildAndRegister();

        // Matches/lighters recipes
        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_MATCHES.getStackForm())
            .input(OrePrefix.stick, Materials.Wood)
            .input(OrePrefix.dustSmall, Materials.Phosphorus)
            .duration(16).EUt(16)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_MATCHES.getStackForm())
            .input(OrePrefix.stick, Materials.Wood)
            .input(OrePrefix.dustSmall, Materials.Phosphor)
            .duration(16).EUt(16)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_MATCHES.getStackForm(4))
            .input(OrePrefix.stick, Materials.Wood, 4)
            .input(OrePrefix.dust, Materials.Phosphorus)
            .duration(64).EUt(16)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_MATCHES.getStackForm(4))
            .input(OrePrefix.stick, Materials.Wood, 4)
            .input(OrePrefix.dust, Materials.Phosphor)
            .duration(64).EUt(16)
            .buildAndRegister();

        PACKER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_MATCHBOX.getStackForm())
            .inputs(TOOL_MATCHES.getStackForm(16))
            .input(OrePrefix.plate, Materials.Paper)
            .duration(64).EUt(16)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_LIGHTER_INVAR.getStackForm())
            .input(OrePrefix.plate, Materials.Invar, 2)
            .inputs(new ItemStack(Items.FLINT, 1))
            .duration(256).EUt(16)
            .buildAndRegister();

        ASSEMBLER_RECIPES
            .recipeBuilder()
            .outputs(TOOL_LIGHTER_PLATINUM.getStackForm())
            .input(OrePrefix.plate, Materials.Platinum, 2)
            .inputs(new ItemStack(Items.FLINT, 1))
            .duration(256).EUt(256)
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_LV.getStackForm())
            .inputs(BATTERY_SU_LV_SULFURIC_ACID.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_LV.getStackForm())
            .inputs(BATTERY_SU_LV_MERCURY.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_MV.getStackForm())
            .inputs(BATTERY_SU_MV_SULFURIC_ACID.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_MV.getStackForm())
            .inputs(BATTERY_SU_MV_MERCURY.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_HV.getStackForm())
            .inputs(BATTERY_SU_HV_SULFURIC_ACID.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_HV.getStackForm())
            .inputs(BATTERY_SU_HV_MERCURY.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_LV.getStackForm())
            .inputs(BATTERY_RE_LV_CADMIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_LV.getStackForm())
            .inputs(BATTERY_RE_LV_LITHIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_LV.getStackForm())
            .inputs(BATTERY_RE_LV_SODIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_MV.getStackForm())
            .inputs(BATTERY_RE_MV_CADMIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_MV.getStackForm())
            .inputs(BATTERY_RE_MV_LITHIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_MV.getStackForm())
            .inputs(BATTERY_RE_MV_SODIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_HV.getStackForm())
            .inputs(BATTERY_RE_HV_CADMIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_HV.getStackForm())
            .inputs(BATTERY_RE_HV_LITHIUM.getStackForm())
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_HULL_HV.getStackForm())
            .inputs(BATTERY_RE_HV_SODIUM.getStackForm())
            .buildAndRegister();
    }

    private static void registerMetaItem2Recipes() {

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_LV_CADMIUM.getStackForm())
            .inputs(BATTERY_HULL_LV.getStackForm())
            .input(OrePrefix.dust, Materials.Cadmium, 2)
            .duration(100).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .inputs(BATTERY_HULL_LV.getStackForm())
            .input(OrePrefix.dust, Materials.Lithium, 2)
            .outputs(BATTERY_RE_LV_LITHIUM.getStackForm())
            .duration(100).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_LV_SODIUM.getStackForm())
            .inputs(BATTERY_HULL_LV.getStackForm())
            .input(OrePrefix.dust, Materials.Sodium, 2)
            .duration(100).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_MV_CADMIUM.getStackForm())
            .inputs(BATTERY_HULL_MV.getStackForm())
            .input(OrePrefix.dust, Materials.Cadmium, 8)
            .duration(400).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_MV_LITHIUM.getStackForm())
            .inputs(BATTERY_HULL_MV.getStackForm())
            .input(OrePrefix.dust, Materials.Lithium, 8)
            .duration(400).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_MV_SODIUM.getStackForm())
            .inputs(BATTERY_HULL_MV.getStackForm())
            .input(OrePrefix.dust, Materials.Sodium, 8)
            .duration(400).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_HV_CADMIUM.getStackForm())
            .inputs(BATTERY_HULL_HV.getStackForm())
            .input(OrePrefix.dust, Materials.Cadmium, 16)
            .duration(1600).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_HV_LITHIUM.getStackForm())
            .inputs(BATTERY_HULL_HV.getStackForm())
            .input(OrePrefix.dust, Materials.Lithium, 16)
            .duration(1600).EUt(2)
            .buildAndRegister();

        CANNER_RECIPES
            .recipeBuilder()
            .outputs(BATTERY_RE_HV_SODIUM.getStackForm())
            .inputs(BATTERY_HULL_HV.getStackForm())
            .input(OrePrefix.dust, Materials.Sodium, 16)
            .duration(1600).EUt(2)
            .buildAndRegister();

        // Dyes recipes
        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 1))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 0))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 12))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 1))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 13))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 2))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 7))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 3))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 1))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 4))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 14))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 5))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 7))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 6))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 9))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 7))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 7))
            .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 8))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 11))
            .inputs(new ItemStack(Blocks.YELLOW_FLOWER, 1, 0))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 3, 11))
            .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 3, 13))
            .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 3, 1))
            .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 3, 9))
            .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5))
            .buildAndRegister();

        EXTRACTOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.DYE, 2, 1))
            .inputs(new ItemStack(Items.BEETROOT, 1))
            .buildAndRegister();

        // Misc
        MACERATOR_RECIPES
            .recipeBuilder()
            .outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Cocoa, 1))
            .inputs(new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()))
            .duration(400).EUt(2)
            .buildAndRegister();

        MACERATOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.SUGAR, 1))
            .inputs(new ItemStack(Items.REEDS, 1))
            .duration(400).EUt(2)
            .buildAndRegister();

        MACERATOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.MELON, 8, 0))
            .inputs(new ItemStack(Blocks.MELON_BLOCK, 1, 0))
            .chancedOutput(new ItemStack(Items.MELON_SEEDS, 1), 8000, 500)
            .duration(400).EUt(2)
            .buildAndRegister();

        MACERATOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.PUMPKIN_SEEDS, 4, 0))
            .inputs(new ItemStack(Blocks.PUMPKIN, 1, 0))
            .duration(400).EUt(2)
            .buildAndRegister();

        MACERATOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.MELON_SEEDS, 1, 0))
            .inputs(new ItemStack(Items.MELON, 1, 0))
            .duration(400).EUt(2)
            .buildAndRegister();

        MACERATOR_RECIPES
            .recipeBuilder()
            .outputs(new ItemStack(Items.STRING, 3))
            .inputs(CountableIngredient.from("blockWool", 1))
            .chancedOutput(new ItemStack(Items.STRING, 1), 2000, 800)
            .duration(400).EUt(2)
            .buildAndRegister();
    }
}
