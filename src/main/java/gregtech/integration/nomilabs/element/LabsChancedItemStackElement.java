/*
    Code adapted from Nomi-Labs https://github.com/Nomi-CEu/Nomi-Labs @ d2d6e89
 */
package gregtech.integration.nomilabs.element;

import gregtech.integration.theoneprobe.TheOneProbeCompatibility;
import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IItemStyle;
import mcjty.theoneprobe.apiimpl.elements.ElementItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;

public class LabsChancedItemStackElement extends ElementItemStack {

    private static final DecimalFormat format = new DecimalFormat("#.#");

    private final int chance;

    public LabsChancedItemStackElement(ItemStack itemStack, int chance, IItemStyle style) {
        super(itemStack, style);
        this.chance = chance;
    }

    public LabsChancedItemStackElement(ByteBuf buf) {
        super(buf);
        chance = buf.readInt();
    }

    @Override
    public void render(int x, int y) {
        super.render(x, y);
        renderChance(chance, x, y);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(chance);
    }

    @Override
    public int getID() {
        return TheOneProbeCompatibility.CHANCED_ITEM_STACK_ELEMENT;
    }

    @SideOnly(Side.CLIENT)
    public static void renderChance(int chance, int x, int y) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5f, 0.5f, 1);
        Minecraft mc = Minecraft.getMinecraft();

        String chanceTxt = formatChance(chance);
        mc.fontRenderer.drawStringWithShadow(chanceTxt, (x + 17) * 2 - 1 - mc.fontRenderer.getStringWidth(chanceTxt),
                                             y * 2, 0xffffffff);

        GlStateManager.popMatrix();

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }

    public static String formatChance(int chance) {
        return format.format(chance / 100.0) + "%";
    }
}
