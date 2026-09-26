package nadiendev.malisisdoorsrevived.door;

/**
 * The states a door goes through.
 *
 * @author Ordinastie
 */
public enum DoorState
{
	CLOSED,
	CLOSING,
	OPENED,
	OPENING;

	public boolean isMoving()
	{
		return this == CLOSING || this == OPENING;
	}

	/**
	 * @return true if the door is closed or closing (the animations are played in reverse)
	 */
	public boolean isClosingOrClosed()
	{
		return this == CLOSING || this == CLOSED;
	}

	public static DoorState byOrdinal(int ordinal)
	{
		DoorState[] values = values();
		return ordinal >= 0 && ordinal < values.length ? values[ordinal] : CLOSED;
	}
}
