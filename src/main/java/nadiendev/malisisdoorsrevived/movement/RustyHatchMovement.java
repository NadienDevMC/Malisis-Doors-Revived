package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.ParallelTransformation;
import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.block.RustyHatchBlock;
import net.minecraft.world.phys.AABB;

/**
 * The wheel of the rusty hatch spins, then the hatch rotates around its hinge.<br>
 * Offsets are relative to the center of the block of the OBJ model (the hatch model spans X from 0 to 2).
 */
public class RustyHatchMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		//bounding boxes are handled by the block
		return null;
	}

	private Rotation getDoorTransformation(DoorBlockEntity be)
	{
		boolean top = RustyHatchBlock.isTop(be.getBlockState());
		float f = -0.5F + 0.125F;
		float offY = top ? f : -0.5F;
		int t = be.getOpeningTime() / 2;
		Rotation rotation = new Rotation(top ? 90 : -90).aroundAxis(0, 0, 1).offset(f, offY, 0).movement(Transformation.SINUSOIDAL);
		if (be.getDoorState().isClosingOrClosed())
			rotation.reversed(true);
		else
			rotation.delay(t);
		return rotation.forTicks(t);
	}

	private Rotation getHandleTransformation(DoorBlockEntity be)
	{
		int t = be.getOpeningTime() / 2;
		Rotation rotation = new Rotation(400).aroundAxis(0, 1, 0).offset(0.5F, 0, 0.5F).movement(Transformation.SINUSOIDAL);
		if (be.getDoorState().isClosingOrClosed())
			rotation.delay(t).reversed(true);
		return rotation.forTicks(t);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		if (part == DoorPart.HANDLE)
			return new ParallelTransformation(getDoorTransformation(be), getHandleTransformation(be));
		if (part == DoorPart.DOOR)
			return getDoorTransformation(be);
		return null;
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
