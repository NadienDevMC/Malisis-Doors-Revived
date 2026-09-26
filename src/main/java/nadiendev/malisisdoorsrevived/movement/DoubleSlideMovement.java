package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Transformation;
import net.malisis.core.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Both doors of a double door slide on the same side.
 */
public class DoubleSlideMovement implements IDoorMovement
{
	private final boolean rightDirection;

	public DoubleSlideMovement(boolean right)
	{
		this.rightDirection = right;
	}

	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (be.isHingeLeft() == rightDirection)
			return null;
		return IDoorMovement.getFullBoundingBox().move(rightDirection ? -1 + DOOR_WIDTH : 1 - DOOR_WIDTH, 0, 0);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		float x = 1 - DOOR_WIDTH;
		if (be.isHingeLeft() == rightDirection)
			x += 1;
		if (rightDirection)
			x *= -1;
		return new Translation(0, 0, 0, x, 0, 0).reversed(be.getDoorState().isClosingOrClosed()).forTicks(be.getOpeningTime());
	}
}
