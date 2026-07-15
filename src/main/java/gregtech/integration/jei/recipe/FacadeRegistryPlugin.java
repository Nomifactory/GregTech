package gregtech.integration.jei.recipe;

import com.google.common.collect.Lists;
import gregtech.api.GTValues;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.covers.facade.FacadeHelper;
import gregtech.common.items.MetaItems;
import gregtech.common.items.behaviors.FacadeItem;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FacadeRegistryPlugin implements IRecipeRegistryPlugin {

    @Override
    @NotNull
    public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
        if (focus.getValue() instanceof ItemStack itemStack) {
            if (focus.getMode() == Mode.OUTPUT) {
                if (MetaItems.COVER_FACADE.isItemEqual(itemStack)) {
                    //looking up recipes of facade cover
                    return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
                }
            } else if (focus.getMode() == Mode.INPUT) {
                if (FacadeHelper.isValidFacade(itemStack)) {
                    //looking up usage of block to make a facade cover
                    return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory,
                                                                   @NotNull IFocus<V> focus)
    {
        if (!VanillaRecipeCategoryUid.CRAFTING.equals(recipeCategory.getUid())) {
            return Collections.emptyList();
        }
        if (focus.getValue() instanceof ItemStack itemStack) {
            if (focus.getMode() == Mode.OUTPUT) {
                if (MetaItems.COVER_FACADE.isItemEqual(itemStack)) {
                    //looking up recipes of facade cover
                    return (List<T>) createFacadeRecipes(itemStack);
                }
            } else if (focus.getMode() == Mode.INPUT) {
                if (FacadeHelper.isValidFacade(itemStack)) {
                    //looking up usage of block to make a facade cover
                    ItemStack coverStack = MetaItems.COVER_FACADE.getStackForm();
                    FacadeItem.setFacadeStack(coverStack, itemStack);
                    return (List<T>) createFacadeRecipes(coverStack);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<IRecipeWrapper> createFacadeRecipes(ItemStack itemStack) {
        return Lists.newArrayList(
            createFacadeRecipe(itemStack, Materials.Aluminium, 5),
            createFacadeRecipe(itemStack, Materials.WroughtIron, 3),
            createFacadeRecipe(itemStack, Materials.Iron, 2));
    }

    private static IRecipeWrapper createFacadeRecipe(ItemStack itemStack, Material material, int facadeAmount) {
        ItemStack itemStackCopy = itemStack.copy();
        itemStackCopy.setCount(facadeAmount);
        return new FacadeRecipeWrapper(new ResourceLocation(GTValues.MODID, "facade_" + material),
            OreDictUnifier.get(OrePrefix.plate, material), itemStackCopy);
    }

    @Override
    @NotNull
    public <T extends IRecipeWrapper> List<T> getRecipeWrappers(@NotNull IRecipeCategory<T> recipeCategory) {
        return Collections.emptyList();
    }
}
