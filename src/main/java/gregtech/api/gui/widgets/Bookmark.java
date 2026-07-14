package gregtech.api.gui.widgets;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Loads HEI's {@code BookmarkItem} class via reflection and encapsulates interactions with it to maintain
 * backwards-compatibility with JEI while supporting click-and-drag from the HEI bookmarks panel.
 */
public class Bookmark {
    private static final Class<?> bookmarkItemClass;
    private static final Field bookmarkIngredientField;
    private static final Method bookmarkDisplayAmount;

    static {
        Class<?> bmTemp;
        Field ingTmp;
        Method daTmp;

        try {
            bmTemp = Class.forName("mezz.jei.bookmarks.BookmarkItem", false, Bookmark.class.getClassLoader());
        } catch(ClassNotFoundException e) {
            bmTemp = null;
        }
        bookmarkItemClass = bmTemp;
        if(bookmarkItemClass != null) {
            try {
                ingTmp = bookmarkItemClass.getDeclaredField("ingredient");
            } catch(NoSuchFieldException e) {
                ingTmp = null;
            }
            try {
                daTmp = bookmarkItemClass.getDeclaredMethod("getDisplayAmount");
            } catch(NoSuchMethodException e) {
                daTmp = null;
            }
        } else {
            ingTmp = null;
            daTmp = null;
        }
        bookmarkIngredientField = ingTmp;
        bookmarkDisplayAmount = daTmp;

    }

    private final Object bookmark;
    private Bookmark(@NotNull Object in) {
        this.bookmark = in;
    }

    /** @throws RuntimeException if {@code in} is not a BookmarkItem */
    @NotNull
    public static Bookmark wrap(@NotNull Object in) {
        if(isBookmark(in))
            return new Bookmark(in);
        throw new RuntimeException("Skill Issue");
    }

    public static void ifIsItemBookmark(Object in, Consumer<Bookmark> handler) {
        if(isItemBookmark(in))
            handler.accept(wrap(in));
    }

    /** @return {@code true} if {@code in} is a bookmark wrapping a FluidStack */
    @Contract(pure = true)
    public static boolean isFluidBookmark(Object in) {
        return (isBookmark(in) && getIngredient(in) instanceof FluidStack);
    }

    public void ifHasFluid(Consumer<FluidStack> fs) {
        if(hasFluid())
            fs.accept(getFluid());
    }

    public boolean hasFluid() {
        return getIngredient() instanceof FluidStack;
    }

    @Nullable
    public FluidStack getFluid() {
        return getIngredient() instanceof FluidStack stack ? stack : null;
    }

    @Nullable
    public static FluidStack getFluid(Object in) {
        return getIngredient(in) instanceof FluidStack stack ? stack : null;
    }

    /** @return {@code true} if {@code in} is a bookmark wrapping an ItemStack */
    @Contract(pure = true)
    public static boolean isItemBookmark(Object in) {
        return (isBookmark(in) && getIngredient(in) instanceof ItemStack);
    }

    public void ifHasItem(Consumer<ItemStack> is) {
        if(hasItem())
            is.accept(getItem());
    }

    public boolean hasItem() {
        return getIngredient() instanceof ItemStack;
    }

    @Nullable
    public ItemStack getItem() {
        return getIngredient() instanceof ItemStack stack ? stack : null;
    }

    @Nullable
    public static ItemStack getItem(Object in) {
        return getIngredient(in) instanceof ItemStack stack ? stack : null;
    }

    @Contract(pure = true)
    public static boolean isBookmark(Object in) {
        if(bookmarkItemClass != null)
            return bookmarkItemClass.isAssignableFrom(in.getClass());
        return false;
    }

    @Contract(pure = true)
    public static @Nullable Object getIngredient(Object o) {
        if(bookmarkIngredientField != null)
            try {
                return bookmarkIngredientField.get(o);
            } catch (Exception ignored) {}
        return null;
    }

    @Contract(pure = true)
    public static @Nullable Long getDisplayAmount(Object o) {
        if(bookmarkDisplayAmount != null)
            try {
                return (long) bookmarkDisplayAmount.invoke(o);
            } catch(Exception ignored) {}
        return null;
    }

    public Object getIngredient() {
        return getIngredient(bookmark);
    }

    public long getDisplayAmount() {
        Long amount = getDisplayAmount(bookmark);
        return amount != null ? amount : 0L;
    }

}
