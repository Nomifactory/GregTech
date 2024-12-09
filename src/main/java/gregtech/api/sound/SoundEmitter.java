package gregtech.api.sound;

import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenGetter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@NotNull
public interface SoundEmitter<T> {

	@Nullable
	SoundEvent getSound();

	T setSound(SoundEvent event);

	@ZenGetter("sound")
	@Nullable
	default String ctGetSound() {
		return getSound() != null ? getSound().getSoundName().toString() : null;
	}
}
