package nadiendev.malisisdoorsrevived.event;

import org.jspecify.annotations.Nullable;

import nadiendev.malisisdoorsrevived.config.MalisisDoorsConfig;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopies;
import nadiendev.malisisdoorsrevived.animated.DoorIntegration;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Replaces the vanilla doors, trapdoors and fence gates by their animated version.<br>
 * The replacement happens when they are placed by a player, or when a player uses one that already exists in the world (generated
 * villages for example). The animated versions drop the vanilla items so the replacement is transparent for the players.
 *
 * @author Ordinastie
 */
public class VanillaDoorReplacer
{
	/**
	 * Gets the animated version of the block, if enabled in the config.
	 */
	@Nullable
	public static Block getReplacement(Block block)
	{
		if (!MalisisDoorsConfig.SERVER_SPEC.isLoaded())
			return null;
		DoorIntegration integration = AnimatedCopies.getIntegration(block);
		if (integration != null && !MalisisDoorsConfig.INTEGRATIONS.get(integration.modId()).get())
			return null;
		if (block instanceof DoorBlock && !MalisisDoorsConfig.ANIMATE_VANILLA_DOORS.get())
			return null;
		if (block instanceof TrapDoorBlock && !MalisisDoorsConfig.ANIMATE_VANILLA_TRAPDOORS.get())
			return null;
		if (block instanceof FenceGateBlock && !MalisisDoorsConfig.ANIMATE_VANILLA_FENCE_GATES.get())
			return null;
		return ModBlocks.getAnimated(block);
	}

	/**
	 * Replaces the block at the position by its animated version.
	 *
	 * @return the new state, or null if the block wasn't replaced
	 */
	@Nullable
	public static BlockState replace(LevelAccessor level, BlockPos pos)
	{
		BlockState state = level.getBlockState(pos);
		Block replacement = getReplacement(state.getBlock());
		if (replacement == null)
			return null;

		//the block entity of doors is in the bottom half
		if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
		{
			pos = pos.below();
			state = level.getBlockState(pos);
			if (state.getBlock() != replacement && getReplacement(state.getBlock()) != replacement)
				return null;
		}

		BlockState newState = copyProperties(state, replacement.defaultBlockState());
		//flag 2 : no neighbor updates, the shape update of the top half of the door will convert it too
		level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
		if (newState.getBlock() instanceof DoorBlock)
		{
			BlockPos above = pos.above();
			BlockState top = level.getBlockState(above);
			if (!(top.getBlock() == replacement))
				level.setBlock(above, newState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Block.UPDATE_CLIENTS);
		}
		return newState;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static BlockState copyProperties(BlockState from, BlockState to)
	{
		for (Property property : from.getProperties())
		{
			if (to.hasProperty(property))
				to = to.setValue(property, from.getValue(property));
		}
		return to;
	}

	/**
	 * Vanilla block placed by a player : replace it.
	 */
	public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event)
	{
		if (event.getLevel().isClientSide())
			return;
		replace(event.getLevel(), event.getPos());
	}

	/**
	 * Vanilla block used by a player : replace it and use the animated version instead.
	 */
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		Level level = event.getLevel();
		Player player = event.getEntity();
		BlockPos pos = event.getPos();
		if (player.isSpectator())
			return;
		BlockState state = level.getBlockState(pos);
		Block replacement = getReplacement(state.getBlock());
		if (replacement == null)
			return;
		//iron doors and trapdoors can't be used by hand, they are replaced when placed only
		if (replacement instanceof IDoorBlock door && door.getDescriptor().getRedstoneBehavior() == RedstoneBehavior.REDSTONE_ONLY)
			return;
		//let the players place blocks against doors when sneaking
		if (player.isSecondaryUseActive() && !player.getItemInHand(event.getHand()).isEmpty())
			return;
		if (replacement instanceof IDoorBlock door && door.getAppearance() instanceof DoorAppearance.Copy copy && copy.isChangedBy(player.getItemInHand(event.getHand())))
			return;

		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.SUCCESS);
		if (level.isClientSide() || event.getHand() != InteractionHand.MAIN_HAND)
			return;

		if (replace(level, pos) == null)
			return;

		BlockState newState = level.getBlockState(pos);
		BlockHitResult hit = event.getHitVec();
		newState.useWithoutItem(level, player, hit);
	}
}
