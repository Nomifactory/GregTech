package gregtech.loaders.recipe;

import gregtech.api.GTValues;
import gregtech.api.items.OreDictNames;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockMachineCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.*;

/**
 * Holder of {@link Component} used for programmatic recipe generation of voltage-tiered items.
 */
public class CraftingComponent {

    /** Tier-marker Materials for things like circuits */
    public static final Component<Material> TIER = tier -> switch(tier) {
        case ULV -> Tier.Primitive;
        case LV -> Tier.Basic;
        case MV -> Tier.Good;
        case HV -> Tier.Advanced;
        case EV -> Tier.Extreme;
        case IV -> Tier.Elite;
        case LuV -> Tier.Master;
        case ZPM -> Tier.Ultimate;
        case UV -> Tier.Superconductor;
        default -> Tier.Infinite;
    };

    /** The primary metal associated with each tier of progression */
    public static final Component<Material> TIER_MATERIAL = tier -> switch(tier) {
        case ULV -> Materials.WroughtIron;
        case LV -> Materials.Steel;
        case MV -> Materials.Aluminium;
        case HV -> Materials.StainlessSteel;
        case EV -> Materials.Titanium;
        case IV -> Materials.TungstenSteel;
        case LuV -> Materials.Chrome;
        case ZPM -> Materials.Iridium;
        case UV -> Materials.Osmium;
        default -> Materials.Darmstadtium;
    };

    /** Standard circuits for the current tier */
    public static final Component<UnificationEntry> CIRCUIT = bind(OrePrefix.circuit, TIER);

    /** Tiered pump components */
    public static final Component<MetaItem<?>.MetaValueItem> PUMP = tier -> switch(tier) {
        case ULV, LV -> MetaItems.ELECTRIC_PUMP_LV;
        case MV -> MetaItems.ELECTRIC_PUMP_MV;
        case HV -> MetaItems.ELECTRIC_PUMP_HV;
        case EV -> MetaItems.ELECTRIC_PUMP_EV;
        case IV -> MetaItems.ELECTRIC_PUMP_IV;
        case LuV -> MetaItems.ELECTRIC_PUMP_LUV;
        case ZPM -> MetaItems.ELECTRIC_PUMP_ZPM;
        default -> MetaItems.ELECTRIC_PUMP_UV;
    };

    /** Tiered materials for generic cables used in most crafting recipes. */
    public static final Component<Material> CABLE_MATERIALS = tier -> switch(tier) {
        case ULV -> Materials.Lead;
        case LV -> Materials.Tin;
        case MV -> Materials.Copper;
        case HV -> Materials.Gold;
        case EV -> Materials.Aluminium;
        case IV -> Materials.Platinum;
        case LuV -> Materials.NiobiumTitanium;
        case ZPM -> Materials.Naquadah;
        case UV -> Materials.NaquadahAlloy;
        default -> MarkerMaterials.Tier.Superconductor;
    };
    
    /** OrePrefixes for tiered cables used in most crafting recipes */
    public static final Component<OrePrefix> CABLE_TYPE = tier -> switch(tier) {
        case MAX -> OrePrefix.wireGtSingle;
        case UV -> OrePrefix.wireGtQuadruple;
        default -> OrePrefix.cableGtSingle;
    }; 

    /** Cables used in most crafting recipes */
    public static final Component<UnificationEntry> CABLE = tier -> {
        var material = CABLE_MATERIALS.getIngredient(tier);
        OrePrefix kind = CABLE_TYPE.getIngredient(tier);
        return new UnificationEntry(kind, material);
    };

    /** Wires used in Electrolyzer crafting recipes */
    public static final Component<UnificationEntry> WIRE = tier -> {
        var material = switch(tier) {
            case ULV, LV -> Materials.Gold;
            case MV -> Materials.Silver;
            case HV -> Materials.Electrum;
            case EV -> Materials.Platinum;
            default -> Materials.Osmium;
        };

        return new UnificationEntry(OrePrefix.wireGtSingle, material);
    };

    /** Quadruple cables, used in Arc furnace recipes. */
    public static final Component<UnificationEntry> CABLE_QUAD = tier -> {
        var material = CABLE_MATERIALS.getIngredient(tier);
        OrePrefix kind = switch(tier) {
            case MAX -> OrePrefix.wireGtQuadruple;
            case UV -> OrePrefix.cableGtSingle;
            default -> OrePrefix.cableGtQuadruple;
        };
        return new UnificationEntry(kind, material);
    };

    /** Tier-appropriate machine hull */
    public static final Component<ItemStack> HULL = tier ->
        MetaTileEntities.HULL[tier].getStackForm();

