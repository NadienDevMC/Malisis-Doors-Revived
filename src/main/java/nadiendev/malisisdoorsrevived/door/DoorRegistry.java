package nadiendev.malisisdoorsrevived.door;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import nadiendev.malisisdoorsrevived.movement.*;
import nadiendev.malisisdoorsrevived.registry.ModSounds;
import nadiendev.malisisdoorsrevived.sound.DoorSound;
import nadiendev.malisisdoorsrevived.sound.IDoorSound;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Registry of the {@link IDoorMovement movements} and {@link IDoorSound sounds} available for the doors.<br>
 * Other mods can register their own movements and sounds, they will be available in the Door Factory.
 *
 * @author Ordinastie
 */
public class DoorRegistry
{
	private static final Map<String, IDoorMovement> MOVEMENTS = new LinkedHashMap<>();
	private static final Map<String, IDoorSound> SOUNDS = new LinkedHashMap<>();

	//movements
	public static final IDoorMovement ROTATING_DOOR = registerMovement("rotating_door", new RotatingDoorMovement());
	public static final IDoorMovement SLIDING_DOOR = registerMovement("sliding_door", new SlidingDoorMovement());
	public static final IDoorMovement SLIDING_UP_DOOR = registerMovement("sliding_up_door", new SlidingUpDoorMovement());
	public static final IDoorMovement SLIDING_SPLIT_DOOR = registerMovement("sliding_split_door", new SlidingSplitDoorMovement());
	public static final IDoorMovement VAULT_DOOR = registerMovement("vault_door", new VaultDoorMovement());
	public static final IDoorMovement ROTATING_SPLIT_DOOR = registerMovement("rotating_split_door", new RotatingSplitMovement());
	public static final IDoorMovement SLIDING_4WAYS = registerMovement("sliding_4ways", new Sliding4WaysMovement());
	public static final IDoorMovement ROTATING_4WAYS = registerMovement("rotating_4ways", new Rotating4WaysMovement());
	public static final IDoorMovement ROTATE_AROUND = registerMovement("rotate_around", new RotateAroundMovement());
	public static final IDoorMovement ROTATE_SLIDE = registerMovement("rotate_slide", new RotateAndSlideMovement());
	public static final IDoorMovement ROTATE_PLACE = registerMovement("rotate_place", new RotateAndPlaceMovement());
	public static final IDoorMovement VANISHING_DOOR = registerMovement("vanishing_door", new VanishingDoorMovement());
	public static final IDoorMovement SPINNING_DOOR = registerMovement("spinning_door", new SpinningDoorMovement());
	public static final IDoorMovement SPINNING_AROUND_DOOR = registerMovement("spinning_around_door", new SpinningAroundDoorMovement());
	public static final IDoorMovement DOUBLE_SLIDE_LEFT = registerMovement("double_slide_left", new DoubleSlideMovement(false));
	public static final IDoorMovement DOUBLE_SLIDE_RIGHT = registerMovement("double_slide_right", new DoubleSlideMovement(true));
	public static final IDoorMovement DOUBLE_ROTATE_LEFT = registerMovement("double_rotate_left", new DoubleRotateMovement(false));
	public static final IDoorMovement DOUBLE_ROTATE_RIGHT = registerMovement("double_rotate_right", new DoubleRotateMovement(true));
	//special movements
	public static final IDoorMovement TRAP_DOOR = registerMovement("trap_door", new TrapDoorMovement());
	public static final IDoorMovement SLIDING_TRAPDOOR = registerMovement("sliding_trapdoor", new SlidingTrapDoorMovement());
	public static final IDoorMovement FENCE_GATE = registerMovement("fence_gate", new FenceGateMovement());
	public static final IDoorMovement CURTAIN = registerMovement("curtain", new CurtainMovement());
	public static final IDoorMovement SALOON_DOOR = registerMovement("saloon_door", new SaloonDoorMovement());
	public static final IDoorMovement VERTICAL_HATCH = registerMovement("vertical_hatch", new VerticalHatchMovement());
	public static final IDoorMovement CARRIAGE_DOOR = registerMovement("carriage_door", new CarriageDoorMovement());
	public static final IDoorMovement DOOR_3X3 = registerMovement("door3x3", new Door3x3Movement());
	public static final IDoorMovement RUSTY_HATCH = registerMovement("rusty_hatch", new RustyHatchMovement());
	public static final IDoorMovement HITECH_DOOR = registerMovement("hitech_door", new HitechDoorMovement());

