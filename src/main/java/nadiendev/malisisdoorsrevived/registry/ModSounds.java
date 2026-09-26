package nadiendev.malisisdoorsrevived.registry;

import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds
{
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MalisisDoors.MODID);

	public static final Supplier<SoundEvent> SLIDING_DOOR = register("slidingdooro");
	public static final Supplier<SoundEvent> JAIL_DOOR = register("jaildoor");
	public static final Supplier<SoundEvent> SPACE_DOOR_OPEN = register("space_door_open");
	public static final Supplier<SoundEvent> SPACE_DOOR_CLOSE = register("space_door_close");
	public static final Supplier<SoundEvent> SHOJI_DOOR = register("shoji_door");
	public static final Supplier<SoundEvent> RUSTY_HATCH_OPEN = register("rustyhatch_open");
	public static final Supplier<SoundEvent> RUSTY_HATCH_CLOSE = register("rustyhatch_close");
	public static final Supplier<SoundEvent> CARRIAGE_OPENING = register("carriage_opening");
	public static final Supplier<SoundEvent> CARRIAGE_CLOSING = register("carriage_closing");
	public static final Supplier<SoundEvent> CARRIAGE_CLOSED = register("carriage_closed");
	public static final Supplier<SoundEvent> RUSTY_DOOR_OPENING = register("rustydoor_opening");
	public static final Supplier<SoundEvent> RUSTY_DOOR_CLOSING = register("rustydoor_closing");
	public static final Supplier<SoundEvent> RUSTY_DOOR_CLOSED = register("rustydoor_closed");

	private static Supplier<SoundEvent> register(String name)
	{
		return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(MalisisDoors.id(name)));
	}
}
