package nadiendev.malisisdoorsrevived.movement;

import net.malisis.core.animation.AlphaTransform;
import net.malisis.core.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorState;
import net.minecraft.world.phys.AABB;

/**
 * The door fades away.
 */
public class VanishingDoorMovement implements IDoorMovement
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
		return new AlphaTransform(255, 0).reversed(be.getDoorState().isClosingOrClosed()).forTicks(be.getOpeningTime());
	}

	@Override
	public boolean shouldRender(DoorBlockEntity be, DoorPart part)
	{
		return be.getDoorState() != DoorState.OPENED;
	}
}
