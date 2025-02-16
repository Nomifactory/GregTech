package gregtech.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IFuelInfo;
import gregtech.api.capability.IFuelable;
import gregtech.api.capability.impl.ItemFuelInfo;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Collection;

import static mcjty.theoneprobe.api.ElementAlignment.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class FuelableInfoProvider extends CapabilityInfoProvider<IFuelable> {

    @Override
    protected Capability<IFuelable> getCapability() {
        return GregtechCapabilities.CAPABILITY_FUELABLE;
    }

    @Override
    public String getID() {
        return GTValues.MODID + ":fuelable_provider";
    }

    @Override
    protected boolean allowDisplaying(IFuelable capability) {
        return !capability.isOneProbeHidden();
    }

    @Override
    protected void addProbeInfo(IFuelable capability, IProbeInfo probeInfo, EntityPlayer player,
                                TileEntity tileEntity, IProbeHitData data) {
        Collection<IFuelInfo> fuels = capability.getFuels();
        if (fuels == null || fuels.isEmpty()) {
            probeInfo.text(WARNING + "{*gregtech.top.fuel_none*}");
            return;
        }
        for (IFuelInfo fuelInfo : fuels) {
            final String fuelName = fuelInfo.getFuelName();
            final int fuelRemaining = fuelInfo.getFuelRemaining();
            final int fuelCapacity = fuelInfo.getFuelCapacity();
            final int fuelMinConsumed = fuelInfo.getFuelMinConsumed();
            final long burnTime = fuelInfo.getFuelBurnTimeLong() / 20;

            IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ALIGN_CENTER));
            if (fuelInfo instanceof ItemFuelInfo ifo)
                horizontalPane.text(INFO + "{*gregtech.top.fuel_name*} ").itemLabel(ifo.getItemStack());
            else
                horizontalPane.text(String.format("%s{*gregtech.top.fuel_name*} {*%s*}", INFO, fuelName));

            horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ALIGN_CENTER));
            horizontalPane.progress(fuelRemaining, fuelCapacity,
                                    probeInfo.defaultProgressStyle()
                                             .suffix(String.format(" / %d ", fuelCapacity))
                                             .borderColor(0x00000000)
                                             .backgroundColor(0x00000000)
                                             .filledColor(0xFFFFE000)
                                             .alternateFilledColor(0xFFEED000));
            if (fuelRemaining < fuelMinConsumed)
                horizontalPane.text(String.format("{*gregtech.top.fuel_min_consume*} %d", fuelMinConsumed));
            else
                horizontalPane.text(String.format("{*gregtech.top.fuel_burn*} %d {*gregtech.top.fuel_time*}", burnTime));
        }
    }

}
