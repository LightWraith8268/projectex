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
		add("itemGroup." + ProjectEX.MOD_ID, "ProjectEX");

		// Matter items
		for (Matter matter : Matter.VALUES) {
			add(ProjectEXItems.MATTER.get(matter).get(), matter.displayName + " Matter");
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

		// Link blocks
		add(ProjectEXBlocks.PERSONAL_LINK.get(), "Personal Link");
		add(ProjectEXBlocks.ENERGY_LINK.get(), "Energy Link");
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

		// Utility items
		add(ProjectEXItems.ARCANE_TABLET.get(), "Arcane Tablet");
		add(ProjectEXItems.KNOWLEDGE_SHARING_BOOK.get(), "Knowledge Sharing Book");
	}
}