    /** Machine hull for one tier below the current tier */
    public static final Component<ItemStack> WORSE_HULL = tier ->
        MetaTileEntities.HULL[tier - 1].getStackForm();

    /** Pipes used as crafting ingredients in Extruders */
    public static final Component<UnificationEntry> PIPE = tier -> {
        var material = switch(tier) {
            case ULV, LV -> Materials.Bronze;
            case MV -> Materials.Steel;
            case HV -> Materials.StainlessSteel;
            case EV -> Materials.Titanium;
            default -> Materials.TungstenSteel;
        };
        return new UnificationEntry(OrePrefix.pipeMedium, material);
    };

    /** Tier-appropriate glass */
    public static final Component<ItemStack> GLASS = tier ->
        new ItemStack(Blocks.GLASS, 1, W);

    /** Materials of tiered plates used in select machine recipes */
    public static final Component<Material> PLATE_MATERIAL = tier -> switch(tier) {
        case ULV, LV -> Materials.Steel;
        case MV -> Materials.Aluminium;
        case HV -> Materials.StainlessSteel;
        case EV -> Materials.Titanium;
        case IV -> Materials.TungstenSteel;
        case LuV -> Materials.HSSG;
        case ZPM -> Materials.HSSE;
        default -> Materials.Darmstadtium;
    };
    
    /** Tiered plates used in select machine recipes */
    public static final Component<UnificationEntry> PLATE = tier -> {
        var material = PLATE_MATERIAL.getIngredient(tier);
        return new UnificationEntry(OrePrefix.plate, material);
    };

    /** Tiered Motors */
    public static final Component<MetaItem<?>.MetaValueItem> MOTOR = tier -> switch(tier) {
        case ULV, LV -> MetaItems.ELECTRIC_MOTOR_LV;
        case MV -> MetaItems.ELECTRIC_MOTOR_MV;
        case HV -> MetaItems.ELECTRIC_MOTOR_HV;
        case EV -> MetaItems.ELECTRIC_MOTOR_EV;
        case IV -> MetaItems.ELECTRIC_MOTOR_IV;
        case LuV -> MetaItems.ELECTRIC_MOTOR_LUV;
        case ZPM -> MetaItems.ELECTRIC_MOTOR_ZPM;
        default -> MetaItems.ELECTRIC_MOTOR_UV;
    };

    /** Tiered Rotors */
    public static final Component<UnificationEntry> ROTOR = tier -> {
        var material = switch(tier) {
            case ULV, LV -> Materials.Tin;
            case MV -> Materials.Bronze;
            case HV -> Materials.Steel;
            case EV -> Materials.StainlessSteel;
            case IV -> Materials.TungstenSteel;
            case LuV -> Materials.Chrome;
            case ZPM -> Materials.Iridium;
            default -> Materials.Osmium;
        };
        return new UnificationEntry(OrePrefix.rotor, material);
    };

    /** Tiered Sensors. ULV uses LV. */
    public static final Component<MetaItem<?>.MetaValueItem> SENSOR = tier -> switch(tier) {
        case ULV, LV -> MetaItems.SENSOR_LV;
        case MV -> MetaItems.SENSOR_MV;
        case HV -> MetaItems.SENSOR_HV;
        case EV -> MetaItems.SENSOR_EV;
        case IV -> MetaItems.SENSOR_IV;
        case LuV -> MetaItems.SENSOR_LUV;
        case ZPM -> MetaItems.SENSOR_ZPM;
        default -> MetaItems.SENSOR_UV;
    };

    /** Tiered grinding heads for macerators */
    public static final Component<?> GRINDER = tier -> switch(tier) {
        case ULV, LV, MV -> new UnificationEntry(OrePrefix.gem, Materials.Diamond);
        default -> OreDictNames.craftingGrinder;
    };

    /** Diamond, used in Lathe recipes */
    public static final Component<UnificationEntry> DIAMOND = tier ->
        new UnificationEntry(OrePrefix.gem, Materials.Diamond);

    /** Tiered Pistons. ULV uses LV. */
    public static final Component<MetaItem<?>.MetaValueItem> PISTON = tier -> switch(tier) {
        case ULV, LV -> MetaItems.ELECTRIC_PISTON_LV;
        case MV -> MetaItems.ELECTRIC_PISTON_MV;
        case HV -> MetaItems.ELECTRIC_PISTON_HV;
        case EV -> MetaItems.ELECTRIC_PISTON_EV;
        case IV -> MetaItems.ELECTRIC_PISTON_IV;
        case LuV -> MetaItems.ELECTRIC_PISTON_LUV;
        case ZPM -> MetaItems.ELECTRIC_PISTON_ZPM;
        default -> MetaItems.ELECTRIC_PISTON_UV;
    };

