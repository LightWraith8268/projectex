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

package dev.latvian.mods.projectex.client.screen;

import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side GUI for Alchemy Table
 *
 * Combines ProjectE transmutation interface with vanilla crafting grid
 * Layout:
 * - Left: Transmutation grid (inherited from TransmutationContainer)
 * - Right: 3x3 crafting grid + result slot
 * - Bottom-left: Klein Star charging slot
 */
public class AlchemyTableScreen extends AbstractContainerScreen<AlchemyTableMenu> {
	// GUI texture - 256x256 PNG
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
			ProjectEX.MOD_ID,
			"textures/gui/alchemy_table.png"
	);

	public AlchemyTableScreen(AlchemyTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		// GUI dimensions (width x height in pixels)
		// Wider than standard to accommodate both transmutation and crafting grids
		this.imageWidth = 276;  // Standard chest = 176, we need extra space for crafting
		this.imageHeight = 196; // Standard crafting table = 166, transmutation = 196

		// Title position
		this.titleLabelX = 6;
		this.titleLabelY = 8;

		// Inventory label position (player inventory text)
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		// Render the background texture
		int x = this.leftPos;
		int y = this.topPos;

		// Draw main GUI background
		graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		// Render the background (darkened screen behind GUI)
		super.render(graphics, mouseX, mouseY, partialTick);

		// Render tooltips for items under mouse cursor
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		// Render the title text ("Alchemy Table")
		graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);

		// Render inventory label ("Inventory")
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
	}
}
