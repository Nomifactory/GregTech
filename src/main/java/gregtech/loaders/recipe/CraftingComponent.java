package gregtech.loaders.recipe;

import gregtech.api.items.OreDictNames;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.MarkerMaterials.Tier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import static gregtech.api.GTValues.*;

public enum CraftingComponent {

    CIRCUIT {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Primitive);
                case LV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Basic);
                case MV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Good);
                case HV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Advanced);
                case EV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Extreme);
                case IV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Elite);
                case LuV:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Master);
                case ZPM:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Ultimate);
                default:
                    return new UnificationEntry(OrePrefix.circuit, Tier.Infinite);
            }
        }
    },
    PUMP {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.ELECTRIC_PUMP_LV;
                case MV:
                    return MetaItems.ELECTRIC_PUMP_MV;
                case HV:
                    return MetaItems.ELECTRIC_PUMP_HV;
                case EV:
                    return MetaItems.ELECTRIC_PUMP_EV;
                case IV:
                    return MetaItems.ELECTRIC_PUMP_IV;
                case LuV:
                    return MetaItems.ELECTRIC_PUMP_LUV;
                case ZPM:
                    return MetaItems.ELECTRIC_PUMP_ZPM;
                default:
                    return MetaItems.ELECTRIC_PUMP_UV;
            }
        }
    },
    CABLE {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Lead);
                case LV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Tin);
                case MV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Copper);
                case HV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Gold);
                case EV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Aluminium);
                case IV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Platinum);
                case LuV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.NiobiumTitanium);
                case ZPM:
                    return new UnificationEntry(OrePrefix.cableGtSingle, Materials.Naquadah);
                case UV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.NaquadahAlloy);
                default:
                    return new UnificationEntry(OrePrefix.wireGtSingle, MarkerMaterials.Tier.Superconductor);
            }
        }
    },
    WIRE {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.wireGtSingle, Materials.Gold);
                case MV:
                    return new UnificationEntry(OrePrefix.wireGtSingle, Materials.Silver);
                case HV:
                    return new UnificationEntry(OrePrefix.wireGtSingle, Materials.Electrum);
                case EV:
                    return new UnificationEntry(OrePrefix.wireGtSingle, Materials.Platinum);
                default:
                    return new UnificationEntry(OrePrefix.wireGtSingle, Materials.Osmium);
            }
        }
    },
    CABLE_QUAD {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Lead);
                case LV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Tin);
                case MV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Copper);
                case HV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Gold);
                case EV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Aluminium);
                case IV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Platinum);
                case LuV:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.NiobiumTitanium);
                case ZPM:
                    return new UnificationEntry(OrePrefix.cableGtQuadruple, Materials.Naquadah);
                case UV:
                    return new UnificationEntry(OrePrefix.cableGtSingle, MarkerMaterials.Tier.Superconductor);
                default:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, MarkerMaterials.Tier.Superconductor);
            }
        }
    },
    HULL {
        @Override
        public Object getIngredient(int tier) {
            return MetaTileEntities.HULL[tier].getStackForm();
        }
    },
    WORSE_HULL {
        @Override
        public Object getIngredient(int tier) {
            return MetaTileEntities.HULL[tier - 1].getStackForm();
        }
    },
    PIPE {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.pipeMedium, Materials.Bronze);
                case MV:
                    return new UnificationEntry(OrePrefix.pipeMedium, Materials.Steel);
                case HV:
                    return new UnificationEntry(OrePrefix.pipeMedium, Materials.StainlessSteel);
                case EV:
                    return new UnificationEntry(OrePrefix.pipeMedium, Materials.Titanium);
                case IV:
                    return new UnificationEntry(OrePrefix.pipeMedium, Materials.TungstenSteel);
                default:
                    return new UnificationEntry(OrePrefix.pipeMedium, Materials.TungstenSteel);
            }
        }
    },
    GLASS {
        @Override
        public Object getIngredient(int tier) {
            return new ItemStack(Blocks.GLASS, 1, W);
        }
    },
    PLATE {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.plate, Materials.Steel);
                case MV:
                    return new UnificationEntry(OrePrefix.plate, Materials.Aluminium);
                case HV:
                    return new UnificationEntry(OrePrefix.plate, Materials.StainlessSteel);
                case EV:
                    return new UnificationEntry(OrePrefix.plate, Materials.Titanium);
                case IV:
                    return new UnificationEntry(OrePrefix.plate, Materials.TungstenSteel);
                case LuV:
                    return new UnificationEntry(OrePrefix.plate, Materials.HSSG);
                case ZPM:
                    return new UnificationEntry(OrePrefix.plate, Materials.HSSE);
                case UV:
                    return new UnificationEntry(OrePrefix.plate, Materials.Darmstadtium);
                default:
                    return new UnificationEntry(OrePrefix.plate, Materials.TungstenSteel);
            }
        }
    },
    MOTOR {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.ELECTRIC_MOTOR_LV;
                case MV:
                    return MetaItems.ELECTRIC_MOTOR_MV;
                case HV:
                    return MetaItems.ELECTRIC_MOTOR_HV;
                case EV:
                    return MetaItems.ELECTRIC_MOTOR_EV;
                case IV:
                    return MetaItems.ELECTRIC_MOTOR_IV;
                case LuV:
                    return MetaItems.ELECTRIC_MOTOR_LUV;
                case ZPM:
                    return MetaItems.ELECTRIC_MOTOR_ZPM;
                default:
                    return MetaItems.ELECTRIC_MOTOR_UV;
            }
        }
    },
    ROTOR {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.rotor, Materials.Tin);
                case MV:
                    return new UnificationEntry(OrePrefix.rotor, Materials.Bronze);
                case HV:
                    return new UnificationEntry(OrePrefix.rotor, Materials.Steel);
                case EV:
                    return new UnificationEntry(OrePrefix.rotor, Materials.StainlessSteel);
                case IV:
                    return new UnificationEntry(OrePrefix.rotor, Materials.TungstenSteel);
                case LuV:
                    return new UnificationEntry(OrePrefix.rotor, Materials.Chrome);
                case ZPM:
                    return new UnificationEntry(OrePrefix.rotor, Materials.Iridium);
                default:
                    return new UnificationEntry(OrePrefix.rotor, Materials.Osmium);
            }
        }
    },
    SENSOR {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.SENSOR_LV;
                case MV:
                    return MetaItems.SENSOR_MV;
                case HV:
                    return MetaItems.SENSOR_HV;
                case EV:
                    return MetaItems.SENSOR_EV;
                case IV:
                    return MetaItems.SENSOR_IV;
                case LuV:
                    return MetaItems.SENSOR_LUV;
                case ZPM:
                    return MetaItems.SENSOR_ZPM;
                default:
                    return MetaItems.SENSOR_UV;
            }
        }
    },
    GRINDER {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.gem, Materials.Diamond);
                case MV:
                    return new UnificationEntry(OrePrefix.gem, Materials.Diamond);
                default:
                    return OreDictNames.craftingGrinder;
            }
        }
    },
    DIAMOND {
        @Override
        public Object getIngredient(int tier) {
            return new UnificationEntry(OrePrefix.gem, Materials.Diamond);
        }
    },
    PISTON {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.ELECTRIC_PISTON_LV;
                case MV:
                    return MetaItems.ELECTRIC_PISTON_MV;
                case HV:
                    return MetaItems.ELECTRIC_PISTON_HV;
                case EV:
                    return MetaItems.ELECTRIC_PISTON_EV;
                case IV:
                    return MetaItems.ELECTRIC_PISTON_IV;
                case LuV:
                    return MetaItems.ELECTRIC_PISTON_LUV;
                case ZPM:
                    return MetaItems.ELECTRIC_PISTON_ZPM;
                default:
                    return MetaItems.ELECTRIC_PISTON_UV;
            }
        }
    },
    EMITTER {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.EMITTER_LV;
                case MV:
                    return MetaItems.EMITTER_MV;
                case HV:
                    return MetaItems.EMITTER_HV;
                case EV:
                    return MetaItems.EMITTER_EV;
                case IV:
                    return MetaItems.EMITTER_IV;
                case LuV:
                    return MetaItems.EMITTER_LUV;
                case ZPM:
                    return MetaItems.EMITTER_ZPM;
                default:
                    return MetaItems.EMITTER_UV;
            }
        }
    },
    CONVEYOR {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.CONVEYOR_MODULE_LV;
                case MV:
                    return MetaItems.CONVEYOR_MODULE_MV;
                case HV:
                    return MetaItems.CONVEYOR_MODULE_HV;
                case EV:
                    return MetaItems.CONVEYOR_MODULE_EV;
                case IV:
                    return MetaItems.CONVEYOR_MODULE_IV;
                case LuV:
                    return MetaItems.CONVEYOR_MODULE_LUV;
                case ZPM:
                    return MetaItems.CONVEYOR_MODULE_ZPM;
                default:
                    return MetaItems.CONVEYOR_MODULE_UV;
            }
        }
    },
    ROBOT_ARM {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.ROBOT_ARM_LV;
                case MV:
                    return MetaItems.ROBOT_ARM_MV;
                case HV:
                    return MetaItems.ROBOT_ARM_HV;
                case EV:
                    return MetaItems.ROBOT_ARM_EV;
                case IV:
                    return MetaItems.ROBOT_ARM_IV;
                case LuV:
                    return MetaItems.ROBOT_ARM_LUV;
                case ZPM:
                    return MetaItems.ROBOT_ARM_ZPM;
                default:
                    return MetaItems.ROBOT_ARM_UV;
            }
        }
    },
    COIL_HEATING {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Copper);
                case MV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Cupronickel);
                case HV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Kanthal);
                case EV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Nichrome);
                case IV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.TungstenSteel);
                case LuV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.HSSG);
                case ZPM:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Naquadah);
                case UV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.NaquadahAlloy);
                default:
                    return new UnificationEntry(OrePrefix.wireGtOctal, Materials.Nichrome);
            }
        }
    },
    COIL_ELECTRIC {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                    return new UnificationEntry(OrePrefix.wireGtSingle, Materials.Tin);
                case LV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Tin);
                case MV:
                    return new UnificationEntry(OrePrefix.wireGtDouble, Materials.Copper);
                case HV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Copper);
                case EV:
                    return new UnificationEntry(OrePrefix.wireGtOctal, Materials.AnnealedCopper);
                case IV:
                    return new UnificationEntry(OrePrefix.wireGtOctal, Materials.AnnealedCopper);
                case LuV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.YttriumBariumCuprate);
                case ZPM:
                    return new UnificationEntry(OrePrefix.wireGtOctal, MarkerMaterials.Tier.Superconductor);
                default:
                    return new UnificationEntry(OrePrefix.wireGtHex, MarkerMaterials.Tier.Superconductor);
            }
        }
    },
    STICK_MAGNETIC {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.stick, Materials.IronMagnetic);
                case MV:
                case HV:
                    return new UnificationEntry(OrePrefix.stick, Materials.SteelMagnetic);
                case EV:
                case IV:
                    return new UnificationEntry(OrePrefix.stick, Materials.NeodymiumMagnetic);
                case LuV:
                case ZPM:
                    return new UnificationEntry(OrePrefix.stickLong, Materials.NeodymiumMagnetic);
                default:
                    return new UnificationEntry(OrePrefix.block, Materials.NeodymiumMagnetic);
            }
        }
    },
    STICK_DISTILLATION {
        @Override
        public Object getIngredient(int tier) {
            return new UnificationEntry(OrePrefix.stick, Materials.Blaze);
        }
    },
    FIELD_GENERATOR {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return MetaItems.FIELD_GENERATOR_LV;
                case MV:
                    return MetaItems.FIELD_GENERATOR_MV;
                case HV:
                    return MetaItems.FIELD_GENERATOR_HV;
                case EV:
                    return MetaItems.FIELD_GENERATOR_EV;
                case IV:
                    return MetaItems.FIELD_GENERATOR_IV;
                case LuV:
                    return MetaItems.FIELD_GENERATOR_LUV;
                case ZPM:
                    return MetaItems.FIELD_GENERATOR_ZPM;
                default:
                    return MetaItems.FIELD_GENERATOR_UV;
            }
        }
    },
    COIL_HEATING_DOUBLE {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Copper);
                case MV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Cupronickel);
                case HV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Kanthal);
                case EV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Nichrome);
                case IV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.TungstenSteel);
                case LuV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.HSSG);
                case ZPM:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.Naquadah);
                case UV:
                    return new UnificationEntry(OrePrefix.wireGtQuadruple, Materials.NaquadahAlloy);
                default:
                    return new UnificationEntry(OrePrefix.wireGtHex, Materials.Nichrome);
            }
        }
    },
    STICK_ELECTROMAGNETIC {
        @Override
        public Object getIngredient(int tier) {
            switch (tier) {
                case ULV:
                case LV:
                    return new UnificationEntry(OrePrefix.stick, Materials.Iron);
                case MV:
                case HV:
                    return new UnificationEntry(OrePrefix.stick, Materials.Steel);
                case EV:
                    return new UnificationEntry(OrePrefix.stick, Materials.Neodymium);
                default:
                    return new UnificationEntry(OrePrefix.stick, Materials.VanadiumGallium);
            }
        }
    };

    abstract public Object getIngredient(int tier);
}
