package nadiendev.malisisdoorsrevived.client.screen;

import java.util.List;

import nadiendev.malisisdoorsrevived.blockentity.DoorFactoryBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.menu.DoorFactoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Screen of the Door Factory.
 *
 * @author Ordinastie
 */
public class DoorFactoryScreen extends AbstractContainerScreen<DoorFactoryMenu>
{
	private static final int ROW_X = 36, ROW_Y = 16, ROW_H = 15, ROW_W = 162;
	private static final int TEXT_COLOR = 0x404040;

	private Button redstoneButton;
	private Button doubleDoorButton;
	private Button proximityButton;
	private Button modeButton;
	private Button createButton;

	public DoorFactoryScreen(DoorFactoryMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		imageWidth = 204;
		imageHeight = 226;
	}

	@Override
	protected void init()
	{
		super.init();
		int x = leftPos + ROW_X;

		arrows(0, DoorFactoryBlockEntity.BUTTON_PREV_MOVEMENT, DoorFactoryBlockEntity.BUTTON_NEXT_MOVEMENT, "<", ">");
		arrows(1, DoorFactoryBlockEntity.BUTTON_PREV_SOUND, DoorFactoryBlockEntity.BUTTON_NEXT_SOUND, "<", ">");
		arrows(2, DoorFactoryBlockEntity.BUTTON_LESS_OPENING, DoorFactoryBlockEntity.BUTTON_MORE_OPENING, "-", "+");
		arrows(3, DoorFactoryBlockEntity.BUTTON_LESS_AUTOCLOSE, DoorFactoryBlockEntity.BUTTON_MORE_AUTOCLOSE, "-", "+");
		redstoneButton = addRenderableWidget(Button	.builder(Component.empty(), b -> click(DoorFactoryBlockEntity.BUTTON_REDSTONE))
													.bounds(x, rowY(4), ROW_W, 14)
													.build());
		doubleDoorButton = addRenderableWidget(Button	.builder(Component.empty(), b -> click(DoorFactoryBlockEntity.BUTTON_DOUBLE_DOOR))
														.bounds(x, rowY(5), ROW_W, 14)
														.build());
		proximityButton = addRenderableWidget(Button.builder(Component.empty(), b -> click(DoorFactoryBlockEntity.BUTTON_PROXIMITY))
													.bounds(x, rowY(6), ROW_W, 14)
													.build());
		modeButton = addRenderableWidget(Button	.builder(Component.empty(), b -> click(DoorFactoryBlockEntity.BUTTON_MODE))
												.bounds(leftPos + 8, topPos + 124, 70, 14)
												.build());
		createButton = addRenderableWidget(Button	.builder(Component.empty(), b -> click(DoorFactoryBlockEntity.BUTTON_CREATE))
													.bounds(leftPos + 84, topPos + 124, 80, 14)
													.build());
		updateButtons();
	}

	private int rowY(int row)
	{
		return topPos + ROW_Y + row * ROW_H;
	}

	private void arrows(int row, int less, int more, String lessText, String moreText)
	{
		addRenderableWidget(Button.builder(Component.literal(lessText), b -> click(less)).bounds(leftPos + ROW_X, rowY(row), 14, 14).build());
		addRenderableWidget(Button.builder(Component.literal(moreText), b -> click(more)).bounds(leftPos + ROW_X + ROW_W - 14, rowY(row), 14, 14).build());
	}

	private void click(int button)
	{
		if (minecraft != null && minecraft.gameMode != null)
			minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
	}

	@Override
	protected void containerTick()
	{
		super.containerTick();
		updateButtons();
	}

