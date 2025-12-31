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

import dev.latvian.mods.projectex.container.AlchemyTableMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Recipe transfer handler for Alchemy Table
 * Phase 6: JEI Integration
 *
 * Handles:
 * - Click: Populate grid with available items (real items + ghost items for transmutable)
 * - Shift-click: Bulk craft with EMC budget validation
 */
public class AlchemyTableRecipeTransferHandler implements IRecipeTransferHandler<AlchemyTableMenu, RecipeHolder<CraftingRecipe>> {
	private final IRecipeTransferHandlerHelper transferHelper;

	public AlchemyTableRecipeTransferHandler(IRecipeTransferHandlerHelper transferHelper) {
		this.transferHelper = transferHelper;
	}

	@Override
	public Class<? extends AlchemyTableMenu> getContainerClass() {
		return AlchemyTableMenu.class;
	}

	@Override
	public Optional<MenuType<AlchemyTableMenu>> getMenuType() {
		// Return empty - we handle this via container class
		return Optional.empty();
	}

	@Override
	public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
		return RecipeTypes.CRAFTING;
	}

	@Override
	public @Nullable IRecipeTransferError transferRecipe(
			AlchemyTableMenu container,
			RecipeHolder<CraftingRecipe> recipeHolder,
			IRecipeSlotsView recipeSlots,
			Player player,
			boolean maxTransfer,
			boolean doTransfer
	) {
		CraftingRecipe recipe = recipeHolder.value();
		// Get ingredient slots from recipe view
		List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

		if (inputSlots.isEmpty()) {
			return transferHelper.createUserErrorWithTooltip(Component.literal("No ingredients in recipe"));
		}

		// Validate we have enough crafting slots (3x3 = 9)
		if (inputSlots.size() > 9) {
			return transferHelper.createUserErrorWithTooltip(Component.literal("Recipe too large for crafting grid"));
		}

		if (!doTransfer) {
			// Just validation - check if we can do the transfer
			return null; // Validation passed
		}

		// TODO: Implement actual recipe transfer logic
		//
		// IMPLEMENTATION REQUIREMENTS:
		// ============================
		//
		// 1. PACKET SYSTEM (REQUIRED):
		//    - JEI runs on client, but item movement must happen on server
		//    - Need to create a custom packet (e.g., RecipeTransferPacket) that:
		//      a. Sends recipe ID + maxTransfer flag from client to server
		//      b. Server receives packet and performs the actual transfer
		//      c. Uses NeoForge's SimpleChannel packet registration
		//    - Reference: ProjectE's transmutation packets or JEI's documentation
		//
		// 2. SERVER-SIDE TRANSFER LOGIC:
		//    When packet received on server, execute:
		//
		//    a. Clear crafting grid (slots 0-8 in AlchemyTableMenu)
		//       - Move items back to player inventory
		//       - Use container.setItem(slotIndex, ItemStack.EMPTY)
		//
		//    b. For each recipe ingredient slot (up to 9 slots):
		//       - Get ingredient from recipeSlots.getSlotViews(RecipeIngredientRole.INPUT)
		//       - Find matching item in player inventory
		//       - If found: move 1 item to corresponding crafting grid slot
		//       - If NOT found: check if player has EMC and knowledge
		//         → If yes: DON'T place item (EMC auto-fill handles it during craft)
		//         → If no: return error via IRecipeTransferError
		//
		//    c. For maxTransfer=true (shift-click):
		//       - Calculate how many times recipe can be crafted (inventory + EMC)
		//       - Use AlchemyTableMenu.calculateMaxCrafts() logic
		//       - Display info tooltip showing craft count estimate
		//       - NOTE: Actual bulk crafting happens when player clicks result slot
		//
		// 3. GHOST ITEM SYSTEM (OPTIONAL - ADVANCED):
		//    - JEI supports "ghost" items (visual-only, semi-transparent)
		//    - Would show items that can be transmuted from EMC
		//    - Requires custom IGuiItemStackGroup rendering
		//    - NOT essential for basic functionality - skip for initial implementation
		//
		// 4. ERROR HANDLING:
		//    Return appropriate IRecipeTransferError for:
		//    - Missing ingredients (no inventory + no EMC)
		//    - Unknown items (no EMC knowledge)
		//    - Recipe too complex (>9 ingredients)
		//    - Use transferHelper.createUserErrorWithTooltip() for user-friendly messages
		//
		// 5. TESTING REQUIREMENTS:
		//    - Test with recipes that have all items in inventory
		//    - Test with recipes requiring EMC transmutation
		//    - Test shift-click bulk transfer
		//    - Test with missing ingredients (should show error)
		//    - Test with unknown items (should show "Learn item first" error)
		//
		// RECOMMENDED IMPLEMENTATION ORDER:
		// 1. Create RecipeTransferPacket class
		// 2. Register packet in ProjectEXNet (or create new network handler)
		// 3. Implement basic transfer (inventory items only, no EMC)
		// 4. Add EMC validation (check knowledge and EMC availability)
		// 5. Add maxTransfer support (bulk craft estimation)
		// 6. Polish error messages and tooltips
		//
		// REFERENCES:
		// - AlchemyTableMenu.java: slots 0-8 are crafting grid
		// - AlchemyTableMenu.consumeIngredients(): EMC auto-fill logic
		// - JEI API docs: https://github.com/mezz/JustEnoughItems/wiki/Recipe-Transfer
		// - NeoForge networking: SimpleChannel packet system

		return null; // Placeholder - remove when implementing
	}
}
