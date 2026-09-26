package nadiendev.malisisdoorsrevived.block;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.malisis.core.util.BlockEntityUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Animated two blocks high door.<br>
 * The door extends the vanilla {@link DoorBlock} so it keeps all its behaviors (placement, villagers, pathfinding, wind charges) but the
 * opening and closing is delegated to its {@link DoorBlockEntity} which plays the animation. The OPEN property is only updated once the
 * animation is finished.
 *
 * @author Ordinastie
 */
public class MalisisDoorBlock extends DoorBlock implements EntityBlock, IDoorBlock
{
	protected final DoorDescriptor descriptor;
	protected final DoorAppearance appearance;
	/** Item dropped and picked (vanilla item for the animated copies of the vanilla doors). */
	@Nullable
	protected final Supplier<? extends Item> item;

	public MalisisDoorBlock(BlockSetType type, Properties properties, DoorDescriptor descriptor, DoorAppearance appearance, @Nullable Supplier<? extends Item> item)
	{
		super(type, properties.dynamicShape());
		this.descriptor = descriptor;
		this.appearance = appearance;
		this.item = item;
	}

	public MalisisDoorBlock(BlockSetType type, Properties properties, DoorDescriptor descriptor, DoorAppearance appearance)
	{
		this(type, properties, descriptor, appearance, null);
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
	public String getDescriptionId()
	{
		return appearance instanceof DoorAppearance.Copy copy ? copy.block().get().getDescriptionId() : super.getDescriptionId();
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
		if (appearance instanceof DoorAppearance.Copy copy && getLootTable() == BuiltInLootTables.EMPTY)
			return copy.copyState(state).getDrops(params);
		return super.getDrops(state, params);
	}

	/**
	 * Gets the {@link DoorBlockEntity} of the door at the position (the block entity is in the bottom half).
	 */
	@Nullable
	public static DoorBlockEntity getDoor(@Nullable BlockGetter level, BlockPos pos)
	{
		if (level == null)
			return null;
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof MalisisDoorBlock))
			return null;
		if (isTop(state))
			pos = pos.below();
		return BlockEntityUtils.getBlockEntity(DoorBlockEntity.class, level, pos);
	}

	public static boolean isTop(BlockState state)
	{
		return state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.UPPER;
	}

	//#region Block entity
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		if (isTop(state))
			return null;
		return createBlockEntity(pos, state);
	}

	protected DoorBlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new DoorBlockEntity(pos, state);
	}

	protected BlockEntityType<? extends DoorBlockEntity> getBlockEntityType()
	{
		return ModBlockEntities.DOOR.get();
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if (isTop(state) || type != getBlockEntityType())
			return null;
		return (BlockEntityTicker<T>) (BlockEntityTicker<DoorBlockEntity>) DoorBlockEntity::tick;
	}
	//#end Block entity

	@Override
	protected RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	//#region Interactions
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

		if (behavior == RedstoneBehavior.REDSTONE_LOCK && (be.isPowered() || be.isDoubleDoorPowered()))
			return InteractionResult.CONSUME;

		be.openOrCloseDoor();
		level.gameEvent(player, be.getDoorState().isClosingOrClosed() ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos);
		return InteractionResult.CONSUME;
	}

	/**
	 * Called by villagers and wind charges.
	 */
	@Override
	public void setOpen(@Nullable Entity entity, Level level, BlockState state, BlockPos pos, boolean open)
	{
		if (!state.is(this) || level.isClientSide())
			return;
		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return;
		RedstoneBehavior behavior = be.getDescriptor().getRedstoneBehavior();
		if (behavior == RedstoneBehavior.REDSTONE_ONLY || (behavior == RedstoneBehavior.REDSTONE_LOCK && (be.isPowered() || be.isDoubleDoorPowered())))
			return;
		if (open ? be.open() : be.close())
			level.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston)
	{
		if (level.isClientSide())
			return;

		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return;

		BlockPos otherHalf = isTop(state) ? pos.below() : pos.above();
		boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(otherHalf);
		if (!defaultBlockState().is(neighborBlock) && powered != state.getValue(POWERED))
		{
			level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
			RedstoneBehavior behavior = be.getDescriptor().getRedstoneBehavior();
			if (behavior == RedstoneBehavior.STANDARD || behavior == RedstoneBehavior.REDSTONE_ONLY)
				be.onPowerChanged(powered);
		}

		be.updateCentered();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		DoorBlockEntity be = getDoor(level, pos);
		if (be != null && !level.isClientSide())
			be.updateCentered();
	}
	//#end Interactions

	//#region Shapes
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return super.getShape(state, level, pos, context);
		return be.getShape(isTop(state), false);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		DoorBlockEntity be = getDoor(level, pos);
		if (be == null)
			return super.getCollisionShape(state, level, pos, context);
		return be.getShape(isTop(state), true);
	}
	//#end Shapes

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
	{
		ItemStack stack = new ItemStack(asItem());
		DoorBlockEntity be = getDoor(level, pos);
		//doors edited in the Door Factory keep their settings
		if (item == null && be != null && be.getSettings() != null)
			stack.set(ModDataComponents.DOOR_SETTINGS.get(), be.getSettings());
		return stack;
	}
}
