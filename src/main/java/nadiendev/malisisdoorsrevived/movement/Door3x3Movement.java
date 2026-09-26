package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Movement of the 3x3 doors : two leaves rotating around their hinge.
 */
public class Door3x3Movement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return null;
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean right = part == DoorPart.RIGHT;
		float fz = 0.5F - DOOR_WIDTH / 2;
		float fx = -1.5F + DOOR_WIDTH / 2;
		float angle = 90;
		if (right)
		{
			fx = 1.5F - DOOR_WIDTH / 2;
			angle = -angle;
		}
		return new Rotation(angle)	.aroundAxis(0, 1, 0)
									.offset(fx, 0, fz)
									.reversed(be.getDoorState().isClosingOrClosed())
									.forTicks(be.getOpeningTime());
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
