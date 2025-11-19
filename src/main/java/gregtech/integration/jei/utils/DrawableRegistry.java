package gregtech.integration.jei.utils;

import gregtech.api.GTValues;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class DrawableRegistry {

    private static final Map<String, IDrawable> drawables = new HashMap<>();

    public static void initDrawable(IGuiHelper helper, String location, int width, int height, String key) {
        drawables.put(key, helper.drawableBuilder(new ResourceLocation(location), 0, 0, width, height).setTextureSize(width, height).build());
    }

    public static void drawDrawable(Minecraft minecraft, String key, int x, int y) {
        drawables.get(key).draw(minecraft, x, y);
    }
}
