package nadiendev.malisisdoorsrevived.blockentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import nadiendev.malisiscorerevived.util.AABBUtils;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Block entity at the origin of a forcefield : stores the area covered by the forcefield and whether it's opened.
 *
 * @author Ordinastie
 */
public class ForcefieldBlockEntity extends BlockEntity
{
	private static final float THICKNESS = 1 / 16F;

	/** Area covered by the forcefield, relative to the origin. */
	@Nullable
	private AABB area;
	private boolean opened = false;

	public ForcefieldBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.FORCEFIELD.get(), pos, state);
	}

	@Nullable
	public AABB getArea()
	{
		return area;
	}

	public void setArea(AABB area)
	{
		this.area = area;
		sync();
	}

	public boolean isOpened()
	{
		return opened;
	}

	public void switchForcefield()
	{
		opened = !opened;
		sync();
	}

	/**
	 * @return the axis perpendicular to the plane of the forcefield
	 */
	public Direction.Axis getAxis()
	{
		if (area == null || area.getYsize() <= 1)
			return Direction.Axis.Y;
		if (area.getXsize() <= 1)
			return Direction.Axis.X;
		return Direction.Axis.Z;
	}

	/**
	 * @return the number of blocks covered by the forcefield
	 */
	public int getSize()
	{
		if (area == null)
			return 0;
		return (int) Math.round(area.getXsize() * area.getYsize() * area.getZsize());
	}

	/**
	 * @return the world positions covered by the forcefield
	 */
	public List<BlockPos> getPositions()
	{
		List<BlockPos> list = new ArrayList<>();
		if (area == null)
		{
			list.add(worldPosition);
			return list;
		}
		BlockPos.betweenClosed(	worldPosition.offset((int) area.minX, (int) area.minY, (int) area.minZ),
								worldPosition.offset((int) area.maxX - 1, (int) area.maxY - 1, (int) area.maxZ - 1))
				.forEach(p -> list.add(p.immutable()));
		return list;
	}

	/**
	 * Gets the shape of the forcefield in the block at pos, relative to pos.
	 */
	public VoxelShape getShape(BlockPos pos, boolean collision)
	{
		if (area == null || (opened && collision))
			return Shapes.empty();

		double min = 0.5 - THICKNESS, max = 0.5 + THICKNESS;
		AABB plane = switch (getAxis())
		{
			case X -> new AABB(area.minX + min, area.minY, area.minZ, area.minX + max, area.maxY, area.maxZ);
			case Y -> new AABB(area.minX, area.minY + min, area.minZ, area.maxX, area.minY + max, area.maxZ);
			case Z -> new AABB(area.minX, area.minY, area.minZ + min, area.maxX, area.maxY, area.minZ + max);
		};
		return AABBUtils.toShape(AABBUtils.intersect(plane, worldPosition, pos));
	}

	public AABB getRenderBounds()
	{
		return area != null ? area.move(worldPosition).inflate(1) : new AABB(worldPosition);
	}

	private void sync()
	{
		if (level == null || level.isClientSide())
			return;
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		opened = tag.getBoolean("opened");
		if (tag.contains("area"))
			area = AABBUtils.read(tag.getCompound("area"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putBoolean("opened", opened);
		if (area != null)
		{
			CompoundTag areaTag = new CompoundTag();
			AABBUtils.write(areaTag, area);
			tag.put("area", areaTag);
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		return saveWithoutMetadata(registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