    /** Tiered Emitters. ULV uses LV. */
    public static final Component<MetaItem<?>.MetaValueItem> EMITTER = tier -> switch(tier) {
        case ULV, LV -> MetaItems.EMITTER_LV;
        case MV -> MetaItems.EMITTER_MV;
        case HV -> MetaItems.EMITTER_HV;
        case EV -> MetaItems.EMITTER_EV;
        case IV -> MetaItems.EMITTER_IV;
        case LuV -> MetaItems.EMITTER_LUV;
        case ZPM -> MetaItems.EMITTER_ZPM;
        default -> MetaItems.EMITTER_UV;
    };

    /** Tiered Conveyors. ULV uses LV. */
    public static final Component<MetaItem<?>.MetaValueItem> CONVEYOR = tier -> switch(tier) {
        case ULV, LV -> MetaItems.CONVEYOR_MODULE_LV;
        case MV -> MetaItems.CONVEYOR_MODULE_MV;
        case HV -> MetaItems.CONVEYOR_MODULE_HV;
        case EV -> MetaItems.CONVEYOR_MODULE_EV;
        case IV -> MetaItems.CONVEYOR_MODULE_IV;
        case LuV -> MetaItems.CONVEYOR_MODULE_LUV;
        case ZPM -> MetaItems.CONVEYOR_MODULE_ZPM;
        default -> MetaItems.CONVEYOR_MODULE_UV;
    };

    /** Tiered Robot Arms. ULV uses LV. */
    public static final Component<MetaItem<?>.MetaValueItem> ROBOT_ARM = tier -> switch(tier) {
        case ULV, LV -> MetaItems.ROBOT_ARM_LV;
        case MV -> MetaItems.ROBOT_ARM_MV;
        case HV -> MetaItems.ROBOT_ARM_HV;
        case EV -> MetaItems.ROBOT_ARM_EV;
        case IV -> MetaItems.ROBOT_ARM_IV;
        case LuV -> MetaItems.ROBOT_ARM_LUV;
        case ZPM -> MetaItems.ROBOT_ARM_ZPM;
        default -> MetaItems.ROBOT_ARM_UV;
    };

    /** Tiered materials for Electric Furnace heating coils */
    public static final Component<Material> COIL_MATERIAL = tier -> switch(tier) {
        case ULV, LV -> Materials.Copper;
        case MV -> Materials.Cupronickel;
        case HV -> Materials.Kanthal;
        case EV -> Materials.Nichrome;
        case IV -> Materials.TungstenSteel;
        case LuV -> Materials.HSSG;
        case ZPM -> Materials.Naquadah;
        default -> Materials.NaquadahAlloy;
    };
    
    /** Electric Furnace heating coils */
    public static final Component<UnificationEntry> COIL_HEATING = tier -> {
        OrePrefix kind = OrePrefix.wireGtDouble;
        if(tier > UV)
            kind = OrePrefix.wireGtOctal;
        var material = COIL_MATERIAL.getIngredient(tier);
        return new UnificationEntry(kind, material);
    };

