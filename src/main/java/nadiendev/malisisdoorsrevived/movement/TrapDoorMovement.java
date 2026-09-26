package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.Rotation;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Vanilla like trapdoor movement : the trapdoor rotates around its hinge, against the block it's attached to.
 */
public class TrapDoorMovement implements IDoorMovement
{
	@Override
	public AABB getClosedBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return be.isTop() ? new AABB(0, 1 - DOOR_WIDTH, 0, 1, 1, 1) : new AABB(0, 0, 0, 1, DOOR_WIDTH, 1);
	}

	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return new AABB(0, 0, 0, 1, 1, DOOR_WIDTH);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		float f = -0.5F + DOOR_WIDTH / 2;
		boolean top = be.isTop();
		return new Rotation(top ? 90 : -90)	.aroundAxis(1, 0, 0)
											.offset(0, top ? -f : f, f)
											.reversed(be.getDoorState().isClosingOrClosed())
											.forTicks(be.getOpeningTime());
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
