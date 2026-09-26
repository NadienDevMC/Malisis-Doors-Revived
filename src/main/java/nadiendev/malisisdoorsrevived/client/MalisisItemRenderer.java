package nadiendev.malisisdoorsrevived.client;

import com.mojang.blaze3d.vertex.PoseStack;

import nadiendev.malisiscorerevived.client.model.ObjModelCache;
import nadiendev.malisiscorerevived.client.render.RenderUtils;
import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.client.renderer.DoorRenderer;
import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Renders the items that need a 3D model built at runtime : custom doors, vertical hatch, hi-tech door and rusty handle.
 *
 * @author Ordinastie
 */
public class MalisisItemRenderer extends BlockEntityWithoutLevelRenderer
{
	private static MalisisItemRenderer instance;

	public MalisisItemRenderer()
	{
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	public static MalisisItemRenderer get()
	{
		if (instance == null)
			instance = new MalisisItemRenderer();
		return instance;
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
	{
		poseStack.pushPose();
		if (stack.is(ModItems.CUSTOM_DOOR.get()))
		{
			DoorMaterials materials = stack.getOrDefault(ModDataComponents.DOOR_MATERIALS.get(), DoorMaterials.DEFAULT);
			centerDoor(poseStack);
			DoorRenderer.renderCustomDoorHalf(poseStack, buffer, materials, false, null, null, light, overlay, 1);
			DoorRenderer.renderCustomDoorHalf(poseStack, buffer, materials, true, null, null, light, overlay, 1);
		}
		else if (stack.is(ModBlocks.VERTICAL_HATCH.get().asItem()))
		{
			centerDoor(poseStack);
			ObjModelCache	.get(MalisisDoors.id("models/vertical_hatch.obj"))
							.render(poseStack.last(),
									buffer.getBuffer(RenderUtils.blockAtlasType(false)),
									RenderUtils.getBlockSprite(MalisisDoors.id("block/vertical_hatch")),
									0xFFFFFFFF,
									light,
									overlay);
		}
		else if (stack.is(ModItems.HITECH_DOOR.get()))
		{
			poseStack.translate(0.5F, 0.2F, 0.5F);
			poseStack.scale(1 / 3F, 1 / 3F, 1 / 3F);
			poseStack.translate(-0.5F, 0, -0.5F);
			ObjModelCache	.get(MalisisDoors.id("models/hitechdoor.obj"))
							.render(poseStack.last(),
									buffer.getBuffer(RenderUtils.blockAtlasType(true)),
									RenderUtils.getBlockSprite(MalisisDoors.id("block/hitechdoor")),
									0xFFFFFFFF,
									light,
									overlay);
		}
		else if (stack.is(ModItems.RUSTY_HANDLE.get()))
		{
			//the wheel of the rusty hatch model is centered on (1, 1)
			poseStack.translate(0.5F, 0.4F, 0.5F);
			poseStack.scale(1.5F, 1.5F, 1.5F);
			poseStack.translate(-1, 0, -1);
			ObjModelCache	.get(MalisisDoors.id("models/rusty_hatch.obj"))
							.render(poseStack.last(),
									buffer.getBuffer(RenderUtils.blockAtlasType(false)),
									RenderUtils.getBlockSprite(MalisisDoors.id("block/rusty_hatch_handle")),
									0xFFFFFFFF,
									light,
									overlay,
									"handle");
		}
		poseStack.popPose();
	}

	/**
	 * Scales a two blocks high door to fit in the item space.
	 */
	private static void centerDoor(PoseStack poseStack)
	{
		poseStack.translate(0.5F, 0, 0.5F);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.translate(-0.5F, 0, -IDoorMovement.DOOR_WIDTH / 2);
	}
}
