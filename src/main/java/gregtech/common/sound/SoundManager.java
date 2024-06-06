package gregtech.common.sound;

import gregtech.api.GTValues;
import gregtech.api.sound.ISoundManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SoundManager implements ISoundManager {
    private static final SoundManager INSTANCE = new SoundManager();

    private final Object2ObjectMap<BlockPos, ISound> soundMap = new Object2ObjectOpenHashMap<>();

    private SoundManager() {

    }

    public static SoundManager getInstance() {
        return INSTANCE;
    }

    public static SoundEvent createSound(String modName, String soundName) {
        ResourceLocation location = new ResourceLocation(modName, soundName);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(location);
        return event;
    }

    public static SoundEvent createSound(String soundName) {
        return createSound(GTValues.MODID, soundName);
    }

    @Override
    public SoundEvent registerSound(SoundEvent event) {
        ForgeRegistries.SOUND_EVENTS.register(event);
        return event;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ISound startTileSound(ResourceLocation location, float volume, BlockPos pos) {
        ISound sound = soundMap.get(pos);
        if (sound == null || !Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(sound)) {
            sound = new PositionedSoundRecord(location, SoundCategory.BLOCKS, volume, 1.0F, true, 0, ISound.AttenuationType.LINEAR, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            soundMap.put(pos, sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        return sound;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void stopTileSound(BlockPos pos) {
        ISound sound = soundMap.get(pos);
        if (sound != null) {
            Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
            soundMap.remove(pos);
        }
    }
}
