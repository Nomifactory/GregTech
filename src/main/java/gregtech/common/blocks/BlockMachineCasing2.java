package gregtech.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

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

    public enum MachineCasingType implements IStringSerializable {

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

    }

}
