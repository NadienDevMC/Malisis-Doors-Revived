package nadiendev.malisisdoorsrevived.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.item.CustomDoorItem;
import nadiendev.malisisdoorsrevived.item.EditableDoorItem;
import nadiendev.malisisdoorsrevived.item.ForcefieldItem;
import nadiendev.malisisdoorsrevived.item.FramedDoorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MalisisDoors.MODID);

	/** Items in the order of the creative tab. */
	public static final List<DeferredItem<? extends Item>> TAB_ITEMS = new ArrayList<>();

	static
	{
		for (DeferredBlock<? extends Block> door : ModBlocks.ITEM_DOORS)
		{
			if (door == ModBlocks.CUSTOM_DOOR)
				continue;
			TAB_ITEMS.add(ITEMS.registerItem(door.getId().getPath(), p -> new EditableDoorItem(door.get(), p), Item.Properties::useBlockDescriptionPrefix));
		}
	}

	public static final DeferredItem<CustomDoorItem> CUSTOM_DOOR = ITEMS.registerItem("custom_door", p -> new CustomDoorItem(ModBlocks.CUSTOM_DOOR.get(), p), Item.Properties::useBlockDescriptionPrefix);

	public static final DeferredItem<BlockItem> SLIDING_TRAPDOOR = blockItem(ModBlocks.SLIDING_TRAPDOOR);
	public static final DeferredItem<BlockItem> CAMO_FENCE_GATE = blockItem(ModBlocks.CAMO_FENCE_GATE);
	public static final DeferredItem<BlockItem> GARAGE_DOOR = blockItem(ModBlocks.GARAGE_DOOR);
	public static final DeferredItem<FramedDoorItem> CARRIAGE_DOOR = framedDoor(ModBlocks.CARRIAGE_DOOR);
	public static final DeferredItem<FramedDoorItem> MEDIEVAL_DOOR = framedDoor(ModBlocks.MEDIEVAL_DOOR);

	static
	{
		ModBlocks.DOORS_3X3.forEach(ModItems::blockItem);
	}

	public static final DeferredItem<BlockItem> RUSTY_HATCH = blockItem(ModBlocks.RUSTY_HATCH);
	public static final DeferredItem<Item> RUSTY_HANDLE = register("rusty_handle", Item::new, p -> p);
	public static final DeferredItem<BlockItem> RUSTY_LADDER = blockItem(ModBlocks.RUSTY_LADDER);
	public static final DeferredItem<BlockItem> HITECH_DOOR = blockItem(ModBlocks.HITECH_DOOR);
	public static final DeferredItem<ForcefieldItem> FORCEFIELD_CONTROLLER = register("forcefield_controller", ForcefieldItem::new, p -> p);
	public static final DeferredItem<BlockItem> DOOR_FACTORY = blockItem(ModBlocks.DOOR_FACTORY);
	public static final DeferredItem<BlockItem> PLAYER_SENSOR = blockItem(ModBlocks.PLAYER_SENSOR);

	private static <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> factory, UnaryOperator<Item.Properties> properties)
	{
		DeferredItem<T> item = ITEMS.registerItem(name, factory, properties);
		TAB_ITEMS.add(item);
		return item;
	}

	private static DeferredItem<BlockItem> blockItem(DeferredBlock<? extends Block> block)
	{
		return register(block.getId().getPath(), p -> new BlockItem(block.get(), p), Item.Properties::useBlockDescriptionPrefix);
	}

	private static DeferredItem<FramedDoorItem> framedDoor(DeferredBlock<? extends Block> block)
	{
		return register(block.getId().getPath(), p -> new FramedDoorItem(block.get(), p), Item.Properties::useBlockDescriptionPrefix);
	}
}
