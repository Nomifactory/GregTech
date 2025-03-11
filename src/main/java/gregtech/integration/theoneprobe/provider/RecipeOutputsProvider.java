/*
    Code adapted from Nomi-Labs https://github.com/Nomi-CEu/Nomi-Labs @ d2d6e89
 */
package gregtech.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.integration.nomilabs.util.*;
import gregtech.integration.nomilabs.element.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementItemStack;
import mcjty.theoneprobe.apiimpl.styles.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

import static mcjty.theoneprobe.api.ElementAlignment.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;
import static gregtech.integration.nomilabs.element.LabsChancedItemStackElement.formatChance;

public class RecipeOutputsProvider extends CapabilityInfoProvider<IWorkable> {

    private static final int AMT_IN_ROW = 10;

    @Override
    public String getID() {
        return GTValues.MODID + ":recipe_outputs";
    }

    @Override
    protected @NotNull Capability<IWorkable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_WORKABLE;
    }

    @Override
    protected void addProbeInfo(IWorkable capability, IProbeInfo info, EntityPlayer player, TileEntity tile,
                                IProbeHitData data) {

        if (capability.getProgress() <= 0 || (!(capability instanceof AbstractRecipeLogic recipe))) return;

        // Generators, Ignore
        if (recipe.getRecipeEUt() < 0) return;

        var lists = createItemFluidElementLists(recipe);
        var items = lists.getLeft();
        var fluids = lists.getRight();

        if (items.isEmpty() && fluids.isEmpty()) return;

        boolean showDetailed = items.size() + fluids.size() <= Config.showItemDetailThresshold && player.isSneaking();
        IProbeInfo mainPanel = info.vertical()
                .text("{*gregtech.top.crafting*}")
                .vertical(info.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor)
                        .spacing(5));

        if (showDetailed) {
            for (var entry : items)
                mainPanel.horizontal(new LayoutStyle().spacing(10).alignment(ALIGN_CENTER))
                         .element(entry.getValue())
                         .text(INFO + entry.getKey());

            for (var entry : fluids)
                mainPanel.horizontal(new LayoutStyle().spacing(10).alignment(ALIGN_CENTER))
                         .element(entry.getValue())
                         .element(entry.getKey());

            return;
        }

        // If outputs and fluid outputs are both of size 1, show on same row instead of over two rows
        boolean condense = items.size() == 1 && fluids.size() == 1;
        IProbeInfo sharedHorizontal = null;

        if (condense)
            sharedHorizontal = createHorizontalLayout(mainPanel);

        if (!items.isEmpty()) {
            IProbeInfo panel;
            if (condense)
                panel = sharedHorizontal;
            else
                panel = createHorizontalLayout(mainPanel);

            addOutputs(items, panel, Pair::getValue);
        }

        if (!fluids.isEmpty()) {
            IProbeInfo panel;
            if (condense)
                panel = sharedHorizontal;
            else
                panel = createHorizontalLayout(mainPanel);

            addOutputs(fluids, panel, Pair::getValue);
        }
    }

    private <T> void addOutputs(List<T> list, IProbeInfo panel, Function<T, IElement> getElement) {
        int idx = 0;

        for (var entry : list) {
            if (idx >= AMT_IN_ROW) break;

            panel.element(getElement.apply(entry));
            idx++;
        }
    }

    private IProbeInfo createHorizontalLayout(IProbeInfo mainPanel) {
        return mainPanel.horizontal(new LayoutStyle().spacing(4));
    }

    private Pair<List<Pair<String, ElementItemStack>>, List<Pair<LabsFluidNameElement, LabsFluidStackElement>>> createItemFluidElementLists(AbstractRecipeLogic recipe) {
        // Items
        var outputs =
            getUnique(recipe.getItemOutputs().subList(0, recipe.getNonChancedItemAmt()),
                      ItemStack::isEmpty, ItemMeta::new, ItemStack::getCount);

        var chancedOutputs =
            getUnique(recipe.getChancedItemOutputs(),
                      (chanced) -> chanced.getKey().isEmpty() || chanced.getValue() == 0,
                      (chanced) -> Pair.of(new ItemMeta(chanced.getKey()), chanced.getValue()),
                      (chanced) -> chanced.getKey().getCount());

        IItemStyle style = new ItemStyle().width(16).height(16);
        List<Pair<String, ElementItemStack>> items = new ArrayList<>();

        for (var output : outputs.entrySet()) {
            ItemStack stack = output.getKey().toStack(output.getValue());
            items.add(Pair.of(stack.getDisplayName(), new ElementItemStack(stack, style)));
        }

        for (var chanced : chancedOutputs.entrySet()) {
            ItemStack stack = chanced.getKey().getKey().toStack(chanced.getValue());
            String display = stack.getDisplayName() + " (" + formatChance(chanced.getKey().getValue()) + ")";
            items.add(Pair.of(display, new LabsChancedItemStackElement(stack, chanced.getKey().getValue(), style)));
        }

        // Fluids
        var fluidOutputs =
            getUnique(recipe.getFluidOutputs(),
                      (stack) -> stack.amount == 0, FluidStack::getFluid, (stack) -> stack.amount);

        List<Pair<LabsFluidNameElement, LabsFluidStackElement>> fluids = new ArrayList<>();

        for (var output : fluidOutputs.entrySet()) {
            FluidStack stack = new FluidStack(output.getKey(), output.getValue());
            fluids.add(Pair.of(new LabsFluidNameElement(stack, false), new LabsFluidStackElement(stack)));
        }

        return Pair.of(items, fluids);
    }

    private <T, K> Map<K, Integer> getUnique(List<T> stacks, Function<T, Boolean> emptyCheck, Function<T, K> getKey,
                                             Function<T, Integer> getCount) {
        Map<K, Integer> map = new Object2ObjectLinkedOpenHashMap<>();

        for (T stack : stacks) {
            if (emptyCheck.apply(stack)) continue;

            map.compute(getKey.apply(stack), (key, count) -> {
                if (count == null) count = 0;
                return count + getCount.apply(stack);
            });
        }

        return map;
    }

    private Map<ItemMeta, Integer> getUniqueItems(List<ItemStack> stacks) {
        Map<ItemMeta, Integer> map = new Object2ObjectLinkedOpenHashMap<>();

        for (var stack : stacks) {
            if (stack.isEmpty()) continue;

            map.compute(new ItemMeta(stack), (meta, count) -> {
                if (count == null) count = 0;
                return count + stack.getCount();
            });
        }

        return map;
    }

    private Map<Fluid, Integer> getUniqueFluids(List<FluidStack> stacks) {
        Map<Fluid, Integer> map = new Object2ObjectLinkedOpenHashMap<>();

        for (var stack : stacks) {
            if (stack.amount == 0) continue;

            map.compute(stack.getFluid(), (meta, amount) -> {
                if (amount == null) amount = 0;
                return amount + stack.amount;
            });
        }

        return map;
    }
}
