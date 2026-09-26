package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.ChainedTransformation;
import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisiscorerevived.animation.Translation;
import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The door rotates around its center, then slides against the side of the block.
 */
public class RotateAndPlaceMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		AABB aabb = AABBUtils.rotate(IDoorMovement.getFullBoundingBox(), be.isHingeLeft() ? -1 : 1);
		return aabb.move(0, 0, -.5F);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		int ot = be.getOpeningTime() / 2;
		float angle = be.isHingeLeft() ? 90 : -90;
		Rotation rotation = new Rotation(angle).aroundAxis(0, 1, 0).offset(0, 0, -0.5F).forTicks(ot);
		Translation translation = new Translation(0, 0, 0.5F - DOOR_WIDTH).forTicks(ot);
		return new ChainedTransformation(rotation, translation).reversed(be.getDoorState().isClosingOrClosed());
	}
}
