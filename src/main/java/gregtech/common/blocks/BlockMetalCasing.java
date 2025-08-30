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

public class BlockMetalCasing extends VariantBlock<BlockMetalCasing.MetalCasingType> {

    public BlockMetalCasing() {
        super(net.minecraft.block.material.Material.IRON);
        setTranslationKey("metal_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setDefaultState(getState(MetalCasingType.BRONZE_BRICKS));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    public enum MetalCasingType implements IStringSerializable, MaterialLookupBlock<MetalCasingType> {

        BRONZE_BRICKS("bronze_bricks", Materials.Bronze),
        PRIMITIVE_BRICKS("primitive_bricks"),
        INVAR_HEATPROOF("invar_heatproof", Materials.Invar),
        ALUMINIUM_FROSTPROOF("aluminium_frostproof", Materials.Aluminium),
        STEEL_SOLID("steel_solid", Materials.Steel),
        STAINLESS_CLEAN("stainless_clean", Materials.StainlessSteel),
        TITANIUM_STABLE("titanium_stable", Materials.Titanium),
        TUNGSTENSTEEL_ROBUST("tungstensteel_robust", Materials.TungstenSteel),
        COKE_BRICKS("coke_bricks");

        private final String name;
        private final Material material;

        MetalCasingType(String name, Material material) {
            this.name = name;
            this.material = material;
        }

        MetalCasingType(String name) {
            this(name, null);
        }

        @Override
        public String getName() {
            return this.name;
        }

        public Material getMaterial() {
            return this.material;
        }

        @Override
        @NotNull
        public VariantBlock<MetalCasingType> getVariantBlock() {
            return MetaBlocks.METAL_CASING;
        }
    }

}
