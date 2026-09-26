package nadiendev.malisisdoorsrevived.animated;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.neoforged.neoforge.event.AddPackFindersEvent;

public class AnimatedCopiesPack implements PackResources
{
	private static final String BLOCKSTATE = "{\"variants\":{\"\":{\"model\":\"minecraft:block/air\"}}}";

	private final PackLocationInfo location;
	private final PackType type;
	@Nullable
	private Map<Identifier, byte[]> files;

	private AnimatedCopiesPack(PackLocationInfo location, PackType type)
	{
		this.location = location;
		this.type = type;
	}

	public static void addPack(AddPackFindersEvent event)
	{
		if (AnimatedCopies.getCopies().isEmpty())
			return;
		Pack pack = create(event.getPackType());
		if (pack != null)
			event.addRepositorySource(consumer -> consumer.accept(pack));
	}

	@Nullable
	private static Pack create(PackType type)
	{
		String name = type == PackType.CLIENT_RESOURCES ? "resources" : "data";
		PackLocationInfo location = new PackLocationInfo(	MalisisDoors.MODID + ":animated_copies_" + name,
															Component.literal("MalisisDoors animated copies"),
															PackSource.BUILT_IN,
															Optional.empty());
		Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier()
		{
			@Override
			public PackResources openPrimary(PackLocationInfo info)
			{
				return new AnimatedCopiesPack(info, type);
			}

			@Override
			public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata)
			{
				return new AnimatedCopiesPack(info, type);
			}
		};
		return Pack.readMetaAndCreate(location, supplier, type, new PackSelectionConfig(true, Pack.Position.BOTTOM, true));
	}

	private Map<Identifier, byte[]> files()
	{
		if (files == null)
			files = type == PackType.CLIENT_RESOURCES ? blockStates() : tags();
		return files;
	}

	private static Map<Identifier, byte[]> blockStates()
	{
		return AnimatedCopies	.getCopies()
								.stream()
								.collect(Collectors.toMap(	copy -> copy.id().withPath(path -> "blockstates/" + path + ".json"),
															copy -> BLOCKSTATE.getBytes(StandardCharsets.UTF_8),
															(a, b) -> a,
															LinkedHashMap::new));
	}

	private static Map<Identifier, byte[]> tags()
	{
		Map<String, List<Identifier>> tags = new LinkedHashMap<>();
		for (AnimatedCopies.Copy copy : AnimatedCopies.getCopies())
		{
			boolean wooden = !(copy.animated() instanceof IDoorBlock door) || door.getDescriptor().getRedstoneBehavior() != RedstoneBehavior.REDSTONE_ONLY;
			switch (copy.animated())
			{
				case DoorBlock door -> {
					tags.computeIfAbsent("doors", k -> new ArrayList<>()).add(copy.id());
					if (wooden)
						tags.computeIfAbsent("wooden_doors", k -> new ArrayList<>()).add(copy.id());
				}
				case TrapDoorBlock trapDoor -> {
					tags.computeIfAbsent("trapdoors", k -> new ArrayList<>()).add(copy.id());
					if (wooden)
						tags.computeIfAbsent("wooden_trapdoors", k -> new ArrayList<>()).add(copy.id());
				}
				case FenceGateBlock gate -> tags.computeIfAbsent("fence_gates", k -> new ArrayList<>()).add(copy.id());
				default -> {}
			}
		}

		Map<Identifier, byte[]> files = new LinkedHashMap<>();
		tags.forEach((tag, ids) -> {
			JsonObject json = new JsonObject();
			json.addProperty("replace", false);
			JsonArray values = new JsonArray();
			ids.forEach(id -> values.add(id.toString()));
			json.add("values", values);
			files.put(Identifier.withDefaultNamespace("tags/block/" + tag + ".json"), json.toString().getBytes(StandardCharsets.UTF_8));
		});
		return files;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... elements)
	{
		return null;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(PackType type, Identifier location)
	{
		if (type != this.type)
			return null;
		byte[] bytes = files().get(location);
		return bytes != null ? () -> new ByteArrayInputStream(bytes) : null;
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput output)
	{
		if (type != this.type)
			return;
		files().forEach((location, bytes) -> {
			if (location.getNamespace().equals(namespace) && location.getPath().startsWith(path + "/"))
				output.accept(location, () -> new ByteArrayInputStream(bytes));
		});
	}

	@Override
	public Set<String> getNamespaces(PackType type)
	{
		if (type != this.type)
			return Set.of();
		return files().keySet().stream().map(Identifier::getNamespace).collect(Collectors.toSet());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> @Nullable T getMetadataSection(MetadataSectionType<T> section)
	{
		if (!"pack".equals(section.name()))
			return null;
		return (T) new PackMetadataSection(location.title(), new InclusiveRange<>(SharedConstants.getCurrentVersion().packVersion(type)));
	}

	@Override
	public PackLocationInfo location()
	{
		return location;
	}

	@Override
	public void close()
	{}
}
