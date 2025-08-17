package gregtech.common.blocks;

import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.common.blocks.BlockFireboxCasing.FireboxCasingType;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;

public class BlockFireboxCasing extends VariantBlock<FireboxCasingType> {

    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockFireboxCasing() {
        super(net.minecraft.block.material.Material.IRON);
        setTranslationKey("boiler_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(FireboxCasingType.BRONZE_FIREBOX).withProperty(ACTIVE, false));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(ACTIVE) ? 15 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        super.createBlockState();
        return new BlockStateContainer(this, VARIANT, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta % 8).withProperty(ACTIVE, meta / 8 >= 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return super.getMetaFromState(state) + (state.getValue(ACTIVE) ? 8 : 0);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return super.getMetaFromState(state);
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    public enum FireboxCasingType implements IStringSerializable, MaterialLookupBlock<FireboxCasingType> {

        BRONZE_FIREBOX("bronze_firebox", Materials.Bronze),
        STEEL_FIREBOX("steel_firebox", Materials.Steel),
        TITANIUM_FIREBOX("titanium_firebox", Materials.Titanium),
        TUNGSTENSTEEL_FIREBOX("tungstensteel_firebox", Materials.TungstenSteel);

        private final String name;
        private final Material material;

        FireboxCasingType(String name, Material material) {
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
        public VariantBlock<FireboxCasingType> getVariantBlock() {
            return MetaBlocks.BOILER_FIREBOX_CASING;
        }
    }

}
