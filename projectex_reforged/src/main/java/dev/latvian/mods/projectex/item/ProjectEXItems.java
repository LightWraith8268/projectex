package dev.latvian.mods.projectex.item;

import dev.latvian.mods.projectex.Matter;
import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.Star;
import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import net.minecraft.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectEXItems {
	public static final DeferredRegister.Items REGISTRY =
			DeferredRegister.createItems(ProjectEX.MOD_ID);

	// Helper method for block items
	private static DeferredItem<BlockItem> blockItem(String id, DeferredBlock<? extends Block> block) {
		return REGISTRY.registerSimpleBlockItem(id, block);
	}

	// Matter-Tiered Energy Link Items
	public static final Map<Matter, DeferredItem<BlockItem>> ENERGY_LINK =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, blockItem(matter.name + "_energy_link",
							ProjectEXBlocks.ENERGY_LINK.get(matter)));
				}
			});

	// Link Block Items
	public static final DeferredItem<BlockItem> PERSONAL_LINK =
			blockItem("personal_link", ProjectEXBlocks.PERSONAL_LINK);

	public static final DeferredItem<BlockItem> REFINED_LINK =
			blockItem("refined_link", ProjectEXBlocks.REFINED_LINK);

	public static final DeferredItem<BlockItem> COMPRESSED_REFINED_LINK =
			blockItem("compressed_refined_link", ProjectEXBlocks.COMPRESSED_REFINED_LINK);

	// Matter-Tiered Block Items
	public static final Map<Matter, DeferredItem<BlockItem>> COLLECTOR =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, blockItem(matter.name + "_collector",
							ProjectEXBlocks.COLLECTOR.get(matter)));
				}
			});

	public static final Map<Matter, DeferredItem<BlockItem>> RELAY =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, blockItem(matter.name + "_relay",
							ProjectEXBlocks.RELAY.get(matter)));
				}
			});

	public static final Map<Matter, DeferredItem<BlockItem>> POWER_FLOWER =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, blockItem(matter.name + "_power_flower",
							ProjectEXBlocks.POWER_FLOWER.get(matter)));
				}
			});

	// Table Block Items
	public static final DeferredItem<BlockItem> STONE_TABLE =
			blockItem("stone_table", ProjectEXBlocks.STONE_TABLE);

	public static final DeferredItem<BlockItem> ALCHEMY_TABLE =
			blockItem("alchemy_table", ProjectEXBlocks.ALCHEMY_TABLE);

	// Star Items
	public static final Map<Star, DeferredItem<Item>> MAGNUM_STAR =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Star star : Star.VALUES) {
					map.put(star, REGISTRY.register("magnum_star_" + star.name,
							() -> new MagnumStarItem(star)));
				}
			});

	public static final Map<Star, DeferredItem<Item>> COLOSSAL_STAR =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Star star : Star.VALUES) {
					map.put(star, REGISTRY.register("colossal_star_" + star.name,
							() -> new ColossalStarItem(star)));
				}
			});

	// Matter Items
	public static final Map<Matter, DeferredItem<Item>> MATTER =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					if (matter.hasMatterItem) {
						map.put(matter, REGISTRY.registerSimpleItem(matter.name + "_matter"));
					}
				}
			});

	// Matter Block Items
	// Only register block items for matter blocks that exist (excludes DARK and RED)
	public static final Map<Matter, DeferredItem<BlockItem>> MATTER_BLOCK =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					// Skip DARK and RED - ProjectE already has those blocks
					if (matter != Matter.DARK && matter != Matter.RED) {
						map.put(matter, blockItem(matter.name + "_matter_block",
								ProjectEXBlocks.MATTER_BLOCK.get(matter)));
					}
				}
			});

	// Special Items
	public static final DeferredItem<Item> FINAL_STAR_SHARD =
			REGISTRY.registerSimpleItem("final_star_shard");

	public static final DeferredItem<Item> FINAL_STAR =
			REGISTRY.register("final_star", FinalStarItem::new);

	public static final DeferredItem<Item> KNOWLEDGE_SHARING_BOOK =
			REGISTRY.register("knowledge_sharing_book", KnowledgeSharingBookItem::new);

	public static final DeferredItem<Item> ARCANE_TABLET =
			REGISTRY.register("arcane_tablet", ArcaneTabletItem::new);

	// Compressed Collectors
	public static final Map<Matter, DeferredItem<Item>> COMPRESSED_COLLECTOR =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, REGISTRY.register(matter.name + "_compressed_collector",
							FoilItem::new));
				}
			});
}
