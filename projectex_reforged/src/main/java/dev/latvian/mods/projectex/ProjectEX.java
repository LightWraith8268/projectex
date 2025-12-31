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

package dev.latvian.mods.projectex;

import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import dev.latvian.mods.projectex.block.entity.EnergyLinkBlockEntity;
import dev.latvian.mods.projectex.block.entity.LinkBaseBlockEntity;
import dev.latvian.mods.projectex.block.entity.ProjectEXBlockEntities;
import dev.latvian.mods.projectex.item.ProjectEXItems;
import dev.latvian.mods.projectex.menu.ProjectEXMenuTypes;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(ProjectEX.MOD_ID)
public class ProjectEX {
	public static final String MOD_ID = "projectex_reforged";

	public static final Direction[] DIRECTIONS = Direction.values();

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

	public static final Supplier<CreativeModeTab> TAB = CREATIVE_TABS.register("main", () ->
			CreativeModeTab.builder()
					.icon(() -> new ItemStack(ProjectEXItems.ARCANE_TABLET.get()))
					.title(Component.translatable("itemGroup." + MOD_ID))
					.displayItems((params, output) -> {
						// Add all items to creative tab
						ProjectEXItems.REGISTRY.getEntries().forEach(entry -> {
							output.accept(entry.get());
						});
					})
					.build()
	);

	public ProjectEX(IEventBus modEventBus) {
		// Register all deferred registers
		ProjectEXBlocks.REGISTRY.register(modEventBus);
		ProjectEXItems.REGISTRY.register(modEventBus);
		ProjectEXBlockEntities.REGISTRY.register(modEventBus);
		ProjectEXMenuTypes.REGISTRY.register(modEventBus);
		CREATIVE_TABS.register(modEventBus);

		// Register capability event handler
		modEventBus.addListener(this::registerCapabilities);
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		// Register EMC Storage capability for all Link block entities
		event.registerBlockEntity(
			PECapabilities.EMC_STORAGE_CAPABILITY,
			ProjectEXBlockEntities.PERSONAL_LINK.get(),
			(blockEntity, side) -> blockEntity
		);

		event.registerBlockEntity(
			PECapabilities.EMC_STORAGE_CAPABILITY,
			ProjectEXBlockEntities.ENERGY_LINK.get(),
			(blockEntity, side) -> blockEntity
		);

		// Only register Refined Link capabilities if storage mod is loaded
		if (ProjectEXBlockEntities.REFINED_LINK != null) {
			event.registerBlockEntity(
				PECapabilities.EMC_STORAGE_CAPABILITY,
				ProjectEXBlockEntities.REFINED_LINK.get(),
				(blockEntity, side) -> blockEntity
			);
		}

		if (ProjectEXBlockEntities.COMPRESSED_REFINED_LINK != null) {
			event.registerBlockEntity(
				PECapabilities.EMC_STORAGE_CAPABILITY,
				ProjectEXBlockEntities.COMPRESSED_REFINED_LINK.get(),
				(blockEntity, side) -> blockEntity
			);
		}

		// Register EMC Storage capability for Collectors and Relays
		// Note: Power Flowers don't need EMC_STORAGE_CAPABILITY because they output directly to player
		event.registerBlockEntity(
			PECapabilities.EMC_STORAGE_CAPABILITY,
			ProjectEXBlockEntities.COLLECTOR.get(),
			(blockEntity, side) -> blockEntity
		);

		event.registerBlockEntity(
			PECapabilities.EMC_STORAGE_CAPABILITY,
			ProjectEXBlockEntities.RELAY.get(),
			(blockEntity, side) -> blockEntity
		);

		// Register Forge Energy capability for Energy Links (all tiers)
		// Allows Energy Links to provide FE to adjacent energy consumers
		event.registerBlockEntity(
			Capabilities.EnergyStorage.BLOCK,
			ProjectEXBlockEntities.ENERGY_LINK.get(),
			(blockEntity, side) -> blockEntity.getEnergyStorage(side)
		);

		// Register EMC Storage capability for Energy Links (all tiers)
		// Allows Energy Links to store and transfer EMC
		event.registerBlockEntity(
			PECapabilities.EMC_STORAGE_CAPABILITY,
			ProjectEXBlockEntities.ENERGY_LINK.get(),
			(blockEntity, side) -> blockEntity
		);
	}
}
