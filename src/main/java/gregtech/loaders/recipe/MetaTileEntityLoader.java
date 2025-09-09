package gregtech.loaders.recipe;

import gregtech.api.cover.ICoverable;
import gregtech.api.items.OreDictNames;
import gregtech.api.metatileentity.ITiered;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.ConfigHolder;

import gregtech.common.blocks.BlockWireCoil.CoilType;
import gregtech.common.blocks.LookupBlock;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.electric.MetaTileEntityHull;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static gregtech.api.GTValues.*;
import static gregtech.api.recipes.ModHandler.Substitution;
import static gregtech.api.recipes.ModHandler.Substitution.sub;
import static gregtech.common.blocks.BlockBoilerCasing.*;
import static gregtech.common.blocks.BlockFireboxCasing.*;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.getTiered;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.BRONZE_HULL;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.BRONZE_BRICKS_HULL;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.STEEL_HULL;
import static gregtech.common.blocks.BlockMachineCasing.MachineCasingType.STEEL_BRICKS_HULL;
import static gregtech.common.blocks.BlockMetalCasing.MetalCasingType.*;
import static gregtech.common.blocks.BlockMultiblockCasing.MultiblockCasingType.*;
import static gregtech.common.blocks.BlockTurbineCasing.TurbineCasingType.*;
import static gregtech.common.blocks.BlockWarningSign.SignType;
import static gregtech.common.blocks.BlockWarningSign.SignType.*;
import static gregtech.common.blocks.BlockWireCoil.CoilType.*;
import static gregtech.loaders.recipe.CraftingComponent.*;

public class MetaTileEntityLoader {

