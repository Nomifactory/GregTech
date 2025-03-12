package gregtech.api.recipes;

import com.google.common.collect.ImmutableList;
import gregtech.api.GTValues;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.recipes.recipeproperties.RecipeProperty;
import gregtech.api.recipes.recipeproperties.RecipePropertyStorage;
import gregtech.api.util.GTUtility;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Arrays.binarySearch;
import static net.minecraft.util.math.MathHelper.abs;

/**
 * Class that represent machine recipe.<p>
 * <p>
 * Recipes are created using {@link RecipeBuilder} or its subclasses in builder-alike pattern. To get RecipeBuilder use {@link RecipeMap#recipeBuilder()}.<p>
 * <p>
 * Example:
 * RecipeMap.POLARIZER_RECIPES.recipeBuilder().inputs(new ItemStack(Items.APPLE)).outputs(new ItemStack(Items.GOLDEN_APPLE)).duration(256).EUt(480).buildAndRegister();<p>
 * This will create and register Polarizer recipe with Apple as input and Golden apple as output, duration - 256 ticks and energy consumption of 480 EU/t.<p>
 * To get example for particular RecipeMap see {@link RecipeMap}<p>
 * <p>
 * Recipes are immutable.
 */
public class Recipe {

    public static int getMaxChancedValue() {
        return 10000;
    }

    public static String formatChanceValue(int outputChance) {
        return String.format("%.2f", outputChance / (getMaxChancedValue() * 1.0) * 100);
    }

    private final List<CountableIngredient> inputs;
    private final NonNullList<ItemStack> outputs;

    /**
     * A chance of 10000 equals 100%
     */
    private final List<ChanceEntry> chancedOutputs;
    private final List<FluidStack> fluidInputs;
    private final List<FluidStack> fluidOutputs;

    private final int duration;

    /**
     * if > 0 means EU/t consumed, if < 0 - produced
     */
    private final int EUt;

    /**
     * GTValues ordinal for this Recipe's base power tier
     */
    private int baseTier = -1;

    /**
     * If this Recipe is hidden from JEI
     */
    private final boolean hidden;

    private final RecipePropertyStorage recipePropertyStorage;

    public Recipe(List<CountableIngredient> inputs, List<ItemStack> outputs, List<ChanceEntry> chancedOutputs,
                  List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs,
                  int duration, int EUt, boolean hidden) {
        this.recipePropertyStorage = new RecipePropertyStorage();
        this.inputs = NonNullList.create();
        this.inputs.addAll(inputs);
        this.outputs = NonNullList.create();
        this.outputs.addAll(outputs);
        this.chancedOutputs = new ArrayList<>(chancedOutputs);
        this.fluidInputs = ImmutableList.copyOf(fluidInputs);
        this.fluidOutputs = ImmutableList.copyOf(fluidOutputs);
        this.duration = duration;
        this.EUt = EUt;
        this.hidden = hidden;
        //sort input elements in descending order (i.e not consumables inputs are last)
        this.inputs.sort(Comparator.comparing(CountableIngredient::getCount).reversed());
    }

    /**
     * @deprecated use {@link #Recipe(List inputs, List outputs, List chancedOutputs, List fluidInputs,
     * List fluidOutputs, int duration, int EUt, boolean hidden)} instead
     * Recipe properties are added by {@link RecipePropertyStorage#store(Map recipeProperties)}
     * on {@link #getRecipePropertyStorage()}
     */
    @Deprecated
    public Recipe(List<CountableIngredient> inputs, List<ItemStack> outputs, List<ChanceEntry> chancedOutputs,
                  List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs,
                  Map<String, Object> recipeProperties, int duration, int EUt, boolean hidden) {
        this(inputs, outputs, chancedOutputs, fluidInputs, fluidOutputs, duration, EUt, hidden);
        recipePropertyStorage.storeOldFormat(recipeProperties);
    }

    public final boolean matches(boolean consumeIfSuccessful, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs, MatchingMode matchingMode) {
        return matches(consumeIfSuccessful, GTUtility.itemHandlerToList(inputs), GTUtility.fluidHandlerToList(fluidInputs), matchingMode);
    }

