package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Transformation;
import net.malisis.core.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The door slides up into the ceiling.
 */
public class SlidingUpDoorMovement implements IDoorMovement
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
		return new Translation(0, 0, 0, 0, 2 - DOOR_WIDTH, 0).reversed(be.getDoorState().isClosingOrClosed()).forTicks(be.getOpeningTime());
	}
}
