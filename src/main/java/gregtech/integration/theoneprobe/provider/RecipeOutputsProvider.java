/*
    Code adapted from Nomi-Labs https://github.com/Nomi-CEu/Nomi-Labs @ 6e14c06
 */
package gregtech.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.integration.nomilabs.util.*;
import gregtech.integration.nomilabs.element.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;

import static mcjty.theoneprobe.api.ElementAlignment.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;

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

        var outputs = getUniqueItems(recipe.getItemOutputs());
        var fluidOutputs = getUniqueFluids(recipe.getFluidOutputs());

        if (outputs.isEmpty() && fluidOutputs.isEmpty()) return;

        boolean showDetailed = outputs.size() + fluidOutputs.size() <= Config.showItemDetailThresshold &&
                player.isSneaking();
        IProbeInfo mainPanel = info.vertical()
                .text("{*gregtech.top.crafting*}")
                .vertical(info.defaultLayoutStyle().borderColor(Config.chestContentsBorderColor)
                        .spacing(5));

        if (showDetailed) {
            for (var entry : outputs.entrySet()) {
                ItemStack stack = entry.getKey().toStack(entry.getValue());
                mainPanel.horizontal(new LayoutStyle().spacing(10).alignment(ALIGN_CENTER))
                        .item(stack, new ItemStyle().width(16).height(16))
                        .text(INFO + stack.getDisplayName());
            }

            for (var entry : fluidOutputs.entrySet()) {
                FluidStack stack = new FluidStack(entry.getKey(), entry.getValue());
                mainPanel.horizontal(new LayoutStyle().spacing(10).alignment(ALIGN_CENTER))
                        .element(new LabsFluidStackElement(stack))
                        .element(new LabsFluidNameElement(stack, false));
            }
            return;
        }

        // If outputs and fluid outputs are both of size 1, show on same row instead of over two rows
        boolean condense = outputs.size() == 1 && fluidOutputs.size() == 1;
        IProbeInfo sharedHorizontal = null;

        if (condense)
            sharedHorizontal = createHorizontalLayout(mainPanel);

        if (!outputs.isEmpty()) {
            IProbeInfo panel;
            if (condense)
                panel = sharedHorizontal;
            else
                panel = createHorizontalLayout(mainPanel);

            addOutputs(outputs, (meta, amt) -> panel.item(meta.toStack(amt), new ItemStyle().width(16).height(16)));
        }

        if (!fluidOutputs.isEmpty()) {
            IProbeInfo panel;
            if (condense)
                panel = sharedHorizontal;
            else
                panel = createHorizontalLayout(mainPanel);

            addOutputs(fluidOutputs,
                    (fluid, amount) -> panel.element(new LabsFluidStackElement(new FluidStack(fluid, amount))));
        }
    }

    private <T> void addOutputs(Map<T, Integer> outputs, BiConsumer<T, Integer> addToPanel) {
        int idx = 0;

        for (var output : outputs.entrySet()) {
            if (idx >= AMT_IN_ROW) break;

            addToPanel.accept(output.getKey(), output.getValue());
            idx++;
        }
    }

    private IProbeInfo createHorizontalLayout(IProbeInfo mainPanel) {
        return mainPanel.horizontal(new LayoutStyle().spacing(4));
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
