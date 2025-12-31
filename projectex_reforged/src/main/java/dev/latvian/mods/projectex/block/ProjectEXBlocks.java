package dev.latvian.mods.projectex.block;

import dev.latvian.mods.projectex.Matter;
import dev.latvian.mods.projectex.ProjectEX;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectEXBlocks {
	public static final DeferredRegister.Blocks REGISTRY =
			DeferredRegister.createBlocks(ProjectEX.MOD_ID);

	// Matter-Tiered Energy Links
	public static final Map<Matter, DeferredBlock<Block>> ENERGY_LINK =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, REGISTRY.register(matter.name + "_energy_link",
							() -> new EnergyLinkBlock(matter)));
				}
			});

	// Link Blocks
	public static final DeferredBlock<Block> PERSONAL_LINK =
			REGISTRY.register("personal_link", () -> new PersonalLinkBlock());

	public static final DeferredBlock<Block> REFINED_LINK =
			REGISTRY.register("refined_link", () -> new RefinedLinkBlock());

	public static final DeferredBlock<Block> COMPRESSED_REFINED_LINK =
			REGISTRY.register("compressed_refined_link", () -> new CompressedRefinedLinkBlock());

	// Matter-Tiered Blocks
	public static final Map<Matter, DeferredBlock<Block>> COLLECTOR =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, REGISTRY.register(matter.name + "_collector",
							() -> new CollectorBlock(matter)));
				}
			});

	public static final Map<Matter, DeferredBlock<Block>> RELAY =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, REGISTRY.register(matter.name + "_relay",
							() -> new RelayBlock(matter)));
				}
			});

	public static final Map<Matter, DeferredBlock<Block>> POWER_FLOWER =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					map.put(matter, REGISTRY.register(matter.name + "_power_flower",
							() -> new PowerFlowerBlock(matter)));
				}
			});

	// Matter Blocks (for Energy Link crafting)
	// Only register matter blocks for tiers that DON'T already exist in ProjectE (excludes DARK, RED)
	public static final Map<Matter, DeferredBlock<Block>> MATTER_BLOCK =
			Util.make(new LinkedHashMap<>(), map -> {
				for (Matter matter : Matter.VALUES) {
					// Skip DARK and RED - ProjectE already has dark_matter_block and red_matter_block
					if (matter != Matter.DARK && matter != Matter.RED) {
						map.put(matter, REGISTRY.register(matter.name + "_matter_block",
								() -> new MatterBlock(matter)));
					}
				}
			});

	// Transmutation Tables
	public static final DeferredBlock<Block> STONE_TABLE =
			REGISTRY.register("stone_table", () -> new StoneTableBlock());

	public static final DeferredBlock<Block> ALCHEMY_TABLE =
			REGISTRY.register("alchemy_table", () -> new AlchemyTableBlock());
}
