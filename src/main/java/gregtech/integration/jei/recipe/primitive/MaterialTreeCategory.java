package gregtech.integration.jei.recipe.primitive;

import com.google.common.collect.ImmutableList;
import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.api.recipes.recipeproperties.BlastTemperatureProperty;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.integration.jei.utils.DrawableRegistry;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MaterialTreeCategory extends PrimitiveRecipeCategory<MaterialTree, MaterialTree> {

    protected String materialName;
    protected String materialFormula;
    protected int materialBFTemp;
    protected String materialAvgM;
    protected String materialAvgP;
    protected String materialAvgN;

    protected final IDrawable slot;
    protected final IDrawable icon;

    protected List<Boolean> itemExists = new ArrayList<>();
    protected List<Boolean> fluidExists = new ArrayList<>();
    // XY positions of ingredients
    protected final static ImmutableList<Integer> ITEM_LOCATIONS = ImmutableList.of(
            // corresponds pair-to-one with PREFIXES in MaterialTree.java
            4, 67,      // dustTiny
            4, 101,     // dust
            4, 135,     // dustSmall
            29, 55,     // cableGtSingle
            29, 85,     // ingotHot
            29, 117,    // ingot
            29, 117,    // gem
            29, 147,    // block
            54, 55,     // wireGtSingle
            54, 85,     // stick
            54, 117,    // nugget
            54, 147,    // plate
            79, 55,     // wireFine
            79, 85,     // frameGt
            104, 55,    // screw
            104, 85,    // bolt
            104, 117,   // gear
            129, 55,    // spring
            129, 85,    // stickLong
            129, 117,   // gearSmall
            129, 147,   // plateDense
            154, 55,    // springSmall
            154, 78,    // ring
            154, 124,   // lens
            154, 147    // foil
    );

    protected ImmutableList<Integer> FLUID_LOCATIONS = ImmutableList.of(
            154, 101    // fluid
    );

    public MaterialTreeCategory(IGuiHelper helper) {
        super("material_tree", "gregtech.jei.materialtree.name", helper.createBlankDrawable(176, 166), helper);
        this.slot = helper.drawableBuilder(GuiTextures.SLOT.imageLocation, 0, 0, 18, 18).setTextureSize(18, 18).build();
        this.icon = helper.createDrawableIngredient(OreDictUnifier.get(OrePrefix.ingot, Materials.Aluminium));

        registerArrow(helper, "2d12", 5, 12);
        registerArrow(helper, "2d16", 5, 16);
        registerArrow(helper, "2r16d37", 18, 40);
        registerArrow(helper, "d14", 5, 14);
        registerArrow(helper, "d7r25u6", 28, 7);
        registerArrow(helper, "d7r50d7", 53, 14);
        registerArrow(helper, "d7r50u6", 53, 7);
        registerArrow(helper, "d7r75d7", 78, 14);
        registerArrow(helper, "d7r75u6", 78, 7);
        registerArrow(helper, "d7r87u22r4", 92, 25);
        registerArrow(helper, "d7r87u46r4", 92, 49);
        registerArrow(helper, "l7", 7, 5);
        registerArrow(helper, "r3d16r4", 7, 19);
        registerArrow(helper, "r3d26r4", 7, 29);
        registerArrow(helper, "r3u15r4", 7, 18);
        registerArrow(helper, "r3u32r4", 7, 35);
        registerArrow(helper, "r3u57r4", 7, 60);
        registerArrow(helper, "r7", 7, 5);
        registerArrow(helper, "u12", 5, 12);
        registerArrow(helper, "u7r50u5", 53, 12);
        registerArrow(helper, "u7r75d6", 78, 7);
        registerArrow(helper, "u7r75u5", 78, 12);
        registerArrow(helper, "u7r87d15r4", 92, 18);
        registerArrow(helper, "u7r87u8r4", 92, 17);
        registerArrow(helper, "r3u62r29", 32, 65);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MaterialTree recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();

        List<List<ItemStack>> itemInputs = ingredients.getInputs(VanillaTypes.ITEM);
        itemExists.clear();
        for (int i = 0; i < ITEM_LOCATIONS.size(); i += 2) {
            itemStackGroup.init(i, true, ITEM_LOCATIONS.get(i), ITEM_LOCATIONS.get(i + 1));
            itemExists.add(!itemInputs.get(i / 2).isEmpty());
        }
        itemStackGroup.set(ingredients);

        List<List<FluidStack>> fluidInputs = ingredients.getInputs(VanillaTypes.FLUID);
        fluidExists.clear();
        for (int i = 0; i < FLUID_LOCATIONS.size(); i += 2) {
            fluidStackGroup.init(0, true, FLUID_LOCATIONS.get(i) + 1, FLUID_LOCATIONS.get(i + 1) + 1);
            fluidExists.add(!fluidInputs.get(i / 2).isEmpty());
        }
        fluidStackGroup.set(ingredients);

        materialName = recipeWrapper.getMaterialName();
        materialFormula = recipeWrapper.getMaterialFormula();
        materialBFTemp = recipeWrapper.getBlastTemp();
        materialAvgM = I18n.format("gregtech.jei.materials.average_mass", recipeWrapper.getAvgM());
        materialAvgN = I18n.format("gregtech.jei.materials.average_neutrons", recipeWrapper.getAvgN());
        materialAvgP = I18n.format("gregtech.jei.materials.average_protons", recipeWrapper.getAvgP());
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        for (int i = 0; i < ITEM_LOCATIONS.size(); i += 2) {
            if (itemExists.get(i / 2)) slot.draw(minecraft, ITEM_LOCATIONS.get(i), ITEM_LOCATIONS.get(i + 1));
        }
        for (int i = 0; i < FLUID_LOCATIONS.size(); i += 2) {
            if (fluidExists.get(i / 2)) slot.draw(minecraft, FLUID_LOCATIONS.get(i), FLUID_LOCATIONS.get(i + 1));
        }

        // %t inspired
        // arrow rendering, aka hardcoded jank
        // indeces are from ITEM_LOCATIONS / MaterialTree.PREFIXES
        // dustTiny <-> dust
        drawArrow(minecraft, "2d16", 10, 85, itemExists.get(0) && itemExists.get(1));
        // dust <-> dustSmall
        drawArrow(minecraft, "2d16", 10, 119, itemExists.get(1) && itemExists.get(2));
        // dust <-> block (if no ingot or gem)
        drawArrow(minecraft, "2r16d37", 22, 107, !itemExists.get(5) &&
                !itemExists.get(6) && itemExists.get(1) && itemExists.get(7));
        // dust -> ingotHot
        drawArrow(minecraft, "r3u15r4", 22, 92, itemExists.get(1) && itemExists.get(4));
        // dust -> ingot/gem (if no ingotHot)
        drawArrow(minecraft, "r3d16r4", 22, 109, !itemExists.get(4) &&
                itemExists.get(1) && (itemExists.get(5) || itemExists.get(6)));
        // ingotHot -> ingot
        drawArrow(minecraft, "d14", 35, 103, itemExists.get(4) && itemExists.get(5));
        // ingot/gem <-> block
        drawArrow(minecraft, "2d12", 35, 135, itemExists.get(7) &&
                (itemExists.get(5) || itemExists.get(6)));
        // ingot -> wireGtSingle
        drawArrow(minecraft, "r3u57r4", 47, 66, itemExists.get(5) && itemExists.get(8));
        // ingot/gem -> stick
        drawArrow(minecraft, "r3u32r4", 47, 91, itemExists.get(9) &&
                (itemExists.get(5) || itemExists.get(6)));
        // ingot -> nugget
        drawArrow(minecraft, "r7", 47, 123, itemExists.get(5) && itemExists.get(10));
        // ingot -> plate
        drawArrow(minecraft, "r3d26r4", 47, 125, itemExists.get(5) && itemExists.get(11));
        // ingot -> wireFine (if no wireGtSingle)
        drawArrow(minecraft, "r3u62r29", 47, 61, !itemExists.get(8) &&
                itemExists.get(5) && itemExists.get(12));
        // block -> plate
        drawArrow(minecraft, "r7", 47, 158, itemExists.get(7) && itemExists.get(11));
        // wireGtSingle -> cableGtSingle
        drawArrow(minecraft, "l7", 47, 57, itemExists.get(8) && itemExists.get(3));
        // wireGtSingle -> wireFine
        drawArrow(minecraft, "r7", 72, 61, itemExists.get(8) && itemExists.get(12));
        // stick -> frameGt
        drawArrow(minecraft, "d7r25u6", 62, 103, itemExists.get(9) && itemExists.get(13));
        // stick -> bolt
        drawArrow(minecraft, "d7r50u6", 62, 103, itemExists.get(9) && itemExists.get(15));
        // stick -> gear
        drawArrow(minecraft, "d7r50d7", 62, 103, itemExists.get(9) && itemExists.get(16));
        // stick -> stickLong
        drawArrow(minecraft, "d7r75u6", 62, 103, itemExists.get(9) && itemExists.get(18));
        // stick -> gearSmall
        drawArrow(minecraft, "d7r75d7", 62, 103, itemExists.get(9) && itemExists.get(19));
        // stick -> springSmall
        drawArrow(minecraft, "d7r87u46r4", 62, 61, itemExists.get(9) && itemExists.get(21));
        // stick -> ring
        drawArrow(minecraft, "d7r87u22r4", 62, 85, itemExists.get(9) && itemExists.get(22));
        // plate -> gear
        drawArrow(minecraft, "u7r50u5", 62, 135, itemExists.get(11) && itemExists.get(16));
        // plate -> gearSmall
        drawArrow(minecraft, "u7r75u5", 62, 135, itemExists.get(11) && itemExists.get(19));
        // plate -> plateDense
        drawArrow(minecraft, "u7r75d6", 62, 140, itemExists.get(11) && itemExists.get(20));
        // plate -> lens
        drawArrow(minecraft, "u7r87u8r4", 62, 130, itemExists.get(11) && itemExists.get(23));
        // plate -> foil
        drawArrow(minecraft, "u7r87d15r4", 62, 140, itemExists.get(11) && itemExists.get(24));
        // bolt -> screw
        drawArrow(minecraft, "u12", 110, 73, itemExists.get(15) && itemExists.get(14));
        // stickLong -> spring
        drawArrow(minecraft, "u12", 135, 73, itemExists.get(18) && itemExists.get(17));

        int linesDrawn = 0;
        if (minecraft.fontRenderer.getStringWidth(materialName) > 176) {
            minecraft.fontRenderer.drawString(minecraft.fontRenderer.trimStringToWidth(materialName, 171) + "...", 0, 0, 0x111111);
            linesDrawn++;
        } else if (!materialName.isEmpty()) {
            minecraft.fontRenderer.drawString(materialName, 0, 0, 0x111111);
            linesDrawn++;
        }
        if (minecraft.fontRenderer.getStringWidth(materialFormula) > 176) {
            minecraft.fontRenderer.drawString(minecraft.fontRenderer.trimStringToWidth(materialFormula, 171) + "...", 0, 9 * linesDrawn, 0x111111);
            linesDrawn++;
        } else if (!materialFormula.isEmpty()) {
            minecraft.fontRenderer.drawString(materialFormula, 0, 9 * linesDrawn, 0x111111);
            linesDrawn++;
        }
        if (materialBFTemp != 0) {
            //minecraft.fontRenderer.drawString(I18n.format("gregtech.recipe.temperature", materialBFTemp), 0, 9 * linesDrawn, 0x111111);
            BlastTemperatureProperty.getInstance().drawInfo(minecraft, 0, 9 * linesDrawn, 0x111111, materialBFTemp);
            linesDrawn++;
        }
        minecraft.fontRenderer.drawString(materialAvgM, 0, 9 * linesDrawn, 0x111111);
        linesDrawn++;
        minecraft.fontRenderer.drawString(materialAvgN, 0, 9 * linesDrawn, 0x111111);
        linesDrawn++;
        minecraft.fontRenderer.drawString(materialAvgP, 0, 9 * linesDrawn, 0x111111);
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(MaterialTree recipe) {
        return recipe;
    }

    private static void registerArrow(IGuiHelper helper, String name, int width, int height) {
        DrawableRegistry.initDrawable(helper, GTValues.MODID + ":textures/gui/arrows/" + name + ".png", width, height, name);
    }

    private static void drawArrow(Minecraft minecraft, String name, int x, int y, boolean shown) {
        if (shown) DrawableRegistry.drawDrawable(minecraft, name, x, y);
    }
}