    /** Electric coils used for Polarizers and Electromagnetic Separators */
    public static final Component<UnificationEntry> COIL_ELECTRIC = tier -> switch(tier) {
        case ULV -> new UnificationEntry(OrePrefix.wireGtSingle, Materials.Tin);
        case LV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Tin);
        case MV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Copper);
        case HV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Copper);
        case EV, IV -> new UnificationEntry(OrePrefix.wireGtOctal, Materials.AnnealedCopper);
        case LuV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.YttriumBariumCuprate);
        case ZPM -> new UnificationEntry(OrePrefix.wireGtOctal, Tier.Superconductor);
        default -> new UnificationEntry(OrePrefix.wireGtHex, Tier.Superconductor);
    };

    /** Magnetic rods. Currently unused. */
    public static final Component<UnificationEntry> STICK_MAGNETIC = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.stick, Materials.IronMagnetic);
        case MV, HV -> new UnificationEntry(OrePrefix.stick, Materials.SteelMagnetic);
        case EV, IV -> new UnificationEntry(OrePrefix.stick, Materials.NeodymiumMagnetic);
        case LuV, ZPM -> new UnificationEntry(OrePrefix.stickLong, Materials.NeodymiumMagnetic);
        default -> new UnificationEntry(OrePrefix.block, Materials.NeodymiumMagnetic);
    };

    /** Blaze rod. Used in Brewery and Distillery recipes. */
    public static final Component<UnificationEntry> STICK_DISTILLATION =
        tier -> new UnificationEntry(OrePrefix.stick, Materials.Blaze);

    /** Tiered Field Generators. ULV uses LV. */
    public static final Component<MetaItem<?>.MetaValueItem> FIELD_GENERATOR = tier -> switch(tier) {
        case ULV, LV -> MetaItems.FIELD_GENERATOR_LV;
        case MV -> MetaItems.FIELD_GENERATOR_MV;
        case HV -> MetaItems.FIELD_GENERATOR_HV;
        case EV -> MetaItems.FIELD_GENERATOR_EV;
        case IV -> MetaItems.FIELD_GENERATOR_IV;
        case LuV -> MetaItems.FIELD_GENERATOR_LUV;
        case ZPM -> MetaItems.FIELD_GENERATOR_ZPM;
        default -> MetaItems.FIELD_GENERATOR_UV;
    };

    /** Double-sized heating coils, used in various machines. */
    public static final Component<UnificationEntry> COIL_HEATING_DOUBLE = tier -> {
        var material = COIL_MATERIAL.getIngredient(tier);
        OrePrefix kind = OrePrefix.wireGtQuadruple;
        if(tier > UV)
            kind = OrePrefix.wireGtHex;
        return new UnificationEntry(kind, material);
    };

    /** Metal rods of magnetizable materials, used for Polarizers and Electromagnetic Separators */
    public static final Component<UnificationEntry> STICK_ELECTROMAGNETIC = tier -> {
        var material = switch(tier) {
            case ULV, LV -> Materials.Iron;
            case MV, HV -> Materials.Steel;
            case EV -> Materials.Neodymium;
            default -> Materials.VanadiumGallium;
        };
        return new UnificationEntry(OrePrefix.stick, material);
    };

    /** Tiered primary plates for machine hulls. Differs somewhat from normal crafting plates. */
    public static final Component<UnificationEntry> HULL_PLATE_1 = tier -> {
        var material = switch(tier) {
            case ULV -> Materials.WroughtIron;
            case LuV -> Materials.Chrome;
            case ZPM -> Materials.Iridium;
            case UV -> Materials.Osmium;
            case MAX -> Materials.Darmstadtium;
            default -> PLATE_MATERIAL.getIngredient(tier);
        };
        return new UnificationEntry(OrePrefix.plate, material);
    };

    /** Tiered plastic */
    public static final Component<FluidMaterial> PLASTIC = tier -> {
        if(tier < ZPM)
            return Materials.Plastic;
        return Materials.Polytetrafluoroethylene;
    };

    /** Tiered secondary plates for machine hulls (crafting table recipes) */
    public static final Component<UnificationEntry> HULL_PLATE_2 = tier -> {
        var material = switch(tier) {
            case ULV -> Materials.Wood;
            case LV, MV -> Materials.WroughtIron;
            default -> PLASTIC.getIngredient(tier);
        };
        return new UnificationEntry(OrePrefix.plate, material);
    };

    /**
     * Materials used for the cables in machine hull recipes.
     */
    public static final Component<Material> HULL_CABLE_MATERIAL = tier -> switch(tier) {
        case ULV -> Materials.RedAlloy;
        case IV -> Materials.Tungsten;
        case LuV -> Materials.VanadiumGallium;
        default -> CABLE_MATERIALS.getIngredient(tier);
    };

    /** Cables used for making Hulls. Differs somewhat from usual cable materials. */
    public static final Component<UnificationEntry> HULL_CABLE = tier -> {
        var material = HULL_CABLE_MATERIAL.getIngredient(tier);
        OrePrefix kind = CABLE_TYPE.getIngredient(tier);
        return new UnificationEntry(kind, material);
    };

    /**
     * Voltage-tiered machine casings
     */
    public static final Component<ItemStack> TIER_CASING = tier ->
        BlockMachineCasing.MachineCasingType.getTiered()[tier].getStack();

    /** Transformer circuit tier. Can return {@code null}! */
    public static final Component<Material> XF_ITEM_TIER = tier -> switch(tier) {
        case EV, IV -> Tier.Advanced;
        case LuV, ZPM -> Tier.Extreme;
        case UV -> Tier.Elite;
        default -> null;
    };

    /** Transformer items. Can return {@code null}! */
    public static final Component<UnificationEntry> XF_ITEM = tier -> {
        var material = XF_ITEM_TIER.getIngredient(tier);
        return (material == null) ? null : new UnificationEntry(OrePrefix.circuit, material);
    };

    /**
     * Different materials used for cables than usual in Transformer and Energy Hatch recipes
     */
    public static final Component<Material> XF_CABLE_MATERIAL = tier -> switch(tier) {
        case IV -> Materials.Tungsten;
        case LuV -> Materials.VanadiumGallium;
        default -> CABLE_MATERIALS.getIngredient(tier);
    };

    /** Cables at tier; used in transformer recipes */
    public static final Component<UnificationEntry> XF_CABLE = tier -> {
        var material = XF_CABLE_MATERIAL.getIngredient(tier);
        var kind = CABLE_TYPE.getIngredient(tier);
        return new UnificationEntry(kind, material);
    };

    /** Cables one tier down; used in transformer recipes */
    public static final Component<UnificationEntry> XF_CABLE_WORSE = tier ->
        XF_CABLE.getIngredient(tier - 1);

    /**
     * Tiered Cables used in Energy Input and Output hatches.
     * Same alternative materials as transformers, but sized the same as
     * the standard cable set.
     */
    public static final Component<UnificationEntry> HATCH_CABLES = tier -> {
        var kind = CABLE_TYPE.getIngredient(tier);
        var material = XF_CABLE_MATERIAL.getIngredient(tier);
        return new UnificationEntry(kind, material);
    };

    /**
     * Charger batteries
     */
    public static final Component<MetaItem<?>.MetaValueItem> BATTERY = tier -> switch(tier) {
        case ULV -> MetaItems.BATTERY_RE_ULV_TANTALUM;
        case LV -> MetaItems.BATTERY_RE_LV_LITHIUM;
        case MV -> MetaItems.BATTERY_RE_MV_LITHIUM;
        case HV -> MetaItems.BATTERY_RE_HV_LITHIUM;
        case EV -> MetaItems.LAPOTRON_CRYSTAL;
        case IV -> MetaItems.ENERGY_LAPOTRONIC_ORB;
        case LuV, ZPM -> MetaItems.ENERGY_LAPOTRONIC_ORB2;
        case UV, MAX -> MetaItems.ZPM2;
        default -> null;
    };

    /** Gears made of the core tier metal */
    public static final Component<UnificationEntry> GEAR = bind(OrePrefix.gear, TIER_MATERIAL);

    /**
     * Materials for wires used in Rotor Holders
     */
    public static final Component<UnificationEntry> ROTOR_HOLDER_WIRE = tier -> {
        var material = CABLE_MATERIALS.getIngredient(tier);
        if (tier == LuV)
            material = Materials.YttriumBariumCuprate;
        return new UnificationEntry(OrePrefix.wireGtHex, material);
    };

    /**
     * Gears used in Rotor Holder recipes
     */
    public static final Component<UnificationEntry> ROTOR_HOLDER_GEAR = tier -> {
        Material material = Materials.HSSS;
        if(tier != MAX)
            material = TIER_MATERIAL.getIngredient(tier);
        return new UnificationEntry(OrePrefix.gear, material);
    };

    /**
     * Small pipes used in Steam Turbine recipes
     */
    public static final Component<UnificationEntry> STEAM_TURBINE_PIPE = tier -> {
        var material = switch(tier) {
            case GTValues.LV -> Materials.Bronze;
            case GTValues.MV -> Materials.Steel;
            default -> Materials.StainlessSteel;
        };
        return new UnificationEntry(OrePrefix.pipeSmall, material);
    };

    /**
     * Primitive Brick and Coke Oven Brick materials corresponding to MetalCasingType enum ordinals.
     * May return {@code null}!
     */
    public static final Component<MetaItem<?>.MetaValueItem> PRIMITIVE_MATERIAL = tier -> switch(tier) {
        // PRIMITIVE_BRICKS.ordinal()
        case 1 -> MetaItems.FIRECLAY_BRICK;
        // COKE_BRICKS.ordinal()
        case 8 -> MetaItems.COKE_OVEN_BRICK;
        // Invalid ordinal
        default -> null;
    };

    /**
     * Given an OrePrefix and a Material-type Component, creates a Component returning a
     * UnificationEntry made of that fixed OrePrefix and the tier-appropriate material.
     */
    public static Component<UnificationEntry> bind(OrePrefix prefix, Component<Material> material) {
        return tier -> new UnificationEntry(prefix, material.getIngredient(tier));
    }

    /**
     * Given an OrePrefix-type Component and a Material-type Component, creates a Component returning a
     * UnificationEntry made from the tier-appropriate OrePrefix and Material.
     */
    public static Component<UnificationEntry> bind(Component<OrePrefix> prefix, Component<Material> material) {
        return tier -> new UnificationEntry(prefix.getIngredient(tier), material.getIngredient(tier));
    }

}
