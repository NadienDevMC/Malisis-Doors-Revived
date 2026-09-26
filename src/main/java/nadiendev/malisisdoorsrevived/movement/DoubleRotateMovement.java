package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.ParallelTransformation;
import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import net.malisis.core.animation.Translation;
import net.malisis.core.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorState;
import net.minecraft.world.phys.AABB;

/**
 * Both doors of a double door rotate on the same side.
 */
public class DoubleRotateMovement implements IDoorMovement
{
	private final boolean rightDirection;

	public DoubleRotateMovement(boolean right)
	{
		this.rightDirection = right;
	}

	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (be.isHingeLeft() == rightDirection)
			return null;
		return AABBUtils.rotate(IDoorMovement.getFullBoundingBox(), rightDirection ? 1 : -1);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean reversed = be.getDoorState().isClosingOrClosed();
		int ot = be.getOpeningTime();
		float angle = 90;
		float hingeX = 0.5F - DOOR_WIDTH / 2;
		float hingeZ = -0.5F + DOOR_WIDTH / 2;
		if (!be.isHingeLeft())
		{
			hingeX = -hingeX;
			angle = -angle;
		}

		Rotation rotation = new Rotation(angle).aroundAxis(0, 1, 0).offset(hingeX, 0, hingeZ).reversed(reversed).forTicks(ot);
		if (be.isHingeLeft() == rightDirection)
		{
			float x = 2 - DOOR_WIDTH;
			if (rightDirection)
				x *= -1;
			Translation translation = new Translation(0, 0, 0, x, 0, 0).reversed(reversed).forTicks(ot);
			return new ParallelTransformation(translation, rotation);
		}
		return rotation;
	}

	@Override
	public boolean shouldRender(DoorBlockEntity be, DoorPart part)
	{
		//the door moving over its pair is hidden once opened
		return !(be.isHingeLeft() == rightDirection && be.getDoorState() == DoorState.OPENED);
	}
}
