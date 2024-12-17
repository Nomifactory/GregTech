package gregtech.integration.theoneprobe.provider;

import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IMuffleable;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class MuffleableInfoProvider extends CapabilityInfoProvider<IMuffleable> {

    @Override
    protected Capability<IMuffleable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_MUFFLEABLE;
    }

    @Override
    public String getID() {
        return "gregtech:sound_emitter_provider";
    }

    @Override
    protected void addProbeInfo(IMuffleable capability, IProbeInfo probeInfo, TileEntity tileEntity, EnumFacing sideHit) {
        if (capability.isMuffled()) {
            probeInfo.text(TextStyleClass.INFOIMP + "{*gregtech.top.muffled*}");
        }
    }
}
