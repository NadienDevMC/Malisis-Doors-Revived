package nadiendev.malisisdoorsrevived.block;

import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.LevelAccessor;
import javax.annotation.Nullable;

import net.malisis.core.multiblock.IMultiBlockController;
import net.malisis.core.multiblock.MultiBlockHelper;
import net.malisis.core.util.BlockEntityUtils;
import nadiendev.malisisdoorsrevived.blockentity.ForcefieldBlockEntity;
import nadiendev.malisisdoorsrevived.item.ForcefieldItem;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Origin of a forcefield. Forcefields are created, toggled and destroyed with the {@link ForcefieldItem forcefield controller}.
 *
 * @author Ordinastie
 */
public class ForcefieldBlock extends Block implements EntityBlock, IMultiBlockController, LiquidBlockContainer
{
	public ForcefieldBlock(Properties properties)
	{
		super(properties.dynamicShape().noOcclusion());
	}

	@Nullable
	public static ForcefieldBlockEntity getForcefield(BlockGetter level, BlockPos origin)
	{
		return BlockEntityUtils.getBlockEntity(ForcefieldBlockEntity.class, level, origin);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ForcefieldBlockEntity(pos, state);
	}

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

	@Override
	public VoxelShape getPartShape(BlockState originState, BlockGetter level, BlockPos origin, BlockPos partPos, CollisionContext context, boolean collision)
	{
		ForcefieldBlockEntity be = getForcefield(level, origin);
		if (be == null)
			return collision ? Shapes.empty() : Shapes.block();
		return be.getShape(partPos, collision);
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

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return useItemOnPart(stack, state, level, pos, pos, player, hand, hit);
	}

	@Override
	public ItemInteractionResult useItemOnPart(ItemStack stack, BlockState originState, Level level, BlockPos origin, BlockPos partPos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if (!(stack.getItem() instanceof ForcefieldItem item))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (level.isClientSide())
			return ItemInteractionResult.SUCCESS;

		ForcefieldBlockEntity be = getForcefield(level, origin);
		if (be == null)
			return ItemInteractionResult.CONSUME;

		int size = be.getSize();
		if (player.isShiftKeyDown())
		{
			if (item.drain(stack, size * ForcefieldItem.DESTROY_COST, player))
			{
				//removing the origin removes the parts
				level.removeBlock(origin, false);
				level.playSound(null, origin, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1, 1);
			}
		}
		else if (item.drain(stack, size * ForcefieldItem.TOGGLE_COST, player))
		{
			be.switchForcefield();
			level.playSound(null, origin, be.isOpened() ? SoundEvents.BEACON_DEACTIVATE : SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.5F, 1.5F);
		}
		return ItemInteractionResult.CONSUME;
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
	{
		if (!state.is(newState.getBlock()))
		{
			ForcefieldBlockEntity be = getForcefield(level, pos);
			if (be != null)
				MultiBlockHelper.removeParts(level, pos, be.getPositions());
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
	{
		return new ItemStack(ModItems.FORCEFIELD_CONTROLLER.get());
	}

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
