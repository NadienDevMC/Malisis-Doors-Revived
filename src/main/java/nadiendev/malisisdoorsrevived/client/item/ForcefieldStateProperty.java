package nadiendev.malisisdoorsrevived.client.item;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import nadiendev.malisisdoorsrevived.item.ForcefieldItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public record ForcefieldStateProperty() implements RangeSelectItemModelProperty
{
	public static final MapCodec<ForcefieldStateProperty> CODEC = MapCodec.unit(new ForcefieldStateProperty());

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed)
	{
		ForcefieldItem.Selection selection = ForcefieldItem.getSelection(stack);
		if (selection == null)
			return 0;
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK)
			return 0.25F;
		BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
		boolean creative = mc.player != null && mc.player.isCreative();
		return ForcefieldItem.isValid(stack, selection.pos(), pos, creative) ? 0.75F : 0.5F;
	}

	@Override
	public MapCodec<ForcefieldStateProperty> type()
	{
		return CODEC;
	}
}
