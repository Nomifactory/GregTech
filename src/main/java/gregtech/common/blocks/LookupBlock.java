package gregtech.common.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience methods for MetaBlock subtype enums to implement to make programmatic generation
 * of recipes more convenient.
 */
public interface LookupBlock<T extends Enum<T> & IStringSerializable> {

    /**
     * @return the VariantBlock instance this enum corresponds to
     */
    @NotNull
    VariantBlock<T> getVariantBlock();

    /**
     * @return a MetaBlock instance of this subtype, as an ItemStack of the desired size
     */
    default ItemStack getStack(int count) {
        return getVariantBlock().getItemVariant((T) this, count);
    }

    /**
     * @return a MetaBlock instance of this subtype, as an ItemStack of size 1
     */
    default ItemStack getStack() {
        return getStack(1);
    }
}
