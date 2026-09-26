package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Each half of the door rotates around a corner, in the plane of the door.
 */
public class VaultDoorMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION && !topBlock)
			return null;
		float x = 0, y = 0;
		if (topBlock != be.isHingeLeft())
			x = topBlock ? DOOR_WIDTH - 1 : 1 - DOOR_WIDTH;
		else
			y = topBlock ? 1 - DOOR_WIDTH : DOOR_WIDTH - 1;
		return IDoorMovement.getHalfBoundingBox().move(x, y, 0);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean topBlock = part == DoorPart.TOP;
		float hinge = -0.5F + DOOR_WIDTH / 2;
		float offsetX = hinge;
		float offsetY = hinge;
		if (topBlock)
			offsetY = 1 - offsetY;
		if (be.isHingeLeft())
			offsetX = -offsetX;

		return new Rotation(-90)	.aroundAxis(0, 0, 1)
									.offset(offsetX, offsetY, 0)
									.reversed(be.getDoorState().isClosingOrClosed())
									.forTicks(be.getOpeningTime());
	}
}
