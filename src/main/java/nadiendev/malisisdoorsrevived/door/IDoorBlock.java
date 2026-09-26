package nadiendev.malisisdoorsrevived.door;

/**
 * Implemented by all the blocks using a {@link nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity}.
 *
 * @author Ordinastie
 */
public interface IDoorBlock
{
	/**
	 * @return the default descriptor of the door
	 */
	DoorDescriptor getDescriptor();

	/**
	 * @return how the door is rendered
	 */
	DoorAppearance getAppearance();
}
