package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.ParallelTransformation;
import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Scale;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Each half of the door spins on itself while shrinking.
 */
public class SpinningDoorMovement implements IDoorMovement
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
		float angle = be.isHingeLeft() ? 720 : -720;
		boolean closed = be.getDoorState().isClosingOrClosed();
		int ot = be.getOpeningTime();

		//both halves are rendered relative to the bottom block, the top half spins around its own center
		float offsetY = part == DoorPart.TOP ? 1 : 0;
		Rotation rotation = new Rotation(angle, 0).aroundAxis(0, 0, 1).offset(0, offsetY, 0).reversed(closed).forTicks(ot);
		Scale scale = new Scale(0, 0, 0).offset(0, offsetY, 0).reversed(closed).forTicks(ot);
		return new ParallelTransformation(rotation, scale);
	}
}
