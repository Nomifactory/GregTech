package gregtech.loaders.recipe;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import static gregtech.common.items.MetaItems.*;

public class ItemRecipeLoader {
    private ItemRecipeLoader() {}

    public static void init() {
        registerMetaItem1Recipes();
        registerMetaItem2Recipes();
    }

    private static void registerMetaItem1Recipes() {
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.dust, Materials.Redstone).input(OrePrefix.plate, Materials.Tin, 2)
                                    .outputs(SPRAY_EMPTY.getStackForm())
                                    .duration(200).EUt(8)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.plateDense, Materials.Steel, 2)
                                    .input(OrePrefix.ring, Materials.Steel, 8)
                                    .outputs(LARGE_FLUID_CELL_STEEL.getStackForm())
                                    .circuitMeta(1).duration(100).EUt(64)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.plateDense, Materials.TungstenSteel, 2)
                                    .input(OrePrefix.ring, Materials.TungstenSteel, 8)
                                    .outputs(LARGE_FLUID_CELL_TUNGSTEN_STEEL.getStackForm())
                                    .circuitMeta(1).duration(200).EUt(256)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.plate, Materials.Tin, 6)
                                    .inputs(SPRAY_EMPTY.getStackForm())
                                    .input(OrePrefix.paneGlass.name(), 1)
                                    .outputs(FOAM_SPRAYER.getStackForm())
                                    .duration(200).EUt(8)
                                    .buildAndRegister();

        // Matches/lighters recipes
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.stick, Materials.Wood).input(OrePrefix.dustSmall, Materials.Phosphorus)
                                    .outputs(TOOL_MATCHES.getStackForm())
                                    .duration(16).EUt(16)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.stick, Materials.Wood).input(OrePrefix.dustSmall, Materials.Phosphor)
                                    .outputs(TOOL_MATCHES.getStackForm())
                                    .duration(16).EUt(16)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.stick, Materials.Wood, 4).input(OrePrefix.dust, Materials.Phosphorus)
                                    .outputs(TOOL_MATCHES.getStackForm(4))
                                    .duration(64).EUt(16)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.stick, Materials.Wood, 4).input(OrePrefix.dust, Materials.Phosphor)
                                    .outputs(TOOL_MATCHES.getStackForm(4))
                                    .duration(64)
                                    .EUt(16)
                                    .buildAndRegister();

        RecipeMaps.PACKER_RECIPES.recipeBuilder()
                                 .inputs(TOOL_MATCHES.getStackForm(16)).input(OrePrefix.plate, Materials.Paper)
                                 .outputs(TOOL_MATCHBOX.getStackForm())
                                 .duration(64)
                                 .EUt(16)
                                 .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.plate, Materials.Invar, 2).inputs(new ItemStack(Items.FLINT, 1))
                                    .outputs(TOOL_LIGHTER_INVAR.getStackForm())
                                    .duration(256)
                                    .EUt(16)
                                    .buildAndRegister();

        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                                    .input(OrePrefix.plate, Materials.Platinum, 2).inputs(new ItemStack(Items.FLINT, 1))
                                    .outputs(TOOL_LIGHTER_PLATINUM.getStackForm())
                                    .duration(256)
                                    .EUt(256)
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_SU_LV_SULFURIC_ACID.getStackForm())
                                    .outputs(BATTERY_HULL_LV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_SU_LV_MERCURY.getStackForm())
                                    .outputs(BATTERY_HULL_LV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_SU_MV_SULFURIC_ACID.getStackForm())
                                    .outputs(BATTERY_HULL_MV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_SU_MV_MERCURY.getStackForm())
                                    .outputs(BATTERY_HULL_MV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_SU_HV_SULFURIC_ACID.getStackForm())
                                    .outputs(BATTERY_HULL_HV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_SU_HV_MERCURY.getStackForm())
                                    .outputs(BATTERY_HULL_HV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_LV_CADMIUM.getStackForm())
                                    .outputs(BATTERY_HULL_LV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_LV_LITHIUM.getStackForm())
                                    .outputs(BATTERY_HULL_LV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_LV_SODIUM.getStackForm())
                                    .outputs(BATTERY_HULL_LV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_MV_CADMIUM.getStackForm())
                                    .outputs(BATTERY_HULL_MV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_MV_LITHIUM.getStackForm())
                                    .outputs(BATTERY_HULL_MV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_MV_SODIUM.getStackForm())
                                    .outputs(BATTERY_HULL_MV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_HV_CADMIUM.getStackForm())
                                    .outputs(BATTERY_HULL_HV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_HV_LITHIUM.getStackForm())
                                    .outputs(BATTERY_HULL_HV.getStackForm())
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(BATTERY_RE_HV_SODIUM.getStackForm())
                                    .outputs(BATTERY_HULL_HV.getStackForm())
                                    .buildAndRegister();
    }

    private static void registerMetaItem2Recipes() {

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_LV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Cadmium, 2)
                                 .outputs(BATTERY_RE_LV_CADMIUM.getStackForm())
                                 .duration(100).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_LV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Lithium, 2)
                                 .outputs(BATTERY_RE_LV_LITHIUM.getStackForm())
                                 .duration(100).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_LV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Sodium, 2)
                                 .outputs(BATTERY_RE_LV_SODIUM.getStackForm())
                                 .duration(100).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_MV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Cadmium, 8)
                                 .outputs(BATTERY_RE_MV_CADMIUM.getStackForm())
                                 .duration(400).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_MV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Lithium, 8)
                                 .outputs(BATTERY_RE_MV_LITHIUM.getStackForm())
                                 .duration(400).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_MV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Sodium, 8)
                                 .outputs(BATTERY_RE_MV_SODIUM.getStackForm())
                                 .duration(400).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_HV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Cadmium, 16)
                                 .outputs(BATTERY_RE_HV_CADMIUM.getStackForm())
                                 .duration(1600).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_HV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Lithium, 16)
                                 .outputs(BATTERY_RE_HV_LITHIUM.getStackForm())
                                 .duration(1600).EUt(2)
                                 .buildAndRegister();

        RecipeMaps.CANNER_RECIPES.recipeBuilder()
                                 .inputs(BATTERY_HULL_HV.getStackForm())
                                 .input(OrePrefix.dust, Materials.Sodium, 16)
                                 .outputs(BATTERY_RE_HV_SODIUM.getStackForm())
                                 .duration(1600).EUt(2)
                                 .buildAndRegister();

        // Dyes recipes
        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 0))
                                    .outputs(new ItemStack(Items.DYE, 2, 1))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 1))
                                    .outputs(new ItemStack(Items.DYE, 2, 12))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 2))
                                    .outputs(new ItemStack(Items.DYE, 2, 13))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 3))
                                    .outputs(new ItemStack(Items.DYE, 2, 7))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 4))
                                    .outputs(new ItemStack(Items.DYE, 2, 1))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 5))
                                    .outputs(new ItemStack(Items.DYE, 2, 14))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 6))
                                    .outputs(new ItemStack(Items.DYE, 2, 7))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 7))
                                    .outputs(new ItemStack(Items.DYE, 2, 9))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.RED_FLOWER, 1, 8))
                                    .outputs(new ItemStack(Items.DYE, 2, 7))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.YELLOW_FLOWER, 1, 0))
                                    .outputs(new ItemStack(Items.DYE, 2, 11))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 0))
                                    .outputs(new ItemStack(Items.DYE, 3, 11))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 1))
                                    .outputs(new ItemStack(Items.DYE, 3, 13))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 4))
                                    .outputs(new ItemStack(Items.DYE, 3, 1))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.DOUBLE_PLANT, 1, 5))
                                    .outputs(new ItemStack(Items.DYE, 3, 9))
                                    .buildAndRegister();

        RecipeMaps.EXTRACTOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Items.BEETROOT, 1))
                                    .outputs(new ItemStack(Items.DYE, 2, 1))
                                    .buildAndRegister();

        // Misc
        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Items.DYE, 1, EnumDyeColor.BROWN.getDyeDamage()))
                                    .outputs(OreDictUnifier.get(OrePrefix.dust, Materials.Cocoa, 1))
                                    .duration(400)
                                    .EUt(2)
                                    .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Items.REEDS, 1))
                                    .outputs(new ItemStack(Items.SUGAR, 1))
                                    .duration(400)
                                    .EUt(2)
                                    .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.MELON_BLOCK, 1, 0))
                                    .outputs(new ItemStack(Items.MELON, 8, 0))
                                    .chancedOutput(new ItemStack(Items.MELON_SEEDS, 1), 8000, 500)
                                    .duration(400)
                                    .EUt(2)
                                    .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Blocks.PUMPKIN, 1, 0))
                                    .outputs(new ItemStack(Items.PUMPKIN_SEEDS, 4, 0))
                                    .duration(400)
                                    .EUt(2)
                                    .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                                    .inputs(new ItemStack(Items.MELON, 1, 0))
                                    .outputs(new ItemStack(Items.MELON_SEEDS, 1, 0))
                                    .duration(400)
                                    .EUt(2)
                                    .buildAndRegister();

        RecipeMaps.MACERATOR_RECIPES.recipeBuilder()
                                    .inputs(CountableIngredient.from("blockWool", 1))
                                    .outputs(new ItemStack(Items.STRING, 3))
                                    .chancedOutput(new ItemStack(Items.STRING, 1), 2000, 800)
                                    .duration(400)
                                    .EUt(2)
                                    .buildAndRegister();
    }
}
