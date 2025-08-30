package gregtech.common.blocks;

import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of {@link LookupBlock} to support blocks with a primary material type
 * and acquisition of {@link UnificationEntry} from those materials.
 */
public interface MaterialLookupBlock<T extends Enum<T> & IStringSerializable> extends LookupBlock<T> {
    /**
     * @return the Material associated with this element, or {@code null} if there isn't one.
     */
    @Nullable
    Material getMaterial();

    /**
     * @return a UnificationEntry of the specified type for this MetaBlock subtype's material, or {@code null}
     * if there is no associated material
     */
    @Nullable
    default UnificationEntry getUE(OrePrefix orePrefix) {
        var material = getMaterial();
        if (material != null)
            return new UnificationEntry(orePrefix, material);
        return null;
    }
}
