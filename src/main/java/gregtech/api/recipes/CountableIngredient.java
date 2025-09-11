package gregtech.api.recipes;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.LookupBlock;
import gregtech.loaders.recipe.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

import java.util.Arrays;
import java.util.Objects;

public class CountableIngredient {

    public static CountableIngredient from(ItemStack stack) {
        return new CountableIngredient(Ingredient.fromStacks(stack), stack.getCount());
    }

    public static CountableIngredient from(ItemStack stack, int amount) {
        return new CountableIngredient(Ingredient.fromStacks(stack), amount);
    }

    public static CountableIngredient from(String oredict) {
        return new CountableIngredient(new OreIngredient(oredict), 1);
    }

    public static CountableIngredient from(String oredict, int count) {
        return new CountableIngredient(new OreIngredient(oredict), count);
    }

    public static CountableIngredient from(OrePrefix prefix, Material material) {
        return from(prefix, material, 1);
    }

    public static CountableIngredient from(OrePrefix prefix, Material material, int count) {
        return new CountableIngredient(new OreIngredient(new UnificationEntry(prefix, material).toString()), count);
    }

    /** @throws java.lang.NullPointerException if the UnificationEntry's material is {@code null} */
    public static CountableIngredient from(UnificationEntry entry, int count) {
        if(entry.material == null)
            throw new NullPointerException("NULL material is not permitted here");
        return from(entry.orePrefix, entry.material, count);
    }

    /** @throws java.lang.NullPointerException if the UnificationEntry's material is {@code null} */
    public static CountableIngredient from(UnificationEntry entry) {
        return from(entry, 1);
    }

    /** @throws java.lang.IllegalArgumentException for unsupported types */
    public static <T> CountableIngredient from(int tier, Component<T> component, int count) {
        T resolved = component.getIngredient(tier);
        if(resolved instanceof UnificationEntry ue)
            return from(ue, count);
        if(resolved instanceof ItemStack stack)
            return from(stack, count);
        if(resolved instanceof String str)
            return from(str, count);
        if(resolved instanceof MetaItem<?>.MetaValueItem meta)
            return from(meta.getStackForm(count));
        if(resolved instanceof LookupBlock<?> block)
            return from(block.getStack(), count);

        throw new IllegalArgumentException("unsupported type");
    }

    private Ingredient ingredient;
    private int count;

    public CountableIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountableIngredient that = (CountableIngredient) o;
        return count == that.count &&
            Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, count);
    }

    @Override
    public String toString() {
        return "CountableIngredient{" +
            "ingredient=" + Arrays.toString(ingredient.getMatchingStacks()) +
            ", count=" + count +
            '}';
    }
}
