package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import net.malisis.core.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/**
 * The top half rotates up and the bottom half rotates down.
 */
public class RotatingSplitMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION && !topBlock)
			return null;
		return AABBUtils.rotate(IDoorMovement.getHalfBoundingBox(), topBlock ? -1 : 1, Direction.Axis.X);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean topBlock = part == DoorPart.TOP;
		float angle = 90;
		float hingeY = -0.5F + DOOR_WIDTH / 2;
		float hingeZ = -0.5F + DOOR_WIDTH / 2;
		if (topBlock)
		{
			angle = -angle;
			hingeY = 1 - hingeY;
		}

		return new Rotation(angle)	.aroundAxis(1, 0, 0)
									.offset(0, hingeY, hingeZ)
									.reversed(be.getDoorState().isClosingOrClosed())
									.forTicks(be.getOpeningTime());
	}
}
