package gregtech.common.sound;

import gregtech.api.GregTechAPI;
import net.minecraft.util.SoundEvent;

public class GTSoundEvents {

    public static SoundEvent MACERATOR = SoundManager.createSound("tick.macerator");
    public static SoundEvent COMPRESSOR = SoundManager.createSound("tick.compressor");
    public static SoundEvent ELECTROLYZER = SoundManager.createSound("tick.electrolyzer");
    public static SoundEvent FURNACE = SoundManager.createSound("tick.furnace");
    public static SoundEvent FORGE_HAMMER = SoundManager.createSound("tick.forge_hammer");
    public static SoundEvent CHEMICAL = SoundManager.createSound("tick.chemical_reactor");
    public static SoundEvent ASSEMBLER = SoundManager.createSound("tick.assembler");
    public static SoundEvent CENTRIFUGE = SoundManager.createSound("tick.centrifuge");
    public static SoundEvent MIXER = SoundManager.createSound("tick.mixer");
    public static SoundEvent HUM = SoundManager.createSound("tick.arc");
    public static SoundEvent BOILER = SoundManager.createSound("tick.boiler");
    public static SoundEvent CHEMICAL_BATH = SoundManager.createSound("tick.bath");
    public static SoundEvent MOTOR = SoundManager.createSound("tick.motor");
    public static SoundEvent CUTTING = SoundManager.createSound("tick.cut");
    public static SoundEvent COOLING = SoundManager.createSound("tick.cooling");

    public static void register() {
        GregTechAPI.soundManager.registerSound(MACERATOR);
        GregTechAPI.soundManager.registerSound(COMPRESSOR);
        GregTechAPI.soundManager.registerSound(ELECTROLYZER);
        GregTechAPI.soundManager.registerSound(FURNACE);
        GregTechAPI.soundManager.registerSound(FORGE_HAMMER);
        GregTechAPI.soundManager.registerSound(CHEMICAL);
        GregTechAPI.soundManager.registerSound(ASSEMBLER);
        GregTechAPI.soundManager.registerSound(CENTRIFUGE);
        GregTechAPI.soundManager.registerSound(MIXER);
        GregTechAPI.soundManager.registerSound(HUM);
        GregTechAPI.soundManager.registerSound(BOILER);
        GregTechAPI.soundManager.registerSound(CHEMICAL_BATH);
        GregTechAPI.soundManager.registerSound(MOTOR);
        GregTechAPI.soundManager.registerSound(CUTTING);
        GregTechAPI.soundManager.registerSound(COOLING);
    }
}
