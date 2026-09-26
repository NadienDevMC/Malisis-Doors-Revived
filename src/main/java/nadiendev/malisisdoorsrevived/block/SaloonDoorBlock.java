package nadiendev.malisisdoorsrevived.block;

import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.SaloonDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Saloon doors : they swing when a player walks through them and can't be opened by hand.
 *
 * @author Ordinastie
 */
public class SaloonDoorBlock extends MalisisDoorBlock
{
	public SaloonDoorBlock(BlockSetType type, Properties properties, DoorDescriptor descriptor, DoorAppearance appearance)
	{
		super(type, properties, descriptor, appearance);
	}

	@Override
	protected DoorBlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new SaloonDoorBlockEntity(pos, state);
	}

	@Override
	protected BlockEntityType<? extends DoorBlockEntity> getBlockEntityType()
	{
		return ModBlockEntities.SALOON_DOOR.get();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
	{
		return InteractionResult.PASS;
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (!(entity instanceof Player) || level.isClientSide())
			return;
		if (!(getDoor(level, pos) instanceof SaloonDoorBlockEntity be) || be.isMoving())
			return;
		be.setOpenDirection(entity);
		be.openOrCloseDoor();
	}
}
