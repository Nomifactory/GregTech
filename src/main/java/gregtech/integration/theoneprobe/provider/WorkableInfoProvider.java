package gregtech.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IWorkable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;

import static mcjty.theoneprobe.api.ElementAlignment.*;
import static mcjty.theoneprobe.api.NumberFormat.*;
import static mcjty.theoneprobe.apiimpl.elements.ElementProgress.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class WorkableInfoProvider extends CapabilityInfoProvider<IWorkable> {

    @Override
    protected Capability<IWorkable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_WORKABLE;
    }

    @Override
    public String getID() {
        return GTValues.MODID + ":workable_provider";
    }

    @Override
    protected void addProbeInfo(IWorkable capability, IProbeInfo probeInfo, EntityPlayer player,
                                TileEntity tileEntity, IProbeHitData data) {
        if(!capability.isActive())
            return;

        int currentProgress = capability.getProgress();
        int maxProgress = capability.getMaxProgress();

        int progressScaled = maxProgress == 0 ? 0 : (int) Math.floor(currentProgress / (maxProgress * 1.0) * 100);

        // Switch to displaying seconds if the recipe is 1s or longer, otherwise show ticks.
        String timeUnit = "t";
        if(maxProgress >= 20) {
            currentProgress = Math.round(currentProgress / 20.0f);
            maxProgress = Math.round(maxProgress / 20.0f);
            timeUnit = "s";
        }

        IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ALIGN_CENTER));
        horizontalPane.text(INFO + "{*gregtech.top.progress*} ");
        String text = String.format(" %s / %s (%d%%)",
                                    timeUnit,
                                    format(maxProgress, COMMAS, timeUnit),
                                    progressScaled);
        horizontalPane.progress(currentProgress, maxProgress, probeInfo.defaultProgressStyle()
            .suffix(text)
            .borderColor(0x00000000)
            .backgroundColor(0x00000000)
            .filledColor(0xFF000099)
            .alternateFilledColor(0xFF000077));

        format(currentProgress, COMMAS, timeUnit);
    }
}
