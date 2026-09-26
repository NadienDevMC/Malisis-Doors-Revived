package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.ChainedTransformation;
import nadiendev.malisiscorerevived.animation.ParallelTransformation;
import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.SaloonDoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Saloon doors swing back and forth when an entity walks through them.
 */
public class SaloonDoorMovement implements IDoorMovement
{
	@Override
	public AABB getClosedBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return getOpenBoundingBox(be, topBlock, type);
	}

	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION)
			return null;
		float f = 1 / 16F;
		return new AABB(0, topBlock ? 0 : 0.25F, .5F - f / 2, 1, topBlock ? 0.75F : 1, .5F + f / 2);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		float f = 1 / 16F;
		float angle = -90;
		float hingeX = 0.5F - f / 2;
		if (be.isHingeLeft())
			angle = -angle;
		if (be instanceof SaloonDoorBlockEntity saloon && saloon.isBackward())
			angle = -angle;

		int t = be.getOpeningTime() / 4;
		ChainedTransformation swing = new ChainedTransformation(swing(angle, hingeX, t),
																swing(-angle * 1.5F, hingeX, t),
																swing(angle * .75F, hingeX, t),
																swing(-angle * .25F, hingeX, t));

		//the model has its hinge on the right, flip it for doors with the hinge on the left
		if (be.isHingeLeft())
			return swing;
		return new ParallelTransformation(new Rotation(180).aroundAxis(0, 1, 0), swing);
	}

	private Rotation swing(float angle, float hingeX, int ticks)
	{
		return new Rotation(angle).aroundAxis(0, 1, 0).offset(hingeX, 0, 0).movement(Transformation.SINUSOIDAL).forTicks(ticks);
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
