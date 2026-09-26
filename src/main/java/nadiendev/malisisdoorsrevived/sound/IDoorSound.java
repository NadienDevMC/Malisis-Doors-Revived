package nadiendev.malisisdoorsrevived.sound;

import javax.annotation.Nullable;

import nadiendev.malisisdoorsrevived.door.DoorState;
import net.minecraft.sounds.SoundEvent;

/**
 * Provides the sounds played by a door when its state changes.
 *
 * @author Ordinastie
 */
@FunctionalInterface
public interface IDoorSound
{
	/**
	 * Gets the sound to play when the door switches to the state.
	 *
	 * @param state the new state of the door
	 * @return the sound, or null for no sound
	 */
	@Nullable
	SoundEvent getSound(DoorState state);
}
