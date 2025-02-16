package gregtech.integration.theoneprobe.provider;

import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IWorkable;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.util.GTUtility;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.*;

import static gregtech.api.GTValues.*;
import static mcjty.theoneprobe.api.ElementAlignment.*;
import static mcjty.theoneprobe.api.NumberFormat.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;
import static net.minecraft.util.text.TextFormatting.*;

public class RecipeLogicInfoProvider extends CapabilityInfoProvider<IWorkable> {
    @Override
    protected Capability<IWorkable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_WORKABLE;
    }

    @Override
    protected void addProbeInfo(IWorkable capability, IProbeInfo probeInfo, EntityPlayer player,
                                TileEntity tileEntity, IProbeHitData data) {
        if(!(capability instanceof AbstractRecipeLogic logic)) return;

        long recipeEUt = logic.getRecipeEUt();
        if(recipeEUt == 0)
            return;
        boolean producing = recipeEUt < 0;
        if(producing)
            recipeEUt *= -1;

        // this function rounds down, so adjust back up accordingly
        int tier = GTUtility.getTierByVoltage(recipeEUt);
        if(recipeEUt < V[MAX] && recipeEUt > V[tier])
            tier++;

        IProbeInfo energyPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ALIGN_CENTER));
        // e.g. "Using 30,720 EU/t (LuV)"
        String prodText = producing ? "producing" : "consuming";
        energyPane.text(
            String.format("%s{*gregtech.top.energy_%s*} %s%s%s(%s%s)", INFO, prodText, RED,
                          ElementProgress.format(recipeEUt, COMMAS, " EU/t "),
                          GREEN, VNF[tier], GREEN));

        List<ItemStack> outputItems = logic.getItemOutputs();
        List<FluidStack> outputFluids = logic.getFluidOutputs();

        if(!outputItems.isEmpty() || !outputFluids.isEmpty())
            probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT))
                     .text(TextStyleClass.INFO + "{*gregtech.top.crafting*}");

        if(!outputItems.isEmpty()) {
            final IProbeInfo itemPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT));
            outputItems.stream().filter(Objects::nonNull).forEach(itemPane::item);
        }

        if(!outputFluids.isEmpty()) {
            IProbeInfo fluidPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_TOPLEFT));
            // Bucket is a workaround for TOP not having its own fluid renderer.
            // Would be better to render the fluid directly like JEI does but not sure how to do that at the moment.
            // Probably have to implement an IElement for FluidStack similar to its ItemStack renderer.
            outputFluids.stream().filter(Objects::nonNull)
                        .map(FluidUtil::getFilledBucket)
                        .forEach(fluidPane::item);
        }

        // TODO: if sneaking, display a vertical list of outputs instead, with the name of each beside the icon

        // TODO: maybe make an Air Collector TOP provider?
    }

    @Override
    public String getID() {
        return MODID + ":recipe_logic_provider";
    }
}
