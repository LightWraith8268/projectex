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
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ProjectEXBlockStateProvider extends BlockStateProvider {
	public ProjectEXBlockStateProvider(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, ProjectEX.MOD_ID, fileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		// Collectors - all tiers
		for (Matter matter : Matter.VALUES) {
			simpleBlock(ProjectEXBlocks.COLLECTOR.get(matter).get(),
				models().cubeAll(matter.name + "_collector",
					modLoc("block/collector/" + matter.name)));
		}

		// Power Flowers - all tiers (custom model with collector and relay textures)
		for (Matter matter : Matter.VALUES) {
			simpleBlock(ProjectEXBlocks.POWER_FLOWER.get(matter).get(),
				models().withExistingParent(matter.name + "_power_flower", modLoc("block/power_flower"))
					.texture("collector", modLoc("block/collector/" + matter.name))
					.texture("relay", modLoc("block/relay/" + matter.name)));
		}

		// Relays - all tiers
		for (Matter matter : Matter.VALUES) {
			simpleBlock(ProjectEXBlocks.RELAY.get(matter).get(),
				models().cubeAll(matter.name + "_relay",
					modLoc("block/relay/" + matter.name)));
		}

		// Link blocks
		simpleBlock(ProjectEXBlocks.PERSONAL_LINK.get(),
			models().cubeAll("personal_link", modLoc("block/personal_link")));
		simpleBlock(ProjectEXBlocks.ENERGY_LINK.get(),
			models().cubeAll("energy_link", modLoc("block/energy_link")));
		simpleBlock(ProjectEXBlocks.REFINED_LINK.get(),
			models().cubeAll("refined_link", modLoc("block/refined_link")));
		simpleBlock(ProjectEXBlocks.COMPRESSED_REFINED_LINK.get(),
			models().cubeAll("compressed_refined_link", modLoc("block/compressed_refined_link")));

		// Tables - horizontal blocks
		horizontalBlock(ProjectEXBlocks.STONE_TABLE.get(),
			models().withExistingParent("stone_table", modLoc("block/stone_table")));
		horizontalBlock(ProjectEXBlocks.ALCHEMY_TABLE.get(),
			models().withExistingParent("alchemy_table", modLoc("block/alchemy_table")));
	}

	private void simpleBlock(Block block, ConfiguredModel model) {
		getVariantBuilder(block).partialState().addModels(model);
	}
}
