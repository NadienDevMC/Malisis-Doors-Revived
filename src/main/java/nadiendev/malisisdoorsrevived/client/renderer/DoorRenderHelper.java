package nadiendev.malisisdoorsrevived.client.renderer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import nadiendev.malisiscorerevived.animation.Transformable;
import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisiscorerevived.util.DirectionUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.movement.DoorPart;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import net.minecraft.core.Direction;

/**
 * Helpers shared by the door renderers.
 *
 * @author Ordinastie
 */
public class DoorRenderHelper
{
	/**
	 * Pushes the pose and sets up the transformations for a part of the door : rotation to the facing of the door, centering, then
	 * animation.<br>
	 * The caller must call {@link #end(PoseStack)} once the part is rendered.
	 *
	 * @param poseStack the pose stack
	 * @param be the door
	 * @param rotation the number of 90 degrees rotations around Y
	 * @param part the part to animate, or null for no animation
	 * @param partialTick the partial tick
	 * @return the transformable (holds the alpha)
	 */
	public static Transformable begin(PoseStack poseStack, DoorBlockEntity be, int rotation, @Nullable DoorPart part, float partialTick)
	{
		poseStack.pushPose();
		Transformable transformable = Transformable.centered(poseStack);
		transformable.rotate(rotation * 90, 0, 1, 0);
		if (be.isCentered())
			transformable.translate(0, 0, 0.5F - IDoorMovement.DOOR_WIDTH / 2);
		if (part != null)
		{
			Transformation<?> transformation = be.getMovement().getTransformation(be, part);
			if (transformation != null)
				transformation.transform(transformable, be.getElapsedTime(partialTick));
		}
		return transformable;
	}

	/**
	 * Same as {@link #begin(PoseStack, DoorBlockEntity, int, DoorPart, float)}, the rotation being deduced from the direction of the door.
	 */
	public static Transformable begin(PoseStack poseStack, DoorBlockEntity be, Direction direction, @Nullable DoorPart part, float partialTick)
	{
		return begin(poseStack, be, DirectionUtils.getRotationCount(direction), part, partialTick);
	}

	/**
	 * Translates the pose back to the corner of the block, ready to render the model.
	 */
	public static void toCorner(PoseStack poseStack)
	{
		poseStack.translate(-0.5F, -0.5F, -0.5F);
	}

	public static void end(PoseStack poseStack)
	{
		poseStack.popPose();
	}
}
