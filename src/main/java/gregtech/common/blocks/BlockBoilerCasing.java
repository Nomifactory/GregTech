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

public class BlockBoilerCasing extends VariantBlock<BlockBoilerCasing.BoilerCasingType> {

    public BlockBoilerCasing() {
        super(net.minecraft.block.material.Material.IRON);
        setTranslationKey("boiler_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(BoilerCasingType.BRONZE_PIPE));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    public enum BoilerCasingType implements IStringSerializable, MaterialLookupBlock<BoilerCasingType> {

        BRONZE_PIPE("bronze_pipe", Materials.Bronze),
        STEEL_PIPE("steel_pipe", Materials.Steel),
        TITANIUM_PIPE("titanium_pipe", Materials.Titanium),
        TUNGSTENSTEEL_PIPE("tungstensteel_pipe", Materials.TungstenSteel);

        private final String name;
        private final Material material;

        BoilerCasingType(String name, Material material) {
            this.name = name;
            this.material = material;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Material getMaterial() {
            return this.material;
        }

        @Override
        @NotNull
        public VariantBlock<BoilerCasingType> getVariantBlock() {
            return MetaBlocks.BOILER_CASING;
        }
    }

}
