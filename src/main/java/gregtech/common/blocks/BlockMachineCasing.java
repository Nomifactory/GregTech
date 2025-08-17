package gregtech.common.blocks;

import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BlockMachineCasing extends VariantBlock<BlockMachineCasing.MachineCasingType> {

    public BlockMachineCasing() {
        super(net.minecraft.block.material.Material.IRON);
        setTranslationKey("machine_casing");
        setHardness(4.0f);
        setResistance(8.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(MachineCasingType.ULV));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    public enum MachineCasingType implements IStringSerializable, MaterialLookupBlock<MachineCasingType> {

        //Voltage-tiered casings
        ULV("ultra_low_voltage"),
        LV("low_voltage"),
        MV("medium_voltage"),
        HV("high_voltage"),
        EV("extreme_voltage"),
        IV("insane_voltage"),
        LuV("ludicrous_voltage"),
        ZPM("zero_point_module"),
        UV("ultra_voltage"),
        MAX("maximum_voltage"),
        BRONZE_HULL("bronze_hull", Materials.Bronze),
        BRONZE_BRICKS_HULL("bronze_bricks_hull", Materials.Bronze),
        STEEL_HULL("steel_hull", Materials.Steel),
        STEEL_BRICKS_HULL("steel_bricks_hull", Materials.Steel);

        private final String name;
        private final Material material;

        MachineCasingType(String name) {
            this(name, null);
        }

        MachineCasingType(String name, Material material) {
            this.name = name;
            this.material = material;
        }

        @Override
        public String getName() {
            return this.name;
        }

        /** Returns the subset of this VariantBlock consisting of tiered machine casings  */
        public static MachineCasingType[] getTiered() {
            return Arrays.copyOfRange(values(), ULV.ordinal(), MAX.ordinal() + 1);
        }

        @Override
        public Material getMaterial() {
            return this.material;
        }

        @Override
        @NotNull
        public VariantBlock<MachineCasingType> getVariantBlock() {
            return MetaBlocks.MACHINE_CASING;
        }
    }

}
