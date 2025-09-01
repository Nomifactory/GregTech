package gregtech.api.metatileentity;

import gregtech.api.unification.material.type.Material;

public interface IMaterial {
    /** @return the {@link Material} this object is composed of */
    Material getMaterial();
}
