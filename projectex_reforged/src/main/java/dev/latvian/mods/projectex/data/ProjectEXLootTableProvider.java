/*
 * ProjectEX
 *
 * Copyright (C) 2024 LatvianModder (original author)
 * Copyright (C) 2024 LightWraith8268 (1.21.1+ NeoForge port)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * https://www.gnu.org/licenses/lgpl-3.0.html
 */

package dev.latvian.mods.projectex.data;

import dev.latvian.mods.projectex.Matter;
import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class ProjectEXLootTableProvider extends BlockLootSubProvider {
	public ProjectEXLootTableProvider(HolderLookup.Provider provider) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
	}

	@Override
	protected void generate() {
		// Matter-tiered Collectors
		for (Matter matter : Matter.VALUES) {
			dropSelf(ProjectEXBlocks.COLLECTOR.get(matter).get());
		}

		// Matter-tiered Relays
		for (Matter matter : Matter.VALUES) {
			dropSelf(ProjectEXBlocks.RELAY.get(matter).get());
		}

		// Matter-tiered Power Flowers
		for (Matter matter : Matter.VALUES) {
			dropSelf(ProjectEXBlocks.POWER_FLOWER.get(matter).get());
		}

		// Matter-tiered Energy Links
		for (Matter matter : Matter.VALUES) {
			dropSelf(ProjectEXBlocks.ENERGY_LINK.get(matter).get());
		}

		// Matter Blocks (exclude DARK and RED - ProjectE has those)
		for (Matter matter : Matter.VALUES) {
			if (matter != Matter.DARK && matter != Matter.RED) {
				dropSelf(ProjectEXBlocks.MATTER_BLOCK.get(matter).get());
			}
		}

		// Link blocks
		dropSelf(ProjectEXBlocks.PERSONAL_LINK.get());
		dropSelf(ProjectEXBlocks.REFINED_LINK.get());
		dropSelf(ProjectEXBlocks.COMPRESSED_REFINED_LINK.get());

		// Tables
		dropSelf(ProjectEXBlocks.STONE_TABLE.get());
		dropSelf(ProjectEXBlocks.ALCHEMY_TABLE.get());
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		// Return all blocks registered by ProjectEX
		Set<Block> blocks = new HashSet<>();

		// Matter-tiered blocks
		for (Matter matter : Matter.VALUES) {
			blocks.add(ProjectEXBlocks.COLLECTOR.get(matter).get());
			blocks.add(ProjectEXBlocks.RELAY.get(matter).get());
			blocks.add(ProjectEXBlocks.POWER_FLOWER.get(matter).get());
			blocks.add(ProjectEXBlocks.ENERGY_LINK.get(matter).get());

			// Matter blocks (exclude DARK and RED)
			if (matter != Matter.DARK && matter != Matter.RED) {
				blocks.add(ProjectEXBlocks.MATTER_BLOCK.get(matter).get());
			}
		}

		// Link blocks
		blocks.add(ProjectEXBlocks.PERSONAL_LINK.get());
		blocks.add(ProjectEXBlocks.REFINED_LINK.get());
		blocks.add(ProjectEXBlocks.COMPRESSED_REFINED_LINK.get());

		// Tables
		blocks.add(ProjectEXBlocks.STONE_TABLE.get());
		blocks.add(ProjectEXBlocks.ALCHEMY_TABLE.get());

		return blocks;
	}
}
