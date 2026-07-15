package gregtech.integration.jei.utils;

import gregtech.api.GTValues;
import net.minecraft.client.resources.I18n;

public class JEIHelpers {

    /**
     * Returns the short name for the minimum required power tier for a specified voltage
     */
    public static String getMinTierForVoltage(long voltage) {
        for (int i = 0; i < GTValues.V.length; i++) {
            if (voltage <= GTValues.V[i]) {
                return GTValues.VN[i];
            }
        }
        return "";
    }

    /** Formats a recipe duration as either seconds or ticks, as appropriate. */
    public static String getDurationText(int duration) {
        if(duration < 20)
            return I18n.format("gregtech.recipe.duration_ticks", duration);
        return I18n.format("gregtech.recipe.duration", duration / 20.0);
    }

}
