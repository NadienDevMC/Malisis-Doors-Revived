package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Transformation;
import net.malisis.core.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The top half slides up and the bottom half slides down.
 */
public class SlidingSplitDoorMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (!topBlock)
			return null;
		return new AABB(0, 1 - DOOR_WIDTH, 0, 1, 1, DOOR_WIDTH);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean top = part == DoorPart.TOP;
		return new Translation(0, 0, 0, 0, top ? 1 - DOOR_WIDTH : -1.1F, 0)	.reversed(be.getDoorState().isClosingOrClosed())
																			.forTicks(be.getOpeningTime());
	}
}
