/*
    Code adapted from Nomi-Labs https://github.com/Nomi-CEu/Nomi-Labs @ 6e14c06,
    with selections as noted merged from GTCEu's RenderUtil @ 102e4e05
 */
package gregtech.integration.nomilabs.element;

import gregtech.api.util.TextFormattingUtil;
import gregtech.integration.theoneprobe.TheOneProbeCompatibility;
import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

/**
 * From <a href=
 * "https://github.com/Supernoobv/GregicProbeCEu/blob/master/src/main/java/vfyjxf/gregicprobe/element/FluidStackElement.java">GregicProbe</a>.
 */
public class LabsFluidStackElement implements IElement {

    // ---- Code From GTCEu's RenderUtil ----

    public static void setGlColorFromInt(int colorValue, int opacity) {
        int i = (colorValue & 0xFF0000) >> 16;
        int j = (colorValue & 0xFF00) >> 8;
        int k = (colorValue & 0xFF);
        GlStateManager.color(i / 255.0f, j / 255.0f, k / 255.0f, opacity / 255.0f);
    }

    public static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop,
                                        int maskRight, double zLevel) {
        double uMin = textureSprite.getMinU();
        double uMax = textureSprite.getMaxU();
        double vMin = textureSprite.getMinV();
        double vMax = textureSprite.getMaxV();
        uMax = uMax - maskRight / 16.0 * (uMax - uMin);
        vMax = vMax - maskTop / 16.0 * (vMax - vMin);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        buffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        buffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        buffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }

    // ---- End Block From GTCEu's RenderUtil ----

    private final String location;
    private final int color;
    private final int amount;

    private TextureAtlasSprite sprite = null;

    public LabsFluidStackElement(@NotNull FluidStack stack) {
        this.location = stack.getFluid().getStill(stack).toString();
        this.color = stack.getFluid().getColor(stack);
        this.amount = stack.amount;
    }

    public LabsFluidStackElement(@NotNull ByteBuf buf) {
        this.location = NetworkTools.readStringUTF8(buf);
        this.color = buf.readInt();
        this.amount = buf.readInt();
    }

    @Override
    public void render(int x, int y) {
        if (sprite == null)
            sprite = getFluidAtlasSprite(location);

        GlStateManager.enableBlend();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        setGlColorFromInt(color, 0xFF);
        drawFluidTexture(x + 1, y - 1, sprite, 2, 2, 0);

        String unit = "mB";
        long qty = amount;
        if (amount > 0) {
            // use Buckets and reduce amount accordingly before compacting
            if(amount >= 1_000) {
                unit = "B";
                qty = amount / 1_000;
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5, 0.5, 1);
            Minecraft minecraft = Minecraft.getMinecraft();
            String format = TextFormattingUtil.formatLongToCompactString(qty) + unit;
            minecraft.fontRenderer.drawStringWithShadow(
                    format,
                    (x + 16) * 2 - minecraft.fontRenderer.getStringWidth(format),
                    (y + 16) * 2 - minecraft.fontRenderer.FONT_HEIGHT,
                    0xFFFFFF);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf) {
        NetworkTools.writeStringUTF8(buf, location);
        buf.writeInt(color);
        buf.writeInt(amount);
    }

    @Override
    public int getID() {
        return TheOneProbeCompatibility.FLUID_STACK_ELEMENT;
    }

    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite getFluidAtlasSprite(String stillLocation) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(stillLocation);
    }
}
