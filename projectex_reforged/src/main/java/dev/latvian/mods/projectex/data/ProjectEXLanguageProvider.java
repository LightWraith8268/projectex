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
import dev.latvian.mods.projectex.Star;
import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import dev.latvian.mods.projectex.item.ProjectEXItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ProjectEXLanguageProvider extends LanguageProvider {
	public ProjectEXLanguageProvider(PackOutput output) {
		super(output, ProjectEX.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		// Creative tab
		add("itemGroup." + ProjectEX.MOD_ID, "ProjectEX Reforged");

		// Matter items - only for tiers that have matter items
		for (Matter matter : Matter.VALUES) {
			if (matter.hasMatterItem) {
				add(ProjectEXItems.MATTER.get(matter).get(), matter.displayName + " Matter");
			}
		}

		// Matter Blocks
		for (Matter matter : Matter.VALUES) {
			add(ProjectEXBlocks.MATTER_BLOCK.get(matter).get(), matter.displayName + " Matter Block");
		}

		// Collectors
		for (Matter matter : Matter.VALUES) {
			add(ProjectEXBlocks.COLLECTOR.get(matter).get(), matter.displayName + " Collector");
		}

		// Power Flowers
		for (Matter matter : Matter.VALUES) {
			add(ProjectEXBlocks.POWER_FLOWER.get(matter).get(), matter.displayName + " Power Flower");
		}

		// Relays
		for (Matter matter : Matter.VALUES) {
			add(ProjectEXBlocks.RELAY.get(matter).get(), matter.displayName + " Relay");
		}

		// Energy Links (Matter-tiered)
		for (Matter matter : Matter.VALUES) {
			add(ProjectEXBlocks.ENERGY_LINK.get(matter).get(), matter.displayName + " Energy Link");
		}

		// Link blocks
		add(ProjectEXBlocks.PERSONAL_LINK.get(), "Personal Link");
		add(ProjectEXBlocks.REFINED_LINK.get(), "Refined Link");
		add(ProjectEXBlocks.COMPRESSED_REFINED_LINK.get(), "Compressed Refined Link");

		// Tables
		add(ProjectEXBlocks.STONE_TABLE.get(), "Stone Transmutation Table");
		add(ProjectEXBlocks.ALCHEMY_TABLE.get(), "Alchemy Transmutation Table");

		// Magnum Stars
		for (Star star : Star.VALUES) {
			add(ProjectEXItems.MAGNUM_STAR.get(star).get(), "Magnum Star " + star.displayName);
		}

		// Colossal Stars
		for (Star star : Star.VALUES) {
			add(ProjectEXItems.COLOSSAL_STAR.get(star).get(), "Colossal Star " + star.displayName);
		}

		// Final Star
		add(ProjectEXItems.FINAL_STAR.get(), "The Final Star");
		add(ProjectEXItems.FINAL_STAR_SHARD.get(), "Final Star Shard");

		// Utility items
		add(ProjectEXItems.ARCANE_TABLET.get(), "Arcane Tablet");
		add(ProjectEXItems.KNOWLEDGE_SHARING_BOOK.get(), "Knowledge Sharing Book");

		// Tooltips - Collectors
		add("block.projectex.collector.tooltip", "Generates EMC every second");
		add("block.projectex.collector.emc_produced", "Produces %s EMC/s");

		// Tooltips - Relays
		add("block.projectex.relay.tooltip", "Transfers EMC and provides bonus to adjacent collectors");
		add("block.projectex.relay.relay_bonus", "Relay Bonus: %s EMC/s");
		add("block.projectex.relay.max_transfer", "Max Transfer: %s EMC/s");

		// Tooltips - Power Flowers
		add("block.projectex.power_flower.tooltip", "Outputs EMC directly to owner's knowledge");

		// Tooltips - Link Blocks
		add("block.projectex.personal_link.tooltip", "Transfers EMC to owner's personal EMC");
		add("block.projectex.energy_link.tooltip", "Bidirectional EMC ↔ FE conversion");
		add("block.projectex.refined_link.tooltip", "Provides EMC to Refined Storage/AE2 network");
		add("block.projectex.compressed_refined_link.tooltip", "High-capacity Refined Storage/AE2 EMC link");

		// Tooltips - Tables
		add("block.projectex.stone_table.tooltip", "Compact transmutation table");
		add("block.projectex.alchemy_table.tooltip", "Advanced transmutation with EMC auto-fill");

		// Tooltips - Special Items
		add("item.projectex.arcane_tablet.tooltip", "Portable Alchemy Table");
		add("item.projectex.knowledge_sharing_book.tooltip", "Share knowledge between players");
		add("item.projectex.final_star.tooltip", "The ultimate EMC storage");
	}
}
