package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import net.malisis.core.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The door rotates around the center of the block.
 */
public class RotateAroundMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return AABBUtils.rotate(IDoorMovement.getFullBoundingBox(), be.isHingeLeft() ? -1 : 1);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		float angle = be.isHingeLeft() ? -90 : 90;
		return new Rotation(angle).aroundAxis(0, 1, 0).reversed(be.getDoorState().isClosingOrClosed()).forTicks(be.getOpeningTime());
	}
}
