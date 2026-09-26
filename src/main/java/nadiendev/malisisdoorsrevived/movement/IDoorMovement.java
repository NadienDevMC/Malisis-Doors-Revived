package nadiendev.malisisdoorsrevived.movement;

import org.jspecify.annotations.Nullable;

import nadiendev.malisiscorerevived.animation.Transformation;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * Defines how a door moves : its animation and its bounding boxes when opened and closed.<br>
 * Everything is defined for a door facing {@link net.minecraft.core.Direction#SOUTH}, the renderer and the block rotate it to the actual
 * facing.<br>
 * Rotations and scales offsets are relative to the center of the block (see {@link nadiendev.malisiscorerevived.animation.Transformable}).
 *
 * @author Ordinastie
 */
public interface IDoorMovement
{
	float DOOR_WIDTH = 0.1875F;

	/**
	 * Gets the bounding box of the door when it's opened.
	 *
	 * @param be the door
	 * @param topBlock whether the box for the top block is requested
	 * @param type the type of bounding box
	 * @return the bounding box, or null for none
	 */
	@Nullable
	AABB getOpenBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type);

	/**
	 * Gets the bounding box of the door when it's closed.
	 */
	@Nullable
	default AABB getClosedBoundingBox(DoorBlockEntity be, boolean topBlock, BoundingBoxType type)
	{
		return getFullBoundingBox();
	}

	/**
	 * Gets the transformation to apply to the part of the door.
	 *
	 * @param be the door
	 * @param part the part to animate
	 * @return the transformation, or null if the part isn't animated
	 */
	@Nullable
	Transformation<?> getTransformation(DoorBlockEntity be, DoorPart part);

	/**
	 * Whether the part of the door should be rendered.
	 */
	default boolean shouldRender(DoorBlockEntity be, DoorPart part)
	{
		return true;
	}

	/**
	 * Special movements are specific to a door and can't be selected in the Door Factory.
	 */
	default boolean isSpecial()
	{
		return false;
	}

	static AABB getFullBoundingBox()
	{
		return new AABB(0, 0, 0, 1, 1, DOOR_WIDTH);
	}

	static AABB getHalfBoundingBox()
	{
		return getFullBoundingBox();
	}
}
