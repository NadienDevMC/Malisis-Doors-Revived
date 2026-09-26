package nadiendev.malisisdoorsrevived.block;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.LevelAccessor;
import org.jspecify.annotations.Nullable;

import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisiscorerevived.util.BlockEntityUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.GarageDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.door.DoorState;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Garage door panel. Stack panels to build a garage door, it opens with redstone and slides into the ceiling.
 *
 * @author Ordinastie
 */
public class GarageDoorBlock extends Block implements EntityBlock, IDoorBlock, LiquidBlockContainer
{
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	private static final AABB PANEL = new AABB(0, 0, 0.5F - IDoorMovement.DOOR_WIDTH / 2, 1, 1, 0.5F + IDoorMovement.DOOR_WIDTH / 2);

	private final DoorDescriptor descriptor = new DoorDescriptor()	.setSound(DoorRegistry.SILENT_SOUND)
																	.setDoubleDoor(false)
																	.setRedstoneBehavior(RedstoneBehavior.REDSTONE_ONLY);

	public GarageDoorBlock(Properties properties)
	{
		super(properties.dynamicShape().noOcclusion());
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, POWERED);
	}

	@Override
	public DoorDescriptor getDescriptor()
	{
		return descriptor;
	}

	@Override
	public DoorAppearance getAppearance()
	{
		return DoorAppearance.SELF;
	}

	@Nullable
	public static GarageDoorBlockEntity getDoor(BlockGetter level, BlockPos pos)
	{
		return BlockEntityUtils.getBlockEntity(GarageDoorBlockEntity.class, level, pos);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		//align with the panels above or below
		for (Direction dir : new Direction[] { Direction.UP, Direction.DOWN })
		{
			BlockState state = context.getLevel().getBlockState(context.getClickedPos().relative(dir));
			if (state.getBlock() == this)
				return defaultBlockState().setValue(FACING, state.getValue(FACING));
		}
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new GarageDoorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if (type != ModBlockEntities.GARAGE_DOOR.get())
			return null;
		return (BlockEntityTicker<T>) (BlockEntityTicker<DoorBlockEntity>) DoorBlockEntity::tick;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.INVISIBLE;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston)
	{
		if (level.isClientSide() || neighborBlock == this)
			return;
		GarageDoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return;

		boolean powered = level.hasNeighborSignal(pos);
		if (powered != state.getValue(POWERED))
		{
			level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
			be.onSignalChanged(powered);
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		GarageDoorBlockEntity be = getDoor(level, pos);
		if (be != null && (be.isMoving() || be.getDoorState() != DoorState.CLOSED))
			return Shapes.empty();
		return AABBUtils.toShape(AABBUtils.rotate(PANEL, state.getValue(FACING)));
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

	/**
	 * Fluids can't flow into this block : it would break it.
	 */
	@Override
	public boolean canPlaceLiquid(@Nullable LivingEntity player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
	{
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState)
	{
		return false;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type)
	{
		return false;
	}
}
