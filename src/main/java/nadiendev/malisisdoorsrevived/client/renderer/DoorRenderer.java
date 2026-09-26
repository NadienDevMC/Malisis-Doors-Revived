package nadiendev.malisisdoorsrevived.client.renderer;

import nadiendev.malisiscorerevived.client.render.SimpleBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import nadiendev.malisiscorerevived.animation.Transformable;
import nadiendev.malisiscorerevived.client.model.ObjModel;
import nadiendev.malisiscorerevived.client.model.ObjModelCache;
import nadiendev.malisiscorerevived.client.render.RenderUtils;
import nadiendev.malisisdoorsrevived.block.MalisisDoorBlock;
import nadiendev.malisisdoorsrevived.blockentity.CustomDoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.movement.DoorPart;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;

/**
 * Renderer of the two blocks high doors.
 *
 * @author Ordinastie
 */
public class DoorRenderer extends SimpleBlockEntityRenderer<DoorBlockEntity>
{
	private static final float W = IDoorMovement.DOOR_WIDTH;
	private static final float FRAME = 1 / 8F;

	public DoorRenderer(BlockEntityRendererProvider.Context context)
	{}

	@Override
	protected void render(DoorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		BlockState state = be.getBlockState();
		if (!(state.getBlock() instanceof MalisisDoorBlock door))
			return;

		Level level = be.getLevel();
		BlockPos pos = be.getBlockPos();
		int topLight = level != null ? LevelRenderer.getLightCoords(level, pos.above()) : packedLight;
		Direction direction = be.getDirection();

		switch (door.getAppearance())
		{
			case DoorAppearance.Obj obj -> renderObj(be, obj, poseStack, buffer, Math.max(packedLight, topLight), packedOverlay, partialTick);
			case DoorAppearance.Custom custom -> {
				renderCustom(be, DoorPart.BOTTOM, poseStack, buffer, packedLight, packedOverlay, partialTick);
				renderCustom(be, DoorPart.TOP, poseStack, buffer, topLight, packedOverlay, partialTick);
			}
			default -> {
				Block modelBlock = door.getAppearance() instanceof DoorAppearance.Copy copy ? copy.block().get() : door;
				for (DoorPart part : new DoorPart[] { DoorPart.BOTTOM, DoorPart.TOP })
				{
					if (!be.getMovement().shouldRender(be, part))
						continue;
					Transformable t = DoorRenderHelper.begin(poseStack, be, direction, part, partialTick);
					DoorRenderHelper.toCorner(poseStack);
					if (part == DoorPart.TOP)
						poseStack.translate(0, 1, 0);
					BlockState modelState = getModelState(modelBlock, state, part == DoorPart.TOP);
					RenderUtils.renderBlockState(	poseStack,
													buffer,
													modelState,
													level,
													part == DoorPart.TOP ? pos.above() : pos,
													part == DoorPart.TOP ? topLight : packedLight,
													packedOverlay,
													t.getAlphaF());
					DoorRenderHelper.end(poseStack);
				}
			}
		}
	}

	/**
	 * Gets the state of the model to render : closed and facing south, the animation handles the rest.
	 */
	public static BlockState getModelState(Block block, BlockState doorState, boolean top)
	{
		BlockState state = block.defaultBlockState();
		if (!state.hasProperty(DoorBlock.HALF))
			return state;
		return state.setValue(DoorBlock.FACING, Direction.SOUTH)
					.setValue(DoorBlock.HINGE, doorState.getValue(DoorBlock.HINGE))
					.setValue(DoorBlock.HALF, top ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER)
					.setValue(DoorBlock.OPEN, false);
	}

	private void renderObj(DoorBlockEntity be, DoorAppearance.Obj obj, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float partialTick)
	{
		ObjModel model = ObjModelCache.get(obj.model());
		TextureAtlasSprite sprite = RenderUtils.getBlockSprite(obj.texture());
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(false));
		Direction direction = be.getDirection();

