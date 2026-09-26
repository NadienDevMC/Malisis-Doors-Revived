package nadiendev.malisisdoorsrevived.block;

import nadiendev.malisisdoorsrevived.blockentity.CustomDoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.HitResult;

/**
 * Door made in the Door Factory from three blocks : one for the frame, one for each panel.
 *
 * @author Ordinastie
 */
public class CustomDoorBlock extends MalisisDoorBlock
{
	public CustomDoorBlock(BlockSetType type, Properties properties, DoorDescriptor descriptor)
	{
		super(type, properties, descriptor, new DoorAppearance.Custom());
	}

	@Override
	protected DoorBlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new CustomDoorBlockEntity(pos, state);
	}

	@Override
	protected BlockEntityType<? extends DoorBlockEntity> getBlockEntityType()
	{
		return ModBlockEntities.CUSTOM_DOOR.get();
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player)
	{
		ItemStack stack = super.getCloneItemStack(level, pos, state, includeData, player);
		if (getDoor(level, pos) instanceof CustomDoorBlockEntity be)
		{
			stack.set(ModDataComponents.DOOR_MATERIALS.get(), be.getMaterials());
			if (be.getSettings() != null)
				stack.set(ModDataComponents.DOOR_SETTINGS.get(), be.getSettings());
		}
		return stack;
	}
}
