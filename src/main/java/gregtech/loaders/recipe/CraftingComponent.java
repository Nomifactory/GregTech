package gregtech.loaders.recipe;

import gregtech.api.items.OreDictNames;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.*;

public class CraftingComponent {

    public static final Component<UnificationEntry> CIRCUIT = tier -> switch(tier) {
        case ULV -> new UnificationEntry(OrePrefix.circuit, Tier.Primitive);
        case LV -> new UnificationEntry(OrePrefix.circuit, Tier.Basic);
        case MV -> new UnificationEntry(OrePrefix.circuit, Tier.Good);
        case HV -> new UnificationEntry(OrePrefix.circuit, Tier.Advanced);
        case EV -> new UnificationEntry(OrePrefix.circuit, Tier.Extreme);
        case IV -> new UnificationEntry(OrePrefix.circuit, Tier.Elite);
        case LuV -> new UnificationEntry(OrePrefix.circuit, Tier.Master);
        case ZPM -> new UnificationEntry(OrePrefix.circuit, Tier.Ultimate);
        default -> new UnificationEntry(OrePrefix.circuit, Tier.Infinite);
    };

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

    public static final Component<UnificationEntry> CABLE = tier -> switch(tier) {
        case ULV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Lead);
        case LV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tin);
        case MV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Copper);
        case HV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold);
        case EV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium);
        case IV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Platinum);
        case LuV -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.NiobiumTitanium);
        case ZPM -> new UnificationEntry(OrePrefix.cableGtSingle, Materials.Naquadah);
        case UV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.NaquadahAlloy);
        default -> new UnificationEntry(OrePrefix.wireGtSingle, Tier.Superconductor);
    };

    public static final Component<UnificationEntry> WIRE = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.wireGtSingle, Materials.Gold);
        case MV -> new UnificationEntry(OrePrefix.wireGtSingle, Materials.Silver);
        case HV -> new UnificationEntry(OrePrefix.wireGtSingle, Materials.Electrum);
        case EV -> new UnificationEntry(OrePrefix.wireGtSingle, Materials.Platinum);
        default -> new UnificationEntry(OrePrefix.wireGtSingle, Materials.Osmium);
    };

    public static final Component<UnificationEntry> CABLE_QUAD = tier -> switch(tier) {
        case ULV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Lead);
        case LV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Tin);
        case MV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Copper);
        case HV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Gold);
        case EV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Aluminium);
        case IV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Platinum);
        case LuV -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium);
        case ZPM -> new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Naquadah);
        case UV -> new UnificationEntry(OrePrefix.cableGtSingle, Tier.Superconductor);
        default -> new UnificationEntry(OrePrefix.wireGtQuadruple, Tier.Superconductor);
    };

    public static final Component<ItemStack> HULL = tier -> MetaTileEntities.HULL[tier].getStackForm();

    public static final Component<ItemStack> WORSE_HULL = tier -> MetaTileEntities.HULL[tier - 1].getStackForm();

    public static final Component<UnificationEntry> PIPE = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.pipeMedium, Materials.Bronze);
        case MV -> new UnificationEntry(OrePrefix.pipeMedium, Materials.Steel);
        case HV -> new UnificationEntry(OrePrefix.pipeMedium, Materials.StainlessSteel);
        case EV -> new UnificationEntry(OrePrefix.pipeMedium, Materials.Titanium);
        default -> new UnificationEntry(OrePrefix.pipeMedium, Materials.TungstenSteel);
    };

    public static final Component<ItemStack> GLASS = tier -> new ItemStack(Blocks.GLASS, 1, W);

    public static final Component<UnificationEntry> PLATE = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.plate, Materials.Steel);
        case MV -> new UnificationEntry(OrePrefix.plate, Materials.Aluminium);
        case HV -> new UnificationEntry(OrePrefix.plate, Materials.StainlessSteel);
        case EV -> new UnificationEntry(OrePrefix.plate, Materials.Titanium);
        case IV -> new UnificationEntry(OrePrefix.plate, Materials.TungstenSteel);
        case LuV -> new UnificationEntry(OrePrefix.plate, Materials.HSSG);
        case ZPM -> new UnificationEntry(OrePrefix.plate, Materials.HSSE);
        case UV -> new UnificationEntry(OrePrefix.plate, Materials.Darmstadtium);
        default -> new UnificationEntry(OrePrefix.plate, Materials.TungstenSteel);
    };

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

    public static final Component<UnificationEntry> ROTOR = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.rotor, Materials.Tin);
        case MV -> new UnificationEntry(OrePrefix.rotor, Materials.Bronze);
        case HV -> new UnificationEntry(OrePrefix.rotor, Materials.Steel);
        case EV -> new UnificationEntry(OrePrefix.rotor, Materials.StainlessSteel);
        case IV -> new UnificationEntry(OrePrefix.rotor, Materials.TungstenSteel);
        case LuV -> new UnificationEntry(OrePrefix.rotor, Materials.Chrome);
        case ZPM -> new UnificationEntry(OrePrefix.rotor, Materials.Iridium);
        default -> new UnificationEntry(OrePrefix.rotor, Materials.Osmium);
    };

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

    public static final Component<?> GRINDER = tier -> switch(tier) {
        case ULV, LV, MV -> new UnificationEntry(OrePrefix.gem, Materials.Diamond);
        default -> OreDictNames.craftingGrinder;
    };

    public static final Component<?> DIAMOND = tier -> new UnificationEntry(OrePrefix.gem, Materials.Diamond);

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

    public static final Component<UnificationEntry> COIL_HEATING = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Copper);
        case MV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Cupronickel);
        case HV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Kanthal);
        case EV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Nichrome);
        case IV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.TungstenSteel);
        case LuV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.HSSG);
        case ZPM -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.Naquadah);
        case UV -> new UnificationEntry(OrePrefix.wireGtDouble, Materials.NaquadahAlloy);
        default -> new UnificationEntry(OrePrefix.wireGtOctal, Materials.Nichrome);
    };

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

    public static final Component<UnificationEntry> STICK_MAGNETIC = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.stick, Materials.IronMagnetic);
        case MV, HV -> new UnificationEntry(OrePrefix.stick, Materials.SteelMagnetic);
        case EV, IV -> new UnificationEntry(OrePrefix.stick, Materials.NeodymiumMagnetic);
        case LuV, ZPM -> new UnificationEntry(OrePrefix.stickLong, Materials.NeodymiumMagnetic);
        default -> new UnificationEntry(OrePrefix.block, Materials.NeodymiumMagnetic);
    };

    public static final Component<UnificationEntry> STICK_DISTILLATION =
        tier -> new UnificationEntry(OrePrefix.stick, Materials.Blaze);

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

    public static final Component<UnificationEntry> COIL_HEATING_DOUBLE = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Copper);
        case MV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Cupronickel);
        case HV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Kanthal);
        case EV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Nichrome);
        case IV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.TungstenSteel);
        case LuV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.HSSG);
        case ZPM -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Naquadah);
        case UV -> new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.NaquadahAlloy);
        default -> new UnificationEntry(OrePrefix.wireGtHex, Materials.Nichrome);
    };

    public static final Component<UnificationEntry> STICK_ELECTROMAGNETIC = tier -> switch(tier) {
        case ULV, LV -> new UnificationEntry(OrePrefix.stick, Materials.Iron);
        case MV, HV -> new UnificationEntry(OrePrefix.stick, Materials.Steel);
        case EV -> new UnificationEntry(OrePrefix.stick, Materials.Neodymium);
        default -> new UnificationEntry(OrePrefix.stick, Materials.VanadiumGallium);
    };
}
