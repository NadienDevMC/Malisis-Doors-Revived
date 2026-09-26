package nadiendev.malisisdoorsrevived.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import nadiendev.malisiscorerevived.util.BlockEntityUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.FenceGateBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Animated fence gate.
 *
 * @author Ordinastie
 */
public class MalisisFenceGateBlock extends FenceGateBlock implements EntityBlock, IDoorBlock
{
	protected final DoorDescriptor descriptor;
	protected final DoorAppearance appearance;
	@Nullable
	protected final Supplier<? extends Item> item;

	public MalisisFenceGateBlock(WoodType type, Properties properties, DoorDescriptor descriptor, DoorAppearance appearance, @Nullable Supplier<? extends Item> item)
	{
		super(type, properties.dynamicShape());
		this.descriptor = descriptor;
		this.appearance = appearance;
		this.item = item;
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

	@Override
	public Item asItem()
	{
		return item != null ? item.get() : super.asItem();
	}


	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos)
	{
		if (appearance instanceof DoorAppearance.Copy copy)
			return copy.copyState(state).getDestroyProgress(player, level, pos);
		return super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player)
	{
		if (appearance instanceof DoorAppearance.Copy copy)
			return copy.copyState(state).canHarvestBlock(level, pos, player);
		return super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params)
	{
		if (appearance instanceof DoorAppearance.Copy copy && getLootTable().isEmpty())
			return copy.copyState(state).getDrops(params);
		return super.getDrops(state, params);
	}

	@Nullable
	public static FenceGateBlockEntity getGate(BlockGetter level, BlockPos pos)
	{
		return BlockEntityUtils.getBlockEntity(FenceGateBlockEntity.class, level, pos);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new FenceGateBlockEntity(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if (type != ModBlockEntities.FENCE_GATE.get())
			return null;
		return (BlockEntityTicker<T>) (BlockEntityTicker<DoorBlockEntity>) DoorBlockEntity::tick;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.INVISIBLE;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
	{
		FenceGateBlockEntity be = getGate(level, pos);
		if (be == null)
			return InteractionResult.PASS;
		if (level.isClientSide())
			return InteractionResult.SUCCESS;

		boolean opening = be.getDoorState().isClosingOrClosed();
		if (opening)
		{
			//like vanilla, the gate opens away from the player
			Direction direction = player.getDirection();
			if (state.getValue(FACING) == direction.getOpposite())
			{
				FenceGateBlockEntity pair = be.getDoubleDoor();
				level.setBlock(pos, state.setValue(FACING, direction), Block.UPDATE_ALL);
				if (pair != null)
					level.setBlock(pair.getBlockPos(), pair.getBlockState().setValue(FACING, direction), Block.UPDATE_ALL);
			}
		}

		be.openOrCloseDoor();
		level.gameEvent(player, opening ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		return InteractionResult.CONSUME;
	}

	@Override
	protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer)
	{
		if (explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK)
		{
			//wind charges
			DoorBlockEntity be = getGate(level, pos);
			if (be != null && explosion.canTriggerBlocks() && !state.getValue(POWERED))
				be.openOrCloseDoor();
			return;
		}
		super.onExplosionHit(state, level, pos, explosion, dropConsumer);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston)
	{
		if (level.isClientSide())
			return;

		boolean powered = level.hasNeighborSignal(pos);
		if (state.getValue(POWERED) == powered)
			return;

		level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
		DoorBlockEntity be = getGate(level, pos);
		if (be != null)
			be.onPowerChanged(powered);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		DoorBlockEntity be = getGate(level, pos);
		if (be != null && be.isMoving())
			return Shapes.empty();
		return super.getCollisionShape(state, level, pos, context);
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player)
	{
		return new ItemStack(asItem());
	}
}
