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

import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Packet for transferring JEI recipe to Alchemy Table crafting grid
 * Sent from client when player clicks on a recipe in JEI
 */
public record RecipeTransferPacket(ResourceLocation recipeId, boolean maxTransfer) implements CustomPacketPayload {
	public static final Type<RecipeTransferPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("projectex_reforged", "recipe_transfer"));

	public static final StreamCodec<ByteBuf, RecipeTransferPacket> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC,
			RecipeTransferPacket::recipeId,
			ByteBufCodecs.BOOL,
			RecipeTransferPacket::maxTransfer,
			RecipeTransferPacket::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	/**
	 * Handle packet on server side
	 */
	public static void handle(RecipeTransferPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer serverPlayer)) {
				return;
			}

			// Get player's open container
			AbstractContainerMenu container = serverPlayer.containerMenu;
			if (!(container instanceof AlchemyTableMenu alchemyTable)) {
				return;
			}

			// Get recipe from recipe manager
			RecipeHolder<?> recipeHolder = serverPlayer.level().getRecipeManager()
					.byKey(packet.recipeId())
					.orElse(null);

			if (recipeHolder == null || !(recipeHolder.value() instanceof CraftingRecipe recipe)) {
				return;
			}

			// Get player's knowledge capability
			IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge == null) {
				return;
			}

			// Clear crafting grid (slots 0-8)
			for (int i = 0; i < 9; i++) {
				ItemStack stack = alchemyTable.getSlot(i).getItem();
				if (!stack.isEmpty()) {
					// Move item back to player inventory
					if (!serverPlayer.getInventory().add(stack)) {
						// If inventory full, drop item
						serverPlayer.drop(stack, false);
					}
					alchemyTable.getSlot(i).set(ItemStack.EMPTY);
				}
			}

			// Get recipe ingredients
			List<Ingredient> ingredients = recipe.getIngredients();
			Inventory playerInventory = serverPlayer.getInventory();

			// For each ingredient slot (up to 9 for 3x3 grid)
			for (int i = 0; i < Math.min(ingredients.size(), 9); i++) {
				Ingredient ingredient = ingredients.get(i);
				if (ingredient.isEmpty()) {
					continue;
				}

				// Find matching item in player inventory
				ItemStack matchingStack = ItemStack.EMPTY;
				int inventorySlot = -1;

				for (int slot = 0; slot < playerInventory.getContainerSize(); slot++) {
					ItemStack invStack = playerInventory.getItem(slot);
					if (ingredient.test(invStack)) {
						matchingStack = invStack;
						inventorySlot = slot;
						break;
					}
				}

				if (!matchingStack.isEmpty() && inventorySlot >= 0) {
					// Found item in inventory - move 1 to crafting grid
					ItemStack singleItem = matchingStack.split(1);
					alchemyTable.getSlot(i).set(singleItem);
					playerInventory.setChanged();
				} else {
					// Item not found in inventory - check if can be transmuted from EMC
					// Get first matching item from ingredient
					ItemStack[] possibleItems = ingredient.getItems();
					if (possibleItems.length > 0) {
						ItemStack testStack = possibleItems[0];
						long emcValue = IEMCProxy.INSTANCE.getValue(testStack);

						if (emcValue > 0 && knowledge.hasKnowledge(testStack)) {
							// Player has knowledge and EMC - don't place item
							// EMC auto-fill will handle it during crafting
							// Leave slot empty (visual indication that it needs EMC)
						} else {
							// Can't craft this recipe - missing item and no EMC/knowledge
							// For now, just leave slot empty
							// TODO: Could return error to show user via IRecipeTransferError
						}
					}
				}
			}

			// Mark container as changed
			alchemyTable.broadcastChanges();
		});
	}
}
