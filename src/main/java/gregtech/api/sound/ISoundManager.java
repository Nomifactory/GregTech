package gregtech.api.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISoundManager {
    SoundEvent registerSound(SoundEvent event);

    @SideOnly(Side.CLIENT)
    ISound startTileSound(ResourceLocation location, float volume, BlockPos pos);

    @SideOnly(Side.CLIENT)
    void stopTileSound(BlockPos pos);
}
