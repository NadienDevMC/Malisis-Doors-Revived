package nadiendev.malisisdoorsrevived.blockentity;

import java.util.List;

import javax.annotation.Nullable;

import nadiendev.malisisdoorsrevived.block.MalisisDoorBlock;
import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.door.DoorSettings;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.menu.DoorFactoryMenu;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the Door Factory.<br>
 * In create mode, a custom door is made from three blocks. In edit mode, the settings are applied to existing doors.
 *
 * @author Ordinastie
 */
public class DoorFactoryBlockEntity extends BlockEntity implements MenuProvider
{
	public static final int FRAME_SLOT = 0, TOP_SLOT = 1, BOTTOM_SLOT = 2, EDIT_SLOT = 3, OUTPUT_SLOT = 4;
	public static final int MAX_OPENING_TIME = 100;
	public static final int MAX_AUTO_CLOSE_TIME = 600;

	//menu buttons
	public static final int BUTTON_PREV_MOVEMENT = 0, BUTTON_NEXT_MOVEMENT = 1, BUTTON_PREV_SOUND = 2, BUTTON_NEXT_SOUND = 3,
			BUTTON_LESS_OPENING = 4, BUTTON_MORE_OPENING = 5, BUTTON_LESS_AUTOCLOSE = 6, BUTTON_MORE_AUTOCLOSE = 7, BUTTON_REDSTONE = 8,
			BUTTON_DOUBLE_DOOR = 9, BUTTON_PROXIMITY = 10, BUTTON_MODE = 11, BUTTON_CREATE = 12;

	//data indexes
	public static final int DATA_MOVEMENT = 0, DATA_SOUND = 1, DATA_OPENING = 2, DATA_AUTOCLOSE = 3, DATA_REDSTONE = 4, DATA_DOUBLE_DOOR = 5,
			DATA_PROXIMITY = 6, DATA_CREATE = 7, DATA_COUNT = 8;

	private final SimpleContainer inventory = new SimpleContainer(5)
	{
		@Override
		public void setChanged()
		{
			super.setChanged();
			DoorFactoryBlockEntity.this.setChanged();
		}
	};

	private int movement = 0;
	private int sound = 0;
	private int openingTime = 6;
	private int autoCloseTime = 0;
	private RedstoneBehavior redstoneBehavior = RedstoneBehavior.STANDARD;
	private boolean doubleDoor = true;
	private boolean proximityDetection = false;
	private boolean createMode = true;

