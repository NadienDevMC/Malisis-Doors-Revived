package nadiendev.malisisdoorsrevived.blockentity;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the custom doors made in the Door Factory.
 *
 * @author Ordinastie
 */
public class CustomDoorBlockEntity extends DoorBlockEntity
{
	private DoorMaterials materials = DoorMaterials.DEFAULT;

	public CustomDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.CUSTOM_DOOR.get(), pos, state);
	}

	public DoorMaterials getMaterials()
	{
		return materials;
	}

	public void setMaterials(DoorMaterials materials)
	{
		this.materials = materials != null ? materials : DoorMaterials.DEFAULT;
	}

	@Override
	protected void readSyncData(ValueInput input)
	{
		super.readSyncData(input);
		input.read("materials", DoorMaterials.CODEC).ifPresent(this::setMaterials);
	}

	@Override
	protected void saveAdditional(ValueOutput output)
	{
		super.saveAdditional(output);
		output.store("materials", DoorMaterials.CODEC, materials);
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter input)
	{
		super.applyImplicitComponents(input);
		DoorMaterials m = input.get(ModDataComponents.DOOR_MATERIALS.get());
		if (m != null)
			setMaterials(m);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder)
	{
		super.collectImplicitComponents(builder);
		builder.set(ModDataComponents.DOOR_MATERIALS.get(), materials);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(ValueOutput output)
	{
		super.removeComponentsFromTag(output);
		output.discard("materials");
	}
}
