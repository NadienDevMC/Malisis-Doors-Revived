package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.ChainedTransformation;
import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisiscorerevived.animation.Translation;
import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * The door rotates around its hinge, then slides into the wall.
 */
public class RotateAndSlideMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return AABBUtils.rotate(IDoorMovement.getFullBoundingBox(), be.isHingeLeft() ? -1 : 1);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		int ot = be.getOpeningTime() / 2;
		float angle = 90;
		float hinge = -0.5F + DOOR_WIDTH / 2;
		float tr = -1 + DOOR_WIDTH;
		if (be.isHingeLeft())
		{
			hinge = -hinge;
			angle = -angle;
			tr = -tr;
		}

		Rotation rotation = new Rotation(angle).aroundAxis(0, 1, 0).offset(hinge, 0, -0.5F + DOOR_WIDTH / 2).forTicks(ot);
		Translation translation = new Translation(tr, 0, 0).forTicks(ot);
		return new ChainedTransformation(rotation, translation).reversed(be.getDoorState().isClosingOrClosed());
	}
}