    public static void init() {
        // Tiered Machine Casings
        registerTieredShapedRecipes(
            getTiered(),
            x -> "casing_%1$s",
            MachineCasingType::getTier,
            LookupBlock::getStack,
            new String[] {
                "PPP",
                "PwP",
                "PPP"
            },
            sub('P', HULL_PLATE_1));

        // Primitive Machine Casings
        registerTieredShapedRecipes(
            arr(PRIMITIVE_BRICKS, COKE_BRICKS),
            x -> "casing_" + x.getName(),
            Enum::ordinal,
            LookupBlock::getStack,
            new String[] {
                "XX",
                "XX"
            },
            sub('X', PRIMITIVE_MATERIAL));

        ModHandler.addShapedRecipe(
            "casing_bronze_bricks",
            BRONZE_BRICKS.getStack(3),
            new String[] {
                "PhP",
                "PBP",
                "PwP"
            },
            sub('P', OrePrefix.plate, Materials.Bronze),
            sub('B', Blocks.BRICK_BLOCK));

        // Metal Casings        
        for(var casing : arr(STEEL_SOLID, TITANIUM_STABLE, INVAR_HEATPROOF,
                             ALUMINIUM_FROSTPROOF, STAINLESS_CLEAN, TUNGSTENSTEEL_ROBUST)) {
            ModHandler.addShapedRecipe(
                "casing_" + casing.getName(),
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
        ModHandler.addShapedRecipe(
            "casing_steel_turbine_casing",
            STEEL_TURBINE_CASING.getStack(3),
            new String[] {
                "PhP",
                "PFP",
                "PwP"
            },
            sub('P', OrePrefix.plate, Materials.Magnalium),
            sub('F', OrePrefix.frameGt, Materials.BlueSteel));

        // Turbine Casings (except Steel)
        for(var casing : arr(STAINLESS_TURBINE_CASING, TITANIUM_TURBINE_CASING, TUNGSTENSTEEL_TURBINE_CASING)) {
            ModHandler.addShapedRecipe(
                "casing_" + casing.getName(),
                casing.getStack(3),
                new String[] {
                    "PhP",
                    "PFP",
                    "PwP"
                },
                sub('P', casing.getUE(OrePrefix.plate)),
                sub('F', STEEL_TURBINE_CASING));
        }

        // Boiler Casings
        for(var casing : BoilerCasingType.values())
            ModHandler.addShapedRecipe(
                "casing_" + casing.getName(),
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
            ModHandler.addShapedRecipe(
                "casing_" + casing.getName(),
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
                ModHandler.addShapedRecipe(
                    "heating_coil_" + coilType.getName(),
                    coilType.getStack(),
                    new String[] {
                        "XXX",
                        "XwX",
                        "XXX"
                    },
                    sub('X', coilType.getUE(OrePrefix.wireGtDouble)));

        // Gearbox Casings
        for(var casing : arr(BRONZE_GEARBOX, STEEL_GEARBOX, TITANIUM_GEARBOX))
            ModHandler.addShapedRecipe(
                "casing_" + casing.getName(),
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
        ModHandler.addShapedRecipe(
            "casing_grate_casing",
            GRATE_CASING.getStack(3),
            new String[] {
                "PVP",
                "PFP",
                "PMP"
            },
            sub('P', Blocks.IRON_BARS),
            sub('F', OrePrefix.frameGt, Materials.Steel),
            sub('M', MetaItems.ELECTRIC_MOTOR_MV),
            sub('V', OrePrefix.rotor, Materials.Steel));

        ModHandler.addShapedRecipe(
            "casing_assembler_casing",
            ASSEMBLER_CASING.getStack(3),
            new String[] {
                "PPP",
                "PFP",
                "PMP"
            },
            resolveComponents(
                IV,
                sub('P', CIRCUIT),
                sub('F', bind(OrePrefix.frameGt, TIER_MATERIAL)),
                sub('M', MOTOR)));

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
        // only register recipes for ULV - UV and MAX
        final MetaTileEntityHull[] hulls =
            Arrays.stream(MetaTileEntities.HULL)
                  .filter(x -> x.getTier() == MAX || x.getTier() < UHV)
                  .toArray(MetaTileEntityHull[]::new);

        if (ConfigHolder.harderMachineHulls)
            registerTieredShapedRecipes(
                "hull_",
                hulls,
                new String[] {
                    "PHP",
                    "CMC"
                },
                sub('M', TIER_CASING),
                sub('C', HULL_CABLE),
                sub('H', HULL_PLATE_1),
                sub('P', HULL_PLATE_2));
        else
            registerTieredShapedRecipes(
                "hull_",
                hulls,
                new String[] {
                    "CMC"
                },
                sub('M', TIER_CASING),
                sub('C', HULL_CABLE));

        // Transformers
        registerTieredShapedRecipes(
            "transformer_",
            MetaTileEntities.TRANSFORMER, true,
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
        registerTieredShapedRecipes(
            "energy_output_hatch_",
            MetaTileEntities.ENERGY_OUTPUT_HATCH,
            arr(" MC"),
            sub('M', HULL),
            sub('C', HATCH_CABLES));

        // Energy Input Hatches
        registerTieredShapedRecipes(
            "energy_input_hatch_",
            MetaTileEntities.ENERGY_INPUT_HATCH,
            arr("CM "),
            sub('M', HULL),
            sub('C', HATCH_CABLES));

        // Fluid Import Hatches
        registerTieredShapedRecipes(
            "fluid_import_hatch_",
            MetaTileEntities.FLUID_IMPORT_HATCH,
            new String[] {
                "G",
                "M"
            },
            sub('M', HULL),
            sub('G', Blocks.GLASS));

        // Fluid Export Hatches
        registerTieredShapedRecipes(
            "fluid_export_hatch_",
            MetaTileEntities.FLUID_EXPORT_HATCH,
            new String[] {
                "M",
                "G"
            },
            sub('M', HULL),
            sub('G', Blocks.GLASS));

        // Item Import Buses
        registerTieredShapedRecipes(
            "item_import_bus_",
            MetaTileEntities.ITEM_IMPORT_BUS,
            new String[] {
                "C",
                "M"
            },
            sub('M', HULL),
            sub('C', OreDictNames.chestWood));

        // Item Export Buses
        registerTieredShapedRecipes(
            "item_export_bus_",
            MetaTileEntities.ITEM_EXPORT_BUS,
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
            registerTieredShapedRecipes(
                MetaTileEntities.BATTERY_BUFFER,
                x -> fmt,
                x -> x[i].getTier(),
                x -> x[i].getStackForm(),
                new String[] {
                    "WTW",
                    "WMW"
                },
                sub('M', HULL),
                sub('W', bind(kinds[j], XF_CABLE_MATERIAL)),
                sub('T', OreDictNames.chestWood));
        }

        // Chargers
        registerTieredShapedRecipes(
            "charger_",
            MetaTileEntities.CHARGER,
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
        registerTieredShapedRecipes(
            "rotor_holder_",
            MetaTileEntities.ROTOR_HOLDER,
            new String[] {
                "WHW",
                "WRW",
                "WWW"
            },
            sub('H', HULL),
            sub('W', ROTOR_HOLDER_WIRE),
            sub('R', ROTOR_HOLDER_GEAR));

        // STEAM MACHINES
        ModHandler.addShapedRecipe(
            "bronze_hull",
            BRONZE_HULL.getStack(),
            new String[] {
                "PPP",
                "PhP",
                "PPP"
            },
            sub('P', OrePrefix.plate, Materials.Bronze));

        ModHandler.addShapedRecipe(
            "bronze_bricks_hull",
            BRONZE_BRICKS_HULL.getStack(),
            new String[] {
                "PPP",
                "PhP",
                "BBB"
            },
            sub('P', OrePrefix.plate, Materials.Bronze),
            sub('B', Blocks.BRICK_BLOCK));

        ModHandler.addShapedRecipe(
            "steel_hull",
            STEEL_HULL.getStack(),
            new String[] {
                "PPP",
                "PhP",
                "PPP"
            },
            sub('P', OrePrefix.plate, Materials.Steel));

        ModHandler.addShapedRecipe(
            "steel_bricks_hull",
            STEEL_BRICKS_HULL.getStack(),
            new String[] {
                "PPP",
                "PhP",
                "BBB"
            },
            sub('P', OrePrefix.plate, Materials.Steel),
            sub('B', Blocks.BRICK_BLOCK));


        ModHandler.addShapedRecipe(
            "steam_boiler_coal_bronze",
            MetaTileEntities.STEAM_BOILER_COAL_BRONZE.getStackForm(),
            new String[] {
                "PPP",
                "P P",
                "BFB"
            },
            sub('F', OreDictNames.craftingFurnace),
            sub('P', OrePrefix.plate, Materials.Bronze),
            sub('B', Blocks.BRICK_BLOCK));

        ModHandler.addShapedRecipe(
            "steam_boiler_coal_steel",
            MetaTileEntities.STEAM_BOILER_COAL_STEEL.getStackForm(),
            new String[] {
                "PPP",
                "P P",
                "BFB"
            },
            sub('F', OreDictNames.craftingFurnace),
            sub('P', OrePrefix.plate, Materials.Steel),
            sub('B', Blocks.BRICK_BLOCK));

        ModHandler.addShapedRecipe(
            "steam_boiler_lava_bronze",
            MetaTileEntities.STEAM_BOILER_LAVA_BRONZE.getStackForm(),
            new String[] {
                "PPP",
                "GGG",
                "PMP"
            },
            sub('M', BRONZE_BRICKS_HULL),
            sub('P', OrePrefix.plate, Materials.Bronze),
            sub('G', Blocks.GLASS));

        ModHandler.addShapedRecipe(
            "steam_boiler_lava_steel",
            MetaTileEntities.STEAM_BOILER_LAVA_STEEL.getStackForm(),
            new String[] {
                "PPP",
                "GGG",
                "PMP"
            },
            sub('M', STEEL_BRICKS_HULL),
            sub('P', OrePrefix.plate, Materials.Steel),
            sub('G', Blocks.GLASS));

        ModHandler.addShapedRecipe(
            "steam_boiler_solar_bronze",
            MetaTileEntities.STEAM_BOILER_SOLAR_BRONZE.getStackForm(),
            new String[] {
                "GGG",
                "SSS",
                "PMP"
            },
            sub('M', BRONZE_BRICKS_HULL),
            sub('P', OrePrefix.pipeSmall, Materials.Bronze),
            sub('S', OrePrefix.plate, Materials.Silver),
            sub('G', Blocks.GLASS));


        ModHandler.addShapedRecipe(
            "steam_furnace_bronze",
            MetaTileEntities.STEAM_FURNACE_BRONZE.getStackForm(),
            new String[] {
                "XXX",
                "XMX",
                "XFX"
            },
            sub('M', BRONZE_BRICKS_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Bronze),
            sub('F', OreDictNames.craftingFurnace));

        ModHandler.addShapedRecipe(
            "steam_furnace_steel",
            MetaTileEntities.STEAM_FURNACE_STEEL.getStackForm(),
            new String[] {
                "XXX",
                "XMX",
                "XFX"
            },
            sub('M', STEEL_BRICKS_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Steel),
            sub('F', OreDictNames.craftingFurnace));

        ModHandler.addShapedRecipe(
            "steam_macerator_bronze",
            MetaTileEntities.STEAM_MACERATOR_BRONZE.getStackForm(),
            new String[] {
                "DXD",
                "XMX",
                "PXP"
            },
            sub('M', BRONZE_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Bronze),
            sub('P', OreDictNames.craftingPiston),
            sub('D', OrePrefix.gem, Materials.Diamond));

        ModHandler.addShapedRecipe(
            "steam_macerator_steel",
            MetaTileEntities.STEAM_MACERATOR_STEEL.getStackForm(),
            new String[] {
                "DXD",
                "XMX",
                "PXP"
            },
            sub('M', STEEL_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Steel),
            sub('P', OreDictNames.craftingPiston),
            sub('D', OrePrefix.gem, Materials.Diamond));

        ModHandler.addShapedRecipe(
            "steam_extractor_bronze",
            MetaTileEntities.STEAM_EXTRACTOR_BRONZE.getStackForm(),
            new String[] {
                "XXX",
                "PMG",
                "XXX"
            },
            sub('M', BRONZE_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Bronze),
            sub('P', OreDictNames.craftingPiston),
            sub('G', Blocks.GLASS));

        ModHandler.addShapedRecipe(
            "steam_extractor_steel",
            MetaTileEntities.STEAM_EXTRACTOR_STEEL.getStackForm(),
            new String[] {
                "XXX",
                "PMG",
                "XXX"
            },
            sub('M', STEEL_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Steel),
            sub('P', OreDictNames.craftingPiston),
            sub('G', Blocks.GLASS));

        ModHandler.addShapedRecipe(
            "steam_hammer_bronze",
            MetaTileEntities.STEAM_HAMMER_BRONZE.getStackForm(),
            new String[] {
                "XPX",
                "XMX",
                "XAX"
            },
            sub('M', BRONZE_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Bronze),
            sub('P', OreDictNames.craftingPiston),
            sub('A', OreDictNames.craftingAnvil));

        ModHandler.addShapedRecipe(
            "steam_hammer_steel",
            MetaTileEntities.STEAM_HAMMER_STEEL.getStackForm(),
            new String[] {
                "XPX",
                "XMX",
                "XAX"
            },
            sub('M', STEEL_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Steel),
            sub('P', OreDictNames.craftingPiston),
            sub('A', OreDictNames.craftingAnvil));

        ModHandler.addShapedRecipe(
            "steam_compressor_bronze",
            MetaTileEntities.STEAM_COMPRESSOR_BRONZE.getStackForm(),
            new String[] {
                "XXX",
                "PMP",
                "XXX"
            },
            sub('M', BRONZE_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Bronze),
            sub('P', OreDictNames.craftingPiston));

        ModHandler.addShapedRecipe(
            "steam_compressor_steel",
            MetaTileEntities.STEAM_COMPRESSOR_STEEL.getStackForm(),
            new String[] {
                "XXX",
                "PMP",
                "XXX"
            },
            sub('M', STEEL_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Steel),
            sub('P', OreDictNames.craftingPiston));

        ModHandler.addShapedRecipe(
            "steam_alloy_smelter_bronze",
            MetaTileEntities.STEAM_ALLOY_SMELTER_BRONZE.getStackForm(),
            new String[] {
                "XXX",
                "FMF",
                "XXX"
            },
            sub('M', BRONZE_BRICKS_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Bronze),
            sub('F', OreDictNames.craftingFurnace));

        ModHandler.addShapedRecipe(
            "steam_alloy_smelter_steel",
            MetaTileEntities.STEAM_ALLOY_SMELTER_STEEL.getStackForm(),
            new String[] {
                "XXX",
                "FMF",
                "XXX"
            },
            sub('M', STEEL_BRICKS_HULL),
            sub('X', OrePrefix.pipeSmall, Materials.Steel),
            sub('F', OreDictNames.craftingFurnace));


        // MULTI BLOCK CONTROLLERS
        ModHandler.addShapedRecipe(
            "bronze_primitive_blast_furnace",
            MetaTileEntities.PRIMITIVE_BLAST_FURNACE.getStackForm(),
            new String[] {
                "PFP",
                "FwF",
                "PFP"
            },
            sub('P', PRIMITIVE_BRICKS),
            sub('F', OreDictNames.craftingFurnace));

        ModHandler.addShapedRecipe(
            "coke_oven",
            MetaTileEntities.COKE_OVEN.getStackForm(),
            new String[] {
                "PIP",
                "IwI",
                "PIP"
            },
            sub('P', COKE_BRICKS),
            sub('I', OrePrefix.plate, Materials.Iron));

        ModHandler.addShapelessRecipe("coke_oven_hatch", MetaTileEntities.COKE_OVEN_HATCH.getStackForm(), COKE_BRICKS.getStack(), MetaTileEntities.BRONZE_TANK.getStackForm());
        ModHandler.addShapedRecipe(
            "electric_blast_furnace",
            MetaTileEntities.ELECTRIC_BLAST_FURNACE.getStackForm(),
            new String[] {
                "FFF",
                "CMC",
                "WCW"
            },
            sub('M', INVAR_HEATPROOF),
            sub('F', OreDictNames.craftingFurnace),
            sub('C', LV, CIRCUIT),
            sub('W', OrePrefix.cableGtSingle, Materials.Tin));

        ModHandler.addShapedRecipe(
            "vacuum_freezer",
            MetaTileEntities.VACUUM_FREEZER.getStackForm(),
            new String[] {
                "PPP",
                "CMC",
                "WCW"
            },
            sub('M', ALUMINIUM_FROSTPROOF),
            sub('P', MetaItems.ELECTRIC_PUMP_HV),
            sub('C', EV, CIRCUIT),
            sub('W', OrePrefix.cableGtSingle, Materials.Gold));

        ModHandler.addShapedRecipe(
            "implosion_compressor",
            MetaTileEntities.IMPLOSION_COMPRESSOR.getStackForm(),
            new String[] {
                "OOO",
                "CMC",
                "WCW"
            },
            sub('M', STEEL_SOLID),
            sub('O', OrePrefix.stone, Materials.Obsidian),
            sub('C', HV, CIRCUIT),
            sub('W', OrePrefix.cableGtSingle, Materials.Aluminium));

        ModHandler.addShapedRecipe(
            "distillation_tower",
            MetaTileEntities.DISTILLATION_TOWER.getStackForm(),
            new String[] {
                "CBC",
                "FMF",
                "CBC"
            },
            resolveComponents(
                EV,
                sub('M', TIER_CASING),
                sub('B', OrePrefix.pipeLarge, Materials.StainlessSteel),
                sub('C', CIRCUIT),
                sub('F', PUMP)));

        ModHandler.addShapedRecipe(
            "cracking_unit",
            MetaTileEntities.CRACKER.getStackForm(),
            new String[] {
                "CEC",
                "PHP",
                "CEC"
            },
            resolveComponents(
                HV,
                sub('C', CUPRONICKEL),
                sub('E', PUMP),
                sub('P', EV, CIRCUIT),
                sub('H', HULL)));


        ModHandler.addShapedRecipe(
            "pyrolyse_oven",
            MetaTileEntities.PYROLYSE_OVEN.getStackForm(),
            new String[] {
                "WEP",
                "EME",
                "WCP"
            },
            resolveComponents(
                MV,
                sub('M', HULL),
                sub('W', PISTON),
                sub('P', COIL_HEATING_DOUBLE),
                sub('E', CIRCUIT),
                sub('C', PUMP)));

        ModHandler.addShapedRecipe(
            "diesel_engine",
            MetaTileEntities.DIESEL_ENGINE.getStackForm(),
            new String[] {
                "PCP",
                "EME",
                "GWG"
            },
            resolveComponents(
                EV,
                sub('M', HULL),
                sub('P', PISTON),
                sub('E', MOTOR),
                sub('C', CIRCUIT),
                sub('W', OrePrefix.wireGtSingle, Materials.TungstenSteel),
                sub('G', GEAR)));

        ModHandler.addShapedRecipe(
            "engine_intake_casing",
            ENGINE_INTAKE_CASING.getStack(),
            new String[] {
                "PhP",
                "RFR",
                "PwP"
            },
            sub('R', OrePrefix.pipeMedium, Materials.Titanium),
            sub('F', TITANIUM_STABLE),
            sub('P', OrePrefix.rotor, Materials.Titanium));

        ModHandler.addShapedRecipe(
            "multi_furnace",
            MetaTileEntities.MULTI_FURNACE.getStackForm(),
            new String[] {
                "PPP",
                "ASA",
                "CAC"
            },
            sub('P', Blocks.FURNACE),
            sub('A', HV, CIRCUIT),
            sub('S', INVAR_HEATPROOF),
            sub('C', OrePrefix.cableGtSingle, Materials.AnnealedCopper));


        ModHandler.addShapedRecipe(
            "large_steam_turbine",
            MetaTileEntities.LARGE_STEAM_TURBINE.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "CSC"
            },
            resolveComponents(
                HV,
                sub('S', OrePrefix.gear, Materials.Steel),
                sub('P', CIRCUIT),
                sub('A', HULL),
                sub('C', OrePrefix.pipeLarge, Materials.Steel)));

        ModHandler.addShapedRecipe(
            "large_gas_turbine",
            MetaTileEntities.LARGE_GAS_TURBINE.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "CSC"
            },
            resolveComponents(
                EV,
                sub('S', OrePrefix.gear, Materials.StainlessSteel),
                sub('P', CIRCUIT),
                sub('A', HULL),
                sub('C', OrePrefix.pipeLarge, Materials.StainlessSteel)));

        ModHandler.addShapedRecipe(
            "large_plasma_turbine",
            MetaTileEntities.LARGE_PLASMA_TURBINE.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "CSC"
            },
            sub('S', OrePrefix.gear, Materials.TungstenSteel),
            sub('P', IV, CIRCUIT),
            sub('A', UV, HULL),
            sub('C', OrePrefix.pipeLarge, Materials.TungstenSteel));


        ModHandler.addShapedRecipe(
            "large_bronze_boiler",
            MetaTileEntities.LARGE_BRONZE_BOILER.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "PSP"
            },
            sub('P', OrePrefix.cableGtSingle, Materials.Tin),
            sub('S', LV, CIRCUIT),
            sub('A', BRONZE_BRICKS));

        ModHandler.addShapedRecipe(
            "large_steel_boiler",
            MetaTileEntities.LARGE_STEEL_BOILER.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "PSP"
            },
            sub('P', OrePrefix.cableGtSingle, Materials.Copper),
            sub('S', HV, CIRCUIT),
            sub('A', STEEL_SOLID));

        ModHandler.addShapedRecipe(
            "large_titanium_boiler",
            MetaTileEntities.LARGE_TITANIUM_BOILER.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "PSP"
            },
            sub('P', OrePrefix.cableGtSingle, Materials.Gold),
            sub('S', EV, CIRCUIT),
            sub('A', TITANIUM_STABLE));

        ModHandler.addShapedRecipe(
            "large_tungstensteel_boiler",
            MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER.getStackForm(),
            new String[] {
                "PSP",
                "SAS",
                "PSP"
            },
            sub('P', OrePrefix.cableGtSingle, Materials.Aluminium),
            sub('S', IV, CIRCUIT),
            sub('A', TUNGSTENSTEEL_ROBUST));



        // GENERATORS
        registerTieredShapedRecipes(
            "diesel_generator_",
            MetaTileEntities.DIESEL_GENERATOR,
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

        registerTieredShapedRecipes(
            "gas_turbine_",
            MetaTileEntities.GAS_TURBINE,
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

        registerTieredShapedRecipes(
            "steam_turbine_",
            MetaTileEntities.STEAM_TURBINE,
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

        ModHandler.addShapedRecipe(
            "workbench_bronze",
            MetaTileEntities.WORKBENCH.getStackForm(),
            new String[] {
                "CWC",
                "PHP",
                "PhP"
            },
            sub('C', OreDictNames.chestWood),
            sub('W', Blocks.CRAFTING_TABLE),
            sub('P', OrePrefix.plate, Materials.Bronze),
            sub('H', BRONZE_HULL));

        ModHandler.addShapedRecipe(
            "magic_energy_absorber",
            MetaTileEntities.MAGIC_ENERGY_ABSORBER.getStackForm(),
            new String[] {
                "PCP",
                "PMP",
                "PCP"
            },
            resolveComponents(
                EV,
                sub('M', HULL),
                sub('P', SENSOR),
                sub('C', CIRCUIT)));

        // MACHINES
        registerMachineRecipe(
            MetaTileEntities.ALLOY_SMELTER,
            new String[] {
                "ECE",
                "CMC",
                "WCW"
            },
            sub('M', HULL),
            sub('E', CIRCUIT),
            sub('W', CABLE),
            sub('C', COIL_HEATING_DOUBLE));

        registerMachineRecipe(
            MetaTileEntities.ASSEMBLER,
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

        registerMachineRecipe(
            MetaTileEntities.BENDER,
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

        registerMachineRecipe(
            MetaTileEntities.CANNER,
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

        registerMachineRecipe(
            MetaTileEntities.COMPRESSOR,
            new String[] {
                " C ",
                "PMP",
                "WCW"
            },
            sub('M', HULL),
            sub('P', PISTON),
            sub('C', CIRCUIT),
            sub('W', CABLE));

        registerMachineRecipe(
            MetaTileEntities.CUTTER,
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

        registerMachineRecipe(
            MetaTileEntities.ELECTRIC_FURNACE,
            new String[] {
                "ECE",
                "CMC",
                "WCW"
            },
            sub('M', HULL),
            sub('E', CIRCUIT),
            sub('W', CABLE),
            sub('C', COIL_HEATING));

        registerMachineRecipe(
            MetaTileEntities.EXTRACTOR,
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

        registerMachineRecipe(
            MetaTileEntities.EXTRUDER,
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

        registerMachineRecipe(
            MetaTileEntities.LATHE,
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

        registerMachineRecipe(
            MetaTileEntities.MACERATOR,
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

        registerMachineRecipe(
            MetaTileEntities.MICROWAVE,
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
            sub('L', OrePrefix.plate, Materials.Lead));

        registerMachineRecipe(
            MetaTileEntities.WIREMILL,
            new String[] {
                "EWE",
                "CMC",
                "EWE"
            },
            sub('M', HULL),
            sub('E', MOTOR),
            sub('C', CIRCUIT),
            sub('W', CABLE));

        registerMachineRecipe(
            MetaTileEntities.CENTRIFUGE,
            new String[] {
                "CEC",
                "WMW",
                "CEC"
            },
            sub('M', HULL),
            sub('E', MOTOR),
            sub('C', CIRCUIT),
            sub('W', CABLE));

        registerMachineRecipe(
            MetaTileEntities.ELECTROLYZER,
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

        registerMachineRecipe(
            MetaTileEntities.THERMAL_CENTRIFUGE,
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

        registerMachineRecipe(
            MetaTileEntities.ORE_WASHER,
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

        registerMachineRecipe(
            MetaTileEntities.PACKER,
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

        registerMachineRecipe(
            MetaTileEntities.UNPACKER,
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

        registerMachineRecipe(
            MetaTileEntities.CHEMICAL_REACTOR,
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

        registerMachineRecipe(
            MetaTileEntities.FLUID_CANNER,
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

        registerMachineRecipe(
            MetaTileEntities.BREWERY,
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
            sub('G', GLASS));

        registerMachineRecipe(
            MetaTileEntities.FERMENTER,
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

        registerMachineRecipe(
            MetaTileEntities.FLUID_EXTRACTOR,
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

        registerMachineRecipe(
            MetaTileEntities.FLUID_SOLIDIFIER,
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

        registerMachineRecipe(
            MetaTileEntities.DISTILLERY,
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

        registerMachineRecipe(
            MetaTileEntities.CHEMICAL_BATH,
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

        registerMachineRecipe(
            MetaTileEntities.POLARIZER,
            new String[] {
                "ZSZ",
                "WMW",
                "ZSZ"
            },
            sub('M', HULL),
            sub('S', STICK_ELECTROMAGNETIC),
            sub('Z', COIL_ELECTRIC),
            sub('W', CABLE));

        registerMachineRecipe(
            MetaTileEntities.ELECTROMAGNETIC_SEPARATOR,
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

        registerMachineRecipe(
            MetaTileEntities.AUTOCLAVE,
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

        registerMachineRecipe(
            MetaTileEntities.MIXER,
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

        registerMachineRecipe(
            MetaTileEntities.LASER_ENGRAVER,
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

        registerMachineRecipe(
            MetaTileEntities.FORMING_PRESS,
            new String[] {
                "WPW",
                "CMC",
                "WPW"
            },
            sub('M', HULL),
            sub('P', PISTON),
            sub('C', CIRCUIT),
            sub('W', CABLE));

        registerMachineRecipe(
            MetaTileEntities.FORGE_HAMMER,
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

        registerMachineRecipe(
            MetaTileEntities.FLUID_HEATER,
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

        registerMachineRecipe(
            MetaTileEntities.SIFTER,
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

        registerMachineRecipe(
            MetaTileEntities.ARC_FURNACE,
            new String[] {
                "WGW",
                "CMC",
                "PPP"
            },
            sub('M', HULL),
            sub('P', PLATE),
            sub('C', CIRCUIT),
            sub('W', CABLE_QUAD),
            sub('G', OrePrefix.ingot, Materials.Graphite));

        registerMachineRecipe(
            MetaTileEntities.PLASMA_ARC_FURNACE,
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
            sub('G', OrePrefix.ingot, Materials.Graphite));

        registerMachineRecipe(
            MetaTileEntities.PUMP,
            new String[] {
                "WGW",
                "GMG",
                "TGT"
            },
            sub('M', HULL),
            sub('W', CIRCUIT),
            sub('G', PUMP),
            sub('T', PISTON));

        registerMachineRecipe(
            MetaTileEntities.FISHER,
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

        registerMachineRecipe(
            MetaTileEntities.AIR_COLLECTOR,
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

        registerMachineRecipe(
            MetaTileEntities.ITEM_COLLECTOR,
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

        registerMachineRecipe(
            MetaTileEntities.BLOCK_BREAKER,
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

        registerMachineRecipe(
            MetaTileEntities.QUANTUM_CHEST,
            new String[] {
                "CPC",
                "PHP",
                "CFC"
            },
            sub('C', CIRCUIT),
            sub('P', PLATE),
            sub('F', FIELD_GENERATOR),
            sub('H', HULL));

        registerMachineRecipe(
            MetaTileEntities.QUANTUM_TANK,
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
        for(var chest : MetaTileEntities.METAL_CHESTS) {
            ModHandler.addShapedRecipe(
                chest.metaTileEntityId.getPath(),
                chest.getStackForm(),
                new String[] {
                    "XXX",
                    "X X",
                    "XXX"
                },
                sub('X', OrePrefix.plate, chest.getMaterial()));
        }

        // Multiblock Tanks
        for (var tank : MetaTileEntities.TANKS) {
            var prefix = tank.getMaterial() == Materials.Wood ? OrePrefix.plank : OrePrefix.plate;
            ModHandler.addShapedRecipe(
                tank.metaTileEntityId.getPath(),
                tank.getStackForm(),
                new String[] {
                    "XYX",
                    "Y Y",
                    "XYX"
                },
                sub('X', prefix, tank.getMaterial()),
                sub('Y', OrePrefix.blockGlass));
        }

        ModHandler.addShapedRecipe(
            "tesla_coil",
            MetaTileEntities.TESLA_COIL.getStackForm(),
            new String[] {
                "XXX",
                "YHY",
                "XXX"
            },
            resolveComponents(
                MV,
                sub('X', OrePrefix.wireGtQuadruple, Materials.Copper),
                sub('Y', EMITTER),
                sub('H', HULL)));
    }

    public static <T extends MetaTileEntity & ITiered> void registerMachineRecipe(T[] metaTileEntities,
                                                                                  String[] definition,
                                                                                  Substitution<?>... subs)
    {
        registerTieredShapedRecipes(
            metaTileEntities,
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
            ModHandler.addShapedRecipe(
                prefix + VN[tier].toLowerCase(),
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
    public static Substitution<?>[] resolveComponents(int tier,
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
            String name = String.format(temp, VN[tier].toLowerCase());
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

}
