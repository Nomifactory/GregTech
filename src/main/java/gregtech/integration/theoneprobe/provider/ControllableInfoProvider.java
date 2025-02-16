package gregtech.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class ControllableInfoProvider extends CapabilityInfoProvider<IControllable> {

    @Override
    protected Capability<IControllable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE;
    }

    @Override
    public String getID() {
        return GTValues.MODID + ":controllable_provider";
    }

    @Override
    protected void addProbeInfo(IControllable capability, IProbeInfo probeInfo, EntityPlayer player,
                                TileEntity tileEntity, IProbeHitData data) {
        if (!capability.isWorkingEnabled()) {
            probeInfo.text(INFOIMP + "{*gregtech.top.working_disabled*}");
        }
    }
}
