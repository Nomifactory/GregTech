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

public class BlockTurbineCasing extends VariantBlock<BlockTurbineCasing.TurbineCasingType> {

    public BlockTurbineCasing() {
        super(net.minecraft.block.material.Material.IRON);
        setTranslationKey("turbine_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(TurbineCasingType.BRONZE_GEARBOX));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    public enum TurbineCasingType implements IStringSerializable, MaterialLookupBlock<TurbineCasingType> {

        BRONZE_GEARBOX("bronze_gearbox", Materials.Bronze),
        STEEL_GEARBOX("steel_gearbox", Materials.Steel),
        TITANIUM_GEARBOX("titanium_gearbox", Materials.Titanium),

        STEEL_TURBINE_CASING("steel_turbine_casing", Materials.Steel),
        TITANIUM_TURBINE_CASING("titanium_turbine_casing", Materials.Titanium),
        STAINLESS_TURBINE_CASING("stainless_turbine_casing", Materials.StainlessSteel),
        TUNGSTENSTEEL_TURBINE_CASING("tungstensteel_turbine_casing", Materials.TungstenSteel);

        private final String name;
        private final Material material;

        TurbineCasingType(String name, Material material) {
            this.name = name;
            this.material = material;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public Material getMaterial() {
            return material;
        }

        @Override
        @NotNull
        public VariantBlock<TurbineCasingType> getVariantBlock() {
            return MetaBlocks.TURBINE_CASING;
        }
    }

}
