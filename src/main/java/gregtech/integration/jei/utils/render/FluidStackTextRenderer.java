package gregtech.integration.jei.utils.render;

import gregtech.api.gui.resources.RenderUtil;
import gregtech.api.recipes.Recipe;
import gregtech.api.util.TextFormattingUtil;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FluidStackTextRenderer extends FluidStackRenderer {

    private int chanceBase = -1;
    private int chanceBoost = -1;
    private boolean notConsumed;

    public FluidStackTextRenderer(int capacityMB, boolean showCapacity, int width, int height, @Nullable IDrawable overlay) {
        super(capacityMB, showCapacity, width, height, overlay);
        this.notConsumed = false;
    }

    public FluidStackTextRenderer setNotConsumed(boolean notConsumed) {
        this.notConsumed = notConsumed;
        return this;
    }

    public FluidStackTextRenderer(int capacityMB, boolean showCapacity, int width, int height, @Nullable IDrawable overlay, Recipe.ChanceEntry chanceEntry) {
        if (chanceEntry != null) {
            this.chanceBase = chanceEntry.getChance();
            this.chanceBoost = chanceEntry.getBoostPerTier();
        }
        this.notConsumed = false;
    }

    @Override
    public void render(@NotNull Minecraft minecraft, int xPosition, int yPosition, @org.jetbrains.annotations.Nullable FluidStack fluidStack) {
        if (fluidStack == null) return;

        GlStateManager.disableBlend();

        RenderUtil.drawFluidForGui(fluidStack, fluidStack.amount, xPosition, yPosition, 17, 17);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 1);
        GlStateManager.translate(0, 0, 160);

        String s = TextFormattingUtil.formatLongToCompactString(fluidStack.amount, 5) + "L";

        FontRenderer fontRenderer = minecraft.fontRenderer;
        fontRenderer.drawStringWithShadow(s, (xPosition + 7 + (s.length() > 5 ? 1 : 0)) * 2 - fontRenderer.getStringWidth(s) + 19, (yPosition + 12) * 2, 0xFFFFFF);

        GlStateManager.popMatrix();

        if (this.chanceBase >= 0) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1);
            GlStateManager.translate(0, 0, 160);

            String s2 = (this.chanceBase / 100) + "%";
            if (this.chanceBoost > 0) s2 += "+";

            fontRenderer.drawStringWithShadow(s2, (xPosition + 6) * 2 - fontRenderer.getStringWidth(s2) + 19, (yPosition + 1) * 2, 0xFFFF00);

            GlStateManager.popMatrix();
        } else if (this.notConsumed) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1);

            fontRenderer.drawStringWithShadow("NC", (xPosition + 6) * 2 - fontRenderer.getStringWidth("NC") + 19, (yPosition + 1) * 2, 0xFFFF00);

            GlStateManager.popMatrix();
        }

        GlStateManager.enableBlend();
    }
}
