package nadiendev.malisisdoorsrevived.sound;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import nadiendev.malisisdoorsrevived.door.DoorState;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Generic {@link IDoorSound} mapping door states to sounds.
 *
 * @author Ordinastie
 */
public class DoorSound implements IDoorSound
{
	private final Map<DoorState, Supplier<? extends SoundEvent>> sounds = new EnumMap<>(DoorState.class);

	public DoorSound on(DoorState state, Supplier<? extends SoundEvent> sound)
	{
		sounds.put(state, sound);
		return this;
	}

	@Nullable
	@Override
	public SoundEvent getSound(DoorState state)
	{
		Supplier<? extends SoundEvent> sound = sounds.get(state);
		return sound != null ? sound.get() : null;
	}

	/**
	 * Silent door.
	 */
	public static DoorSound silent()
	{
		return new DoorSound();
	}

	/**
	 * Sound played when the door starts opening and when it's fully closed (vanilla behavior).
	 */
	public static DoorSound openAndClosed(Supplier<? extends SoundEvent> open, Supplier<? extends SoundEvent> close)
	{
		return new DoorSound().on(DoorState.OPENING, open).on(DoorState.CLOSED, close);
	}

	/**
	 * Sounds played when the door starts opening and starts closing.
	 */
	public static DoorSound openingAndClosing(Supplier<? extends SoundEvent> opening, Supplier<? extends SoundEvent> closing)
	{
		return new DoorSound().on(DoorState.OPENING, opening).on(DoorState.CLOSING, closing);
	}

	/**
	 * Same sound when the door starts opening and starts closing.
	 */
	public static DoorSound moving(Supplier<? extends SoundEvent> sound)
	{
		return openingAndClosing(sound, sound);
	}

	/**
	 * Door sounds of the {@link BlockSetType}.
	 */
	public static DoorSound door(BlockSetType type)
	{
		return openAndClosed(type::doorOpen, type::doorClose);
	}

	/**
	 * Trapdoor sounds of the {@link BlockSetType}.
	 */
	public static DoorSound trapDoor(BlockSetType type)
	{
		return openAndClosed(type::trapdoorOpen, type::trapdoorClose);
	}

	/**
	 * Fence gate sounds of the {@link WoodType}.
	 */
	public static DoorSound fenceGate(WoodType type)
	{
		return openAndClosed(type::fenceGateOpen, type::fenceGateClose);
	}
}