	private final ContainerData data = new ContainerData()
	{
		@Override
		public int get(int index)
		{
			return switch (index)
			{
				case DATA_MOVEMENT -> movement;
				case DATA_SOUND -> sound;
				case DATA_OPENING -> openingTime;
				case DATA_AUTOCLOSE -> autoCloseTime;
				case DATA_REDSTONE -> redstoneBehavior.ordinal();
				case DATA_DOUBLE_DOOR -> doubleDoor ? 1 : 0;
				case DATA_PROXIMITY -> proximityDetection ? 1 : 0;
				case DATA_CREATE -> createMode ? 1 : 0;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value)
		{
			switch (index)
			{
				case DATA_MOVEMENT -> movement = value;
				case DATA_SOUND -> sound = value;
				case DATA_OPENING -> openingTime = value;
				case DATA_AUTOCLOSE -> autoCloseTime = value;
				case DATA_REDSTONE -> redstoneBehavior = RedstoneBehavior.values()[Mth.clamp(value, 0, RedstoneBehavior.values().length - 1)];
				case DATA_DOUBLE_DOOR -> doubleDoor = value != 0;
				case DATA_PROXIMITY -> proximityDetection = value != 0;
				case DATA_CREATE -> createMode = value != 0;
				default -> {}
			}
		}

		@Override
		public int getCount()
		{
			return DATA_COUNT;
		}
	};

	public DoorFactoryBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.DOOR_FACTORY.get(), pos, state);
		sound = Math.max(0, DoorRegistry.listSounds().indexOf("vanilla_door"));
	}

	public SimpleContainer getInventory()
	{
		return inventory;
	}

	public ContainerData getData()
	{
		return data;
	}

	@Override
	public Component getDisplayName()
	{
		return Component.translatable("container.malisisdoors.door_factory");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
	{
		return new DoorFactoryMenu(containerId, playerInventory, this, data);
	}

	//#region Settings
	public DoorSettings getSettings()
	{
		List<String> movements = DoorRegistry.listMovements();
		List<String> sounds = DoorRegistry.listSounds();
		return new DoorSettings(movements.get(Mth.clamp(movement, 0, movements.size() - 1)),
								sounds.get(Mth.clamp(sound, 0, sounds.size() - 1)),
								openingTime,
								autoCloseTime,
								redstoneBehavior,
								doubleDoor,
								proximityDetection);
	}

	/**
	 * Handles a button clicked in the menu.
	 */
	public boolean handleButton(int button, Player player)
	{
		int movements = DoorRegistry.listMovements().size();
		int sounds = DoorRegistry.listSounds().size();
		int step = player.isShiftKeyDown() ? 10 : 1;
		switch (button)
		{
			case BUTTON_PREV_MOVEMENT -> movement = Math.floorMod(movement - 1, movements);
			case BUTTON_NEXT_MOVEMENT -> movement = Math.floorMod(movement + 1, movements);
			case BUTTON_PREV_SOUND -> sound = Math.floorMod(sound - 1, sounds);
			case BUTTON_NEXT_SOUND -> sound = Math.floorMod(sound + 1, sounds);
			case BUTTON_LESS_OPENING -> openingTime = Mth.clamp(openingTime - step, 1, MAX_OPENING_TIME);
			case BUTTON_MORE_OPENING -> openingTime = Mth.clamp(openingTime + step, 1, MAX_OPENING_TIME);
			case BUTTON_LESS_AUTOCLOSE -> autoCloseTime = Mth.clamp(autoCloseTime - step * 10, 0, MAX_AUTO_CLOSE_TIME);
			case BUTTON_MORE_AUTOCLOSE -> autoCloseTime = Mth.clamp(autoCloseTime + step * 10, 0, MAX_AUTO_CLOSE_TIME);
			case BUTTON_REDSTONE -> redstoneBehavior = redstoneBehavior.next();
			case BUTTON_DOUBLE_DOOR -> doubleDoor = !doubleDoor;
			case BUTTON_PROXIMITY -> proximityDetection = !proximityDetection;
			case BUTTON_MODE -> createMode = !createMode;
			case BUTTON_CREATE -> createDoor();
			default -> {
				return false;
			}
		}
		setChanged();
		return true;
	}
	//#end Settings

	//#region Door creation
	public static boolean isMaterial(ItemStack stack)
	{
		return stack.getItem() instanceof BlockItem item && !(item.getBlock() instanceof MalisisDoorBlock);
	}

	/**
	 * Whether the stack is a door whose settings can be edited.
	 */
	public static boolean isEditableDoor(ItemStack stack)
	{
		return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof MalisisDoorBlock door && door.asItem() == stack.getItem();
	}

	private static BlockState getState(ItemStack stack)
	{
		return ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
	}

	public ItemStack getExpectedOutput()
	{
		if (createMode)
		{
			ItemStack frame = inventory.getItem(FRAME_SLOT);
			ItemStack top = inventory.getItem(TOP_SLOT);
			ItemStack bottom = inventory.getItem(BOTTOM_SLOT);
			if (!isMaterial(frame) || !isMaterial(top) || !isMaterial(bottom))
				return ItemStack.EMPTY;
			ItemStack door = new ItemStack(ModItems.CUSTOM_DOOR.get());
			door.set(ModDataComponents.DOOR_MATERIALS.get(), new DoorMaterials(getState(frame), getState(top), getState(bottom)));
			door.set(ModDataComponents.DOOR_SETTINGS.get(), getSettings());
			return door;
		}

		ItemStack edit = inventory.getItem(EDIT_SLOT);
		if (!isEditableDoor(edit))
			return ItemStack.EMPTY;
		ItemStack door = edit.copy();
		door.set(ModDataComponents.DOOR_SETTINGS.get(), getSettings());
		return door;
	}

	public void createDoor()
	{
		if (level == null || level.isClientSide())
			return;

		ItemStack expected = getExpectedOutput();
		if (expected.isEmpty())
			return;

		ItemStack output = inventory.getItem(OUTPUT_SLOT);
		if (!output.isEmpty())
		{
			if (!ItemStack.isSameItemSameComponents(output, expected) || output.getCount() + expected.getCount() > output.getMaxStackSize())
				return;
		}

		if (createMode)
		{
			inventory.removeItem(FRAME_SLOT, 1);
			inventory.removeItem(TOP_SLOT, 1);
			inventory.removeItem(BOTTOM_SLOT, 1);
		}
		else
			inventory.setItem(EDIT_SLOT, ItemStack.EMPTY);

		if (output.isEmpty())
			inventory.setItem(OUTPUT_SLOT, expected);
		else
			output.grow(expected.getCount());
		setChanged();
	}

	public boolean isCreateMode()
	{
		return createMode;
	}

	/**
	 * Drops the content of the factory when it's broken.
	 */
	public void dropContents()
	{
		if (level != null)
			net.minecraft.world.Containers.dropContents(level, worldPosition, inventory);
	}
	//#end Door creation

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, items, registries);
		for (int i = 0; i < items.size(); i++)
			inventory.setItem(i, items.get(i));

		List<String> movements = DoorRegistry.listMovements();
		List<String> sounds = DoorRegistry.listSounds();
		movement = Math.max(0, movements.indexOf(tag.getString("movement")));
		if (tag.contains("sound"))
			sound = Math.max(0, sounds.indexOf(tag.getString("sound")));
		openingTime = Mth.clamp(tag.getInt("openingTime"), 1, MAX_OPENING_TIME);
		autoCloseTime = Mth.clamp(tag.getInt("autoCloseTime"), 0, MAX_AUTO_CLOSE_TIME);
		redstoneBehavior = RedstoneBehavior.values()[Mth.clamp(tag.getInt("redstoneBehavior"), 0, RedstoneBehavior.values().length - 1)];
		doubleDoor = tag.getBoolean("doubleDoor");
		proximityDetection = tag.getBoolean("proximityDetection");
		createMode = !tag.contains("createMode") || tag.getBoolean("createMode");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		NonNullList<ItemStack> items = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
		for (int i = 0; i < items.size(); i++)
			items.set(i, inventory.getItem(i));
		ContainerHelper.saveAllItems(tag, items, registries);

		DoorSettings settings = getSettings();
		tag.putString("movement", settings.movement());
		tag.putString("sound", settings.sound());
		tag.putInt("openingTime", openingTime);
		tag.putInt("autoCloseTime", autoCloseTime);
		tag.putInt("redstoneBehavior", redstoneBehavior.ordinal());
		tag.putBoolean("doubleDoor", doubleDoor);
		tag.putBoolean("proximityDetection", proximityDetection);
		tag.putBoolean("createMode", createMode);
	}
}
