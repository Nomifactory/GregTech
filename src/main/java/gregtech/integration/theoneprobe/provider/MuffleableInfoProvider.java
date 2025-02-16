package gregtech.integration.theoneprobe.provider;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IMuffleable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;

import static mcjty.theoneprobe.api.TextStyleClass.*;

public class MuffleableInfoProvider extends CapabilityInfoProvider<IMuffleable> {

    @Override
    protected Capability<IMuffleable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_MUFFLEABLE;
    }

    @Override
    public String getID() {
        return GTValues.MODID + ":sound_emitter_provider";
    }

    @Override
    protected void addProbeInfo(IMuffleable capability, IProbeInfo probeInfo, EntityPlayer player,
                                TileEntity tileEntity, IProbeHitData data) {
        if (capability.isMuffled())
            probeInfo.text(INFOIMP + "{*gregtech.top.muffled*}");
    }
}
