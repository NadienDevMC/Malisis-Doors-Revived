package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import net.malisis.core.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Vanilla like movement : the door rotates around its hinge.
 */
public class RotatingDoorMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return AABBUtils.rotate(IDoorMovement.getFullBoundingBox(), be.isHingeLeft() ? -1 : 1);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		float angle = -90;
		float hingeX = -0.5F + DOOR_WIDTH / 2;
		if (be.isHingeLeft())
		{
			hingeX = -hingeX;
			angle = -angle;
		}

		return new Rotation(angle)	.aroundAxis(0, 1, 0)
									.offset(hingeX, 0, -0.5F + DOOR_WIDTH / 2)
									.reversed(be.getDoorState().isClosingOrClosed())
									.forTicks(be.getOpeningTime());
	}
}
