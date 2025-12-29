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

package dev.latvian.mods.projectex.integration.jei;

import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.client.screen.AlchemyTableScreen;
import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI Plugin for ProjectEX
 * Phase 6: JEI Integration
 *
 * Provides recipe transfer support for Alchemy Table
 */
@JeiPlugin
public class ProjectEXJEIPlugin implements IModPlugin {
	private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(
			ProjectEX.MOD_ID,
			"jei_plugin"
	);

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		// Register Alchemy Table recipe transfer handler for crafting recipes
		registration.addRecipeTransferHandler(
				new AlchemyTableRecipeTransferHandler(registration.getTransferHelper()),
				RecipeTypes.CRAFTING
		);
	}
}
