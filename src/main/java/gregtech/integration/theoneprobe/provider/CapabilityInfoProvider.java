package gregtech.integration.theoneprobe.provider;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public abstract class CapabilityInfoProvider<T> implements IProbeInfoProvider {

    protected abstract Capability<T> getCapability();

    protected abstract void addProbeInfo(T capability, IProbeInfo probeInfo, EntityPlayer player, TileEntity tileEntity,
                                         IProbeHitData data);

    protected boolean allowDisplaying(T capability) {
        return true;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (blockState.getBlock().hasTileEntity(blockState)) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity == null) return;
            Capability<T> capability = getCapability();
            T resultCapability = tileEntity.getCapability(capability, null);
            if (resultCapability != null && allowDisplaying(resultCapability)) {
                addProbeInfo(resultCapability, probeInfo, player, tileEntity, data);
            }
        }
    }

}
