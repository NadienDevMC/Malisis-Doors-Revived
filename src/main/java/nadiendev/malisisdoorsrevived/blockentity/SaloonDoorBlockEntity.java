package nadiendev.malisisdoorsrevived.blockentity;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the saloon doors : the doors swing away from the entity walking through them.
 *
 * @author Ordinastie
 */
public class SaloonDoorBlockEntity extends DoorBlockEntity
{
	private boolean openBackward = false;

	public SaloonDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.SALOON_DOOR.get(), pos, state);
	}

	public boolean isBackward()
	{
		return openBackward;
	}

	public void setBackward(boolean backward)
	{
		this.openBackward = backward;
	}

	/**
	 * Sets the direction the door will swing to, depending on the position of the entity.
	 */
	public void setOpenDirection(Entity entity)
	{
		double entityPos;
		double doorPos;
		switch (getDirection())
		{
			case NORTH -> {
				entityPos = entity.getZ();
				doorPos = worldPosition.getZ() + 0.5;
			}
			case SOUTH -> {
				entityPos = -entity.getZ();
				doorPos = -worldPosition.getZ() - 0.5;
			}
			case EAST -> {
				entityPos = -entity.getX();
				doorPos = -worldPosition.getX() - 0.5;
			}
			default -> {
				entityPos = entity.getX();
				doorPos = worldPosition.getX() + 0.5;
			}
		}
		openBackward = entityPos < doorPos;
		DoorBlockEntity dd = getDoubleDoor();
		if (dd instanceof SaloonDoorBlockEntity saloon)
			saloon.openBackward = openBackward;
	}

	@Override
	protected void readSyncData(ValueInput input)
	{
		super.readSyncData(input);
		openBackward = input.getBooleanOr("backward", false);
	}

	@Override
	protected void saveAdditional(ValueOutput output)
	{
		super.saveAdditional(output);
		output.putBoolean("backward", openBackward);
	}
}
