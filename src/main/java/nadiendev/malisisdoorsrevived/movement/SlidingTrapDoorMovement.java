package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisiscorerevived.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The trapdoor slides toward the block it's attached to.
 */
public class SlidingTrapDoorMovement implements IDoorMovement
{
	@Override
	public AABB getClosedBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return be.isTop() ? new AABB(0, 1 - DOOR_WIDTH / 2, 0, 1, 1, 1) : new AABB(0, 0, 0, 1, DOOR_WIDTH / 2, 1);
	}

	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return getClosedBoundingBox(be, topBlock, type).move(0, 0, DOOR_WIDTH - 1);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		return new Translation(0, 0, 0, 0, 0, DOOR_WIDTH - 1).reversed(be.getDoorState().isClosingOrClosed()).forTicks(be.getOpeningTime());
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
