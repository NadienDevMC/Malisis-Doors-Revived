package nadiendev.malisisdoorsrevived.block;

import java.util.List;

import org.jspecify.annotations.Nullable;

import nadiendev.malisisdoorsrevived.blockentity.MultiBlockDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisiscorerevived.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

/**
 * Heavy 2x2 hatch placed against a wall, on the floor or on the ceiling. It is opened with a wheel.<br>
 * The {@link #FACING} property is the direction of the wall the hatch is attached to.<br>
 * The model and boxes are defined for a wall on the east (+X) side of the origin, the hatch spanning X from -1 to 1 and Z from 0 to 2.
 *
 * @author Ordinastie
 */
public class RustyHatchBlock extends MultiBlockDoorBlock
{
	public static final BooleanProperty TOP = BooleanProperty.create("top");

	private static final float F = 0.125F;
	private static final List<BlockPos> POSITIONS = List.of(new BlockPos(0, 0, 0),
															new BlockPos(-1, 0, 0),
															new BlockPos(0, 0, 1),
															new BlockPos(-1, 0, 1));

	public RustyHatchBlock(Properties properties, DoorAppearance appearance)
	{
		super(properties, new DoorDescriptor()	.setMovement(DoorRegistry.RUSTY_HATCH)
												.setSound(DoorRegistry.RUSTY_HATCH_SOUND)
												.setDoubleDoor(false)
												.setOpeningTime(60), appearance);
		registerDefaultState(defaultBlockState().setValue(TOP, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(TOP);
	}

	public static boolean isTop(BlockState state)
	{
		return state.hasProperty(TOP) && state.getValue(TOP);
	}

	/**
	 * The definitions are made for a wall on the east : one rotation less than the default south facing definitions.
	 */
	@Override
	public int getRotation(BlockState state)
	{
		return DirectionUtils.getRotationCount(state.getValue(FACING)) - 1;
	}

	@Nullable
	@Override
	protected BlockState getPlacementState(BlockPlaceContext context)
	{
		Direction side = context.getClickedFace();
		if (side.getAxis() == Direction.Axis.Y)
			return null;
		double hitY = context.getClickLocation().y - context.getClickedPos().getY();
		return defaultBlockState().setValue(FACING, side.getOpposite()).setValue(TOP, hitY > 0.5);
	}

	@Override
	protected List<BlockPos> getRelativePositions(BlockState state)
	{
		return POSITIONS;
	}

	@Override
	public AABB[] getBoundingBoxes(MultiBlockDoorBlockEntity be, BlockState state, boolean collision)
	{
		boolean top = isTop(state);
		float y0 = top ? 1 - F : 0;
		float y1 = top ? 1 : F;
		if (!be.isOpened() && !be.isMoving())
			return new AABB[] { new AABB(-1, y0, 0, 1, y1, 2) };
		if (collision)
			return null;
		//frame around the opening
		return new AABB[] {	new AABB(-1, y0, 0, 1, y1, F),
							new AABB(-1, y0, 2 - F, 1, y1, 2),
							new AABB(-1, y0, 0, -1 + F, y1, 2),
							new AABB(1 - F, y0, 0, 1, y1, 2) };
	}
}
