package gregtech.integration.theoneprobe.provider;

import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IWorkable;
import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.util.GTUtility;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;

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
    }

    @Override
    public String getID() {
        return MODID + ":recipe_logic_provider";
    }
}