    public final boolean matches(boolean consumeIfSuccessful, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
        return matches(consumeIfSuccessful, GTUtility.itemHandlerToList(inputs), GTUtility.fluidHandlerToList(fluidInputs), MatchingMode.DEFAULT);
    }

    public boolean matches(boolean consumeIfSuccessful, List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        return matches(consumeIfSuccessful, inputs, fluidInputs, MatchingMode.DEFAULT);
    }

    /**
     * This methods aim to verify if the current recipe matches the given inputs according to matchingMode mode.
     *
     * @param consumeIfSuccessful if true and matchingMode is equal to {@link MatchingMode#DEFAULT} will consume the inputs of the recipe.
     * @param inputs              Items input or Collections.emptyList() if none.
     * @param fluidInputs         Fluids input or Collections.emptyList() if none.
     * @param matchingMode        How this method should check if inputs matches according to {@link MatchingMode} description.
     * @return true if the recipe matches the given inputs false otherwise.
     */
    public boolean matches(boolean consumeIfSuccessful, List<ItemStack> inputs, List<FluidStack> fluidInputs, MatchingMode matchingMode) {
        Pair<Boolean, Integer[]> fluids = null;
        Pair<Boolean, Integer[]> items = null;

        if (matchingMode == MatchingMode.IGNORE_FLUIDS) {
            if (getInputs().isEmpty()) {
                return false;
            }
        } else {
            fluids = matchesFluid(fluidInputs);
            if (!fluids.getKey()) {
                return false;
            }
        }

        if (matchingMode == MatchingMode.IGNORE_ITEMS) {
            if (getFluidInputs().isEmpty()) {
                return false;
            }
        } else {
            items = matchesItems(inputs);
            if (!items.getKey()) {
                return false;
            }
        }

        if (consumeIfSuccessful && matchingMode == MatchingMode.DEFAULT) {
            Integer[] fluidAmountInTank = fluids.getValue();
            Integer[] itemAmountInSlot = items.getValue();
            for (int i = 0; i < fluidAmountInTank.length; i++) {
                FluidStack fluidStack = fluidInputs.get(i);
                int fluidAmount = fluidAmountInTank[i];
                if (fluidStack == null || fluidStack.amount == fluidAmount)
                    continue;
                fluidStack.amount = fluidAmount;
                if (fluidStack.amount == 0)
                    fluidInputs.set(i, null);
            }
            for (int i = 0; i < itemAmountInSlot.length; i++) {
                ItemStack itemInSlot = inputs.get(i);
                int itemAmount = itemAmountInSlot[i];
                if (itemInSlot.isEmpty() || itemInSlot.getCount() == itemAmount)
                    continue;
                itemInSlot.setCount(itemAmountInSlot[i]);
            }
        }

        return true;
    }

    private Pair<Boolean, Integer[]> matchesItems(List<ItemStack> inputs) {
        Integer[] itemAmountInSlot = new Integer[inputs.size()];

        for (int i = 0; i < itemAmountInSlot.length; i++) {
            ItemStack itemInSlot = inputs.get(i);
            itemAmountInSlot[i] = itemInSlot.isEmpty() ? 0 : itemInSlot.getCount();
        }

        for (CountableIngredient ingredient : this.inputs) {
            int ingredientAmount = ingredient.getCount();
            boolean isNotConsumed = false;
            if (ingredientAmount == 0) {
                ingredientAmount = 1;
                isNotConsumed = true;
            }
            for (int i = 0; i < inputs.size(); i++) {
                ItemStack inputStack = inputs.get(i);
                if (inputStack.isEmpty() || !ingredient.getIngredient().apply(inputStack))
                    continue;
                int itemAmountToConsume = Math.min(itemAmountInSlot[i], ingredientAmount);
                ingredientAmount -= itemAmountToConsume;
                if (!isNotConsumed) itemAmountInSlot[i] -= itemAmountToConsume;
                if (ingredientAmount == 0) break;
            }
            if (ingredientAmount > 0)
                return Pair.of(false, itemAmountInSlot);
        }

        return Pair.of(true, itemAmountInSlot);
    }

