package nadiendev.malisisdoorsrevived.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import nadiendev.malisiscorerevived.multiblock.MultiBlockHelper;
import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisisdoorsrevived.blockentity.ForcefieldBlockEntity;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Forcefield controller : creates a forcefield between two corners, toggles it and destroys it.<br>
 * The controller uses energy that regenerates over time.
 *
 * @author Ordinastie
 */
public class ForcefieldItem extends Item
{
	public static final int MAX_ENERGY = 2000;
	public static final int CREATE_COST = 20;
	public static final int TOGGLE_COST = 5;
	public static final int DESTROY_COST = 20;
	/** Time (in ticks) after which the selection of the first corner is canceled. */
	public static final int SELECTION_TIMEOUT = 100;

	/**
	 * First corner of the forcefield being created.
	 */
	public record Selection(BlockPos pos, long time)
	{
		public static final Codec<Selection> CODEC = RecordCodecBuilder.create(instance -> instance
				.group(BlockPos.CODEC.fieldOf("pos").forGetter(Selection::pos), Codec.LONG.fieldOf("time").forGetter(Selection::time))
				.apply(instance, Selection::new));
		public static final StreamCodec<ByteBuf, Selection> STREAM_CODEC = StreamCodec.composite(	BlockPos.STREAM_CODEC,
																									Selection::pos,
																									ByteBufCodecs.VAR_LONG,
																									Selection::time,
																									Selection::new);
	}

	public ForcefieldItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}

	//#region Energy
	public static int getEnergy(ItemStack stack)
	{
		//a new controller is fully charged
		return stack.getOrDefault(ModDataComponents.ENERGY.get(), MAX_ENERGY);
	}

	public static void setEnergy(ItemStack stack, int energy)
	{
		stack.set(ModDataComponents.ENERGY.get(), Mth.clamp(energy, 0, MAX_ENERGY));
	}

	/**
	 * Drains energy from the controller.
	 *
	 * @return true if the controller had enough energy
	 */
	public boolean drain(ItemStack stack, int amount, @Nullable Player player)
	{
		if (player != null && player.isCreative())
			return true;
		int energy = getEnergy(stack);
		if (energy < amount)
			return false;
		setEnergy(stack, energy - amount);
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot)
	{
		Selection selection = stack.get(ModDataComponents.FORCEFIELD_SELECTION.get());
		if (selection != null && level.getGameTime() - selection.time() > SELECTION_TIMEOUT)
			stack.remove(ModDataComponents.FORCEFIELD_SELECTION.get());

		int energy = getEnergy(stack);
		if (energy < MAX_ENERGY)
			setEnergy(stack, energy + (entity instanceof Player p && p.isCreative() ? 20 : 5));
	}

	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return getEnergy(stack) < MAX_ENERGY;
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13F * getEnergy(stack) / MAX_ENERGY);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		return 0x3FC8FF;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
	{
		return true;
	}
	//#end Energy

	//#region Forcefield creation
	@Nullable
	public static Selection getSelection(ItemStack stack)
	{
		return stack.get(ModDataComponents.FORCEFIELD_SELECTION.get());
	}

	/**
	 * Gets the number of blocks of the forcefield, or -1 if the area is not a plane.
	 */
	public static int getDoorSize(AABB aabb)
	{
		int diffX = (int) Math.round(aabb.getXsize());
		int diffY = (int) Math.round(aabb.getYsize());
		int diffZ = (int) Math.round(aabb.getZsize());
		if (diffX == 1 && diffZ != 1)
			return diffY * diffZ;
		if (diffY == 1)
			return diffX * diffZ;
		if (diffZ == 1 && diffX != 1)
			return diffX * diffY;
		return -1;
	}

	/**
	 * Checks whether a forcefield can be created between the selection and the position.
	 */
	public static boolean isValid(ItemStack stack, BlockPos start, BlockPos end, boolean creative)
	{
		int size = getDoorSize(AABBUtils.encompassing(start, end));
		return size > 1 && (creative || getEnergy(stack) >= size * CREATE_COST);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		ItemStack stack = context.getItemInHand();
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

		Selection selection = getSelection(stack);
		if (selection == null)
		{
			if (!level.isClientSide())
				stack.set(ModDataComponents.FORCEFIELD_SELECTION.get(), new Selection(pos, level.getGameTime()));
			return InteractionResult.SUCCESS;
		}

		if (level.isClientSide())
			return InteractionResult.SUCCESS;

		stack.remove(ModDataComponents.FORCEFIELD_SELECTION.get());
		BlockPos start = selection.pos();
		AABB aabb = AABBUtils.encompassing(start, pos);
		if (!isValid(stack, start, pos, player != null && player.isCreative()))
		{
			level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 1, 0.5F);
			return InteractionResult.FAIL;
		}

		BlockPos origin = BlockPos.containing(aabb.minX, aabb.minY, aabb.minZ);
		List<BlockPos> positions = new ArrayList<>();
		BlockPos.betweenClosed(origin, BlockPos.containing(aabb.maxX - 1, aabb.maxY - 1, aabb.maxZ - 1)).forEach(p -> positions.add(p.immutable()));

		BlockPlaceContext placeContext = new BlockPlaceContext(context);
		for (BlockPos p : positions)
		{
			if (!level.getBlockState(p).canBeReplaced(placeContext) || (player != null && !level.mayInteract(player, p)))
			{
				level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 1, 0.5F);
				return InteractionResult.FAIL;
			}
		}

		BlockState state = ModBlocks.FORCEFIELD.get().defaultBlockState();
		if (!level.setBlock(origin, state, Block.UPDATE_ALL) || !(level.getBlockEntity(origin) instanceof ForcefieldBlockEntity be))
			return InteractionResult.FAIL;

		be.setArea(aabb.move(-origin.getX(), -origin.getY(), -origin.getZ()));
		MultiBlockHelper.placeParts(level, origin, positions);
		drain(stack, getDoorSize(aabb) * CREATE_COST, player);
		level.playSound(null, origin, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1, 1.5F);
		return InteractionResult.CONSUME;
	}
	//#end Forcefield creation

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag)
	{
		tooltip.accept(Component.translatable("item.malisisdoors.forcefield_controller.energy", getEnergy(stack), MAX_ENERGY).withStyle(ChatFormatting.AQUA));
		Selection selection = getSelection(stack);
		if (selection != null)
			tooltip.accept(Component.translatable("item.malisisdoors.forcefield_controller.selection", selection.pos().toShortString())
									.withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable("item.malisisdoors.forcefield_controller.help").withStyle(ChatFormatting.DARK_GRAY));
	}
}
