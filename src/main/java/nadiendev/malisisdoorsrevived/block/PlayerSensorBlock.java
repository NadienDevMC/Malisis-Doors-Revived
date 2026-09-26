package nadiendev.malisisdoorsrevived.block;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlayerSensorBlock extends DirectionalBlock
{
	public static final MapCodec<PlayerSensorBlock> CODEC = simpleCodec(PlayerSensorBlock::new);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	private static final int TICK_RATE = 5;
	private static final int MAX_RANGE = 6;

	private static final VoxelShape NORTH_SHAPE = box(2, 2, 14, 14, 4, 16);
	private static final VoxelShape SOUTH_SHAPE = box(2, 2, 0, 14, 4, 2);
	private static final VoxelShape WEST_SHAPE = box(14, 2, 2, 16, 4, 14);
	private static final VoxelShape EAST_SHAPE = box(0, 2, 2, 2, 4, 14);
	private static final VoxelShape UP_SHAPE = box(6, 0, 6, 10, 1, 10);
	private static final VoxelShape DOWN_SHAPE = box(6, 15, 6, 10, 16, 10);

	public PlayerSensorBlock(Properties properties)
	{
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec()
	{
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return switch (state.getValue(FACING))
		{
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			case UP -> UP_SHAPE;
			case DOWN -> DOWN_SHAPE;
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState state = defaultBlockState();
		for (Direction direction : context.getNearestLookingDirections())
		{
			state = state.setValue(FACING, direction.getOpposite());
			if (state.canSurvive(context.getLevel(), context.getClickedPos()))
				return state;
		}
		return null;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		Direction facing = state.getValue(FACING);
		BlockPos support = pos.relative(facing.getOpposite());
		return level.getBlockState(support).isFaceSturdy(level, support, facing);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
	{
		if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos))
			return Blocks.AIR.defaultBlockState();
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston)
	{
		if (!oldState.is(state.getBlock()))
			level.scheduleTick(pos, this, TICK_RATE);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		level.scheduleTick(pos, this, TICK_RATE);

		boolean detected = !level.getEntitiesOfClass(Player.class, getDetectionBox(level, pos, state.getValue(FACING)), EntitySelector.NO_SPECTATORS)
									.isEmpty();
		if (detected == state.getValue(POWERED))
			return;

		level.setBlock(pos, state.setValue(POWERED, detected), Block.UPDATE_ALL);
		updateNeighbours(state, level, pos);
	}

	private AABB getDetectionBox(Level level, BlockPos pos, Direction facing)
	{
		double x1 = pos.getX(), x2 = pos.getX() + 1;
		double z1 = pos.getZ(), z2 = pos.getZ() + 1;
		if (facing.getAxis() == Direction.Axis.X)
		{
			x1 -= 1;
			x2 += 1;
		}
		else if (facing.getAxis() == Direction.Axis.Z)
		{
			z1 -= 1;
			z2 += 1;
		}

		int step = facing == Direction.UP ? 1 : -1;
		int range = 1;
		while (range < MAX_RANGE && level.isEmptyBlock(pos.above(step * range)))
			range++;

		int y1 = Math.min(pos.getY(), pos.getY() + step * range);
		int y2 = Math.max(pos.getY(), pos.getY() + step * range);
		return new AABB(x1, y1, z1, x2, y2, z2);
	}

	@Override
	protected boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	{
		if (!movedByPiston && !state.is(newState.getBlock()) && state.getValue(POWERED))
			updateNeighbours(state, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	private void updateNeighbours(BlockState state, Level level, BlockPos pos)
	{
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(state.getValue(FACING).getOpposite()), this);
	}
}
