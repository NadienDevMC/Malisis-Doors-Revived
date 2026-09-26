package nadiendev.malisisdoorsrevived.blockentity;

import java.util.List;

import javax.annotation.Nullable;

import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisiscorerevived.util.Timer;
import nadiendev.malisisdoorsrevived.block.MalisisDoorBlock;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.DoorSettings;
import nadiendev.malisisdoorsrevived.door.DoorState;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.movement.BoundingBoxType;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import nadiendev.malisisdoorsrevived.sound.IDoorSound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Block entity of all the animated doors.<br>
 * Handles the state of the door (opening, opened, closing, closed), the timing of the animation, the double doors, redstone, proximity
 * detection and automatic closing.<br>
 * The server is authoritative : the state is sent to the clients which play the animation locally.
 *
 * @author Ordinastie
 */
public class DoorBlockEntity extends BlockEntity
{
	private static final DoorDescriptor DEFAULT_DESCRIPTOR = new DoorDescriptor();
	private static final AABB DETECTION_BOX = new AABB(0, 0, -1, 1, 2, 3);

	protected DoorState doorState = DoorState.CLOSED;
	protected boolean moving;
	protected boolean timerNeedsStart;
	protected final Timer timer = new Timer();
	protected boolean centered;
	/** Settings overriding the default descriptor of the block (doors edited in the Door Factory). */
	@Nullable
	protected DoorSettings settings;
	@Nullable
	private DoorDescriptor settingsDescriptor;

	private final VoxelShape[] shapeCache = new VoxelShape[4];

	public DoorBlockEntity(BlockPos pos, BlockState state)
	{
		this(ModBlockEntities.DOOR.get(), pos, state);
	}