		if (model.getGroup("handle4") != null)
		{
			//vertical hatch : static frame, animated door and handle
			renderGroups(be, model, null, poseStack, consumer, sprite, light, overlay, partialTick, direction, "frame");
			renderGroups(be, model, DoorPart.DOOR, poseStack, consumer, sprite, light, overlay, partialTick, direction, "door");
			renderGroups(be, model, DoorPart.HANDLE, poseStack, consumer, sprite, light, overlay, partialTick, direction, "handle4");
		}
		else
			renderGroups(be, model, DoorPart.DOOR, poseStack, consumer, sprite, light, overlay, partialTick, direction);
	}

	private void renderGroups(DoorBlockEntity be, ObjModel model, DoorPart part, PoseStack poseStack, VertexConsumer consumer, TextureAtlasSprite sprite, int light, int overlay, float partialTick, Direction direction, String... groups)
	{
		DoorRenderHelper.begin(poseStack, be, direction, part, partialTick);
		DoorRenderHelper.toCorner(poseStack);
		model.render(poseStack.last(), consumer, sprite, 0xFFFFFFFF, light, overlay, groups);
		DoorRenderHelper.end(poseStack);
	}

	private void renderCustom(DoorBlockEntity be, DoorPart part, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, float partialTick)
	{
		if (!be.getMovement().shouldRender(be, part))
			return;
		DoorMaterials materials = be instanceof CustomDoorBlockEntity custom ? custom.getMaterials() : DoorMaterials.DEFAULT;
		Transformable t = DoorRenderHelper.begin(poseStack, be, be.getDirection(), part, partialTick);
		DoorRenderHelper.toCorner(poseStack);
		renderCustomDoorHalf(poseStack, buffer, materials, part == DoorPart.TOP, be.getLevel(), be.getBlockPos(), light, overlay, t.getAlphaF());
		DoorRenderHelper.end(poseStack);
	}

	/**
	 * Renders half of a custom door (in the coordinates of the bottom block).
	 */
	public static void renderCustomDoorHalf(PoseStack poseStack, MultiBufferSource buffer, DoorMaterials materials, boolean top, Level level, BlockPos pos, int light, int overlay, float alpha)
	{
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(true));
		PoseStack.Pose pose = poseStack.last();
		float y = top ? 1 : 0;
		int a = (int) (alpha * 255) << 24;

		BlockState frame = materials.frame();
		int frameColor = (tint(frame, level, pos) & 0xFFFFFF) | a;
		RenderUtils.BoxTexture frameTexture = RenderUtils.BoxTexture.of(side -> RenderUtils.getSprite(frame, side));
		RenderUtils.renderBox(pose, consumer, new AABB(0, y, 0, FRAME, y + 1, W), frameTexture, frameColor, light, overlay);
		RenderUtils.renderBox(pose, consumer, new AABB(1 - FRAME, y, 0, 1, y + 1, W), frameTexture, frameColor, light, overlay);
		if (top)
			RenderUtils.renderBox(pose, consumer, new AABB(FRAME, y + 1 - FRAME, 0, 1 - FRAME, y + 1, W), frameTexture, frameColor, light, overlay);
		else
			RenderUtils.renderBox(pose, consumer, new AABB(FRAME, y, 0, 1 - FRAME, y + FRAME, W), frameTexture, frameColor, light, overlay);

		BlockState material = top ? materials.top() : materials.bottom();
		int materialColor = (tint(material, level, pos) & 0xFFFFFF) | a;
		RenderUtils.BoxTexture materialTexture = RenderUtils.BoxTexture.of(side -> RenderUtils.getSprite(material, side));
		AABB panel = top ? new AABB(FRAME, y, W * 0.2F, 1 - FRAME, y + 1 - FRAME, W * 0.8F) : new AABB(FRAME, y + FRAME, W * 0.2F, 1 - FRAME, y + 1, W * 0.8F);
		RenderUtils.renderBox(pose, consumer, panel, materialTexture, materialColor, light, overlay);
	}

	private static int tint(BlockState state, Level level, BlockPos pos)
	{
		if (state.is(Blocks.GRASS_BLOCK))
			return 0xFFFFFFFF;
		return RenderUtils.getTint(state, level, pos);
	}

	@Override
	public AABB getRenderBoundingBox(DoorBlockEntity be)
	{
		return new AABB(be.getBlockPos()).expandTowards(0, 1, 0).inflate(1);
	}
}
