package gregtech.common.blocks;

import gregtech.api.GTValues;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public class BlockMachineCasing2 extends VariantBlock<BlockMachineCasing2.MachineCasingType> {

    public BlockMachineCasing2() {
        super(Material.IRON);
        setTranslationKey("machine_casing");
        setHardness(4.0f);
        setResistance(8.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(MachineCasingType.UHV));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    public enum MachineCasingType implements IStringSerializable, LookupBlock<MachineCasingType> {

        //Voltage-tiered casings
        UHV("ultra_high_voltage"),
        UEV("ultra_excessive_voltage"),
        UIV("ultra_immense_voltage"),
        UXV("ultra_extreme_voltage"),
        OpV("overpowered_voltage");

        private final String name;

        MachineCasingType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        /**
         * @param tier a {@link gregtech.api.GTValues} tier ordinal between {@link GTValues#UHV}
         *             and {@link GTValues#OpV}
         */
        public static MachineCasingType byTier(int tier) {
            return values()[tier - GTValues.UHV];
        }

        @Override
        public @NotNull VariantBlock<MachineCasingType> getVariantBlock() {
            return MetaBlocks.MACHINE_CASING2;
        }
    }

}
