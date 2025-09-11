package gregtech.api.recipes;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.Recipe.ChanceEntry;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.api.util.EnumValidationResult;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ValidationResult;
import gregtech.common.blocks.LookupBlock;
import gregtech.loaders.recipe.Component;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;
import java.util.function.Predicate;

import static gregtech.api.recipes.CountableIngredient.from;

/**
 * @see Recipe
 */

public abstract class RecipeBuilder<R extends RecipeBuilder<R>> {

    protected RecipeMap<R> recipeMap;

    protected List<CountableIngredient> inputs;
    protected NonNullList<ItemStack> outputs;
    protected List<ChanceEntry> chancedOutputs;

    protected List<FluidStack> fluidInputs;
    protected List<FluidStack> fluidOutputs;

    protected int duration, EUt;
    protected boolean hidden = false;

    protected EnumValidationResult recipeStatus = EnumValidationResult.VALID;

    protected RecipeBuilder() {
        this.inputs = NonNullList.create();
        this.outputs = NonNullList.create();
        this.chancedOutputs = new ArrayList<>();

        this.fluidInputs = new ArrayList<>(0);
        this.fluidOutputs = new ArrayList<>(0);
    }

    protected RecipeBuilder(Recipe recipe, RecipeMap<R> recipeMap) {
        this.recipeMap = recipeMap;
        this.inputs = NonNullList.create();
        this.inputs.addAll(recipe.getInputs());
        this.outputs = NonNullList.create();
        this.outputs.addAll(GTUtility.copyStackList(recipe.getOutputs()));
        this.chancedOutputs = new ArrayList<>(recipe.getChancedOutputs());

        this.fluidInputs = GTUtility.copyFluidList(recipe.getFluidInputs());
        this.fluidOutputs = GTUtility.copyFluidList(recipe.getFluidOutputs());

        this.duration = recipe.getDuration();
        this.EUt = recipe.getEUt();
        this.hidden = recipe.isHidden();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    protected RecipeBuilder(RecipeBuilder<R> recipeBuilder) {
        this.recipeMap = recipeBuilder.recipeMap;
        this.inputs = NonNullList.create();
        this.inputs.addAll(recipeBuilder.getInputs());
        this.outputs = NonNullList.create();
        this.outputs.addAll(GTUtility.copyStackList(recipeBuilder.getOutputs()));
        this.chancedOutputs = new ArrayList<>(recipeBuilder.chancedOutputs);

        this.fluidInputs = GTUtility.copyFluidList(recipeBuilder.getFluidInputs());
        this.fluidOutputs = GTUtility.copyFluidList(recipeBuilder.getFluidOutputs());
        this.duration = recipeBuilder.duration;
        this.EUt = recipeBuilder.EUt;
        this.hidden = recipeBuilder.hidden;
    }

    public boolean applyProperty(String key, Object value) {
        return false;
    }

    public boolean applyProperty(String key, ItemStack item) {
        return false;
    }

    public R inputs(ItemStack... inputs) {
        return inputs(Arrays.asList(inputs));
    }

    private static final Predicate<ItemStack> nullOrEmpty = stack -> stack == null || stack.isEmpty();

    public R inputs(Collection<ItemStack> inputs) {
        if (GTUtility.iterableContains(inputs, nullOrEmpty)) {
            GTLog.logger.error("Input cannot contain null or empty ItemStacks. Inputs: {}", inputs);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }

        for(ItemStack stack : inputs)
            if (!nullOrEmpty.test(stack))
                this.inputs.add(from(stack));

        return (R) this;
    }

    public R input(String oredict, int count) {
        return inputs(from(oredict, count));
    }

    public R input(OrePrefix orePrefix, Material material) {
        return inputs(from(orePrefix, material, 1));
    }

    public R input(OrePrefix orePrefix, Material material, int count) {
        return inputs(from(orePrefix, material, count));
    }

    public R input(UnificationEntry entry) {
        return input(entry, 1);
    }

    public R input(UnificationEntry entry, int count) {
        return inputs(from(entry, count));
    }

    public <T extends Enum<T> & IStringSerializable> R input(LookupBlock<T> block, int count) {
        return inputs(block.getStack(count));
    }

    public <T extends Enum<T> & IStringSerializable> R input(LookupBlock<T> block) {
        return input(block, 1);
    }

    public R input(MetaItem<?>.MetaValueItem item, int count) {
        return inputs(item.getStackForm(count));
    }

    public R input(MetaItem<?>.MetaValueItem item) {
        return inputs(item.getStackForm());
    }

    public R input(Item item) {
        return input(item, 1);
    }

    public R input(Item item, int count) {
        return inputs(new ItemStack(item, count));
    }

    public R input(Item item, int count, int meta) {
        return inputs(new ItemStack(item, count, meta));
    }

    public R input(Item item, int count, boolean wild) {
        return inputs(new ItemStack(item, count, OreDictionary.WILDCARD_VALUE));
    }

    public R input(Block item) {
        return input(item, 1);
    }

    public R input(Block item, int count) {
        return inputs(new ItemStack(item, count));
    }

    public R input(Block item, int count, boolean wild) {
        return inputs(new ItemStack(item, count, OreDictionary.WILDCARD_VALUE));
    }

    /**
     * Resolves a single {@link Component} and adds it to the inputs with a count of 1.
     * @throws IllegalArgumentException if the resolved type isn't supported.
     */
    public <T> R input(int tier, Component<T> component) {
        return input(tier, component, 1);
    }

    /**
     * Resolves a single {@link Component} and adds it to the inputs with the specified count.
     * @throws IllegalArgumentException if the resolved type isn't supported.
     */
    public R input(int tier, Component<?> component, int count) {
        return inputs(from(tier, component, count));
    }

    /**
     * For {@link Component}, which are resolved before adding to inputs.
     * @throws IllegalArgumentException if an input type isn't supported
     */
    public R inputs(int tier, Component<?>... components) {
        List<CountableIngredient> ingredients = new ArrayList<>(components.length);
        for(var component : components)
            ingredients.add(from(tier, component, 1));
        return inputs(ingredients.toArray(new CountableIngredient[0]));
    }

    public R inputs(CountableIngredient... inputs) {
        List<CountableIngredient> ingredients = new ArrayList<>();
        for (CountableIngredient input : inputs) {
            if (input.getCount() < 0) {
                GTLog.logger.error("Count cannot be less than 0. Actual: {}.", input.getCount());
                GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            } else {
                ingredients.add(input);
            }
        }

        return inputsIngredients(ingredients);
    }

    public R inputsIngredients(Collection<CountableIngredient> ingredients) {
        this.inputs.addAll(ingredients);
        return (R) this;
    }

    public R notConsumable(ItemStack itemStack) {
        return inputs(from(itemStack, 0));
    }

    public R notConsumable(OrePrefix prefix, Material material) {
        return input(prefix, material, 0);
    }

    public R notConsumable(Ingredient ingredient) {
        return inputs(new CountableIngredient(ingredient, 0));
    }

    public R notConsumable(MetaItem<?>.MetaValueItem item) {
        return inputs(from(item.getStackForm(), 0));
    }

    public R notConsumable(FluidMaterial fluidMat) {
        return fluidInputs(new FluidStack(fluidMat.getFluid(1), 0));
    }

    public R notConsumable(FluidStack fluidStack) {
        return fluidInputs(new FluidStack(fluidStack, 0));
    }

    public R output(OrePrefix orePrefix, Material material) {
        return outputs(OreDictUnifier.get(orePrefix, material, 1));
    }

    public R output(OrePrefix orePrefix, Material material, int count) {
        return outputs(OreDictUnifier.get(orePrefix, material, count));
    }

    public R output(Item item) {
        return output(item, 1);
    }

    public R output(Item item, int count) {
        return outputs(new ItemStack(item, count));
    }

    public R output(Item item, int count, int meta) {
        return outputs(new ItemStack(item, count, meta));
    }

    public R output(Block item) {
        return output(item, 1);
    }

    public R output(Block item, int count) {
        return outputs(new ItemStack(item, count));
    }

    public R output(LookupBlock<?> block, int count) {
        return outputs(block.getStack(count));
    }

    public R output(LookupBlock<?> block) {
        return outputs(block.getStack());
    }

    public R output(MetaItem<?>.MetaValueItem item, int count) {
        return outputs(item.getStackForm(count));
    }

    public R output(MetaItem<?>.MetaValueItem item) {
        return outputs(item.getStackForm());
    }

    public R output(MetaTileEntity entity, int count) {
        return outputs(entity.getStackForm(count));
    }

    public R output(MetaTileEntity entity) {
        return outputs(entity.getStackForm());
    }

    /**
     * Resolves a single tiered {@link Component} to an ItemStack and adds it to the outputs.
     * @throws IllegalArgumentException if the type is not supported.
     */
    public <T> R output(int tier, Component<T> component) {
        T resolved = component.getIngredient(tier);
        if(resolved instanceof ItemStack stack)
            return outputs(stack);
        if(resolved instanceof MetaItem<?>.MetaValueItem item)
            return outputs(item.getStackForm());

        throw new IllegalArgumentException("unsupported type");
    }

    public R outputs(ItemStack... outputs) {
        return outputs(Arrays.asList(outputs));
    }

    public R outputs(Collection<ItemStack> outputs) {
        outputs = new ArrayList<>(outputs);
        outputs.removeIf(stack -> stack == null || stack.isEmpty());
        this.outputs.addAll(outputs);
        return (R) this;
    }

    public R fluidInputs(FluidStack... inputs) {
        return fluidInputs(Arrays.asList(inputs));
    }

    public R fluidInputs(Collection<FluidStack> inputs) {
        if (inputs.contains(null)) {
            GTLog.logger.error("Fluid input cannot contain null FluidStacks. Inputs: {}", inputs);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.fluidInputs.addAll(inputs);
        this.fluidInputs.removeIf(Objects::isNull);
        return (R) this;
    }

    public R fluidOutputs(FluidStack... outputs) {
        return fluidOutputs(Arrays.asList(outputs));
    }

    public R fluidOutputs(Collection<FluidStack> outputs) {
        outputs = new ArrayList<>(outputs);
        outputs.removeIf(Objects::isNull);
        this.fluidOutputs.addAll(outputs);
        return (R) this;
    }

    public R chancedOutput(ItemStack stack, int chance, int tierChanceBoost) {
        if (stack == null || stack.isEmpty()) {
            return (R) this;
        }
        if (0 >= chance || chance > Recipe.getMaxChancedValue()) {
            GTLog.logger.error("Chance cannot be less or equal to 0 or more than {}. Actual: {}.", Recipe.getMaxChancedValue(), chance);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
            return (R) this;
        }
        this.chancedOutputs.add(new ChanceEntry(stack.copy(), chance, tierChanceBoost));
        return (R) this;
    }

    public R duration(int duration) {
        this.duration = duration;
        return (R) this;
    }

    public R EUt(int EUt) {
        this.EUt = EUt;
        return (R) this;
    }

    public R hidden() {
        this.hidden = true;
        return (R) this;
    }

    public R setRecipeMap(RecipeMap<R> recipeMap) {
        this.recipeMap = recipeMap;
        return (R) this;
    }

    public abstract R copy();

    protected EnumValidationResult finalizeAndValidate() {
        return validate();
    }

    public abstract ValidationResult<Recipe> build();

    protected EnumValidationResult validate() {
        if (EUt == 0) {
            GTLog.logger.error("EU/t cannot be equal to 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        if (duration <= 0) {
            GTLog.logger.error("Duration cannot be less or equal to 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        if (recipeStatus == EnumValidationResult.INVALID) {
            GTLog.logger.error("Invalid recipe, read the errors above: {}", this);
        }
        return recipeStatus;
    }

    public void buildAndRegister() {
        ValidationResult<Recipe> validationResult = build();
        recipeMap.addRecipe(validationResult);
    }

    ///////////////////
    //    Getters    //
    ///////////////////

    public List<CountableIngredient> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public List<ChanceEntry> getChancedOutputs() {
        return chancedOutputs;
    }

    public List<FluidStack> getFluidInputs() {
        return fluidInputs;
    }

    public List<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("recipeMap", recipeMap)
            .append("inputs", inputs)
            .append("outputs", outputs)
            .append("chancedOutputs", chancedOutputs)
            .append("fluidInputs", fluidInputs)
            .append("fluidOutputs", fluidOutputs)
            .append("duration", duration)
            .append("EUt", EUt)
            .append("hidden", hidden)
            .append("recipeStatus", recipeStatus)
            .toString();
    }
}
