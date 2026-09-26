package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Movement of the big doors (carriage and medieval doors) : two leaves rotating around their hinge.
 */
public class CarriageDoorMovement implements IDoorMovement
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
		float fx = 0;
		float angle = 105;
		if (right)
		{
			fx = 3;
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
