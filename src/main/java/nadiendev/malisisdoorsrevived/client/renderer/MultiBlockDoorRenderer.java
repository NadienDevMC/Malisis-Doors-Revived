package nadiendev.malisisdoorsrevived.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.malisis.core.client.model.ObjModel;
import net.malisis.core.client.model.ObjModelCache;
import net.malisis.core.client.render.RenderUtils;
import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.block.BigDoorBlock;
import nadiendev.malisisdoorsrevived.block.Door3x3Block;
import nadiendev.malisisdoorsrevived.block.HitechDoorBlock;
import nadiendev.malisisdoorsrevived.block.RustyHatchBlock;
import nadiendev.malisisdoorsrevived.blockentity.MultiBlockDoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.movement.DoorPart;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Renderer of the multi-block doors : big doors, 3x3 doors, rusty hatch and hi-tech door.
 *
 * @author Ordinastie
 */
public class MultiBlockDoorRenderer implements BlockEntityRenderer<MultiBlockDoorBlockEntity>
{
	private static final float W = IDoorMovement.DOOR_WIDTH;
	private static final ResourceLocation RUSTY_HATCH_HANDLE = MalisisDoors.id("block/rusty_hatch_handle");
	private static final ResourceLocation RUSTY_HATCH_FRAME = ResourceLocation.withDefaultNamespace("block/furnace_top");

	public MultiBlockDoorRenderer(BlockEntityRendererProvider.Context context)
	{}

	@Override
	public void render(MultiBlockDoorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
	{
		BlockState state = be.getBlockState();
		if (!(state.getBlock() instanceof IDoorBlock door))
			return;

		if (state.getBlock() instanceof Door3x3Block && door.getAppearance() instanceof DoorAppearance.Textured textured)
			render3x3(be, textured, poseStack, buffer, light, overlay, partialTick);
		else if (door.getAppearance() instanceof DoorAppearance.Obj obj)
		{
			ObjModel model = ObjModelCache.get(obj.model());
			TextureAtlasSprite sprite = RenderUtils.getBlockSprite(obj.texture());
			if (state.getBlock() instanceof BigDoorBlock)
				renderBigDoor(be, model, sprite, poseStack, buffer, light, overlay, partialTick);
			else if (state.getBlock() instanceof RustyHatchBlock)
				renderRustyHatch(be, model, sprite, poseStack, buffer, light, overlay, partialTick);
			else if (state.getBlock() instanceof HitechDoorBlock)
				renderHitechDoor(be, model, sprite, poseStack, buffer, light, overlay, partialTick);
		}
	}

	private void renderBigDoor(MultiBlockDoorBlockEntity be, ObjModel model, TextureAtlasSprite sprite, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float partialTick)
	{
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(false));
		int rotation = be.getRotation();

		//frame made of the block used in the recipe
		BlockState frame = be.getFrameState();
		DoorRenderHelper.begin(poseStack, be, rotation, null, partialTick);
		DoorRenderHelper.toCorner(poseStack);
		model.render(	poseStack.last(),
						consumer,
						RenderUtils.getSprite(frame, Direction.SOUTH),
						RenderUtils.getTint(frame, be.getLevel(), be.getBlockPos()),
						light,
						overlay,
						"frame");
		DoorRenderHelper.end(poseStack);

		for (DoorPart part : new DoorPart[] { DoorPart.LEFT, DoorPart.RIGHT })
		{
			DoorRenderHelper.begin(poseStack, be, rotation, part, partialTick);
			DoorRenderHelper.toCorner(poseStack);
			model.render(poseStack.last(), consumer, sprite, 0xFFFFFFFF, light, overlay, part == DoorPart.LEFT ? "left" : "right");
			DoorRenderHelper.end(poseStack);
		}
	}

	private void render3x3(MultiBlockDoorBlockEntity be, DoorAppearance.Textured textured, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float partialTick)
	{
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(false));
		TextureAtlasSprite sprite = RenderUtils.getBlockSprite(textured.texture());
		RenderUtils.BoxTexture texture = leafTexture(sprite, new AABB(-1, 0, 1 - W, 2, 3, 1));
		int rotation = be.getRotation();

