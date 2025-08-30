package gregtech.loaders.recipe;

@FunctionalInterface
public interface Component<T> {
    T getIngredient(int tier);
}