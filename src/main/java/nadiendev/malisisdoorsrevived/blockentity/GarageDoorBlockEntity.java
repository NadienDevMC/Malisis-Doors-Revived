package nadiendev.malisisdoorsrevived.blockentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nadiendev.malisiscorerevived.util.BlockEntityUtils;
import nadiendev.malisisdoorsrevived.door.DoorState;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Block entity of a garage door panel.<br>
 * Panels stacked vertically form a garage door, controlled by the top panel. Garage doors next to each other open together.
 *
 * @author Ordinastie
 */
public class GarageDoorBlockEntity extends DoorBlockEntity
{
	/** Time for a panel to move by one block. */
	public static final int PANEL_TIME = 10;

	/** Redstone signal received while the door was moving, applied once the animation ends. */
	private Boolean pendingPower;

	public GarageDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.GARAGE_DOOR.get(), pos, state);
	}

	@Nullable
	public GarageDoorBlockEntity getDoor(Direction dir)
	{
		if (level == null)
			return null;
		GarageDoorBlockEntity be = BlockEntityUtils.getBlockEntity(GarageDoorBlockEntity.class, level, worldPosition.relative(dir));
		if (be == null || be.getDirection() != getDirection())
			return null;
		if (dir.getAxis() != Direction.Axis.Y)
			return be;
		return be.getDoorState() == getDoorState() ? be : null;
	}

	public GarageDoorBlockEntity getTopDoor()
	{
		GarageDoorBlockEntity be = this;
		GarageDoorBlockEntity up;
		int i = 0;
		while ((up = be.getDoor(Direction.UP)) != null && i++ < 256)
			be = up;
		return be;
	}

	public boolean isTopDoor()
	{
		return getDoor(Direction.UP) == null;
	}

	/**
	 * @return the panels of this garage door, from the top one
	 */
	public List<GarageDoorBlockEntity> getDoors()
	{
		List<GarageDoorBlockEntity> doors = new ArrayList<>();
		GarageDoorBlockEntity be = getTopDoor();
		int i = 0;
		while (be != null && i++ < 256)
		{
			doors.add(be);
			be = be.getDoor(Direction.DOWN);
		}
		return doors;
	}

	@Override
	public int getOpeningTime()
	{
		return getTopDoor().getDoors().size() * PANEL_TIME;
	}

	@Override
	public DoorBlockEntity getDoubleDoor()
	{
		return null;
	}

	@Override
	public void onPowerChanged(boolean powered)
	{
		GarageDoorBlockEntity top = getTopDoor();
		if (top != this)
		{
			top.onPowerChanged(powered);
			return;
		}

		DoorState target = powered ? DoorState.OPENING : DoorState.CLOSING;
		if (doorState == target || doorState == (powered ? DoorState.OPENED : DoorState.CLOSED))
			return;

		for (GarageDoorBlockEntity be : getDoors())
			be.setDoorState(target);

		//open the garage doors next to this one
		Direction side = getDirection().getClockWise();
		for (Direction dir : new Direction[] { side, side.getOpposite() })
		{
			GarageDoorBlockEntity be = getDoor(dir);
			if (be != null)
				be.onPowerChanged(powered);
		}
	}

	/**
	 * Called when the redstone signal received by a panel changes.
	 */
	public void onSignalChanged(boolean powered)
	{
		GarageDoorBlockEntity top = getTopDoor();
		if (top.isMoving())
			top.pendingPower = powered;
		else
			top.onPowerChanged(powered);
	}

	@Override
	protected void tick()
	{
		super.tick();
		if (level.isClientSide() || moving || pendingPower == null)
			return;
		boolean powered = pendingPower;
		pendingPower = null;
		onPowerChanged(powered);
	}

	@Override
	public void openOrCloseDoor()
	{
		onPowerChanged(doorState.isClosingOrClosed());
	}

	@Override
	public void playSound()
	{}

	public AABB getRenderBounds()
	{
		int size = getDoors().size();
		return new AABB(worldPosition).inflate(size + 1);
	}
}
