package gregtech.api.gui.igredient;

import mezz.jei.api.gui.IGhostIngredientHandler.Target;

import java.util.List;

public interface IGhostIngredientTarget {

    List<Target<?>> getPhantomTargets(Object ingredient);

    /** Clamps and casts {@code in} to an {@code int} valued between {@code 1} and {@code Integer.MAX_VALUE} */
    default int clampLong(long in) {
        if(in <= 1L)
            return 1;
        if(in > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        return (int) in;
    }

}
