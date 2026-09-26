package nadiendev.malisisdoorsrevived.blockentity;

import javax.annotation.Nullable;

import net.malisis.core.util.BlockEntityUtils;
import net.malisis.core.util.DirectionUtils;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the animated fence gates.<br>
 * Two fence gates next to each other are paired : each one opens as a single leaf.
 *
 * @author Ordinastie
 */
public class FenceGateBlockEntity extends DoorBlockEntity
{
	public FenceGateBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.FENCE_GATE.get(), pos, state);
	}

	@Nullable
	@Override
	public FenceGateBlockEntity getDoubleDoor()
	{
		if (level == null)
			return null;
		Direction side = getDirection().getClockWise();
		FenceGateBlockEntity be = BlockEntityUtils.getBlockEntity(FenceGateBlockEntity.class, level, worldPosition.relative(side));
		if (isMatchingDoubleDoor(be))
			return be;
		be = BlockEntityUtils.getBlockEntity(FenceGateBlockEntity.class, level, worldPosition.relative(side.getOpposite()));
		return isMatchingDoubleDoor(be) ? be : null;
	}

	@Override
	public boolean isMatchingDoubleDoor(@Nullable DoorBlockEntity be)
	{
		if (be == null || be == this)
			return false;
		if (getBlockState().getBlock() != be.getBlockState().getBlock())
			return false;
		if (isOpened() != be.isOpened())
			return false;
		return getDirection().getAxis() == be.getDirection().getAxis();
	}

	/**
	 * Gets the side where the paired fence gate is, relative to the model (facing south).
	 *
	 * @return true if the pair is on the left (-X), false if on the right (+X), null if the gate is not paired
	 */
	@Nullable
	public Boolean isPairedOnLeft()
	{
		FenceGateBlockEntity pair = getDoubleDoor();
		if (pair == null)
			return null;
		Direction right = DirectionUtils.rotate(Direction.EAST, DirectionUtils.getRotationCount(getDirection()));
		return !worldPosition.relative(right).equals(pair.getBlockPos());
	}
}
