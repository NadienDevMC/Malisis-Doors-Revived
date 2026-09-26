package nadiendev.malisisdoorsrevived.menu;

import nadiendev.malisisdoorsrevived.blockentity.DoorFactoryBlockEntity;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Menu of the Door Factory.
 *
 * @author Ordinastie
 */
public class DoorFactoryMenu extends AbstractContainerMenu
{
	public static final int INVENTORY_X = 20, INVENTORY_Y = 144;

	private final Container container;
	private final ContainerData data;
	private final ContainerLevelAccess access;
	private final DoorFactoryBlockEntity factory;

	/**
	 * Client side constructor.
	 */
	public DoorFactoryMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf)
	{
		this(containerId, playerInventory, getFactory(playerInventory, buf.readBlockPos()), new SimpleContainerData(DoorFactoryBlockEntity.DATA_COUNT));
	}

	private static DoorFactoryBlockEntity getFactory(Inventory playerInventory, BlockPos pos)
	{
		return playerInventory.player.level().getBlockEntity(pos) instanceof DoorFactoryBlockEntity be ? be : null;
	}

	/**
	 * Server side constructor (the factory is null on the client if its block entity is not loaded).
	 */
	public DoorFactoryMenu(int containerId, Inventory playerInventory, DoorFactoryBlockEntity factory, ContainerData data)
	{
		super(ModMenus.DOOR_FACTORY.get(), containerId);
		this.factory = factory;
		this.container = factory != null ? factory.getInventory() : new SimpleContainer(5);
		this.data = data;
		this.access = factory != null && factory.getLevel() != null	? ContainerLevelAccess.create(factory.getLevel(), factory.getBlockPos())
																	: ContainerLevelAccess.NULL;

		checkContainerSize(container, 5);
		checkContainerDataCount(data, DoorFactoryBlockEntity.DATA_COUNT);

		addSlot(new MaterialSlot(container, DoorFactoryBlockEntity.FRAME_SLOT, 12, 30));
		addSlot(new MaterialSlot(container, DoorFactoryBlockEntity.TOP_SLOT, 12, 62));
		addSlot(new MaterialSlot(container, DoorFactoryBlockEntity.BOTTOM_SLOT, 12, 94));
		addSlot(new EditSlot(container, DoorFactoryBlockEntity.EDIT_SLOT, 12, 62));
		addSlot(new OutputSlot(container, DoorFactoryBlockEntity.OUTPUT_SLOT, 178, 120));

		for (int row = 0; row < 3; row++)
			for (int col = 0; col < 9; col++)
				addSlot(new Slot(playerInventory, col + row * 9 + 9, INVENTORY_X + col * 18, INVENTORY_Y + row * 18));
		for (int col = 0; col < 9; col++)
			addSlot(new Slot(playerInventory, col, INVENTORY_X + col * 18, INVENTORY_Y + 58));

		addDataSlots(data);
	}

	public int getData(int index)
	{
		return data.get(index);
	}

	public boolean isCreateMode()
	{
		return data.get(DoorFactoryBlockEntity.DATA_CREATE) != 0;
	}

	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		if (factory == null)
			return false;
		boolean result = factory.handleButton(id, player);
		if (result)
			broadcastChanges();
		return result;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(access, player, ModBlocks.DOOR_FACTORY.get());
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		Slot slot = slots.get(index);
		if (!slot.hasItem())
			return ItemStack.EMPTY;

		ItemStack stack = slot.getItem();
		ItemStack copy = stack.copy();
		int factorySlots = 5;
		if (index < factorySlots)
		{
			if (!moveItemStackTo(stack, factorySlots, slots.size(), true))
				return ItemStack.EMPTY;
		}
		else
		{
			boolean moved = false;
			if (!isCreateMode() && DoorFactoryBlockEntity.isEditableDoor(stack))
				moved = moveItemStackTo(stack, DoorFactoryBlockEntity.EDIT_SLOT, DoorFactoryBlockEntity.EDIT_SLOT + 1, false);
			else if (isCreateMode() && DoorFactoryBlockEntity.isMaterial(stack))
				moved = moveItemStackTo(stack, DoorFactoryBlockEntity.FRAME_SLOT, DoorFactoryBlockEntity.BOTTOM_SLOT + 1, false);
			if (!moved)
				return ItemStack.EMPTY;
		}

		if (stack.isEmpty())
			slot.setByPlayer(ItemStack.EMPTY);
		else
			slot.setChanged();

		if (stack.getCount() == copy.getCount())
			return ItemStack.EMPTY;
		slot.onTake(player, stack);
		return copy;
	}

	private class MaterialSlot extends Slot
	{
		public MaterialSlot(Container container, int slot, int x, int y)
		{
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return DoorFactoryBlockEntity.isMaterial(stack);
		}

		@Override
		public boolean isActive()
		{
			return isCreateMode();
		}
	}

	private class EditSlot extends Slot
	{
		public EditSlot(Container container, int slot, int x, int y)
		{
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return DoorFactoryBlockEntity.isEditableDoor(stack);
		}

		@Override
		public boolean isActive()
		{
			return !isCreateMode();
		}
	}

	private static class OutputSlot extends Slot
	{
		public OutputSlot(Container container, int slot, int x, int y)
		{
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return false;
		}
	}
}
