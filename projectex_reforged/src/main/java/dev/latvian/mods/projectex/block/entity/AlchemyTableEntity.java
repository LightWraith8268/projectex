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

package dev.latvian.mods.projectex.block.entity;

import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableEntity extends BlockEntity implements MenuProvider {
	public AlchemyTableEntity(BlockPos pos, BlockState state) {
		super(ProjectEXBlockEntities.ALCHEMY_TABLE.get(), pos, state);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.projectex_reforged.alchemy_table");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		// Create the Alchemy Table menu for the block-based variant
		// Use MAIN_HAND variant with 0 param to avoid constructor mismatch
		return new AlchemyTableMenu(containerId, playerInventory, net.minecraft.world.InteractionHand.MAIN_HAND);
	}
}
