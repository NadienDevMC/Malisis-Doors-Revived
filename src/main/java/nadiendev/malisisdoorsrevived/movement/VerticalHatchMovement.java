package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.ParallelTransformation;
import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import net.malisis.core.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The handle of the hatch spins, then the hatch rotates around its hinge.
 */
public class VerticalHatchMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		AABB aabb = IDoorMovement.getFullBoundingBox().move(be.isHingeLeft() ? DOOR_WIDTH : -DOOR_WIDTH, 0, 0);
		return AABBUtils.rotate(aabb, be.isHingeLeft() ? -1 : 1);
	}

	private Rotation getDoorTransformation(DoorBlockEntity be)
	{
		float angle = -90;
		float hingeX = -0.5F;
		int t = be.getOpeningTime() / 2;
		if (be.isHingeLeft())
		{
			hingeX = -hingeX;
			angle = -angle;
		}

		Rotation rotation = new Rotation(angle).aroundAxis(0, 1, 0).offset(hingeX, 0, -0.5F + DOOR_WIDTH);
		if (be.getDoorState().isClosingOrClosed())
			rotation.reversed(true);
		else
			rotation.delay(t);
		return rotation.forTicks(t);
	}

	private Rotation getHandleTransformation(DoorBlockEntity be)
	{
		int t = be.getOpeningTime() / 2;
		Rotation rotation = new Rotation(45, 600).aroundAxis(0, 0, 1).offset(0, .53125F, 0).movement(Transformation.SINUSOIDAL);
		if (be.getDoorState().isClosingOrClosed())
			rotation.delay(t).reversed(true);
		return rotation.forTicks(t);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		if (part == DoorPart.HANDLE)
			return new ParallelTransformation(getDoorTransformation(be), getHandleTransformation(be));
		if (part == DoorPart.DOOR)
			return getDoorTransformation(be);
		return null;
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
