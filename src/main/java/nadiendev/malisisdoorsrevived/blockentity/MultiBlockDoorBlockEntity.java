package nadiendev.malisisdoorsrevived.blockentity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisisdoorsrevived.block.MultiBlockDoorBlock;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Block entity at the origin of the multi-block doors (big doors, 3x3 doors, rusty hatch, hi-tech door).
 *
 * @author Ordinastie
 */
public class MultiBlockDoorBlockEntity extends DoorBlockEntity
{
	/** Block used to render the frame of the big doors. */
	@Nullable
	protected BlockState frameState;

	private final Map<Long, VoxelShape> partShapes = new HashMap<>();

	public MultiBlockDoorBlockEntity(BlockPos pos, BlockState state)
	{
		this(ModBlockEntities.MULTIBLOCK_DOOR.get(), pos, state);
	}

	public MultiBlockDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	public BlockState getFrameState()
	{
		return frameState != null ? frameState : Blocks.QUARTZ_BLOCK.defaultBlockState();
	}

	public void setFrameState(@Nullable BlockState frameState)
	{
		this.frameState = frameState;
		sync();
	}

	@Override
	public DoorBlockEntity getDoubleDoor()
	{
		return null;
	}

	/**
	 * @return the number of 90 degrees rotations applied to the model and bounding boxes
	 */
	public int getRotation()
	{
		BlockState state = getBlockState();
		return state.getBlock() instanceof MultiBlockDoorBlock block ? block.getRotation(state) : 0;
	}

	/**
	 * Gets the shape of the part of the multi-block at partPos, relative to partPos.
	 */
	public VoxelShape getPartShape(BlockPos partPos, boolean collision)
	{
		long key = partPos.subtract(worldPosition).asLong() * 2 + (collision ? 1 : 0);
		VoxelShape shape = partShapes.get(key);
		if (shape == null)
		{
			shape = computePartShape(partPos, collision);
			partShapes.put(key, shape);
		}
		return shape;
	}

	protected VoxelShape computePartShape(BlockPos partPos, boolean collision)
	{
		BlockState state = getBlockState();
		if (!(state.getBlock() instanceof MultiBlockDoorBlock block))
			return Shapes.block();

		AABB[] boxes = block.getBoundingBoxes(this, state, collision);
		if (boxes == null)
			return Shapes.empty();

		int rotation = getRotation();
		AABB[] parts = new AABB[boxes.length];
		for (int i = 0; i < boxes.length; i++)
		{
			if (boxes[i] == null)
				continue;
			AABB rotated = AABBUtils.rotate(boxes[i], rotation);
			parts[i] = AABBUtils.intersect(rotated, worldPosition, partPos);
		}
		return AABBUtils.toShape(parts);
	}

	@Override
	protected void invalidateShapes()
	{
		super.invalidateShapes();
		if (partShapes != null)
			partShapes.clear();
	}

	/**
	 * @return the bounding box of all the blocks of the multi-block, for rendering
	 */
	public AABB getRenderBounds()
	{
		BlockState state = getBlockState();
		if (!(state.getBlock() instanceof MultiBlockDoorBlock block))
			return new AABB(worldPosition);
		AABB aabb = new AABB(worldPosition);
		for (BlockPos pos : block.getPositions(worldPosition, state))
			aabb = aabb.minmax(new AABB(pos));
		return aabb.inflate(1);
	}

	@Override
	protected void readSyncData(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.readSyncData(tag, registries);
		if (tag.contains("frame"))
			BlockState.CODEC.parse(NbtOps.INSTANCE, tag.get("frame")).result().ifPresent(s -> frameState = s);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		if (frameState != null)
			BlockState.CODEC.encodeStart(NbtOps.INSTANCE, frameState).result().ifPresent(t -> tag.put("frame", t));
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput input)
	{
		super.applyImplicitComponents(input);
		BlockState frame = input.get(ModDataComponents.FRAME_STATE.get());
		if (frame != null)
			frameState = frame;
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder)
	{
		super.collectImplicitComponents(builder);
		if (frameState != null)
			builder.set(ModDataComponents.FRAME_STATE.get(), frameState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(CompoundTag tag)
	{
		super.removeComponentsFromTag(tag);
		tag.remove("frame");
	}
}
