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

package dev.latvian.mods.projectex.client;

import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.client.screen.AlchemyTableScreen;
import dev.latvian.mods.projectex.menu.ProjectEXMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-side initialization for ProjectEX
 * Registers screens, renderers, and other client-only features
 */
@SuppressWarnings("removal")  // EventBusSubscriber.Bus is deprecated but still functional
@EventBusSubscriber(modid = ProjectEX.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ProjectEXClient {

	/**
	 * Register GUI screens for menus
	 * Called during client setup phase
	 */
	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		// Register Alchemy Table screen
		event.register(ProjectEXMenuTypes.ALCHEMY_TABLE.get(), AlchemyTableScreen::new);
	}
}
