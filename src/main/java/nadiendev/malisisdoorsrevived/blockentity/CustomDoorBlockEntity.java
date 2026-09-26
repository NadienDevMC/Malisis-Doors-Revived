package nadiendev.malisisdoorsrevived.blockentity;

import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
	protected void readSyncData(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.readSyncData(tag, registries);
		if (tag.contains("materials"))
			DoorMaterials.CODEC.parse(NbtOps.INSTANCE, tag.get("materials")).result().ifPresent(this::setMaterials);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		DoorMaterials.CODEC.encodeStart(NbtOps.INSTANCE, materials).result().ifPresent(t -> tag.put("materials", t));
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput input)
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
	public void removeComponentsFromTag(CompoundTag tag)
	{
		super.removeComponentsFromTag(tag);
		tag.remove("materials");
	}
}
