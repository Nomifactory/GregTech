package gregtech.common.covers.filter;

import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.*;
import gregtech.api.unification.stack.ItemAndMetadata;
import gregtech.api.util.ItemStackKey;
import gregtech.common.metatileentities.electric.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SmartItemFilter extends ItemFilter {

    private SmartFilteringMode filteringMode = SmartFilteringMode.ELECTROLYZER;
    private SmartMatchingMode matchingMode = SmartMatchingMode.DEFAULT;

    public SmartFilteringMode getFilteringMode() {
        return filteringMode;
    }

    public void setFilteringMode(SmartFilteringMode filteringMode) {
        this.filteringMode = filteringMode;
        markDirty();
    }

    public SmartMatchingMode getMatchingMode() {
        return matchingMode;
    }

    public void setMatchingMode(SmartMatchingMode matchingMode) {
        filteringMode.transferStackSizesCache.clear();
        this.matchingMode = matchingMode;
        markDirty();
    }

    @Override
    public int getSlotTransferLimit(Object matchSlot, Set<ItemStackKey> matchedStacks, int globalTransferLimit) {
        ItemAndMetadataAndStackSize itemAndMetadata = (ItemAndMetadataAndStackSize) matchSlot;
        return itemAndMetadata.transferStackSize;
    }

    @Override
    public Object matchItemStack(ItemStack itemStack) {
        return matchItemStack(itemStack, null);
    }

    public Object matchItemStackWithCache(ItemStack itemStack) {
        ItemAndMetadata itemAndMetadata = new ItemAndMetadata(itemStack);
        Integer cachedTransferRateValue = filteringMode.transferStackSizesCache.get(itemAndMetadata);

        if (cachedTransferRateValue == null) {
            ItemStack infinitelyBigStack = itemStack.copy();
            infinitelyBigStack.setCount(Integer.MAX_VALUE);

            Recipe recipe = filteringMode.recipeMap.findRecipe(Long.MAX_VALUE, Collections.singletonList(infinitelyBigStack), Collections.emptyList(), Integer.MAX_VALUE, matchingMode.matchingMode);

            if (recipe == null) {
                filteringMode.transferStackSizesCache.put(itemAndMetadata, 0);
                cachedTransferRateValue = 0;
            } else {
                CountableIngredient inputIngredient = recipe.getInputs().iterator().next();
                filteringMode.transferStackSizesCache.put(itemAndMetadata, inputIngredient.getCount());
                cachedTransferRateValue = inputIngredient.getCount();
            }
        }

        if (cachedTransferRateValue == 0) {
            return null;
        }
        return new ItemAndMetadataAndStackSize(itemAndMetadata, cachedTransferRateValue);
    }

    public Object matchItemStack(ItemStack itemStack, MetaTileEntity targetEntity) {
        // Use the logic with caching if ignoring fluids since changes in tanks won't matter
        if(matchingMode == SmartMatchingMode.IGNORE_FLUID)
            return matchItemStackWithCache(itemStack);

        ItemAndMetadata itemAndMetadata = new ItemAndMetadata(itemStack);
        ItemStack infinitelyBigStack = itemStack.copy();
        infinitelyBigStack.setCount(Integer.MAX_VALUE);

        List<FluidStack> inputFluids = Collections.emptyList();
        if(targetEntity != null) {
            inputFluids = new ArrayList<>();
            /* Multiblock parts need to proxy to the registered controller to get its input fluids.
               Unfortunately they only know the most recently registered controller, so this will be a bit wonky
               with parts shared by multiple controllers if they aren't also sharing the same fluid inputs.

               If it's not attached to a controller, there's no way to know what fluids are present.

               This is also limited to RecipeMapMultiblockController multiblocks, since it's the only kind that
               lets you see its input fluids.
            */
            if (targetEntity instanceof MetaTileEntityMultiblockPart part && part.isAttachedToMultiBlock()) {
                if (part.getController() instanceof RecipeMapMultiblockController rmmc)
                    for(var tank : rmmc.getInputFluidInventory())
                        inputFluids.add(tank.getFluid());
            // All other MTEs we can just directly get the input tanks
            } else
                for(var tank : targetEntity.getImportFluids())
                    inputFluids.add(tank.getFluid());
        }

        // tests whether the specified ingredient matches our input stack
        final Predicate<CountableIngredient> isTarget = ci -> ci.getIngredient().apply(infinitelyBigStack);

        // only match a recipe actually involving this item
        Recipe recipe = filteringMode.recipeMap
            .findRecipe(Long.MAX_VALUE,
                        Collections.singletonList(infinitelyBigStack),
                        inputFluids,
                        Integer.MAX_VALUE,
                        matchingMode.matchingMode,
                        r -> r.getInputs().stream().anyMatch(isTarget));

        // if a recipe was found, get the right ingredient
        if(recipe != null)
            for(var input : recipe.getInputs())
                if (isTarget.test(input))
                    return new ItemAndMetadataAndStackSize(itemAndMetadata, input.getCount());

        // no match
        return null;
    }

    @Override
    public void initUI(Consumer<Widget> widgetGroup) {
        widgetGroup.accept(new CycleButtonWidget(10, 0, 75, 20,
            SmartFilteringMode.class, this::getFilteringMode, this::setFilteringMode)
            .setTooltipHoverString("cover.smart_item_filter.filtering_mode.description"));
        widgetGroup.accept(new CycleButtonWidget(10, 20, 75, 20,
            SmartMatchingMode.class, this::getMatchingMode, this::setMatchingMode)
            .setTooltipHoverString("cover.smart_item_filter.matching_mode.description"));
    }

    @Override
    public int getTotalOccupiedHeight() {
        return 20;
    }

    @Override
    public boolean showGlobalTransferLimitSlider() {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("FilterMode", filteringMode.ordinal());
        tagCompound.setInteger("MatchingMode", matchingMode.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        this.filteringMode = SmartFilteringMode.values()[tagCompound.getInteger("FilterMode")];
        if (tagCompound.hasKey("MatchingMode")) {
            this.matchingMode = SmartMatchingMode.values()[tagCompound.getInteger("MatchingMode")];
        }
    }

    private class ItemAndMetadataAndStackSize {
        public final ItemAndMetadata itemAndMetadata;
        public final int transferStackSize;

        public ItemAndMetadataAndStackSize(ItemAndMetadata itemAndMetadata, int transferStackSize) {
            this.itemAndMetadata = itemAndMetadata;
            this.transferStackSize = transferStackSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemAndMetadataAndStackSize)) return false;
            ItemAndMetadataAndStackSize that = (ItemAndMetadataAndStackSize) o;
            return itemAndMetadata.equals(that.itemAndMetadata);
        }

        @Override
        public int hashCode() {
            return itemAndMetadata.hashCode();
        }
    }

    public enum SmartFilteringMode implements IStringSerializable {
        ELECTROLYZER("cover.smart_item_filter.filtering_mode.electrolyzer", RecipeMaps.ELECTROLYZER_RECIPES),
        CENTRIFUGE("cover.smart_item_filter.filtering_mode.centrifuge", RecipeMaps.CENTRIFUGE_RECIPES),
        SIFTER("cover.smart_item_filter.filtering_mode.sifter", RecipeMaps.SIFTER_RECIPES),
        WASHER("recipemap.orewasher.name", RecipeMaps.ORE_WASHER_RECIPES),
        BATH("recipemap.chemical_bath.name", RecipeMaps.CHEMICAL_BATH_RECIPES);

        private final Map<ItemAndMetadata, Integer> transferStackSizesCache = new HashMap<>();
        public final String localeName;
        public final RecipeMap<?> recipeMap;

        SmartFilteringMode(String localeName, RecipeMap<?> recipeMap) {
            this.localeName = localeName;
            this.recipeMap = recipeMap;
        }

        @Override
        public String getName() {
            return localeName;
        }
    }

    public enum SmartMatchingMode implements IStringSerializable {

        DEFAULT("cover.smart_item_filter.matching_mode.default", MatchingMode.DEFAULT),
        IGNORE_FLUID("cover.smart_item_filter.matching_mode.ignore_fluid", MatchingMode.IGNORE_FLUIDS);

        public final String localeName;
        public final MatchingMode matchingMode;

        SmartMatchingMode(String localeName, MatchingMode matchingMode) {
            this.localeName = localeName;
            this.matchingMode = matchingMode;
        }

        @Override
        public String getName() {
            return localeName;
        }

    }

}