    private Pair<Boolean, Integer[]> matchesFluid(List<FluidStack> fluidInputs) {
        Integer[] fluidAmountInTank = new Integer[fluidInputs.size()];

        for (int i = 0; i < fluidAmountInTank.length; i++) {
            FluidStack fluidInTank = fluidInputs.get(i);
            fluidAmountInTank[i] = fluidInTank == null ? 0 : fluidInTank.amount;
        }

        for (FluidStack fluid : this.fluidInputs) {
            int fluidAmount = fluid.amount;
            boolean isNotConsumed = false;
            if (fluidAmount == 0) {
                fluidAmount = 1;
                isNotConsumed = true;
            }
            for (int i = 0; i < fluidInputs.size(); i++) {
                FluidStack tankFluid = fluidInputs.get(i);
                if (tankFluid == null || !tankFluid.isFluidEqual(fluid))
                    continue;
                int fluidAmountToConsume = Math.min(fluidAmountInTank[i], fluidAmount);
                fluidAmount -= fluidAmountToConsume;
                if (!isNotConsumed) fluidAmountInTank[i] -= fluidAmountToConsume;
                if (fluidAmount == 0) break;
            }
            if (fluidAmount > 0)
                return Pair.of(false, fluidAmountInTank);
        }
        return Pair.of(true, fluidAmountInTank);
    }

    ///////////////////
    //    Getters    //
    ///////////////////