	public DoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		if (state.hasProperty(BlockStateProperties.OPEN) && state.getValue(BlockStateProperties.OPEN))
			doorState = DoorState.OPENED;
	}

	//#region Getters
	public DoorDescriptor getDescriptor()
	{
		if (settingsDescriptor != null)
			return settingsDescriptor;
		return getBlockState().getBlock() instanceof IDoorBlock door ? door.getDescriptor() : DEFAULT_DESCRIPTOR;
	}

	public IDoorMovement getMovement()
	{
		return getDescriptor().getMovement();
	}

	public int getOpeningTime()
	{
		return getDescriptor().getOpeningTime();
	}

	public DoorState getDoorState()
	{
		return doorState;
	}

	public boolean isMoving()
	{
		return moving;
	}

	public Timer getTimer()
	{
		return timer;
	}

	/**
	 * Gets the time elapsed since the start of the animation, for rendering.<br>
	 * When the door is not moving, a time long past the end of the animation is returned.
	 */
	public float getElapsedTime(float partialTick)
	{
		if (!moving || timerNeedsStart)
			return 100_000;
		return timer.elapsedTime(level, partialTick);
	}

	public Direction getDirection()
	{
		BlockState state = getBlockState();
		return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.SOUTH;
	}

	public boolean isHingeLeft()
	{
		BlockState state = getBlockState();
		return state.hasProperty(BlockStateProperties.DOOR_HINGE) && state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.LEFT;
	}

	/**
	 * @return true for trapdoors placed in the top half of the block
	 */
	public boolean isTop()
	{
		BlockState state = getBlockState();
		return state.hasProperty(BlockStateProperties.HALF) && state.getValue(BlockStateProperties.HALF) == Half.TOP;
	}

	public boolean isTopBlock(BlockPos pos)
	{
		return worldPosition.above().equals(pos);
	}

	/**
	 * @return whether the door is opened (not moving)
	 */
	public boolean isOpened()
	{
		BlockState state = getBlockState();
		if (state.hasProperty(BlockStateProperties.OPEN))
			return state.getValue(BlockStateProperties.OPEN);
		return doorState == DoorState.OPENED;
	}

	public boolean isPowered()
	{
		BlockState state = getBlockState();
		return state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED);
	}

	public boolean isCentered()
	{
		return centered;
	}

	@Nullable
	public DoorSettings getSettings()
	{
		return settings;
	}

	public void setSettings(@Nullable DoorSettings settings)
	{
		this.settings = settings;
		this.settingsDescriptor = settings != null ? DoorDescriptor.fromSettings(settings) : null;
		invalidateShapes();
	}
	//#end Getters

	//#region Door logic
	public void openOrCloseDoor()
	{
		if (doorState == DoorState.OPENED || doorState == DoorState.OPENING)
			close();
		else
			open();
	}

	public boolean open()
	{
		if (doorState == DoorState.OPENING || doorState == DoorState.OPENED)
			return false;

		setDoorState(DoorState.OPENING);
		DoorBlockEntity dd = getDoubleDoor();
		if (dd != null)
			dd.setDoorState(DoorState.OPENING);
		return true;
	}

	public boolean close()
	{
		if (doorState == DoorState.CLOSING || doorState == DoorState.CLOSED)
			return false;

		setDoorState(DoorState.CLOSING);
		DoorBlockEntity dd = getDoubleDoor();
		if (dd != null)
			dd.setDoorState(DoorState.CLOSING);
		return true;
	}

	/**
	 * Changes the state of the door.<br>
	 * On the server, the state is sent to the clients and the block state is updated when the animation ends.
	 *
	 * @param newState the new state
	 */
	public void setDoorState(DoorState newState)
	{
		if (doorState == newState)
			return;

		doorState = newState;
		invalidateShapes();
		if (level == null)
			return;

		if (newState.isMoving())
		{
			if (moving && !timerNeedsStart)
			{
				//reverse the animation from its current point
				long remaining = Math.max(0, getOpeningTime() - timer.elapsedTick(level));
				timer.setRelativeStart(level, -remaining);
			}
			else
			{
				timer.start(level);
				moving = true;
				timerNeedsStart = false;
			}
		}
		else
		{
			moving = false;
			timerNeedsStart = false;
			if (!level.isClientSide())
				updateOpenProperty(newState == DoorState.OPENED);
		}

		if (!level.isClientSide())
		{
			playSound();
			sync();
		}
	}

	/**
	 * Updates the OPEN property of the block state at the end of the animation.
	 */
	protected void updateOpenProperty(boolean open)
	{
		BlockState state = level.getBlockState(worldPosition);
		if (state.getBlock() == getBlockState().getBlock() && state.hasProperty(BlockStateProperties.OPEN)
				&& state.getValue(BlockStateProperties.OPEN) != open)
			level.setBlock(worldPosition, state.setValue(BlockStateProperties.OPEN, open), Block.UPDATE_ALL);
	}

	public void playSound()
	{
		if (level == null || level.isClientSide())
			return;
		IDoorSound sound = getDescriptor().getSound();
		SoundEvent event = sound != null ? sound.getSound(doorState) : null;
		if (event != null)
			level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 1F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	/**
	 * Called when the redstone power received by the door changes.
	 */
	public void onPowerChanged(boolean powered)
	{
		if (powered)
			open();
		else if (!isDoubleDoorPowered())
			close();
	}

	public boolean isDoubleDoorPowered()
	{
		DoorBlockEntity dd = getDoubleDoor();
		return dd != null && dd.isPowered();
	}

	/**
	 * Gets the other door of a double door.
	 *
	 * @return the other door, or null if this door is not part of a double door
	 */
	@Nullable
	public DoorBlockEntity getDoubleDoor()
	{
		if (level == null || !getDescriptor().isDoubleDoor() || !(getBlockState().getBlock() instanceof DoorBlock))
			return null;

		Direction offset = getDirection().getCounterClockWise();
		if (isHingeLeft())
			offset = offset.getOpposite();
		DoorBlockEntity be = MalisisDoorBlock.getDoor(level, worldPosition.relative(offset));
		return isMatchingDoubleDoor(be) ? be : null;
	}

	public boolean isMatchingDoubleDoor(@Nullable DoorBlockEntity be)
	{
		if (be == null || be == this)
			return false;
		if (getBlockState().getBlock() != be.getBlockState().getBlock())
			return false;
		if (getDirection() != be.getDirection())
			return false;
		if (getMovement() != be.getMovement())
			return false;
		if (isOpened() != be.isOpened())
			return false;
		return isHingeLeft() != be.isHingeLeft();
	}

	/**
	 * Checks whether the door should be centered in its block (when placed between fences, walls, bars or panes).
	 */
	public boolean shouldCenter()
	{
		if (level == null)
			return false;
		Direction side = getDirection().getClockWise();
		return isCenterBlock(level.getBlockState(worldPosition.relative(side)))
				|| isCenterBlock(level.getBlockState(worldPosition.relative(side.getOpposite())));
	}

	public static boolean isCenterBlock(BlockState state)
	{
		return state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS) || state.getBlock() instanceof IronBarsBlock;
	}

	public void setCentered(boolean centered)
	{
		if (this.centered == centered)
			return;
		this.centered = centered;
		invalidateShapes();
		sync();
	}

	/**
	 * Updates whether this door (and its double door) should be centered.
	 */
	public void updateCentered()
	{
		boolean center = shouldCenter();
		DoorBlockEntity dd = getDoubleDoor();
		if (dd != null)
		{
			center |= dd.shouldCenter();
			dd.setCentered(center);
		}
		setCentered(center);
	}

	protected boolean hasPlayer()
	{
		AABB box = AABBUtils.rotate(DETECTION_BOX, getDirection().getOpposite()).move(worldPosition);
		List<Player> players = level.getEntitiesOfClass(Player.class, box, p -> !p.isSpectator());
		return !players.isEmpty();
	}

	protected boolean doubleDoorHasPlayer()
	{
		DoorBlockEntity dd = getDoubleDoor();
		return dd != null && dd.hasPlayer();
	}
	//#end Door logic

	//#region Ticking
	public static void tick(Level level, BlockPos pos, BlockState state, DoorBlockEntity be)
	{
		be.tick();
	}

	protected void tick()
	{
		if (timerNeedsStart)
		{
			timer.start(level);
			timerNeedsStart = false;
		}

		//animation finished, update state
		if (moving && timer.elapsedTick(level) >= getOpeningTime())
			setDoorState(doorState == DoorState.CLOSING ? DoorState.CLOSED : DoorState.OPENED);

		if (level.isClientSide())
			return;

		//the block state was changed by something else (wind charge, commands...)
		if (!moving && getBlockState().hasProperty(BlockStateProperties.OPEN))
		{
			boolean open = getBlockState().getValue(BlockStateProperties.OPEN);
			if (open != (doorState == DoorState.OPENED))
			{
				doorState = open ? DoorState.OPENED : DoorState.CLOSED;
				timer.start(level);
				invalidateShapes();
				sync();
			}
		}

		DoorDescriptor desc = getDescriptor();
		//player in proximity, open the doors
		if (desc.hasProximityDetection())
		{
			if (hasPlayer() || doubleDoorHasPlayer())
			{
				if (!open() && doorState == DoorState.OPENED)
					timer.setRelativeStart(level, -getOpeningTime());
				return;
			}
			if (desc.getAutoCloseTime() <= 0 && doorState == DoorState.OPENED && !isPowered() && !isDoubleDoorPowered())
			{
				close();
				return;
			}
		}

		//time to auto-close
		if (desc.getAutoCloseTime() > 0 && doorState == DoorState.OPENED && !isPowered() && !isDoubleDoorPowered()
				&& timer.elapsedTick(level) > getOpeningTime() + desc.getAutoCloseTime())
			close();
	}
	//#end Ticking

	//#region Shapes
	/**
	 * Gets the shape of the door for the block at the bottom (or top) of the door.
	 *
	 * @param top whether the shape of the top block is requested
	 * @param collision whether the collision shape is requested
	 * @return the shape
	 */
	public VoxelShape getShape(boolean top, boolean collision)
	{
		int index = (top ? 2 : 0) + (collision ? 1 : 0);
		VoxelShape shape = shapeCache[index];
		if (shape == null)
		{
			shape = computeShape(top, collision);
			shapeCache[index] = shape;
		}
		return shape;
	}

	protected VoxelShape computeShape(boolean top, boolean collision)
	{
		IDoorMovement movement = getMovement();
		BoundingBoxType type = collision ? BoundingBoxType.COLLISION : BoundingBoxType.SELECTION;
		AABB aabb;
		if (moving)
		{
			if (collision)
				return Shapes.empty();
			aabb = movement.getClosedBoundingBox(this, top, type);
		}
		else
			aabb = isOpened() ? movement.getOpenBoundingBox(this, top, type) : movement.getClosedBoundingBox(this, top, type);

		if (aabb == null)
			return Shapes.empty();
		if (centered)
			aabb = aabb.move(0, 0, 0.5F - IDoorMovement.DOOR_WIDTH / 2);
		aabb = AABBUtils.rotate(aabb, getDirection());
		return AABBUtils.clip(AABBUtils.toShape(aabb));
	}

	protected void invalidateShapes()
	{
		for (int i = 0; i < shapeCache.length; i++)
			shapeCache[i] = null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBlockState(BlockState state)
	{
		super.setBlockState(state);
		invalidateShapes();
	}
	//#end Shapes

	//#region Sync
	protected void sync()
	{
		if (level == null || level.isClientSide())
			return;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		readSyncData(tag, registries);
		doorState = DoorState.byOrdinal(tag.getInt("state"));
		moving = doorState.isMoving();
		//the start of the timer is not saved : restart it (animation and auto-close delay)
		timerNeedsStart = true;
		invalidateShapes();
	}

	/**
	 * Reads the data shared between the server and the client, except the door state.
	 */
	protected void readSyncData(CompoundTag tag, HolderLookup.Provider registries)
	{
		centered = tag.getBoolean("centered");
		if (tag.contains("settings"))
			DoorSettings.CODEC.parse(NbtOps.INSTANCE, tag.get("settings")).result().ifPresent(this::setSettings);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putInt("state", doorState.ordinal());
		tag.putBoolean("centered", centered);
		if (settings != null)
			DoorSettings.CODEC.encodeStart(NbtOps.INSTANCE, settings).result().ifPresent(t -> tag.put("settings", t));
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput input)
	{
		super.applyImplicitComponents(input);
		DoorSettings s = input.get(ModDataComponents.DOOR_SETTINGS.get());
		if (s != null)
			setSettings(s);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder)
	{
		super.collectImplicitComponents(builder);
		if (settings != null)
			builder.set(ModDataComponents.DOOR_SETTINGS.get(), settings);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(CompoundTag tag)
	{
		super.removeComponentsFromTag(tag);
		tag.remove("settings");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		return saveWithoutMetadata(registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		CompoundTag tag = packet.getTag();
		if (tag.isEmpty())
			return;
		readSyncData(tag, registries);
		invalidateShapes();
		//play the animation locally
		setDoorState(DoorState.byOrdinal(tag.getInt("state")));
	}
	//#end Sync
}
