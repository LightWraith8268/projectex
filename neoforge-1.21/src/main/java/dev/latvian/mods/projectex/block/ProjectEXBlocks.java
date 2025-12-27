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

	// Link Blocks
	public static final DeferredBlock<Block> ENERGY_LINK =
			REGISTRY.register("energy_link", EnergyLinkBlock::new);

	public static final DeferredBlock<Block> PERSONAL_LINK =
			REGISTRY.register("personal_link", PersonalLinkBlock::new);

	public static final DeferredBlock<Block> REFINED_LINK =
			REGISTRY.register("refined_link", RefinedLinkBlock::new);

	public static final DeferredBlock<Block> COMPRESSED_REFINED_LINK =
			REGISTRY.register("compressed_refined_link", CompressedRefinedLinkBlock::new);

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

	// Link Blocks
	public static final DeferredBlock<Block> PERSONAL_LINK =
			REGISTRY.register("personal_link", PersonalLinkBlock::new);

	public static final DeferredBlock<Block> ENERGY_LINK =
			REGISTRY.register("energy_link", EnergyLinkBlock::new);

	public static final DeferredBlock<Block> REFINED_LINK =
			REGISTRY.register("refined_link", RefinedLinkBlock::new);

	public static final DeferredBlock<Block> COMPRESSED_REFINED_LINK =
			REGISTRY.register("compressed_refined_link", CompressedRefinedLinkBlock::new);

	// Transmutation Tables
	public static final DeferredBlock<Block> STONE_TABLE =
			REGISTRY.register("stone_table", StoneTableBlock::new);

	public static final DeferredBlock<Block> ALCHEMY_TABLE =
			REGISTRY.register("alchemy_table", AlchemyTableBlock::new);
}