    public List<CountableIngredient> getInputs() {
        return inputs;
    }

    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }

    /**
     * Computes real outputs of a recipe, truncated to a maximum number of output slots.
     *
     * @param maxOutputSlots the number of output slots to consider
     * @param random the Random to use for chanced recipe outputs
     * @param overclocks the number of overclocks for applying tiered bonuses
     * @return the outputs of the current iteration of running the recipe
     */
    public List<ItemStack> getResultItemOutputs(int maxOutputSlots, @NotNull Random random, int overclocks) {
        assert maxOutputSlots >= 0;
        assert overclocks >= 0;

        // Nothing to return if there are no output slots
        if(maxOutputSlots == 0)
            return Collections.emptyList();

        // Get the non-chanced outputs
        ArrayList<ItemStack> outputs = new ArrayList<>(GTUtility.copyStackList(getOutputs()));

        // If there's enough fixed outputs to reach the max, return as many as will fit.
        if (outputs.size() >= maxOutputSlots)
            return outputs.subList(0, maxOutputSlots);

        // See how many chanced outputs we have room for
        int chancedSlots = maxOutputSlots - outputs.size();

        // compute the chanced outputs
        List<Pair<ItemStack, Integer>> chancedOutputs = getChancedRecipeOutputsAtTier(overclocks);

        // Roll each chanced output to see if it is actually produced
        for(var chancedOutput : chancedOutputs) {
            // stop if no space left
            if(chancedSlots == 0) break;

            if (random.nextInt(Recipe.getMaxChancedValue()) <= chancedOutput.getValue()) {
                outputs.add(chancedOutput.getKey());
                chancedSlots--;
            }
        }
        return outputs;
    }

    /** Cache last chance-at-tier result to avoid recomputing if called repeatedly */
    @Nullable
    private Pair<List<Pair<ItemStack, Integer>>, Integer> cachedChance = null;

    /**
     * @param overclocks the number of tiers above the base recipe tier
     * @return a list containing all chanced outputs paired with their chance value at the given overclock tier
     */
    public List<Pair<ItemStack, Integer>> getChancedRecipeOutputsAtTier(int overclocks) {
        assert overclocks >= 0;

        if(cachedChance != null && cachedChance.getValue() == overclocks)
            return cachedChance.getLeft();

        List<Pair<ItemStack, Integer>> result = new ArrayList<>();
        var cf = RecipeMap.getChanceFunction();
        for(ChanceEntry entry : getChancedOutputs()) {
            int computedChance = cf.chanceFor(entry.getChance(), entry.getBoostPerTier(), overclocks);
            result.add(Pair.of(entry.getItemStack().copy(), Math.min(Recipe.getMaxChancedValue(), computedChance)));
        }

        cachedChance = Pair.of(result, overclocks);

        return result;
    }

    /**
     * @param maxOutputSlots the maximum number of outputs
     * @return a list of all guaranteed and chanced items produced by this recipe,
     * truncated if necessary to contain at most {@code maxOutputSlots} items.
     */
    public List<ItemStack> getAllItemOutputs(int maxOutputSlots) {
        List<ItemStack> outputs = new ArrayList<>(GTUtility.copyStackList(getOutputs()));
        for(var co : getChancedOutputs())
            outputs.add(co.getItemStack());
        if (outputs.size() > maxOutputSlots)
            outputs = outputs.subList(0, maxOutputSlots);
        return outputs;
    }

    public List<ChanceEntry> getChancedOutputs() {
        return chancedOutputs;
    }

    public List<FluidStack> getFluidInputs() {
        return fluidInputs;
    }

    public boolean hasInputFluid(FluidStack fluid) {
        for (FluidStack fluidStack : fluidInputs) {
            if (fluidStack.isFluidEqual(fluid)) {
                return true;
            }
        }
        return false;
    }

    public List<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    public int getDuration() {
        return duration;
    }

    public int getEUt() {
        return EUt;
    }

    /**
     * @return The tier of machine required to run this recipe, as defined by {@link GTValues}
     */
    public int getBaseTier() {

        // Lazily compute and store for later recall
        if(baseTier == -1) {
            int tier = binarySearch(GTValues.V, abs(EUt));
            if(tier < 0)
                tier = abs(tier + 1);
            baseTier = tier;
        }

        return baseTier;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean hasValidInputsForDisplay() {
        boolean hasValidInputs = true;
        for (CountableIngredient ingredient : inputs) {
            ItemStack[] matchingItems = ingredient.getIngredient().getMatchingStacks();
            hasValidInputs &= Arrays.stream(matchingItems).anyMatch(s -> !s.isEmpty());
        }
        return hasValidInputs;
    }

    /**
     * Retrieve a property or fallback value from the property store
     * @param property the desired property
     * @param defaultValue fallback value
     * @param <T> type of the property
     * @return the requested property's value, or the fallback value if a value isn't available.
     */
    public <T> T getProperty(RecipeProperty<T> property, T defaultValue) {
        return recipePropertyStorage.getRecipePropertyValue(property, defaultValue);
    }

    //region RecipeProperties

    /**
     * Provides full access to {@link RecipePropertyStorage} for this Recipe
     * @return RecipePropertyStorage
     */
    public RecipePropertyStorage getRecipePropertyStorage(){
        return recipePropertyStorage;
    }

    /**
     * @deprecated use {@link RecipePropertyStorage#getRecipePropertyValue(RecipeProperty recipeProperty, Object defaultValue)}
     * on {@link #getRecipePropertyStorage()}
     */
    @Deprecated
    public boolean getBooleanProperty(String key) {
        return getProperty(key);
    }

    /**
     * @deprecated use {@link RecipePropertyStorage#getRecipePropertyValue(RecipeProperty recipeProperty, Object defaultValue)}
     * on {@link #getRecipePropertyStorage()}
     */
    @Deprecated
    public int getIntegerProperty(String key) {
        return getProperty(key);
    }

    /**
     * @deprecated use {@link RecipePropertyStorage#getRecipePropertyValue(RecipeProperty recipeProperty, Object defaultValue)}
     * on {@link #getRecipePropertyStorage()}
     */
    @Deprecated
    public String getStringProperty(String key) {
        return getProperty(key);
    }

    /**
     * @deprecated use {@link RecipePropertyStorage#getRecipePropertyValue(RecipeProperty recipeProperty, Object defaultValue)}
     * on {@link #getRecipePropertyStorage()}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        AbstractMap.SimpleEntry<RecipeProperty<?>, Object> recipePropertySet = getRecipePropertyStorage().getRecipeProperty(key);

        if (recipePropertySet == null) {
            throw new IllegalArgumentException();
        }

        return (T) recipePropertySet.getKey().castValue(recipePropertySet.getValue());
    }

    //endregion RecipeProperties

    public static class ChanceEntry {
        private final ItemStack itemStack;
        private final int chance;
        private final int boostPerTier;

        public ChanceEntry(ItemStack itemStack, int chance, int boostPerTier) {
            this.itemStack = itemStack.copy();
            this.chance = chance;
            this.boostPerTier = boostPerTier;
        }

        public ItemStack getItemStack() {
            return itemStack.copy();
        }

        public int getChance() {
            return chance;
        }

        public int getBoostPerTier() {
            return boostPerTier;
        }
    }
}
