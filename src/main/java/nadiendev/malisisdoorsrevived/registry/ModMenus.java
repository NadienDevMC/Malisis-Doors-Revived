package nadiendev.malisisdoorsrevived.registry;

import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.menu.DoorFactoryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus
{
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MalisisDoors.MODID);

	public static final Supplier<MenuType<DoorFactoryMenu>> DOOR_FACTORY = MENUS.register("door_factory",
																							() -> IMenuTypeExtension.create(DoorFactoryMenu::new));
}
