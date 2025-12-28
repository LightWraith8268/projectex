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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ProjectEXItemModelProvider extends ItemModelProvider {
	public ProjectEXItemModelProvider(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, ProjectEX.MOD_ID, fileHelper);
	}

	@Override
	protected void registerModels() {
		// Block items - simple parent references
		for (Matter matter : Matter.VALUES) {
			withExistingParent(matter.name + "_collector", modLoc("block/" + matter.name + "_collector"));
			withExistingParent(matter.name + "_power_flower", modLoc("block/" + matter.name + "_power_flower"));
			withExistingParent(matter.name + "_relay", modLoc("block/" + matter.name + "_relay"));
		}

		withExistingParent("personal_link", modLoc("block/personal_link"));
		withExistingParent("energy_link", modLoc("block/energy_link"));
		withExistingParent("refined_link", modLoc("block/refined_link"));
		withExistingParent("compressed_refined_link", modLoc("block/compressed_refined_link"));
		withExistingParent("stone_table", modLoc("block/stone_table"));
		withExistingParent("alchemy_table", modLoc("block/alchemy_table"));

		// Matter items
		for (Matter matter : Matter.VALUES) {
			singleTexture(matter.name + "_matter", mcLoc("item/generated"),
				"layer0", modLoc("item/matter/" + matter.name));
		}

		// Magnum Stars (6 tiers)
		for (int i = 0; i < 6; i++) {
			singleTexture("magnum_star_" + Star.VALUES[i].name, mcLoc("item/generated"),
				"layer0", modLoc("item/magnum_star/" + Star.VALUES[i].name));
		}

		// Colossal Stars (6 tiers)
		for (int i = 0; i < 6; i++) {
			singleTexture("colossal_star_" + Star.VALUES[i].name, mcLoc("item/generated"),
				"layer0", modLoc("item/colossal_star/" + Star.VALUES[i].name));
		}

		// Final Star
		singleTexture("final_star", mcLoc("item/generated"),
			"layer0", modLoc("item/final_star"));

		// Utility items
		singleTexture("arcane_tablet", mcLoc("item/generated"),
			"layer0", modLoc("item/arcane_tablet"));
		singleTexture("knowledge_sharing_book", mcLoc("item/generated"),
			"layer0", modLoc("item/knowledge_sharing_book"));
	}
}