	//sounds
	public static final IDoorSound SILENT_SOUND = registerSound("silent_door", DoorSound.silent());
	public static final IDoorSound WOODEN_DOOR_SOUND = registerSound("vanilla_door", DoorSound.door(BlockSetType.OAK));
	public static final IDoorSound IRON_DOOR_SOUND = registerSound("iron_door", DoorSound.door(BlockSetType.IRON));
	public static final IDoorSound WOODEN_TRAPDOOR_SOUND = registerSound("wooden_trapdoor", DoorSound.trapDoor(BlockSetType.OAK));
	public static final IDoorSound IRON_TRAPDOOR_SOUND = registerSound("iron_trapdoor", DoorSound.trapDoor(BlockSetType.IRON));
	public static final IDoorSound GLASS_DOOR_SOUND = registerSound("glass_door", DoorSound.moving(ModSounds.SLIDING_DOOR));
	public static final IDoorSound JAIL_DOOR_SOUND = registerSound("jail_door", DoorSound.moving(ModSounds.JAIL_DOOR));
	public static final IDoorSound PNEUMATIC_SOUND = registerSound("pneumatic_door", DoorSound.openingAndClosing(ModSounds.SPACE_DOOR_OPEN, ModSounds.SPACE_DOOR_CLOSE));
	public static final IDoorSound SHOJI_DOOR_SOUND = registerSound("shoji_door", DoorSound.moving(ModSounds.SHOJI_DOOR));
	public static final IDoorSound RUSTY_HATCH_SOUND = registerSound("rusty_hatch", DoorSound.openingAndClosing(ModSounds.RUSTY_HATCH_OPEN, ModSounds.RUSTY_HATCH_CLOSE));
	public static final IDoorSound CARRIAGE_DOOR_SOUND = registerSound("carriage_door", threeStates(ModSounds.CARRIAGE_OPENING, ModSounds.CARRIAGE_CLOSING, ModSounds.CARRIAGE_CLOSED));
	public static final IDoorSound FENCE_GATE_SOUND = registerSound("fence_gate", DoorSound.fenceGate(WoodType.OAK));
	public static final IDoorSound RUSTY_DOOR_SOUND = registerSound("rusty_door", threeStates(ModSounds.RUSTY_DOOR_OPENING, ModSounds.RUSTY_DOOR_CLOSING, ModSounds.RUSTY_DOOR_CLOSED));

	private static IDoorSound threeStates(Supplier<SoundEvent> opening, Supplier<SoundEvent> closing, Supplier<SoundEvent> closed)
	{
		return DoorSound.openingAndClosing(opening, closing).on(DoorState.CLOSED, closed);
	}

	public static <T extends IDoorMovement> T registerMovement(String id, T movement)
	{
		if (MOVEMENTS.containsKey(id))
			throw new IllegalArgumentException(String.format("Door movement %s already in registry", id));
		MOVEMENTS.put(id, movement);
		return movement;
	}

	public static <T extends IDoorSound> T registerSound(String id, T sound)
	{
		if (SOUNDS.containsKey(id))
			throw new IllegalArgumentException(String.format("Door sound %s already in registry", id));
		SOUNDS.put(id, sound);
		return sound;
	}

	@Nullable
	public static IDoorMovement getMovement(String id)
	{
		return MOVEMENTS.get(id);
	}

	@Nullable
	public static IDoorSound getSound(String id)
	{
		return SOUNDS.get(id);
	}

	/**
	 * Gets the id of the movement (unregistered movements default to "rotating_door").
	 */
	public static String getId(@Nullable IDoorMovement movement)
	{
		for (Map.Entry<String, IDoorMovement> entry : MOVEMENTS.entrySet())
			if (entry.getValue() == movement)
				return entry.getKey();
		return "rotating_door";
	}

	/**
	 * Gets the id of the sound (unregistered sounds default to "silent_door").
	 */
	public static String getId(@Nullable IDoorSound sound)
	{
		for (Map.Entry<String, IDoorSound> entry : SOUNDS.entrySet())
			if (entry.getValue() == sound)
				return entry.getKey();
		return "silent_door";
	}

	/**
	 * @return the ids of the movements that can be selected in the Door Factory
	 */
	public static List<String> listMovements()
	{
		return MOVEMENTS.entrySet().stream().filter(e -> !e.getValue().isSpecial()).map(Map.Entry::getKey).toList();
	}

	/**
	 * @return the ids of all the sounds
	 */
	public static List<String> listSounds()
	{
		return Collections.unmodifiableList(List.copyOf(SOUNDS.keySet()));
	}
}
