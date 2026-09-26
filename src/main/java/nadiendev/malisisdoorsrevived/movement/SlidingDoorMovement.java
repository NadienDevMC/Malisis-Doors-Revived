package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Transformation;
import net.malisis.core.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The door slides sideways, toward its hinge.
 */
public class SlidingDoorMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return IDoorMovement.getFullBoundingBox().move(be.isHingeLeft() ? 1 - DOOR_WIDTH : -1 + DOOR_WIDTH, 0, 0);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		return new Translation(0, 0, 0, be.isHingeLeft() ? 1 - DOOR_WIDTH : -1 + DOOR_WIDTH, 0, 0)
				.reversed(be.getDoorState().isClosingOrClosed())
				.forTicks(be.getOpeningTime());
	}
}
