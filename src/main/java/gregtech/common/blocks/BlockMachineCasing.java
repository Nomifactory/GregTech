package gregtech.common.blocks;

import gregtech.api.GTValues;
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
        ULV("ultra_low_voltage", GTValues.ULV),
        LV("low_voltage", GTValues.LV),
        MV("medium_voltage", GTValues.MV),
        HV("high_voltage", GTValues.HV),
        EV("extreme_voltage", GTValues.EV),
        IV("insane_voltage", GTValues.IV),
        LuV("ludicrous_voltage", GTValues.LuV),
        ZPM("zero_point_module", GTValues.ZPM),
        UV("ultra_voltage", GTValues.UV),
        MAX("maximum_voltage", GTValues.MAX),
        BRONZE_HULL("bronze_hull", Materials.Bronze),
        BRONZE_BRICKS_HULL("bronze_bricks_hull", Materials.Bronze),
        STEEL_HULL("steel_hull", Materials.Steel),
        STEEL_BRICKS_HULL("steel_bricks_hull", Materials.Steel);

        private final String name;
        private final Material material;
        private final int tier;

        MachineCasingType(String name, int tier) {
            this(name, null, tier);
        }

        MachineCasingType(String name, Material material) {
            this(name, material, -1);
        }

        MachineCasingType(String name, Material material, int tier) {
            this.name = name;
            this.material = material;
            this.tier = tier;
        }

        @Override
        public String getName() {
            return this.name;
        }

        /** Returns the subset of this VariantBlock consisting of tiered machine casings  */
        public static MachineCasingType[] getTiered() {
            return Arrays.stream(values())
                         .filter(x -> x.getTier() > -1)
                         .toArray(MachineCasingType[]::new);
        }

        /** @return the tier of this element, or {@code -1} if there is no tier. */
        public int getTier() {
            return tier;
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
