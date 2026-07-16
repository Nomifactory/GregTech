package gregtech.integration.jei.recipe;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.Recipe.ChanceEntry;
import gregtech.api.recipes.recipeproperties.RecipeProperty;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.util.ItemStackHashStrategy;
import gregtech.integration.jei.utils.JEIHelpers;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static gregtech.integration.jei.utils.JEIHelpers.getDurationText;

public class GTRecipeWrapper implements IRecipeWrapper {
    private static final int LINE_HEIGHT = 10;

    private final Hash.Strategy<ItemStack> strategy = ItemStackHashStrategy.comparingAllButCount();

    private final Set<ItemStack> notConsumedInput = new ObjectOpenCustomHashSet<>(strategy);
    private final Map<ItemStack, ChanceEntry> chanceOutput = new Object2ObjectOpenCustomHashMap<>(strategy);
    private final List<FluidStack> notConsumedFluidInput = new ArrayList<>();

    public final ITooltipCallback<ItemStack> itemCallback = this::addItemTooltip;
    public final ITooltipCallback<FluidStack> fluidCallback = this::addFluidTooltip;

    private final Recipe recipe;

    public GTRecipeWrapper(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@NotNull IIngredients ingredients) {
        if (!recipe.getInputs().isEmpty()) {
            List<CountableIngredient> recipeInputs = recipe.getInputs();
            List<List<ItemStack>> matchingInputs = new ArrayList<>(recipeInputs.size());
            for (CountableIngredient ingredient : recipeInputs) {
                List<ItemStack> ingredientValues = Arrays.stream(ingredient.getIngredient().getMatchingStacks())
                        .map(ItemStack::copy)
                        .sorted(OreDictUnifier.getItemStackComparator())
                        .collect(Collectors.toList());
                ingredientValues.forEach(stack -> {
                    if (ingredient.getCount() == 0) {
                        notConsumedInput.add(stack);
                        stack.setCount(1);
                    } else stack.setCount(ingredient.getCount());
                });
                matchingInputs.add(ingredientValues);
            }
            ingredients.setInputLists(VanillaTypes.ITEM, matchingInputs);
        }

        if (!recipe.getFluidInputs().isEmpty()) {
            List<FluidStack> recipeInputs = recipe.getFluidInputs()
                    .stream().map(FluidStack::copy)
                    .collect(Collectors.toList());
            recipeInputs.forEach(stack -> {
                if (stack.amount == 0) {
                    notConsumedFluidInput.add(stack);
                    stack.amount = 1;
                }
            });
            ingredients.setInputs(VanillaTypes.FLUID, recipeInputs);
        }

        if (!recipe.getOutputs().isEmpty() || !recipe.getChancedOutputs().isEmpty()) {
            List<ItemStack> recipeOutputs = recipe.getOutputs()
                    .stream().map(ItemStack::copy).collect(Collectors.toList());
            List<ChanceEntry> chancedOutputs = recipe.getChancedOutputs();
            for (ChanceEntry chancedEntry : chancedOutputs) {
                ItemStack chancedStack = chancedEntry.getItemStack();
                chanceOutput.put(chancedStack, chancedEntry);
                recipeOutputs.add(chancedStack);
            }

            recipeOutputs.sort(Comparator.comparingInt(stack -> {
                ChanceEntry chanceEntry = chanceOutput.get(stack);
                if (chanceEntry == null)
                    return 0;
                return chanceEntry.getChance();
            }));
            ingredients.setOutputs(VanillaTypes.ITEM, recipeOutputs);
        }

        if (!recipe.getFluidOutputs().isEmpty()) {
            List<FluidStack> recipeOutputs = recipe.getFluidOutputs()
                    .stream().map(FluidStack::copy).collect(Collectors.toList());
            ingredients.setOutputs(VanillaTypes.FLUID, recipeOutputs);
        }
    }

    private void addItemTooltip(int slotIndex, boolean input, ItemStack itemStack, List<String> tooltip) {
        ChanceEntry entry;
        if(!input && (entry = chanceOutput.get(itemStack)) != null)
            addChancedOutputsText(entry, tooltip);
        if(input && notConsumedInput.contains(itemStack))
            tooltip.add(I18n.format("gregtech.recipe.not_consumed"));
    }

    private void addFluidTooltip(int slotIndex, boolean input, FluidStack fluidStack, List<String> tooltip) {
        if(input && notConsumedFluidInput.contains(fluidStack))
            tooltip.add(I18n.format("gregtech.recipe.not_consumed"));
    }

    private void addChancedOutputsText(@NotNull ChanceEntry entry, List<String> tooltip) {
        double chance = entry.getChance() / 100.0;
        double boost = entry.getBoostPerTier() / 100.0;
        if(entry.getBoostPerTier() > 0)
            tooltip.add(I18n.format("gregtech.recipe.chance", chance, boost));
        else
            tooltip.add(I18n.format("gregtech.recipe.fixed_chance", chance));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int yPosition = recipeHeight - getPropertyListHeight();
        long absEUt = Math.abs((long) recipe.getEUt());

        String totalText = I18n.format("gregtech.recipe.total", absEUt * recipe.getDuration());

        minecraft.fontRenderer.drawString(totalText, 0, yPosition, 0x111111);
        minecraft.fontRenderer.drawString(getEUText(recipe, absEUt), 0, yPosition += LINE_HEIGHT, 0x111111);
        minecraft.fontRenderer.drawString(getDurationText(recipe.getDuration()), 0, yPosition += LINE_HEIGHT, 0x111111);
        // change this method to return updated yPosition if you want to add elements after it
        drawProperties(recipe, minecraft, yPosition);
    }

    private String getEUText(Recipe recipe, long absEUt) {
        String tierName = JEIHelpers.getMinTierForVoltage(recipe.getEUt());
        if(recipe.getEUt() >= 0)
            return I18n.format("gregtech.recipe.eu", absEUt, tierName);
        else
            return I18n.format("gregtech.recipe.eu_inverted", absEUt, tierName);
    }

    private void drawProperties(Recipe recipe, Minecraft minecraft, int yPosition) {
        for (Map.Entry<RecipeProperty<?>, Object> propertyEntry : recipe.getRecipePropertyStorage().getRecipeProperties()) {
            if(!propertyEntry.getKey().isHidden()) {
                propertyEntry.getKey().drawInfo(minecraft, 0, yPosition += LINE_HEIGHT, 0x111111, propertyEntry.getValue());
            }
        }
    }

    private int getPropertyListHeight() {
        return (recipe.getRecipePropertyStorage().getSize() + 3) * LINE_HEIGHT;
    }

}
