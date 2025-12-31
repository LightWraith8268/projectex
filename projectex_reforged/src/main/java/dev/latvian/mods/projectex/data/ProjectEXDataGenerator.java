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

import dev.latvian.mods.projectex.ProjectEX;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProjectEX.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ProjectEXDataGenerator {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		// Client-side providers
		generator.addProvider(event.includeClient(), new ProjectEXBlockStateProvider(output, fileHelper));
		generator.addProvider(event.includeClient(), new ProjectEXItemModelProvider(output, fileHelper));
		generator.addProvider(event.includeClient(), new ProjectEXLanguageProvider(output));

		// Server-side providers
		generator.addProvider(event.includeServer(), new ProjectEXRecipeProvider(output, lookupProvider));
		generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(),
				List.of(new LootTableProvider.SubProviderEntry(ProjectEXLootTableProvider::new, LootContextParamSets.BLOCK)),
				lookupProvider));
	}
}
