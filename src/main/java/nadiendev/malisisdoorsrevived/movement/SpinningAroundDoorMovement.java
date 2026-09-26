package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.ParallelTransformation;
import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Scale;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Each half of the door spins around the center of the door while shrinking.
 */
public class SpinningAroundDoorMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION)
			return null;
		return IDoorMovement.getFullBoundingBox();
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean top = part == DoorPart.TOP;
		boolean doubleDoor = be.getDoubleDoor() != null;
		boolean closed = be.getDoorState().isClosingOrClosed();
		int ot = be.getOpeningTime();
		float offsetX = doubleDoor ? (be.isHingeLeft() ? -0.5F : 0.5F) : 0;

		//both halves are rendered relative to the bottom block : the center of the door is at the junction of both halves
		float offsetY = top ? 1 : 0;
		Rotation around = new Rotation(720).aroundAxis(0, 0, 1).offset(offsetX, 0.5F, 0).reversed(closed).forTicks(ot);
		Rotation self = new Rotation(-720).aroundAxis(0, 0, 1).offset(0, offsetY, 0).reversed(closed).forTicks(ot);
		Scale scale = new Scale(0, 0, 0).offset(0, offsetY, 0).reversed(closed).forTicks(ot);
		return new ParallelTransformation(around, self, scale);
	}
}
