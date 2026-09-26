package nadiendev.malisisdoorsrevived.door;

import javax.annotation.Nullable;

import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import nadiendev.malisisdoorsrevived.sound.IDoorSound;

/**
 * Describes the behavior of a door : its movement, its sound, how long it takes to open, how it reacts to redstone...
 *
 * @author Ordinastie
 */
public class DoorDescriptor
{
	protected IDoorMovement movement = DoorRegistry.ROTATING_DOOR;
	protected IDoorSound sound = DoorRegistry.WOODEN_DOOR_SOUND;
	protected int openingTime = 6;
	protected int autoCloseTime = 0;
	protected boolean doubleDoor = true;
	protected boolean proximityDetection = false;
	protected RedstoneBehavior redstoneBehavior = RedstoneBehavior.STANDARD;

	public DoorDescriptor()
	{}

	public DoorDescriptor(DoorDescriptor other)
	{
		movement = other.movement;
		sound = other.sound;
		openingTime = other.openingTime;
		autoCloseTime = other.autoCloseTime;
		doubleDoor = other.doubleDoor;
		proximityDetection = other.proximityDetection;
		redstoneBehavior = other.redstoneBehavior;
	}

	public IDoorMovement getMovement()
	{
		return movement;
	}

	public DoorDescriptor setMovement(IDoorMovement movement)
	{
		this.movement = movement;
		return this;
	}

	@Nullable
	public IDoorSound getSound()
	{
		return sound;
	}

	public DoorDescriptor setSound(IDoorSound sound)
	{
		this.sound = sound;
		return this;
	}

	public int getOpeningTime()
	{
		return openingTime;
	}

	public DoorDescriptor setOpeningTime(int openingTime)
	{
		this.openingTime = Math.max(1, openingTime);
		return this;
	}

	public int getAutoCloseTime()
	{
		return autoCloseTime;
	}

	public DoorDescriptor setAutoCloseTime(int autoCloseTime)
	{
		this.autoCloseTime = Math.max(0, autoCloseTime);
		return this;
	}

	public boolean isDoubleDoor()
	{
		return doubleDoor;
	}

	public DoorDescriptor setDoubleDoor(boolean doubleDoor)
	{
		this.doubleDoor = doubleDoor;
		return this;
	}

	public boolean hasProximityDetection()
	{
		return proximityDetection;
	}

	public DoorDescriptor setProximityDetection(boolean proximityDetection)
	{
		this.proximityDetection = proximityDetection;
		return this;
	}

	public RedstoneBehavior getRedstoneBehavior()
	{
		return redstoneBehavior;
	}

	public DoorDescriptor setRedstoneBehavior(RedstoneBehavior redstoneBehavior)
	{
		this.redstoneBehavior = redstoneBehavior;
		return this;
	}

	/**
	 * Creates the {@link DoorSettings} matching this descriptor.
	 */
	public DoorSettings toSettings()
	{
		return new DoorSettings(DoorRegistry.getId(movement),
								DoorRegistry.getId(sound),
								openingTime,
								autoCloseTime,
								redstoneBehavior,
								doubleDoor,
								proximityDetection);
	}

	/**
	 * Creates a descriptor from {@link DoorSettings}.
	 */
	public static DoorDescriptor fromSettings(DoorSettings settings)
	{
		DoorDescriptor desc = new DoorDescriptor();
		IDoorMovement movement = DoorRegistry.getMovement(settings.movement());
		IDoorSound sound = DoorRegistry.getSound(settings.sound());
		if (movement != null)
			desc.setMovement(movement);
		if (sound != null)
			desc.setSound(sound);
		desc.setOpeningTime(settings.openingTime());
		desc.setAutoCloseTime(settings.autoCloseTime());
		desc.setRedstoneBehavior(settings.redstoneBehavior());
		desc.setDoubleDoor(settings.doubleDoor());
		desc.setProximityDetection(settings.proximityDetection());
		return desc;
	}
}
