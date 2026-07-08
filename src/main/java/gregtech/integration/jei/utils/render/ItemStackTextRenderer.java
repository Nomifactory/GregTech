package gregtech.integration.jei.utils.render;

import gregtech.api.recipes.Recipe;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackTextRenderer extends ItemStackRenderer {

    private final int chanceBase;
    private final int chanceBoost;
    private final boolean notConsumed;

    public ItemStackTextRenderer(Recipe.ChanceEntry chanceEntry) {
        if (chanceEntry != null) {
            this.chanceBase = chanceEntry.getChance();
            this.chanceBoost = chanceEntry.getBoostPerTier();
        } else {
            this.chanceBase = -1;
            this.chanceBoost = -1;
        }
        this.notConsumed = false;
    }

    public ItemStackTextRenderer(int chanceBase, int chanceBoost) {
        this.chanceBase = chanceBase;
        this.chanceBoost = chanceBoost;
        this.notConsumed = false;
    }

    public ItemStackTextRenderer(boolean notConsumed) {
        this.chanceBase = -1;
        this.chanceBoost = -1;
        this.notConsumed = notConsumed;
    }

    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable ItemStack ingredient) {
        super.render(minecraft, xPosition, yPosition, ingredient);

        if (this.chanceBase >= 0) {
            GlStateManager.disableBlend();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1);
            GlStateManager.translate(0, 0, 160);

            String s = (this.chanceBase / 100) + "%";
            if (this.chanceBoost > 0) s += "+";

            FontRenderer fontRenderer = minecraft.fontRenderer;
            fontRenderer.drawStringWithShadow(s, (xPosition + 6) * 2 - fontRenderer.getStringWidth(s) + 19, (yPosition + 1) * 2, 0xFFFF00);

            GlStateManager.popMatrix();
            GlStateManager.enableBlend();
        } else if (notConsumed) {
            GlStateManager.disableBlend();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 1);
            GlStateManager.translate(0, 0, 160);

            FontRenderer fontRenderer = minecraft.fontRenderer;
            fontRenderer.drawStringWithShadow("NC", (xPosition + 6) * 2 - fontRenderer.getStringWidth("NC") + 19, (yPosition + 1) * 2, 0xFFFF00);

            GlStateManager.popMatrix();
            GlStateManager.enableBlend();
        }
    }
}
