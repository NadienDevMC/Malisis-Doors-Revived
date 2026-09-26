package nadiendev.malisisdoorsrevived.registry;

import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs
{
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MalisisDoors.MODID);

	public static final Supplier<CreativeModeTab> MALISIS_DOORS = TABS.register("malisisdoors",
																				() -> CreativeModeTab	.builder()
																										.title(Component.translatable("itemGroup.malisisdoors"))
																										.icon(() -> new ItemStack(ModBlocks.WOOD_SLIDING_DOOR.get()))
																										.displayItems((params, output) -> {
																											ModItems.TAB_ITEMS.forEach(item -> output.accept(item.get()));
																											//an example of custom door
																											ItemStack custom = new ItemStack(ModItems.CUSTOM_DOOR.get());
																											custom.set(ModDataComponents.DOOR_MATERIALS.get(),
																														new DoorMaterials(	Blocks.DARK_OAK_PLANKS.defaultBlockState(),
																																			Blocks.GLASS.defaultBlockState(),
																																			Blocks.OAK_PLANKS.defaultBlockState()));
																											output.accept(custom);
																										})
																										.build());
}