	private void updateButtons()
	{
		if (redstoneButton == null)
			return;
		RedstoneBehavior behavior = RedstoneBehavior.values()[Mth.clamp(menu.getData(DoorFactoryBlockEntity.DATA_REDSTONE),
																		0,
																		RedstoneBehavior.values().length - 1)];
		redstoneButton.setMessage(Component.translatable(behavior.getTranslationKey()));
		doubleDoorButton.setMessage(Component.translatable("malisisdoors.gui.double_door."
				+ (menu.getData(DoorFactoryBlockEntity.DATA_DOUBLE_DOOR) != 0 ? "on" : "off")));
		proximityButton.setMessage(Component.translatable("malisisdoors.gui.proximity."
				+ (menu.getData(DoorFactoryBlockEntity.DATA_PROXIMITY) != 0 ? "on" : "off")));
		modeButton.setMessage(Component.translatable("malisisdoors.gui.mode." + (menu.isCreateMode() ? "create" : "edit")));
		createButton.setMessage(Component.translatable("malisisdoors.gui." + (menu.isCreateMode() ? "create" : "apply")));
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(graphics, mouseX, mouseY, partialTick);
		renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		int x = leftPos, y = topPos;
		//panel
		graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF000000);
		graphics.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFFFFFFFF);
		graphics.fill(x + 3, y + 3, x + imageWidth - 1, y + imageHeight - 1, 0xFF555555);
		graphics.fill(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3, 0xFFC6C6C6);

		//text background of the value rows
		for (int row = 0; row < 4; row++)
			graphics.fill(x + ROW_X + 15, y + ROW_Y + row * ROW_H, x + ROW_X + ROW_W - 15, y + ROW_Y + row * ROW_H + 14, 0xFF8B8B8B);

		for (Slot slot : menu.slots)
		{
			if (slot.isActive())
				drawSlot(graphics, x + slot.x, y + slot.y);
		}
	}

	private static void drawSlot(GuiGraphics graphics, int x, int y)
	{
		graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF373737);
		graphics.fill(x, y, x + 18, y + 18, 0xFFFFFFFF);
		graphics.fill(x, y, x + 17, y + 17, 0xFF8B8B8B);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		graphics.drawString(font, title, 8, 5, TEXT_COLOR, false);

		if (menu.isCreateMode())
		{
			graphics.drawString(font, Component.translatable("malisisdoors.gui.frame"), 8, 20, TEXT_COLOR, false);
			graphics.drawString(font, Component.translatable("malisisdoors.gui.top"), 8, 52, TEXT_COLOR, false);
			graphics.drawString(font, Component.translatable("malisisdoors.gui.bottom"), 8, 84, TEXT_COLOR, false);
		}
		else
			graphics.drawString(font, Component.translatable("malisisdoors.gui.door"), 8, 52, TEXT_COLOR, false);

		List<String> movements = DoorRegistry.listMovements();
		List<String> sounds = DoorRegistry.listSounds();
		String movement = movements.get(Mth.clamp(menu.getData(DoorFactoryBlockEntity.DATA_MOVEMENT), 0, movements.size() - 1));
		String sound = sounds.get(Mth.clamp(menu.getData(DoorFactoryBlockEntity.DATA_SOUND), 0, sounds.size() - 1));
		int autoClose = menu.getData(DoorFactoryBlockEntity.DATA_AUTOCLOSE);

		drawValue(graphics, 0, Component.translatable("malisisdoors.movement." + movement));
		drawValue(graphics, 1, Component.translatable("malisisdoors.sound." + sound));
		drawValue(graphics, 2, Component.translatable("malisisdoors.gui.opening_time", menu.getData(DoorFactoryBlockEntity.DATA_OPENING)));
		drawValue(graphics,
					3,
					autoClose > 0	? Component.translatable("malisisdoors.gui.auto_close_time", autoClose)
									: Component.translatable("malisisdoors.gui.auto_close_off"));
	}

	private void drawValue(GuiGraphics graphics, int row, Component text)
	{
		int center = ROW_X + ROW_W / 2;
		int width = font.width(text);
		int maxWidth = ROW_W - 32;
		if (width > maxWidth)
			text = Component.literal(font.substrByWidth(text, maxWidth - 6).getString() + "...");
		graphics.drawCenteredString(font, text, center, ROW_Y + row * ROW_H + 3, 0xFFFFFF);
	}
}
