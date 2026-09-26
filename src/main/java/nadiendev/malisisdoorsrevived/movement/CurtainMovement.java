package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Scale;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The curtain folds toward its hinge.
 */
public class CurtainMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		AABB aabb = IDoorMovement.getFullBoundingBox();
		return be.isHingeLeft()	? new AABB(1 - DOOR_WIDTH, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
								: new AABB(aabb.minX, aabb.minY, aabb.minZ, DOOR_WIDTH, aabb.maxY, aabb.maxZ);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		return new Scale(1, 1, 1, DOOR_WIDTH, 1, 1.5F)	.offset(be.isHingeLeft() ? 0.5F : -0.5F, 0, -0.5F)
														.reversed(be.getDoorState().isClosingOrClosed())
														.forTicks(be.getOpeningTime());
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
