package gregtech.loaders.recipe;

import com.github.bsideup.jabel.Desugar;
import gregtech.api.GTValues;
import gregtech.api.cover.ICoverable;
import gregtech.api.items.OreDictNames;
import gregtech.api.metatileentity.ITiered;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.ConfigHolder;

import gregtech.common.blocks.BlockWireCoil.CoilType;
import gregtech.common.blocks.LookupBlock;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static gregtech.api.recipes.ModHandler.Substitution;
import static gregtech.api.recipes.ModHandler.Substitution.sub;
import static gregtech.common.blocks.BlockBoilerCasing.*;
import static gregtech.common.blocks.BlockFireboxCasing.*;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.*;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.*;
import static gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType.*;
import static gregtech.common.blocks.BlockTurbineCasing.TurbineCasingType.*;
import static gregtech.common.blocks.BlockWarningSign.SignType;
import static gregtech.common.blocks.BlockWarningSign.SignType.*;
import static gregtech.common.blocks.BlockWireCoil.CoilType.*;
import static gregtech.loaders.recipe.CraftingComponent.*;
import static gregtech.loaders.recipe.MetaTileEntityLoader.TieredElement.*;

public class MetaTileEntityLoader {

    public static void init() {
        // Tiered Machine Casings
        registerTieredShapedRecipes(MachineCasingType.getTiered(),
                                    x -> "casing_%1$s",
                                    Enum::ordinal,
                                    LookupBlock::getStack,
                                    new String[] {
                                        "PPP",
                                        "PwP",
                                        "PPP"
                                    },
                                    sub('P', HULL_PLATE_1));

        // Primitive Machine Casings
        registerTieredShapedRecipes(arr(PRIMITIVE_BRICKS, COKE_BRICKS),
                                    x -> "casing_" + x.getName(),
                                    Enum::ordinal,
                                    LookupBlock::getStack,
                                    new String[] {
                                        "XX",
                                        "XX"
                                    },
                                    sub('X', PRIMITIVE_MATERIAL));

        ModHandler.addShapedRecipe("casing_bronze_bricks",
                                   BRONZE_BRICKS.getStack(3),
                                   new String[]{
                                       "PhP",
                                       "PBP",
                                       "PwP"
                                   },
                                   sub('P', new UnificationEntry(OrePrefix.plate, Materials.Bronze)),
                                   sub('B', new ItemStack(Blocks.BRICK_BLOCK, 1)));

        // Metal Casings        
        for(var casing : arr(STEEL_SOLID, TITANIUM_STABLE, INVAR_HEATPROOF,
                             ALUMINIUM_FROSTPROOF, STAINLESS_CLEAN, TUNGSTENSTEEL_ROBUST)) {
            ModHandler.addShapedRecipe("casing_" + casing.getName(),
                                       casing.getStack(3),
                                       new String[] {
                                           "PhP",
                                           "PFP",
                                           "PwP"
                                       },
                                       sub('P', casing.getUE(OrePrefix.plate)),
                                       sub('F', casing.getUE(OrePrefix.frameGt)));
        }

        // Steel Turbine Casing
        ModHandler.addShapedRecipe("casing_steel_turbine_casing",
                                   STEEL_TURBINE_CASING.getStack(3),
                                   new String[] {
                                       "PhP",
                                       "PFP",
                                       "PwP"
                                   },
                                   sub('P', new UnificationEntry(OrePrefix.plate, Materials.Magnalium)),
                                   sub('F', new UnificationEntry(OrePrefix.frameGt, Materials.BlueSteel)));

        // Turbine Casings (except Steel)
        for(var casing : arr(STAINLESS_TURBINE_CASING, TITANIUM_TURBINE_CASING, TUNGSTENSTEEL_TURBINE_CASING)) {
            ModHandler.addShapedRecipe("casing_" + casing.getName(),
                                       casing.getStack(3),
                                       new String[] {
                                           "PhP",
                                           "PFP",
                                           "PwP"
                                       },
                                       sub('P', casing.getUE(OrePrefix.plate)),
                                       sub('F', STEEL_TURBINE_CASING.getStack()));
        }

        // Boiler Casings
        for(var casing : BoilerCasingType.values())
            ModHandler.addShapedRecipe("casing_" + casing.getName(),
                                       casing.getStack(3),
                                       new String[] {
                                           "PIP",
                                           "IFI",
                                           "PIP"
                                       },
                                       sub('P', casing.getUE(OrePrefix.plate)),
                                       sub('F', casing.getUE(OrePrefix.frameGt)),
                                       sub('I', casing.getUE(OrePrefix.pipeMedium)));

        // Firebox Casings
        for(var casing : FireboxCasingType.values())
            ModHandler.addShapedRecipe("casing_" + casing.getName(),
                                       casing.getStack(3),
                                       new String[] {
                                           "PSP",
                                           "SFS",
                                           "PSP"
                                       },
                                       sub('P', casing.getUE(OrePrefix.plate)),
                                       sub('F', casing.getUE(OrePrefix.frameGt)),
                                       sub('S', casing.getUE(OrePrefix.stick)));

        // Wire Coils (except Fusion)
        for (CoilType coilType : CoilType.values())
            if (coilType.getMaterial() != null)
                ModHandler.addShapedRecipe("heating_coil_" + coilType.getName(),
                                           coilType.getStack(),
                                           new String[]{
                                               "XXX",
                                               "XwX",
                                               "XXX"
                                           },
                                           sub('X', coilType.getUE(OrePrefix.wireGtDouble)));

        // Gearbox Casings
        for(var casing : arr(BRONZE_GEARBOX, STEEL_GEARBOX, TITANIUM_GEARBOX))
            ModHandler.addShapedRecipe("casing_" + casing.getName(),
                                       casing.getStack(3),
                                       new String[] {
                                           "PhP",
                                           "GFG",
                                           "PwP"
                                       },
                                       sub('P', casing.getUE(OrePrefix.plate)),
                                       sub('F', casing.getUE(OrePrefix.frameGt)),
                                       sub('G', casing.getUE(OrePrefix.gear)));

        // Multiblock Casings
        ModHandler.addShapedRecipe("casing_grate_casing",
                                   GRATE_CASING.getStack(3),
                                   new String[] {
                                       "PVP",
                                       "PFP",
                                       "PMP"
                                   },
                                   sub('P', new ItemStack(Blocks.IRON_BARS, 1)),
                                   sub('F', new UnificationEntry(OrePrefix.frameGt, Materials.Steel)),
                                   sub('M', MetaItems.ELECTRIC_MOTOR_MV),
                                   sub('V', new UnificationEntry(OrePrefix.rotor, Materials.Steel)));

        ModHandler.addShapedRecipe("casing_assembler_casing",
                                   ASSEMBLER_CASING.getStack(3),
                                   new String[] {
                                       "PVP",
                                       "PFP",
                                       "PMP"
                                   },
                                   sub('P', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Elite)),
                                   sub('F', new UnificationEntry(OrePrefix.frameGt, Materials.TungstenSteel)),
                                   sub('M', MetaItems.ELECTRIC_MOTOR_IV),
                                   sub('V', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Elite)));

        // Warning Signs
        ModHandler.addShapedRecipe("warning_sign_yellow_stripes", YELLOW_STRIPES.getStack(), "Y  ", " M ", "  B", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_small_yellow_stripes", SMALL_YELLOW_STRIPES.getStack(), "  Y", " M ", "B  ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_radioactive_hazard", RADIOACTIVE_HAZARD.getStack(), " YB", " M ", "   ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_bio_hazard", BIO_HAZARD.getStack(), " Y ", " MB", "   ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_explosion_hazard", EXPLOSION_HAZARD.getStack(), " Y ", " M ", "  B", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_fire_hazard", FIRE_HAZARD.getStack(), " Y ", " M ", " B ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_acid_hazard", ACID_HAZARD.getStack(), " Y ", " M ", "B  ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_magic_hazard", MAGIC_HAZARD.getStack(), " Y ", "BM ", "   ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_frost_hazard", FROST_HAZARD.getStack(), "BY ", " M ", "   ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");
        ModHandler.addShapedRecipe("warning_sign_noise_hazard", NOISE_HAZARD.getStack(), "   ", " M ", "BY ", 'M', STEEL_SOLID.getStack(), 'Y', "dyeYellow", 'B', "dyeBlack");

        // Warning Sign to Steel Casing recipes
        for(var sign : SignType.values())
            ModHandler.addShapelessRecipe(sign.getName() + "_to_steel_solid_casing", STEEL_SOLID.getStack(), sign.getStack());

        // Machine Hulls
        if (ConfigHolder.harderMachineHulls)
            registerTieredShapedRecipes("hull_",
                                        MetaTileEntities.HULL,
                                        new String[] {
                                            "PHP",
                                            "CMC"
                                        },
                                        sub('M', TIER_CASING),
                                        sub('C', HULL_CABLE),
                                        sub('H', HULL_PLATE_1),
                                        sub('P', HULL_PLATE_2));
        else
            registerTieredShapedRecipes("hull_",
                                        MetaTileEntities.HULL,
                                        new String[] {
                                            "CMC"
                                        },
                                        sub('M', TIER_CASING),
                                        sub('C', HULL_CABLE));

        // Transformers
        registerTieredShapedRecipes("transformer_", MetaTileEntities.TRANSFORMER, true,
                                    new String[] {
                                        "KBB",
                                        "CM ",
                                        "KBB"
                                    },
                                    sub('M', HULL),
                                    sub('C', XF_CABLE),
                                    sub('B', XF_CABLE_WORSE),
                                    sub('K', XF_ITEM));

        // Energy Output Hatches
        registerTieredShapedRecipes("energy_output_hatch_", MetaTileEntities.ENERGY_OUTPUT_HATCH,
                                    arr(" MC"),
                                    sub('M', HULL),
                                    sub('C', HATCH_CABLES));

        // Energy Input Hatches
        registerTieredShapedRecipes("energy_input_hatch_", MetaTileEntities.ENERGY_INPUT_HATCH,
                                    arr("CM "),
                                    sub('M', HULL),
                                    sub('C', HATCH_CABLES));

        // Fluid Import Hatches
        registerTieredShapedRecipes("fluid_import_hatch_", MetaTileEntities.FLUID_IMPORT_HATCH,
                                    new String[] {
                                        "G",
                                        "M"
                                    },
                                    sub('M', HULL),
                                    sub('G', new ItemStack(Blocks.GLASS)));

        // Fluid Export Hatches
        registerTieredShapedRecipes("fluid_export_hatch_", MetaTileEntities.FLUID_EXPORT_HATCH,
                                    new String[] {
                                        "M",
                                        "G"
                                    },
                                    sub('M', HULL),
                                    sub('G', new ItemStack(Blocks.GLASS)));

        // Item Import Buses
        registerTieredShapedRecipes("item_import_bus_", MetaTileEntities.ITEM_IMPORT_BUS,
                                    new String[] {
                                        "C",
                                        "M"
                                    },
                                    sub('M', HULL),
                                    sub('C', OreDictNames.chestWood));

        // Item Export Buses
        registerTieredShapedRecipes("item_export_bus_", MetaTileEntities.ITEM_EXPORT_BUS,
                                    new String[] {
                                        "M",
                                        "C"
                                    },
                                    sub('M', HULL),
                                    sub('C', OreDictNames.chestWood));

        // Battery Buffers, 1x1 ~ 4x4, registered in tier order, grouped by size.
        final var kinds =
            arr(OrePrefix.wireGtSingle,
                OrePrefix.wireGtQuadruple,
                OrePrefix.wireGtOctal,
                OrePrefix.wireGtHex);

        for(int j = 0; j < kinds.length; j++) {
            final int i = j;
            String fmt = String.format("battery_buffer_%%1$s_%1$dx%1$d", j + 1);
            registerTieredShapedRecipes(MetaTileEntities.BATTERY_BUFFER,
                                        x -> fmt,
                                        x -> x[i].getTier(),
                                        x -> x[i].getStackForm(),
                                        new String[] {
                                            "WTW",
                                            "WMW",
                                            },
                                        sub('M', HULL),
                                        sub('W', bind(kinds[j], XF_CABLE_MATERIAL)),
                                        sub('T', OreDictNames.chestWood));
        }

        // Chargers
        registerTieredShapedRecipes("charger_", MetaTileEntities.CHARGER,
                                    new String[] {
                                        "WTW",
                                        "WMW",
                                        "BCB"
                                    },
                                    sub('M', HULL),
                                    sub('W', bind(OrePrefix.wireGtHex, XF_CABLE_MATERIAL)),
                                    sub('T', OreDictNames.chestWood),
                                    sub('B', BATTERY),
                                    sub('C', CIRCUIT));

        // Rotor Holders
        registerTieredShapedRecipes("rotor_holder_", MetaTileEntities.ROTOR_HOLDER,
                                    new String[] {
                                        "WHW",
                                        "WRW",
                                        "WWW"
                                    },
                                    sub('H', HULL),
                                    sub('W', ROTOR_HOLDER_WIRE),
                                    sub('R', ROTOR_HOLDER_GEAR));

        // STEAM MACHINES
        ModHandler.addShapedRecipe("bronze_hull", BRONZE_HULL.getStack(), "PPP", "PhP", "PPP", 'P', new UnificationEntry(OrePrefix.plate, Materials.Bronze));
        ModHandler.addShapedRecipe("bronze_bricks_hull", BRONZE_BRICKS_HULL.getStack(), "PPP", "PhP", "BBB", 'P', new UnificationEntry(OrePrefix.plate, Materials.Bronze), 'B', new ItemStack(Blocks.BRICK_BLOCK));
        ModHandler.addShapedRecipe("steel_hull", STEEL_HULL.getStack(), "PPP", "PhP", "PPP", 'P', new UnificationEntry(OrePrefix.plate, Materials.Steel));
        ModHandler.addShapedRecipe("steel_bricks_hull", STEEL_BRICKS_HULL.getStack(), "PPP", "PhP", "BBB", 'P', new UnificationEntry(OrePrefix.plate, Materials.Steel), 'B', new ItemStack(Blocks.BRICK_BLOCK));

        ModHandler.addShapedRecipe("steam_boiler_coal_bronze", MetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm(), "PPP", "P P", "BFB", 'F', OreDictNames.craftingFurnace, 'P', new UnificationEntry(OrePrefix.plate, Materials.Bronze), 'B', new ItemStack(Blocks.BRICK_BLOCK));
        ModHandler.addShapedRecipe("steam_boiler_coal_steel", MetaTileEntities.STEAM_BOILER_COAL_STEEL.getStackForm(), "PPP", "P P", "BFB", 'F', OreDictNames.craftingFurnace, 'P', new UnificationEntry(OrePrefix.plate, Materials.Steel), 'B', new ItemStack(Blocks.BRICK_BLOCK));
        ModHandler.addShapedRecipe("steam_boiler_lava_bronze", MetaTileEntities.STEAM_BOILER_LAVA_BRONZE.getStackForm(), "PPP", "GGG", "PMP", 'M', BRONZE_BRICKS_HULL.getStack(), 'P', new UnificationEntry(OrePrefix.plate, Materials.Bronze), 'G', new ItemStack(Blocks.GLASS, 1));
        ModHandler.addShapedRecipe("steam_boiler_lava_steel", MetaTileEntities.STEAM_BOILER_LAVA_STEEL.getStackForm(), "PPP", "GGG", "PMP", 'M', STEEL_BRICKS_HULL.getStack(), 'P', new UnificationEntry(OrePrefix.plate, Materials.Steel), 'G', new ItemStack(Blocks.GLASS, 1));
        ModHandler.addShapedRecipe("steam_boiler_solar_bronze", MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm(), "GGG", "SSS", "PMP", 'M', BRONZE_BRICKS_HULL.getStack(), 'P', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'S', new UnificationEntry(OrePrefix.plate, Materials.Silver), 'G', new ItemStack(Blocks.GLASS));

        ModHandler.addShapedRecipe("steam_furnace_bronze", MetaTileEntities.STEAM_FURNACE_BRONZE.getStackForm(), "XXX", "XMX", "XFX", 'M', BRONZE_BRICKS_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'F', OreDictNames.craftingFurnace);
        ModHandler.addShapedRecipe("steam_furnace_steel", MetaTileEntities.STEAM_FURNACE_STEEL.getStackForm(), "XXX", "XMX", "XFX", 'M', STEEL_BRICKS_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'F', OreDictNames.craftingFurnace);
        ModHandler.addShapedRecipe("steam_macerator_bronze", MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm(), "DXD", "XMX", "PXP", 'M', BRONZE_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'P', OreDictNames.craftingPiston, 'D', new UnificationEntry(OrePrefix.gem, Materials.Diamond));
        ModHandler.addShapedRecipe("steam_macerator_steel", MetaTileEntities.STEAM_MACERATOR_STEEL.getStackForm(), "DXD", "XMX", "PXP", 'M', STEEL_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'P', OreDictNames.craftingPiston, 'D', new UnificationEntry(OrePrefix.gem, Materials.Diamond));
        ModHandler.addShapedRecipe("steam_extractor_bronze", MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm(), "XXX", "PMG", "XXX", 'M', BRONZE_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'P', OreDictNames.craftingPiston, 'G', new ItemStack(Blocks.GLASS));
        ModHandler.addShapedRecipe("steam_extractor_steel", MetaTileEntities.STEAM_EXTRACTOR_STEEL.getStackForm(), "XXX", "PMG", "XXX", 'M', STEEL_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'P', OreDictNames.craftingPiston, 'G', new ItemStack(Blocks.GLASS));
        ModHandler.addShapedRecipe("steam_hammer_bronze", MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(), "XPX", "XMX", "XAX", 'M', BRONZE_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'P', OreDictNames.craftingPiston, 'A', OreDictNames.craftingAnvil);
        ModHandler.addShapedRecipe("steam_hammer_steel", MetaTileEntities.STEAM_HAMMER_STEEL.getStackForm(), "XPX", "XMX", "XAX", 'M', STEEL_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'P', OreDictNames.craftingPiston, 'A', OreDictNames.craftingAnvil);
        ModHandler.addShapedRecipe("steam_compressor_bronze", MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm(), "XXX", "PMP", "XXX", 'M', BRONZE_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'P', OreDictNames.craftingPiston);
        ModHandler.addShapedRecipe("steam_compressor_steel", MetaTileEntities.STEAM_COMPRESSOR_STEEL.getStackForm(), "XXX", "PMP", "XXX", 'M', STEEL_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'P', OreDictNames.craftingPiston);
        ModHandler.addShapedRecipe("steam_alloy_smelter_bronze", MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(), "XXX", "FMF", "XXX", 'M', BRONZE_BRICKS_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Bronze), 'F', OreDictNames.craftingFurnace);
        ModHandler.addShapedRecipe("steam_alloy_smelter_steel", MetaTileEntities.STEAM_ALLOY_SMELTER_STEEL.getStackForm(), "XXX", "FMF", "XXX", 'M', STEEL_BRICKS_HULL.getStack(), 'X', new UnificationEntry(OrePrefix.pipeSmall, Materials.Steel), 'F', OreDictNames.craftingFurnace);

        // MULTI BLOCK CONTROLLERS
        ModHandler.addShapedRecipe("bronze_primitive_blast_furnace", MetaTileEntities.PRIMITIVE_BLAST_FURNACE.getStackForm(), "PFP", "FwF", "PFP", 'P', PRIMITIVE_BRICKS.getStack(), 'F', OreDictNames.craftingFurnace);
        ModHandler.addShapedRecipe("coke_oven", MetaTileEntities.COKE_OVEN.getStackForm(), "PIP", "IwI", "PIP", 'P', COKE_BRICKS.getStack(), 'I', new UnificationEntry(OrePrefix.plate, Materials.Iron));
        ModHandler.addShapelessRecipe("coke_oven_hatch", MetaTileEntities.COKE_OVEN_HATCH.getStackForm(), COKE_BRICKS.getStack(), MetaTileEntities.BRONZE_TANK.getStackForm());
        ModHandler.addShapedRecipe("electric_blast_furnace", MetaTileEntities.ELECTRIC_BLAST_FURNACE.getStackForm(), "FFF", "CMC", "WCW", 'M', INVAR_HEATPROOF.getStack(), 'F', OreDictNames.craftingFurnace, 'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Basic), 'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tin));
        ModHandler.addShapedRecipe("vacuum_freezer", MetaTileEntities.VACUUM_FREEZER.getStackForm(), "PPP", "CMC", "WCW", 'M', ALUMINIUM_FROSTPROOF.getStack(), 'P', MetaItems.ELECTRIC_PUMP_HV, 'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Extreme), 'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold));
        ModHandler.addShapedRecipe("implosion_compressor", MetaTileEntities.IMPLOSION_COMPRESSOR.getStackForm(), "OOO", "CMC", "WCW", 'M', STEEL_SOLID.getStack(), 'O', new UnificationEntry(OrePrefix.stone, Materials.Obsidian), 'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Advanced), 'W', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium));
        ModHandler.addShapedRecipe("distillation_tower", MetaTileEntities.DISTILLATION_TOWER.getStackForm(), "CBC", "FMF", "CBC", 'M', EV.getStack(), 'B', new UnificationEntry(OrePrefix.pipeLarge, Materials.StainlessSteel), 'C', new UnificationEntry(OrePrefix.circuit, Tier.Extreme), 'F', MetaItems.ELECTRIC_PUMP_EV);
        ModHandler.addShapedRecipe("cracking_unit", MetaTileEntities.CRACKER.getStackForm(), "CEC", "PHP", "CEC", 'C', CUPRONICKEL.getStack(), 'E', MetaItems.ELECTRIC_PUMP_HV, 'P', new UnificationEntry(OrePrefix.circuit, Tier.Extreme), 'H', MetaTileEntities.HULL[GTValues.HV].getStackForm());

        ModHandler.addShapedRecipe("pyrolyse_oven", MetaTileEntities.PYROLYSE_OVEN.getStackForm(), "WEP", "EME", "WCP", 'M', MetaTileEntities.HULL[GTValues.MV].getStackForm(), 'W', MetaItems.ELECTRIC_PISTON_MV, 'P', new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Cupronickel), 'E', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Good), 'C', MetaItems.ELECTRIC_PUMP_MV);
        ModHandler.addShapedRecipe("diesel_engine", MetaTileEntities.DIESEL_ENGINE.getStackForm(), "PCP", "EME", "GWG", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'P', MetaItems.ELECTRIC_PISTON_EV, 'E', MetaItems.ELECTRIC_MOTOR_EV, 'C', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Extreme), 'W', new UnificationEntry(OrePrefix.wireGtSingle, Materials.TungstenSteel), 'G', new UnificationEntry(OrePrefix.gear, Materials.Titanium));
        ModHandler.addShapedRecipe("engine_intake_casing", ENGINE_INTAKE_CASING.getStack(), "PhP", "RFR", "PwP", 'R', new UnificationEntry(OrePrefix.pipeMedium, Materials.Titanium), 'F', TITANIUM_STABLE.getStack(), 'P', new UnificationEntry(OrePrefix.rotor, Materials.Titanium));
        ModHandler.addShapedRecipe("multi_furnace", MetaTileEntities.MULTI_FURNACE.getStackForm(), "PPP", "ASA", "CAC", 'P', Blocks.FURNACE, 'A', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Advanced), 'S', INVAR_HEATPROOF.getStack(), 'C', new UnificationEntry(OrePrefix.cableGtSingle, Materials.AnnealedCopper));

        ModHandler.addShapedRecipe("large_steam_turbine", MetaTileEntities.LARGE_STEAM_TURBINE.getStackForm(), "PSP", "SAS", "CSC", 'S', new UnificationEntry(OrePrefix.gear, Materials.Steel), 'P', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Advanced), 'A', MetaTileEntities.HULL[GTValues.HV].getStackForm(), 'C', new UnificationEntry(OrePrefix.pipeLarge, Materials.Steel));
        ModHandler.addShapedRecipe("large_gas_turbine", MetaTileEntities.LARGE_GAS_TURBINE.getStackForm(), "PSP", "SAS", "CSC", 'S', new UnificationEntry(OrePrefix.gear, Materials.StainlessSteel), 'P', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Extreme), 'A', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'C', new UnificationEntry(OrePrefix.pipeLarge, Materials.StainlessSteel));
        ModHandler.addShapedRecipe("large_plasma_turbine", MetaTileEntities.LARGE_PLASMA_TURBINE.getStackForm(), "PSP", "SAS", "CSC", 'S', new UnificationEntry(OrePrefix.gear, Materials.TungstenSteel), 'P', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Elite), 'A', MetaTileEntities.HULL[GTValues.UV].getStackForm(), 'C', new UnificationEntry(OrePrefix.pipeLarge, Materials.TungstenSteel));

        ModHandler.addShapedRecipe("large_bronze_boiler", MetaTileEntities.LARGE_BRONZE_BOILER.getStackForm(), "PSP", "SAS", "PSP", 'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tin), 'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Basic), 'A', BRONZE_BRICKS.getStack());
        ModHandler.addShapedRecipe("large_steel_boiler", MetaTileEntities.LARGE_STEEL_BOILER.getStackForm(), "PSP", "SAS", "PSP", 'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Copper), 'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Advanced), 'A', STEEL_SOLID.getStack());
        ModHandler.addShapedRecipe("large_titanium_boiler", MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm(), "PSP", "SAS", "PSP", 'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold), 'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Extreme), 'A', TITANIUM_STABLE.getStack());
        ModHandler.addShapedRecipe("large_tungstensteel_boiler", MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER.getStackForm(), "PSP", "SAS", "PSP", 'P', new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium), 'S', new UnificationEntry(OrePrefix.circuit, MarkerMaterials.Tier.Elite), 'A', TUNGSTENSTEEL_ROBUST.getStack());


        // GENERATORS
        registerTieredShapedRecipes("diesel_generator_", MetaTileEntities.DIESEL_GENERATOR,
                                    new String[] {
                                        "PCP",
                                        "EME",
                                        "GWG"
                                    },
                                    sub('M', HULL),
                                    sub('P', PISTON),
                                    sub('E', MOTOR),
                                    sub('C', CIRCUIT),
                                    sub('W', CABLE),
                                    sub('G', GEAR));

        registerTieredShapedRecipes("gas_turbine_", MetaTileEntities.GAS_TURBINE,
                                    new String[] {
                                        "CRC",
                                        "RMR",
                                        "EWE"
                                    },
                                    sub('M', HULL),
                                    sub('E', MOTOR),
                                    sub('R', ROTOR),
                                    sub('C', CIRCUIT),
                                    sub('W', CABLE));

        registerTieredShapedRecipes("steam_turbine_", MetaTileEntities.STEAM_TURBINE,
                                    new String[] {
                                        "PCP",
                                        "RMR",
                                        "EWE"
                                    },
                                    sub('M', HULL),
                                    sub('E', MOTOR),
                                    sub('R', ROTOR),
                                    sub('C', CIRCUIT),
                                    sub('W', CABLE),
                                    sub('P', STEAM_TURBINE_PIPE));

        ModHandler.addShapedRecipe("workbench_bronze", MetaTileEntities.WORKBENCH.getStackForm(), "CWC", "PHP", "PhP", 'C', OreDictNames.chestWood, 'W', new ItemStack(Blocks.CRAFTING_TABLE), 'P', new UnificationEntry(OrePrefix.plate, Materials.Bronze), 'H', BRONZE_HULL.getStack());

        ModHandler.addShapedRecipe("magic_energy_absorber", MetaTileEntities.MAGIC_ENERGY_ABSORBER.getStackForm(), "PCP", "PMP", "PCP", 'M', MetaTileEntities.HULL[GTValues.EV].getStackForm(), 'P', MetaItems.SENSOR_EV, 'C', new UnificationEntry(OrePrefix.circuit, Tier.Extreme));

        // MACHINES
        registerMachineRecipe(MetaTileEntities.ALLOY_SMELTER,
                              new String[] {
                                  "ECE",
                                  "CMC",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('E', CIRCUIT),
                              sub('W', CABLE),
                              sub('C', COIL_HEATING_DOUBLE));

        registerMachineRecipe(MetaTileEntities.ASSEMBLER,
                              new String[] {
                                  "ACA",
                                  "VMV",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('V', CONVEYOR),
                              sub('A', ROBOT_ARM),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.BENDER,
                              new String[] {
                                  "PwP",
                                  "CMC",
                                  "EWE"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.CANNER,
                              new String[] {
                                  "WPW",
                                  "CMC",
                                  "GGG"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.COMPRESSOR,
                              new String[] {
                                  " C ",
                                  "PMP",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.CUTTER,
                              new String[] {
                                  "WCG",
                                  "VMB",
                                  "CWE"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('V', CONVEYOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS),
                              sub('B', OreDictNames.craftingDiamondBlade));

        registerMachineRecipe(MetaTileEntities.ELECTRIC_FURNACE,
                              new String[] {
                                  "ECE",
                                  "CMC",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('E', CIRCUIT),
                              sub('W', CABLE),
                              sub('C', COIL_HEATING));

        registerMachineRecipe(MetaTileEntities.EXTRACTOR,
                              new String[] {
                                  "GCG",
                                  "EMP",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('E', PISTON),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.EXTRUDER,
                              new String[] {
                                  "CCE",
                                  "XMP",
                                  "CCE"
                              },
                              sub('M', HULL),
                              sub('X', PISTON),
                              sub('E', CIRCUIT),
                              sub('P', PIPE),
                              sub('C', COIL_HEATING_DOUBLE));

        registerMachineRecipe(MetaTileEntities.LATHE,
                              new String[] {
                                  "WCW",
                                  "EMD",
                                  "CWP"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('D', DIAMOND));

        registerMachineRecipe(MetaTileEntities.MACERATOR,
                              new String[] {
                                  "PEG",
                                  "WWM",
                                  "CCW"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GRINDER));

        registerMachineRecipe(MetaTileEntities.MICROWAVE,
                              new String[] {
                                  "LWC",
                                  "LMR",
                                  "LEC"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('R', EMITTER),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('L', new UnificationEntry(OrePrefix.plate, Materials.Lead)));

        registerMachineRecipe(MetaTileEntities.WIREMILL,
                              new String[] {
                                  "EWE",
                                  "CMC",
                                  "EWE"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.CENTRIFUGE,
                              new String[] {
                                  "CEC",
                                  "WMW",
                                  "CEC"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.ELECTROLYZER,
                              new String[] {
                                  "IGI",
                                  "IMI",
                                  "CWC"
                              },
                              sub('M', HULL),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('I', WIRE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.THERMAL_CENTRIFUGE,
                              new String[] {
                                  "CEC",
                                  "OMO",
                                  "WEW"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('O', COIL_HEATING_DOUBLE));

        registerMachineRecipe(MetaTileEntities.ORE_WASHER,
                              new String[] {
                                  "RGR",
                                  "CEC",
                                  "WMW"
                              },
                              sub('M', HULL),
                              sub('R', ROTOR),
                              sub('E', MOTOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.PACKER,
                              new String[] {
                                  "BCB",
                                  "RMV",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('R', ROBOT_ARM),
                              sub('V', CONVEYOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('B', OreDictNames.chestWood));

        registerMachineRecipe(MetaTileEntities.UNPACKER,
                              new String[] {
                                  "BCB",
                                  "VMR",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('R', ROBOT_ARM),
                              sub('V', CONVEYOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('B', OreDictNames.chestWood));

        registerMachineRecipe(MetaTileEntities.CHEMICAL_REACTOR,
                              new String[] {
                                  "GRG",
                                  "WEW",
                                  "CMC"
                              },
                              sub('M', HULL),
                              sub('R', ROTOR),
                              sub('E', MOTOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.FLUID_CANNER,
                              new String[] {
                                  "GCG",
                                  "GMG",
                                  "WPW"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.BREWERY,
                              new String[] {
                                  "GPG",
                                  "WMW",
                                  "CBC"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('B', STICK_DISTILLATION),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', new ItemStack(Blocks.GLASS)));

        registerMachineRecipe(MetaTileEntities.FERMENTER,
                              new String[] {
                                  "WPW",
                                  "GMG",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.FLUID_EXTRACTOR,
                              new String[] {
                                  "GCG",
                                  "PME",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('E', PISTON),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.FLUID_SOLIDIFIER,
                              new String[] {
                                  "PGP",
                                  "WMW",
                                  "CBC"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS),
                              sub('B', OreDictNames.chestWood));

        registerMachineRecipe(MetaTileEntities.DISTILLERY,
                              new String[] {
                                  "GBG",
                                  "CMC",
                                  "WPW"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('B', STICK_DISTILLATION),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.CHEMICAL_BATH,
                              new String[] {
                                  "VGW",
                                  "PGV",
                                  "CMC"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('V', CONVEYOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.POLARIZER,
                              new String[] {
                                  "ZSZ",
                                  "WMW",
                                  "ZSZ"
                              },
                              sub('M', HULL),
                              sub('S', STICK_ELECTROMAGNETIC),
                              sub('Z', COIL_ELECTRIC),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.ELECTROMAGNETIC_SEPARATOR,
                              new String[] {
                                  "VWZ",
                                  "WMS",
                                  "CWZ"
                              },
                              sub('M', HULL),
                              sub('S', STICK_ELECTROMAGNETIC),
                              sub('Z', COIL_ELECTRIC),
                              sub('V', CONVEYOR),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.AUTOCLAVE,
                              new String[] {
                                  "IGI",
                                  "IMI",
                                  "CPC"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('C', CIRCUIT),
                              sub('I', PLATE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.MIXER,
                              new String[] {
                                  "GRG",
                                  "GEG",
                                  "CMC"
                              },
                              sub('M', HULL),
                              sub('E', MOTOR),
                              sub('R', ROTOR),
                              sub('C', CIRCUIT),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.LASER_ENGRAVER,
                              new String[] {
                                  "PEP",
                                  "CMC",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('E', EMITTER),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.FORMING_PRESS,
                              new String[] {
                                  "WPW",
                                  "CMC",
                                  "WPW"
                              },
                              sub('M', HULL),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.FORGE_HAMMER,
                              new String[] {
                                  "WPW",
                                  "CMC",
                                  "WAW"
                              },
                              sub('M', HULL),
                              sub('P', PISTON),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('A', OreDictNames.craftingAnvil));

        registerMachineRecipe(MetaTileEntities.FLUID_HEATER,
                              new String[] {
                                  "OGO",
                                  "PMP",
                                  "WCW"
                              },
                              sub('M', HULL),
                              sub('P', PUMP),
                              sub('O', COIL_HEATING_DOUBLE),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('G', GLASS));

        registerMachineRecipe(MetaTileEntities.SIFTER,
                              new String[] {
                                  "WFW",
                                  "PMP",
                                  "CFC"
                              },
                              sub('M', HULL),
                              sub('P', PISTON),
                              sub('F', MetaItems.ITEM_FILTER),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.ARC_FURNACE,
                              new String[] {
                                  "WGW",
                                  "CMC",
                                  "PPP"
                              },
                              sub('M', HULL),
                              sub('P', PLATE),
                              sub('C', CIRCUIT),
                              sub('W', CABLE_QUAD),
                              sub('G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite)));

        registerMachineRecipe(MetaTileEntities.PLASMA_ARC_FURNACE,
                              new String[] {
                                  "WGW",
                                  "CMC",
                                  "TPT"
                              },
                              sub('M', HULL),
                              sub('P', PLATE),
                              sub('C', CIRCUIT),
                              sub('W', CABLE_QUAD),
                              sub('T', PUMP),
                              sub('G', new UnificationEntry(OrePrefix.ingot, Materials.Graphite)));

        registerMachineRecipe(MetaTileEntities.PUMP,
                              new String[] {
                                  "WGW",
                                  "GMG",
                                  "TGT"
                              },
                              sub('M', HULL),
                              sub('W', CIRCUIT),
                              sub('G', PUMP),
                              sub('T', PISTON));

        registerMachineRecipe(MetaTileEntities.FISHER,
                              new String[] {
                                  "WTW",
                                  "PMP",
                                  "TGT"
                              },
                              sub('M', HULL),
                              sub('W', CIRCUIT),
                              sub('G', PUMP),
                              sub('T', MOTOR),
                              sub('P', PISTON));

        registerMachineRecipe(MetaTileEntities.AIR_COLLECTOR,
                              new String[] {
                                  "WFW",
                                  "PHP",
                                  "WCW"
                              },
                              sub('W', Blocks.IRON_BARS),
                              sub('F', MetaItems.FLUID_FILTER),
                              sub('P', PUMP),
                              sub('H', HULL),
                              sub('C', CIRCUIT));

        registerMachineRecipe(MetaTileEntities.ITEM_COLLECTOR,
                              new String[] {
                                  "MRM",
                                  "RHR",
                                  "CWC"
                              },
                              sub('M', MOTOR),
                              sub('R', ROTOR),
                              sub('H', HULL),
                              sub('C', CIRCUIT),
                              sub('W', CABLE));

        registerMachineRecipe(MetaTileEntities.BLOCK_BREAKER,
                              new String[] {
                                  "MGM",
                                  "CHC",
                                  "WSW"
                              },
                              sub('M', MOTOR),
                              sub('H', HULL),
                              sub('C', CIRCUIT),
                              sub('W', CABLE),
                              sub('S', Blocks.CHEST),
                              sub('G', MetaItems.COMPONENT_GRINDER_DIAMOND));

        registerMachineRecipe(MetaTileEntities.QUANTUM_CHEST,
                              new String[] {
                                  "CPC",
                                  "PHP",
                                  "CFC"
                              },
                              sub('C', CIRCUIT),
                              sub('P', PLATE),
                              sub('F', FIELD_GENERATOR),
                              sub('H', HULL));

        registerMachineRecipe(MetaTileEntities.QUANTUM_TANK,
                              new String[] {
                                  "CFC",
                                  "PHP",
                                  "CPC"
                              },
                              sub('C', CIRCUIT),
                              sub('P', PLATE),
                              sub('F', FIELD_GENERATOR),
                              sub('H', HULL));

        // Wooden Chests
        ModHandler.addShapelessRecipe("small_wooden_chest", MetaTileEntities.SMALL_WOODEN_CHEST.getStackForm(8), "chest", 's');
        ModHandler.addShapelessRecipe("wooden_chest", MetaTileEntities.WOODEN_CHEST.getStackForm(), "chest", 'r');

        // Metal Chests
        for(var chest : arr(
            of("bronze_chest",          MetaTileEntities.BRONZE_CHEST,          Materials.Bronze),
            of("steel_chest",           MetaTileEntities.STEEL_CHEST,           Materials.Steel),
            of("stainless_steel_chest", MetaTileEntities.STAINLESS_STEEL_CHEST, Materials.StainlessSteel),
            of("titanium_chest",        MetaTileEntities.TITANIUM_CHEST,        Materials.Titanium),
            of("tungsten_steel_chest",  MetaTileEntities.TUNGSTENSTEEL_CHEST,   Materials.TungstenSteel)))
        {
            ModHandler.addShapedRecipe(chest.name, chest.element.getStackForm(),
                                       new String[] {
                                           "XXX",
                                           "X X",
                                           "XXX"
                                       },
                                       sub('X', new UnificationEntry(OrePrefix.plate, chest.material)));
        }

        // Multiblock Tanks
        for (var tank : arr(
            of("wooden_tank",          MetaTileEntities.WOODEN_TANK,          Materials.Wood),
            of("bronze_tank",          MetaTileEntities.BRONZE_TANK,          Materials.Bronze),
            of("steel_tank",           MetaTileEntities.STEEL_TANK,           Materials.Steel),
            of("stainless_steel_tank", MetaTileEntities.STAINLESS_STEEL_TANK, Materials.StainlessSteel),
            of("titanium_tank",        MetaTileEntities.TITANIUM_TANK,        Materials.Titanium),
            of("tungsten_steel_tank",  MetaTileEntities.TUNGSTENSTEEL_TANK,   Materials.TungstenSteel)))
        {
            var prefix = tank.material == Materials.Wood ? OrePrefix.plank : OrePrefix.plate;
            ModHandler.addShapedRecipe(tank.name, tank.element.getStackForm(),
                                       new String[] {
                                           "XYX",
                                           "Y Y",
                                           "XYX"
                                       },
                                       sub('X', new UnificationEntry(prefix, tank.material)),
                                       sub('Y', new UnificationEntry(OrePrefix.blockGlass)));
        }

        ModHandler.addShapedRecipe("tesla_coil",
                                   MetaTileEntities.TESLA_COIL.getStackForm(),
                                   new String[] {
                                       "XXX",
                                       "YHY",
                                       "XXX"
                                   },
                                   sub('X', new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Copper)),
                                   sub('Y', MetaItems.EMITTER_MV),
                                   sub('H', MetaTileEntities.HULL[GTValues.MV].getStackForm()));
    }

    public static <T extends MetaTileEntity & ITiered> void registerMachineRecipe(T[] metaTileEntities,
                                                                                  String[] definition,
                                                                                  Substitution<?>... subs)
    {
        registerTieredShapedRecipes(metaTileEntities,
                                    MetaTileEntity::getMetaName,
                                    ITiered::getTier,
                                    MetaTileEntity::getStackForm,
                                    definition,
                                    subs);
    }

    public static <T extends ICoverable & ITiered>
    void registerTieredShapedRecipes(String prefix,
                                     T[] targets,
                                     String[] definition,
                                     Substitution<?>... subs)
    {
        registerTieredShapedRecipes(prefix, targets,false, definition, subs);
    }

    /**
     * Generates tiered recipes, optionally performing cleaning to cull unresolvable
     * elements from the recipe definition, if some tiers have an item and others don't.
     *
     * @param prefix     text to prepend to the tier name for the recipe name
     * @param targets    tiered machines to generate recipes for
     * @param doClean    whether to cull unresolvable mappings from the definition
     * @param definition the recipe definition (one to three Strings)
     * @param subs       substitution mappings
     * @param <T>        type of target elements
     */
    public static <T extends ICoverable & ITiered>
    void registerTieredShapedRecipes(String prefix,
                                     T[] targets,
                                     boolean doClean,
                                     final String[] definition,
                                     Substitution<?>... subs)
    {
        for(T target : targets){
            int tier = target.getTier();
            var defCopy = definition;
            var subsResolved = resolveComponents(tier, subs);
            if(doClean) {
                defCopy= Arrays.copyOf(definition, definition.length);
                subsResolved = clean(defCopy, subsResolved);
            }
            ModHandler.addShapedRecipe(prefix + GTValues.VN[tier].toLowerCase(),
                                       target.getStackForm(),
                                       defCopy, subsResolved);
        }
    }

    /**
     * Resolves Component-typed targets to the respective tiered ingredient.
     *
     * @param tier the tier of components to resolve
     * @param subs substitution mappings
     * @return a new array containing tier-resolved substitution mappings
     */
    private static Substitution<?>[] resolveComponents(int tier,
                                                       Substitution<?>... subs) {
        Substitution<?>[] result = new ModHandler.Substitution[subs.length];
        for(int i = 0; i < subs.length; i++) {
            var current = subs[i];
            var value = current.value();
            if(value instanceof Component<?> c)
                value = c.getIngredient(tier);
            result[i] = sub(current.key(), value);
        }
        return result;
    }

    /**
     * Removes unresolved characters from a recipe definition, and
     * the associated replacement mapping.
     *
     * @param definition the recipe definition
     * @param subs substitution mappings to apply
     * @return the definition is modified in-place. A new array of non-null-valued
     *         substitution mappings is returned.
     */
    private static Substitution<?>[] clean(String[] definition,
                                           Substitution<?>... subs) {
        List<Substitution<?>> valid = new ArrayList<>(subs.length);
        for(var sub : subs)
            // cull the matcher character from the recipe definition
            if (sub.value() == null)
                for(int s = 0; s < definition.length; s++)
                    definition[s] = definition[s].replaceAll(String.valueOf(sub.key()), " ");
            else
                valid.add(sub);

        return valid.toArray(new ModHandler.Substitution[0]);
    }


    /**
     * For things that need more complicated tier-based generation.
     *
     * @param elements the set of things to register recipes for
     * @param nameFunction a function producing a formatting string where the first substitution is the machine tier
     * @param tierFunction a function to generate a tier ordinal, given an input element
     * @param stackFunction a function to generate an ItemStack, given an input element
     * @param definition an array of String defining the recipe
     * @param subs substitution mappings for the recipe
     * @param <T> the type of input elements
     */
    public static <T> void registerTieredShapedRecipes(T[] elements,
                                                       Function<T, String> nameFunction,
                                                       Function<T, Integer> tierFunction,
                                                       Function<T, ItemStack> stackFunction,
                                                       String[] definition,
                                                       Substitution<?>... subs)
    {
        for(T element : elements) {
            int tier = tierFunction.apply(element);
            ItemStack stack = stackFunction.apply(element);
            String temp = nameFunction.apply(element);
            String name = String.format(temp, GTValues.VN[tier].toLowerCase());
            var resolved = resolveComponents(tier, subs);
            ModHandler.addShapedRecipe(name, stack, definition, resolved);
        }
    }

    /**
     * Convenience function for creating inline arrays
     */
    @SafeVarargs
    public static <T> T[] arr(T... ts) {
        return ts;
    }

    /**
     * Utility record for type-safe recipe generation code for elements which
     * can't report their own name and material in a suitable way.
     * @param name     the name to use for this element
     * @param element  the target element
     * @param material the element's associated material
     */
    @Desugar
    record TieredElement(String name, ICoverable element, Material material) {
        /** Static initializer for convenience. */
        public static TieredElement of(String name, ICoverable mte, Material material) {
            return new TieredElement(name, mte, material);
        }
    }

}
