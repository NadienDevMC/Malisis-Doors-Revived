package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import net.malisis.core.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/**
 * Each half of the door rotates in a different direction.
 */
public class Rotating4WaysMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION)
			return null;
		Direction.Axis axis = topBlock == be.isHingeLeft() ? Direction.Axis.X : Direction.Axis.Y;
		return AABBUtils.rotate(IDoorMovement.getHalfBoundingBox(), be.isHingeLeft() ? -1 : 1, axis);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean topBlock = part == DoorPart.TOP;
		float angle = 90;
		float hingeX = -0.5F + DOOR_WIDTH / 2;
		float hingeY = -0.5F + DOOR_WIDTH / 2;
		float hingeZ = -0.5F + DOOR_WIDTH / 2;
		int axisX = 0;
		int axisY = 0;
		if (topBlock)
		{
			angle = -angle;
			hingeY = 1 - hingeY;
		}
		if (be.isHingeLeft())
			hingeX = -hingeX;
		if (topBlock == be.isHingeLeft())
			axisX = 1;
		else
			axisY = 1;

		return new Rotation(angle)	.aroundAxis(axisX, axisY, 0)
									.offset(hingeX, hingeY, hingeZ)
									.reversed(be.getDoorState().isClosingOrClosed())
									.forTicks(be.getOpeningTime());
	}
}
