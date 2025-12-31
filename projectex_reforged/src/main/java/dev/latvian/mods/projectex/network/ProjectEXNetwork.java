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

package dev.latvian.mods.projectex.network;

import dev.latvian.mods.projectex.ProjectEX;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Network handler for ProjectEX packets
 * Registers custom packets for client-server communication
 */
@EventBusSubscriber(modid = ProjectEX.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ProjectEXNetwork {

	/**
	 * Register all ProjectEX network packets
	 */
	@SubscribeEvent
	public static void registerPayloads(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(ProjectEX.MOD_ID)
				.versioned("1.0.0")
				.optional();

		// Register RecipeTransferPacket (client -> server)
		registrar.playToServer(
				RecipeTransferPacket.TYPE,
				RecipeTransferPacket.STREAM_CODEC,
				RecipeTransferPacket::handle
		);
	}
}
