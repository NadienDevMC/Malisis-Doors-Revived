package nadiendev.malisisdoorsrevived.client.renderer;

import nadiendev.malisiscorerevived.client.render.SimpleBlockEntityRenderer;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import nadiendev.malisiscorerevived.animation.ChainedTransformation;
import nadiendev.malisiscorerevived.animation.ParallelTransformation;
import nadiendev.malisiscorerevived.animation.Rotation;
import nadiendev.malisiscorerevived.animation.Transformable;
import nadiendev.malisiscorerevived.animation.Translation;
import nadiendev.malisiscorerevived.client.render.RenderUtils;
import nadiendev.malisiscorerevived.util.DirectionUtils;
import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.blockentity.GarageDoorBlockEntity;
import nadiendev.malisisdoorsrevived.movement.IDoorMovement;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/**
 * Renderer of the garage doors. The top panel renders all the panels of the door : they slide up, then rotate and slide into the ceiling.
 *
 * @author Ordinastie
 */
public class GarageDoorRenderer extends SimpleBlockEntityRenderer<GarageDoorBlockEntity>
{
	private static final float W = IDoorMovement.DOOR_WIDTH;
	private static final AABB PANEL = new AABB(0, 0, 0.5F - W / 2, 1, 1, 0.5F + W / 2);

	public GarageDoorRenderer(BlockEntityRendererProvider.Context context)
	{}

	@Override
	protected void render(GarageDoorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		if (!be.isTopDoor() || be.getLevel() == null)
			return;

		TextureAtlasSprite base = RenderUtils.getBlockSprite(MalisisDoors.id("block/garage_door"));
		TextureAtlasSprite top = RenderUtils.getBlockSprite(MalisisDoors.id("block/garage_door_top"));
		VertexConsumer consumer = buffer.getBuffer(RenderUtils.blockAtlasType(false));
		int t = GarageDoorBlockEntity.PANEL_TIME;
		float elapsed = be.getElapsedTime(partialTick);
		boolean reversed = be.getDoorState().isClosingOrClosed();
		int rotation = DirectionUtils.getRotationCount(be.getDirection());

		List<GarageDoorBlockEntity> doors = be.getDoors();
		for (GarageDoorBlockEntity panel : doors)
		{
			int delta = be.getBlockPos().getY() - panel.getBlockPos().getY();
			int delta2 = doors.size() - (delta + 1);

			Translation vertical = new Translation(0, -delta, 0, 0, 0, 0).forTicks(t * delta);
			ParallelTransformation topRotate = new ParallelTransformation(	new Translation(0, 1, 0).forTicks(t),
																			new Rotation(0, -90).aroundAxis(1, 0, 0).offset(-0.5F, -0.5F, 0).forTicks(t));
			Translation horizontal = new Translation(0, 0, 0, 0, delta2, 0).forTicks(t * delta2);
			ChainedTransformation chained = new ChainedTransformation(vertical, topRotate, horizontal);
			chained.reversed(reversed);

			poseStack.pushPose();
			Transformable transformable = Transformable.centered(poseStack);
			transformable.rotate(rotation * 90, 0, 1, 0);
			chained.transform(transformable, elapsed);
			poseStack.translate(-0.5F, -0.5F, -0.5F);

			boolean isTop = panel == be;
			RenderUtils.BoxTexture texture = RenderUtils.BoxTexture.of(side -> isTop && side.getAxis() == Direction.Axis.Z ? top : base);
			int light = LevelRenderer.getLightCoords(be.getLevel(), panel.getBlockPos());
			RenderUtils.renderBox(poseStack.last(), consumer, PANEL, texture, 0xFFFFFFFF, light, packedOverlay);
			poseStack.popPose();
		}
	}

	@Override
	public AABB getRenderBoundingBox(GarageDoorBlockEntity be)
	{
		return be.getRenderBounds();
	}
}
