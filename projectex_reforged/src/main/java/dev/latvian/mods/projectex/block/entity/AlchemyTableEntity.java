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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.IMenuProviderExtension;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableEntity extends BlockEntity implements MenuProvider, IMenuProviderExtension {
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
		// -1 for selected slot means no hand slot selected (block-based, not item-based)
		return new AlchemyTableMenu(containerId, playerInventory, InteractionHand.MAIN_HAND, -1);
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		// Send hand and selected slot to client for menu construction
		// Block-based table uses MAIN_HAND with no selected slot (-1)
		buffer.writeEnum(InteractionHand.MAIN_HAND);
		buffer.writeInt(-1);  // -1 indicates no hand slot selected (block-based)
	}
}
