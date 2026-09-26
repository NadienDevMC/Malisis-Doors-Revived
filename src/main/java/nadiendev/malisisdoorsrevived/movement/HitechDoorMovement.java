package nadiendev.malisisdoorsrevived.movement;

import nadiendev.malisiscorerevived.animation.ParallelTransformation;
import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisiscorerevived.animation.Translation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Hi-tech door : the latches rotate, then both panels slide sideways.<br>
 * Parts : {@link DoorPart#LEFT} and {@link DoorPart#RIGHT} for the panels, {@link DoorPart#TOP} and {@link DoorPart#BOTTOM} for the
 * latches (attached respectively to the left and right panels).
 */
public class HitechDoorMovement implements IDoorMovement
{
	private static final float SLIDE = 1.3125F;

	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return null;
	}

	private Translation getPanelTransformation(DoorBlockEntity be, boolean left)
	{
		float x = left ? -SLIDE : SLIDE;
		if (be.getDoorState().isClosingOrClosed())
			return new Translation(x, 0, 0, 0, 0, 0).forTicks(15);
		return new Translation(0, 0, 0, x, 0, 0).forTicks(15, 6);
	}

	private Rotation getLatchTransformation(DoorBlockEntity be)
	{
		if (be.getDoorState().isClosingOrClosed())
			return new Rotation(90, 0).aroundAxis(0, 0, 1).offset(0, 0.5F, 0).forTicks(6, 15);
		return new Rotation(0, 90).aroundAxis(0, 0, 1).offset(0, 0.5F, 0).forTicks(6);
	}

	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		return switch (part)
		{
			case LEFT -> getPanelTransformation(be, true);
			case RIGHT -> getPanelTransformation(be, false);
			case TOP -> new ParallelTransformation(getPanelTransformation(be, true), getLatchTransformation(be));
			case BOTTOM -> new ParallelTransformation(getPanelTransformation(be, false), getLatchTransformation(be));
			default -> null;
		};
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
