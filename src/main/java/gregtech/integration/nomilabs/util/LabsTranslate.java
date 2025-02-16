/*
    Code adapted from Nomi-Labs https://github.com/Nomi-CEu/Nomi-Labs @ 6e14c06
 */
package gregtech.integration.nomilabs.util;

import gregtech.api.util.GTLog;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.IllegalFormatException;

public class LabsTranslate {

    public static String translate(String key, Object... params) {
        if (FMLCommonHandler.instance().getSide().isServer())
            return translateServerSide(key, params); // I18n is not available on Dedicated Servers
        try {
            return net.minecraft.client.resources.I18n.format(key, params);
        } catch (Exception e) {
            return translateServerSide(key, params);
        }
    }

    @SuppressWarnings("deprecation")
    private static String translateServerSide(String key, Object... params) {
        try {
            var localTranslated = I18n.translateToLocalFormatted(key, params);
            if (!localTranslated.equals(key)) return localTranslated;

            // Try fallback
            var fallbackTranslated = I18n.translateToFallback(key);
            if (!fallbackTranslated.equals(key) && params.length != 0) {
                try {
                    fallbackTranslated = String.format(fallbackTranslated, params);
                } catch (IllegalFormatException ife) {
                    fallbackTranslated = "Format error: " + fallbackTranslated;
                    GTLog.logger.error(ife);
                }
            }
            return fallbackTranslated;
        } catch (Exception e) {
            return key;
        }
    }
}
