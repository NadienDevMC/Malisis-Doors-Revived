package nadiendev.malisisdoorsrevived.movement;

import javax.annotation.Nullable;

import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.FenceGateBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Fence gate movement : each leaf rotates around its post, away from the player who opened it.<br>
 * When two fence gates are next to each other, each one opens as a single leaf.
 */
public class FenceGateMovement implements IDoorMovement
{
	@Override
	public AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		//fence gates use the vanilla shapes
		return null;
	}

	@Nullable
	@Override
	public Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part)
	{
		boolean left = part == DoorPart.LEFT;
		if (be instanceof FenceGateBlockEntity gate)
		{
			//a gate paired with another one opens as a single leaf, hinged on the post away from the other gate
			Boolean pairedOnLeft = gate.isPairedOnLeft();
			if (pairedOnLeft != null)
				left = !pairedOnLeft;
		}

		float hinge = -0.5F + 0.125F / 2;
		float angle = -90;
		if (!left)
		{
			hinge = -hinge;
			angle = -angle;
		}

		return new Rotation(angle)	.aroundAxis(0, 1, 0)
									.offset(hinge, 0, 0)
									.reversed(be.getDoorState().isClosingOrClosed())
									.forTicks(be.getOpeningTime());
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}
}