		for (DoorPart part : new DoorPart[] { DoorPart.LEFT, DoorPart.RIGHT })
		{
			DoorRenderHelper.begin(poseStack, be, rotation, part, partialTick);
			DoorRenderHelper.toCorner(poseStack);
			AABB box = part == DoorPart.LEFT ? new AABB(-1, 0, 1 - W, 0.5F, 3, 1) : new AABB(0.5F, 0, 1 - W, 2, 3, 1);
			RenderUtils.renderBox(poseStack.last(), consumer, box, texture, 0xFFFFFFFF, light, overlay);
			DoorRenderHelper.end(poseStack);
		}
	}

	private static RenderUtils.BoxTexture leafTexture(TextureAtlasSprite sprite, AABB area)
	{
		float u = (float) (W / area.getXsize());
		float v = (float) (W / area.getYsize());
		return new RenderUtils.BoxTexture()
		{
			@Override
			public TextureAtlasSprite getSprite(Direction side)
			{
				return sprite;
			}

			@Override
			public float[] getUVs(Direction side, AABB box)
			{
				float[] uvs = RenderUtils.areaUVs(side, box, area);
				float minX = (float) ((box.minX - area.minX) / area.getXsize());
				float maxX = (float) ((box.maxX - area.minX) / area.getXsize());
				return switch (side)
				{
					case WEST -> new float[] { minX, uvs[1], minX + u, uvs[3] };
					case EAST -> new float[] { maxX - u, uvs[1], maxX, uvs[3] };
					case UP -> new float[] { uvs[0], 0, uvs[2], v };
					case DOWN -> new float[] { uvs[0], 1 - v, uvs[2], 1 };
					default -> uvs;
				};
			}
		};
	}

	private void renderRustyHatch(MultiBlockDoorBlockEntity be, ObjModel model, TextureAtlasSprite sprite, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float partialTick)
	{
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(false));
		boolean top = RustyHatchBlock.isTop(be.getBlockState());
		int rotation = be.getRotation();

		renderRustyHatchGroup(be, model, null, "frame", RenderUtils.getBlockSprite(RUSTY_HATCH_FRAME), top, rotation, poseStack, consumer, light, overlay, partialTick);
		renderRustyHatchGroup(be, model, DoorPart.DOOR, "door", sprite, top, rotation, poseStack, consumer, light, overlay, partialTick);
		renderRustyHatchGroup(	be,
								model,
								DoorPart.HANDLE,
								"handle",
								RenderUtils.getBlockSprite(RUSTY_HATCH_HANDLE),
								top,
								rotation,
								poseStack,
								consumer,
								light,
								overlay,
								partialTick);
	}

	private void renderRustyHatchGroup(MultiBlockDoorBlockEntity be, ObjModel model, DoorPart part, String group, TextureAtlasSprite sprite, boolean top, int rotation, PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float partialTick)
	{
		//the model spans X from 0 to 2 : move it so that the wall (east) is at the edge of the origin block
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation * 90));
		poseStack.translate(-1, top ? 1 - 0.125F : 0, 0);
		poseStack.translate(-0.5F, -0.5F, -0.5F);
		DoorRenderHelper.begin(poseStack, be, 0, part, partialTick);
		DoorRenderHelper.toCorner(poseStack);
		model.render(poseStack.last(), consumer, sprite, 0xFFFFFFFF, light, overlay, group);
		DoorRenderHelper.end(poseStack);
		poseStack.popPose();
	}

	private void renderHitechDoor(MultiBlockDoorBlockEntity be, ObjModel model, TextureAtlasSprite sprite, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float partialTick)
	{
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(true));
		int rotation = be.getRotation();
		renderGroups(be, model, sprite, DoorPart.LEFT, rotation, poseStack, consumer, light, overlay, partialTick, "doorleft", "glassleft");
		renderGroups(be, model, sprite, DoorPart.RIGHT, rotation, poseStack, consumer, light, overlay, partialTick, "doorright", "glassright");
		renderGroups(be, model, sprite, DoorPart.TOP, rotation, poseStack, consumer, light, overlay, partialTick, "latchtop");
		renderGroups(be, model, sprite, DoorPart.BOTTOM, rotation, poseStack, consumer, light, overlay, partialTick, "latchbottom");
	}

	private void renderGroups(MultiBlockDoorBlockEntity be, ObjModel model, TextureAtlasSprite sprite, DoorPart part, int rotation, PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float partialTick, String... groups)
	{
		DoorRenderHelper.begin(poseStack, be, rotation, part, partialTick);
		DoorRenderHelper.toCorner(poseStack);
		model.render(poseStack.last(), consumer, sprite, 0xFFFFFFFF, light, overlay, groups);
		DoorRenderHelper.end(poseStack);
	}

	@Override
	public boolean shouldRenderOffScreen(MultiBlockDoorBlockEntity be)
	{
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(MultiBlockDoorBlockEntity be)
	{
		return be.getRenderBounds();
	}
}
