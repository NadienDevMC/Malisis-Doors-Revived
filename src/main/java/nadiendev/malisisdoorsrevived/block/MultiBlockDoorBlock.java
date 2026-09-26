package nadiendev.malisisdoorsrevived.block;

import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.LevelAccessor;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.malisis.core.multiblock.IMultiBlockController;
import net.malisis.core.multiblock.MultiBlockHelper;
import net.malisis.core.util.BlockEntityUtils;
import net.malisis.core.util.DirectionUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.MultiBlockDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Base class for the doors spanning several blocks.<br>
 * The block at the origin holds the {@link MultiBlockDoorBlockEntity} and the rest of the space is filled with
 * {@link net.malisis.core.multiblock.MultiBlockPartBlock parts} delegating to this block.<br>
 * Positions and bounding boxes are defined for a door facing {@link Direction#SOUTH}, relative to the origin.
 *
 * @author Ordinastie
 */
public abstract class MultiBlockDoorBlock extends Block implements EntityBlock, IMultiBlockController, IDoorBlock, LiquidBlockContainer
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	protected final DoorDescriptor descriptor;
	protected final DoorAppearance appearance;

	protected MultiBlockDoorBlock(Properties properties, DoorDescriptor descriptor, DoorAppearance appearance)
	{
		super(properties.dynamicShape().noOcclusion());
		this.descriptor = descriptor;
		this.appearance = appearance;
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
		return appearance;
	}

	//#region Multi-block definition
	/**
	 * @return the positions occupied by the door (facing south), relative to the origin
	 */
	protected abstract List<BlockPos> getRelativePositions(BlockState state);

	/**
	 * Gets the bounding boxes of the door (facing south), relative to the origin.
	 *
	 * @param be the block entity of the door
	 * @param state the state of the origin
	 * @param collision whether the collision boxes are requested (otherwise, the selection boxes)
	 * @return the bounding boxes
	 */
	@Nullable
	public abstract AABB[] getBoundingBoxes(MultiBlockDoorBlockEntity be, BlockState state, boolean collision);

	/**
	 * @return the number of 90 degrees rotations (right-handed, around Y) to apply to the definitions made facing south
	 */
	public int getRotation(BlockState state)
	{
		return DirectionUtils.getRotationCount(state.getValue(FACING));
	}

	/**
	 * Gets the world positions occupied by the door.
	 */
	public List<BlockPos> getPositions(BlockPos origin, BlockState state)
	{
		int rotation = Math.floorMod(getRotation(state), 4);
		List<BlockPos> list = new ArrayList<>();
		for (BlockPos rel : getRelativePositions(state))
		{
			int x = rel.getX(), z = rel.getZ();
			for (int i = 0; i < rotation; i++)
			{
				int tmp = x;
				x = z;
				z = -tmp;
			}
			list.add(origin.offset(x, rel.getY(), z));
		}
		return list;
	}

	/**
	 * Gets the state placed for the context, before the multi-block space is checked.
	 */
	@Nullable
	protected BlockState getPlacementState(BlockPlaceContext context)
	{
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	//#end Multi-block definition

	//#region Placement
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState state = getPlacementState(context);
		if (state == null)
			return null;
		if (!MultiBlockHelper.canPlace(context, getPositions(context.getClickedPos(), state)))
			return null;
		return state;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide())
			MultiBlockHelper.placeParts(level, pos, getPositions(pos, state));
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	{
		if (!state.is(newState.getBlock()))
			MultiBlockHelper.removeParts(level, pos, getPositions(pos, state));
		super.onRemove(state, level, pos, newState, movedByPiston);
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
	//#end Placement

	//#region Block entity
	@Nullable
	public static MultiBlockDoorBlockEntity getDoor(BlockGetter level, BlockPos origin)
	{
		return BlockEntityUtils.getBlockEntity(MultiBlockDoorBlockEntity.class, level, origin);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new MultiBlockDoorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if (type != ModBlockEntities.MULTIBLOCK_DOOR.get())
			return null;
		return (BlockEntityTicker<T>) (BlockEntityTicker<DoorBlockEntity>) DoorBlockEntity::tick;
	}
	//#end Block entity

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
	{
		return true;
	}

	//#region Interactions
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
	{
		MultiBlockDoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return InteractionResult.PASS;
		if (be.getDescriptor().getRedstoneBehavior() == RedstoneBehavior.REDSTONE_ONLY)
			return InteractionResult.PASS;
		if (level.isClientSide())
			return InteractionResult.SUCCESS;
		if (!canToggle(be))
			return InteractionResult.CONSUME;

		be.openOrCloseDoor();
		level.gameEvent(player, be.getDoorState().isClosingOrClosed() ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos);
		return InteractionResult.CONSUME;
	}

	/**
	 * Whether the door can be opened or closed by hand right now.
	 */
	protected boolean canToggle(MultiBlockDoorBlockEntity be)
	{
		return !(be.getDescriptor().getRedstoneBehavior() == RedstoneBehavior.REDSTONE_LOCK && be.isPowered());
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		updatePower(state, level, pos);
	}

	@Override
	public void onPartNeighborChanged(BlockState originState, Level level, BlockPos origin, BlockPos partPos, Block neighborBlock, BlockPos neighborPos)
	{
		updatePower(originState, level, origin);
	}

	protected void updatePower(BlockState state, Level level, BlockPos origin)
	{
		if (level.isClientSide())
			return;
		boolean powered = false;
		for (BlockPos pos : getPositions(origin, state))
		{
			if (level.hasNeighborSignal(pos))
			{
				powered = true;
				break;
			}
		}
		if (powered == state.getValue(POWERED))
			return;

		level.setBlock(origin, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
		MultiBlockDoorBlockEntity be = getDoor(level, origin);
		if (be == null)
			return;
		RedstoneBehavior behavior = be.getDescriptor().getRedstoneBehavior();
		if (behavior == RedstoneBehavior.STANDARD || behavior == RedstoneBehavior.REDSTONE_ONLY)
			be.onPowerChanged(powered);
	}
	//#end Interactions

	//#region Shapes
	@Override
	public VoxelShape getPartShape(BlockState originState, BlockGetter level, BlockPos origin, BlockPos partPos, CollisionContext context, boolean collision)
	{
		MultiBlockDoorBlockEntity be = getDoor(level, origin);
		if (be == null)
			return collision ? Shapes.empty() : Shapes.block();
		return be.getPartShape(partPos, collision);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return getPartShape(state, level, pos, pos, context, false);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return getPartShape(state, level, pos, pos, context, true);
	}
	//#end Shapes

	/**
	 * Fluids can't flow into this block : it would break it.
	 */
	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
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
