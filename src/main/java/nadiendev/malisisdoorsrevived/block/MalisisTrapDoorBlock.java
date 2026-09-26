package nadiendev.malisisdoorsrevived.block;

import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import nadiendev.malisiscorerevived.util.BlockEntityUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.TrapDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Animated trapdoor.
 *
 * @author Ordinastie
 */
public class MalisisTrapDoorBlock extends TrapDoorBlock implements EntityBlock, IDoorBlock
{
	protected final BlockSetType blockSetType;
	protected final DoorDescriptor descriptor;
	protected final DoorAppearance appearance;
	@Nullable
	protected final Supplier<? extends Item> item;

	public MalisisTrapDoorBlock(BlockSetType type, Properties properties, DoorDescriptor descriptor, DoorAppearance appearance, @Nullable Supplier<? extends Item> item)
	{
		super(type, properties.dynamicShape());
		this.blockSetType = type;
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
	public static DoorBlockEntity getDoor(BlockGetter level, BlockPos pos)
	{
		return BlockEntityUtils.getBlockEntity(TrapDoorBlockEntity.class, level, pos);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TrapDoorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if (type != ModBlockEntities.TRAPDOOR.get())
			return null;
		return (BlockEntityTicker<T>) (BlockEntityTicker<DoorBlockEntity>) DoorBlockEntity::tick;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.INVISIBLE;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if (appearance instanceof DoorAppearance.Copy copy && copy.isChangedBy(stack))
		{
			BlockPos target = pos;
			level.setBlock(target, copy.copyState(level.getBlockState(target)), Block.UPDATE_ALL);
			return InteractionResult.PASS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hit);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
	{
		if (appearance instanceof DoorAppearance.Copy copy)
			copy.copyState(state).randomTick(level, pos, random);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
	{
		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return InteractionResult.PASS;

		RedstoneBehavior behavior = be.getDescriptor().getRedstoneBehavior();
		if (behavior == RedstoneBehavior.REDSTONE_ONLY)
			return InteractionResult.PASS;

		if (level.isClientSide())
			return InteractionResult.SUCCESS;

		if (behavior == RedstoneBehavior.REDSTONE_LOCK && be.isPowered())
			return InteractionResult.CONSUME;

		be.openOrCloseDoor();
		level.gameEvent(player, be.getDoorState().isClosingOrClosed() ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos);
		return InteractionResult.CONSUME;
	}

	@Override
	protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer)
	{
		if (explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK)
		{
			//wind charges
			DoorBlockEntity be = getDoor(level, pos);
			if (be != null && explosion.canTriggerBlocks() && blockSetType.canOpenByWindCharge() && !state.getValue(POWERED))
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
		if (powered == state.getValue(POWERED))
			return;

		BlockState newState = state.setValue(POWERED, powered);
		if (state.getValue(WATERLOGGED))
			level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
		level.setBlock(pos, newState, Block.UPDATE_CLIENTS);

		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return;
		RedstoneBehavior behavior = be.getDescriptor().getRedstoneBehavior();
		if (behavior == RedstoneBehavior.STANDARD || behavior == RedstoneBehavior.REDSTONE_ONLY)
			be.onPowerChanged(powered);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return super.getShape(state, level, pos, context);
		return be.getShape(false, false);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return super.getCollisionShape(state, level, pos, context);
		return be.getShape(false, true);
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player)
	{
		return new ItemStack(asItem());
	}
}
