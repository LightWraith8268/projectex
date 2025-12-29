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

package dev.latvian.mods.projectex.container;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.math.BigInteger;
import java.util.*;

/**
 * Alchemy Table Menu - Enhanced Transmutation Container with Crafting Grid
 *
 * Extends ProjectE's TransmutationContainer to add:
 * - 3x3 Crafting grid (slots 0-8)
 * - Crafting output slot (slot 9)
 * - Klein Star charging slot (slot 10)
 *
 * Implementation follows Phase 1 of ALCHEMY_TABLE_SPEC.md
 */
public class AlchemyTableMenu extends TransmutationContainer {

	// Crafting grid container (3x3 = 9 slots)
	private final TransientCraftingContainer craftingGrid = new TransientCraftingContainer(this, 3, 3);

	// Crafting result container (1 slot)
	private final ResultContainer craftResult = new ResultContainer();

	// Klein Star charging container (1 slot)
	private final SimpleContainer kleinStarSlot = new SimpleContainer(1);

	// Player reference for recipe lookup
	private final Player player;

	/**
	 * Network constructor - called on client when opening GUI from packet
	 */
	public static AlchemyTableMenu fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf data) {
		// Read hand and selected slot from packet
		InteractionHand hand = data.readEnum(InteractionHand.class);
		int selectedSlot = data.readInt();
		return new AlchemyTableMenu(windowId, playerInv, hand, selectedSlot);
	}

	/**
	 * Constructor for portable Arcane Tablet (held in hand)
	 */
	public AlchemyTableMenu(int windowId, Inventory playerInv, InteractionHand hand) {
		this(windowId, playerInv, hand, 0);
	}

	/**
	 * Constructor for both portable Arcane Tablet and block-based Alchemy Table
	 * @param selectedSlot The slot index of the hand item (-1 for block-based)
	 */
	public AlchemyTableMenu(int windowId, Inventory playerInv, InteractionHand hand, int selectedSlot) {
		// Call parent TransmutationContainer constructor with hand and selected slot
		// This initializes the transmutation inventory and slots
		super(windowId, playerInv, hand, selectedSlot);

		this.player = playerInv.player;

		// Add our custom slots for crafting and charging
		initCustomSlots();
	}

	/**
	 * Initialize custom slots for crafting grid, result, and Klein Star charging
	 */
	private void initCustomSlots() {
		// Add 3x3 crafting grid (9 slots)
		// Position on right side of GUI, offset from transmutation grid
		int craftingStartX = 98;  // X position for crafting grid start
		int craftingStartY = 18;  // Y position for crafting grid start

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				int slotIndex = col + row * 3;
				addSlot(new Slot(craftingGrid, slotIndex,
						craftingStartX + col * 18,
						craftingStartY + row * 18));
			}
		}

		// Add crafting result slot (1 slot)
		// Position to the right of the crafting grid
		addSlot(new Slot(craftResult, 0, craftingStartX + 94, craftingStartY + 18) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false; // Result slot - can't place items directly
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				// Called when player takes crafted item (normal click)
				onCraftedItem(stack);
				super.onTake(player, stack);
			}

			@Override
			public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
				// Called during shift-click operations
				int craftedCount = oldStack.getCount() - newStack.getCount();
				// Track how many items were actually crafted for potential EMC refund
				super.onQuickCraft(oldStack, newStack);
			}
		});

		// Add Klein Star charging slot (1 slot)
		// Position at bottom-left, similar to ProjectE transmutation table
		addSlot(new Slot(kleinStarSlot, 0, 12, 74) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				// Only accept Klein Stars (and our custom Magnum/Colossal/Final stars)
				return SlotPredicates.RELAY_INV.test(stack);
			}
		});
	}

	/**
	 * Called when an item is crafted - handles recipe consumption with smart ingredient priority
	 * Phase 2: Ingredient consumption (inventory first, then EMC)
	 * Phase 3: Auto-learn system (items with EMC are automatically learned)
	 */
	private void onCraftedItem(ItemStack craftedStack) {
		if (player.level().isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
			return; // Only process on server
		}

		// Get the current recipe
		CraftingInput craftingInput = craftingGrid.asCraftInput();
		Optional<RecipeHolder<CraftingRecipe>> optionalRecipe = player.level()
				.getRecipeManager()
				.getRecipeFor(RecipeType.CRAFTING, craftingInput, player.level());

		if (optionalRecipe.isEmpty()) {
			return; // No valid recipe
		}

		CraftingRecipe recipe = optionalRecipe.get().value();

		// Consume ingredients with priority: inventory first, then EMC for shortfall
		boolean success = consumeIngredients(recipe, false);

		if (success) {
			// Phase 3: Auto-learn the crafted item if it has EMC value
			autoLearnItem(craftedStack, serverPlayer);

			// Clear the crafting grid after successful craft
			craftingGrid.clearContent();
			updateCraftingResult();
		}
	}

	/**
	 * Auto-learn system - automatically learns crafted items with EMC values
	 * Phase 3: Auto-learn implementation
	 *
	 * @param craftedStack The item that was crafted
	 * @param serverPlayer The player who crafted the item
	 */
	private void autoLearnItem(ItemStack craftedStack, ServerPlayer serverPlayer) {
		// Check if item has EMC value
		long emcValue = IEMCProxy.INSTANCE.getValue(craftedStack);
		if (emcValue <= 0) {
			// Item has no EMC value - don't learn
			return;
		}

		// Get player's knowledge provider
		IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) {
			return;
		}

		// Check if player already knows this item
		if (knowledge.hasKnowledge(craftedStack)) {
			// Already learned - no feedback needed
			return;
		}

		// Learn the item
		boolean learned = knowledge.addKnowledge(craftedStack);

		if (learned) {
			// Visual and audio feedback for learning
			// Play level-up sound (higher pitch for learning)
			serverPlayer.level().playSound(
					null,
					serverPlayer.blockPosition(),
					SoundEvents.PLAYER_LEVELUP,
					SoundSource.PLAYERS,
					0.5F,  // Volume
					2.0F   // Pitch (higher = more "sparkly")
			);

			// Display message in action bar (above hotbar)
			Component learnMessage = Component.literal("§6✦ Learned: §f")
					.append(craftedStack.getHoverName())
					.append(Component.literal(" §7(§e" + emcValue + " EMC§7)"));

			serverPlayer.displayClientMessage(learnMessage, true);

			// Sync knowledge to client
			knowledge.sync(serverPlayer);
		}
	}

	/**
	 * Consume ingredients for crafting with smart priority system
	 * Priority: 1. Real items from inventory, 2. EMC transmutation for missing items
	 *
	 * @param recipe The recipe being crafted
	 * @param simulate If true, check if crafting is possible without consuming
	 * @return true if ingredients were successfully consumed (or would be in simulate mode)
	 */
	private boolean consumeIngredients(CraftingRecipe recipe, boolean simulate) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return false;
		}

		// Get player's knowledge provider for EMC access
		IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) {
			return false;
		}

		// Build ingredient requirement map from recipe
		Map<ItemStack, Integer> ingredientNeeds = new HashMap<>();
		for (Ingredient ingredient : recipe.getIngredients()) {
			if (ingredient.isEmpty()) {
				continue;
			}

			// Get first matching item from ingredient (for EMC lookup)
			ItemStack[] matchingStacks = ingredient.getItems();
			if (matchingStacks.length == 0) {
				continue;
			}

			ItemStack representativeStack = matchingStacks[0].copy();
			ingredientNeeds.merge(representativeStack, 1, Integer::sum);
		}

		// Track EMC cost and items to consume
		long totalEmcCost = 0;
		List<ItemStack> itemsToConsume = new ArrayList<>();

		// For each ingredient, check inventory first, then calculate EMC shortfall
		for (Map.Entry<ItemStack, Integer> entry : ingredientNeeds.entrySet()) {
			ItemStack ingredientStack = entry.getKey();
			int needed = entry.getValue();

			// Count how many of this ingredient are in player inventory
			int foundInInventory = 0;
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack invStack = player.getInventory().getItem(i);
				if (ItemStack.isSameItemSameComponents(invStack, ingredientStack)) {
					foundInInventory += invStack.getCount();
				}
			}

			// Calculate shortfall (how many need to be transmuted)
			int shortfall = needed - foundInInventory;

			if (shortfall > 0) {
				// Need to transmute some items - calculate EMC cost
				long emcPerItem = IEMCProxy.INSTANCE.getValue(ingredientStack);

				if (emcPerItem <= 0) {
					// Item has no EMC value - can't transmute
					return false;
				}

				// Check if player has learned this item
				if (!knowledge.hasKnowledge(ingredientStack)) {
					// Player hasn't learned this item - can't transmute
					return false;
				}

				totalEmcCost += emcPerItem * shortfall;
			}

			// Track items to consume from inventory (up to the amount needed)
			int toConsumeFromInventory = Math.min(needed, foundInInventory);
			if (toConsumeFromInventory > 0) {
				ItemStack consumeStack = ingredientStack.copy();
				consumeStack.setCount(toConsumeFromInventory);
				itemsToConsume.add(consumeStack);
			}
		}

		// Check if player has enough EMC for the shortfall
		BigInteger playerEmc = knowledge.getEmc();
		if (playerEmc.compareTo(BigInteger.valueOf(totalEmcCost)) < 0) {
			// Not enough EMC
			return false;
		}

		// If simulating, we're done (everything checks out)
		if (simulate) {
			return true;
		}

		// Execute consumption: inventory first, then EMC
		// 1. Consume from inventory
		for (ItemStack consumeStack : itemsToConsume) {
			int remainingToConsume = consumeStack.getCount();

			for (int i = 0; i < player.getInventory().getContainerSize() && remainingToConsume > 0; i++) {
				ItemStack invStack = player.getInventory().getItem(i);

				if (ItemStack.isSameItemSameComponents(invStack, consumeStack)) {
					int toTake = Math.min(remainingToConsume, invStack.getCount());
					invStack.shrink(toTake);
					remainingToConsume -= toTake;

					if (invStack.isEmpty()) {
						player.getInventory().setItem(i, ItemStack.EMPTY);
					}
				}
			}
		}

		// 2. Consume EMC for shortfall
		if (totalEmcCost > 0) {
			BigInteger newEmc = playerEmc.subtract(BigInteger.valueOf(totalEmcCost));
			knowledge.setEmc(newEmc);
			knowledge.syncEmc(serverPlayer);
		}

		return true;
	}

	/**
	 * Klein Star Auto-Charging (Phase 4)
	 * Called every tick to charge Klein Star from player's EMC pool
	 * Default charge rate: 1000 EMC/tick (configurable later)
	 */
	public void chargeKleinStar() {
		if (player.level().isClientSide) {
			return; // Only process on server
		}

		// Only charge for server players
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		// Check if Klein Star slot has an item
		ItemStack kleinStarStack = kleinStarSlot.getItem(0);
		if (kleinStarStack.isEmpty()) {
			return;
		}

		// Get Klein Star's EMC holder capability
		IItemEmcHolder kleinStarHolder = kleinStarStack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (kleinStarHolder == null) {
			return; // Not a Klein Star
		}

		// Get player's knowledge provider for EMC access
		IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) {
			return; // Player has no EMC capability
		}

		// Calculate how much EMC to transfer
		BigInteger playerEmc = knowledge.getEmc();
		long neededEmc = kleinStarHolder.getNeededEmc(kleinStarStack);

		if (neededEmc <= 0 || playerEmc.compareTo(BigInteger.ZERO) <= 0) {
			return; // Klein Star is full or player has no EMC
		}

		// Charge rate: 1000 EMC/tick (configurable later)
		long chargeRate = 1000L;
		long emcToTransfer = Math.min(chargeRate, Math.min(playerEmc.longValue(), neededEmc));

		// Try to insert EMC into Klein Star (simulate first)
		long actualInserted = kleinStarHolder.insertEmc(kleinStarStack, emcToTransfer, IEmcStorage.EmcAction.SIMULATE);

		if (actualInserted > 0) {
			// Extract from player
			BigInteger newPlayerEmc = playerEmc.subtract(BigInteger.valueOf(actualInserted));
			knowledge.setEmc(newPlayerEmc);

			// Insert into Klein Star (execute)
			kleinStarHolder.insertEmc(kleinStarStack, actualInserted, IEmcStorage.EmcAction.EXECUTE);

			// Mark container as changed to sync to client
			broadcastChanges();
		}
	}

	/**
	 * Update the crafting result slot based on current crafting grid contents
	 */
	private void updateCraftingResult() {
		if (player.level().isClientSide) {
			return; // Only process on server
		}

		// Create CraftingInput from current crafting grid contents
		CraftingInput craftingInput = craftingGrid.asCraftInput();

		// Look up recipe based on crafting grid contents
		Optional<RecipeHolder<CraftingRecipe>> optionalRecipe = player.level()
				.getRecipeManager()
				.getRecipeFor(RecipeType.CRAFTING, craftingInput, player.level());

		if (optionalRecipe.isPresent()) {
			RecipeHolder<CraftingRecipe> recipeHolder = optionalRecipe.get();
			CraftingRecipe recipe = recipeHolder.value();

			// Check if player can craft this recipe
			ItemStack result = recipe.assemble(craftingInput, player.level().registryAccess());
			craftResult.setItem(0, result);
		} else {
			// No valid recipe - clear result slot
			craftResult.setItem(0, ItemStack.EMPTY);
		}

		// Notify client of change
		broadcastChanges();
	}

	/**
	 * Called when container contents change - update crafting result
	 */
	@Override
	public void slotsChanged(Container container) {
		super.slotsChanged(container);

		if (container == craftingGrid) {
			updateCraftingResult();
		}
	}

	/**
	 * Called every tick on the server - used for Klein Star auto-charging
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		// Charge Klein Star from player's EMC pool (1000 EMC/tick)
		chargeKleinStar();
	}

	/**
	 * Called when player closes the GUI
	 */
	@Override
	public void removed(Player player) {
		super.removed(player);

		// Return crafting grid items to player inventory
		if (!player.level().isClientSide) {
			for (int i = 0; i < craftingGrid.getContainerSize(); i++) {
				ItemStack stack = craftingGrid.removeItemNoUpdate(i);
				if (!stack.isEmpty()) {
					player.drop(stack, false);
				}
			}

			// Return Klein Star to player inventory
			ItemStack kleinStar = kleinStarSlot.removeItemNoUpdate(0);
			if (!kleinStar.isEmpty()) {
				player.drop(kleinStar, false);
			}
		}
	}

	/**
	 * Shift-click handling - implements bulk crafting with EMC budget calculation
	 * Phase 2: Smart shift-click behavior
	 */
	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		Slot slot = slots.get(slotIndex);
		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack slotStack = slot.getItem();
		ItemStack originalStack = slotStack.copy();

		// Check if this is the crafting result slot (shift-clicking to bulk craft)
		if (slot == slots.get(slots.size() - 2)) { // Result slot is second-to-last (Klein Star is last)
			return handleBulkCrafting(slotStack);
		}

		// Default shift-click behavior for other slots
		return super.quickMoveStack(player, slotIndex);
	}

	/**
	 * Handle shift-click on crafting result slot (bulk crafting)
	 * Crafts as many items as possible based on:
	 * - Available EMC
	 * - Output item stack limit (up to 64 or item max)
	 * - Recipe ingredient availability
	 */
	private ItemStack handleBulkCrafting(ItemStack craftedStack) {
		if (player.level().isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
			return ItemStack.EMPTY;
		}

		// Get the current recipe
		CraftingInput craftingInput = craftingGrid.asCraftInput();
		Optional<RecipeHolder<CraftingRecipe>> optionalRecipe = player.level()
				.getRecipeManager()
				.getRecipeFor(RecipeType.CRAFTING, craftingInput, player.level());

		if (optionalRecipe.isEmpty()) {
			return ItemStack.EMPTY;
		}

		CraftingRecipe recipe = optionalRecipe.get().value();

		// Get player's knowledge provider for EMC access
		IKnowledgeProvider knowledge = serverPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) {
			return ItemStack.EMPTY;
		}

		// Calculate maximum number of crafts possible
		int maxCrafts = calculateMaxCrafts(recipe, knowledge);

		if (maxCrafts <= 0) {
			return ItemStack.EMPTY;
		}

		// Limit to stack size (64 or item max)
		int stackLimit = craftedStack.getMaxStackSize();
		maxCrafts = Math.min(maxCrafts, stackLimit / craftedStack.getCount());

		// Perform bulk crafting
		int totalCrafted = 0;
		boolean hasLearnedThisSession = false; // Track if we've learned this item in this bulk craft

		for (int i = 0; i < maxCrafts; i++) {
			// Try to craft one item
			if (!consumeIngredients(recipe, true)) {
				break; // Can't craft anymore (out of EMC or ingredients)
			}

			// Actually consume ingredients
			if (!consumeIngredients(recipe, false)) {
				break; // Something went wrong
			}

			// Add crafted item to player inventory
			ItemStack result = craftedStack.copy();
			if (!player.getInventory().add(result)) {
				// Inventory full - stop crafting
				break;
			}

			// Auto-learn on first craft only (prevent spam)
			if (!hasLearnedThisSession) {
				autoLearnItem(craftedStack, serverPlayer);
				hasLearnedThisSession = true;
			}

			totalCrafted++;
		}

		if (totalCrafted > 0) {
			// Clear crafting grid and update result
			craftingGrid.clearContent();
			updateCraftingResult();

			// Return a stack representing what was crafted (for sound/animation purposes)
			ItemStack craftedResult = craftedStack.copy();
			craftedResult.setCount(totalCrafted * craftedStack.getCount());
			return craftedResult;
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Calculate maximum number of times a recipe can be crafted
	 * Based on: available inventory items, player EMC, and stack limits
	 */
	private int calculateMaxCrafts(CraftingRecipe recipe, IKnowledgeProvider knowledge) {
		int maxCrafts = Integer.MAX_VALUE;

		// Build ingredient requirement map from recipe
		Map<ItemStack, Integer> ingredientNeeds = new HashMap<>();
		for (Ingredient ingredient : recipe.getIngredients()) {
			if (ingredient.isEmpty()) {
				continue;
			}

			ItemStack[] matchingStacks = ingredient.getItems();
			if (matchingStacks.length == 0) {
				continue;
			}

			ItemStack representativeStack = matchingStacks[0].copy();
			ingredientNeeds.merge(representativeStack, 1, Integer::sum);
		}

		// Calculate max crafts based on inventory items and EMC budget
		BigInteger playerEmc = knowledge.getEmc();
		long totalEmcCostPerCraft = 0;

		for (Map.Entry<ItemStack, Integer> entry : ingredientNeeds.entrySet()) {
			ItemStack ingredientStack = entry.getKey();
			int neededPerCraft = entry.getValue();

			// Count how many of this ingredient are in player inventory
			int foundInInventory = 0;
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack invStack = player.getInventory().getItem(i);
				if (ItemStack.isSameItemSameComponents(invStack, ingredientStack)) {
					foundInInventory += invStack.getCount();
				}
			}

			// Calculate how many crafts are possible with inventory items
			int craftsFromInventory = foundInInventory / neededPerCraft;

			// If we need more than inventory provides, calculate EMC cost
			if (craftsFromInventory < maxCrafts) {
				long emcPerItem = IEMCProxy.INSTANCE.getValue(ingredientStack);

				if (emcPerItem <= 0 || !knowledge.hasKnowledge(ingredientStack)) {
					// Can't transmute this item - limit to inventory
					maxCrafts = Math.min(maxCrafts, craftsFromInventory);
					continue;
				}

				// Calculate EMC cost per craft for items not in inventory
				int itemsNeededFromEmc = Math.max(0, neededPerCraft - (foundInInventory % neededPerCraft));
				totalEmcCostPerCraft += emcPerItem * itemsNeededFromEmc;
			}

			maxCrafts = Math.min(maxCrafts, craftsFromInventory);
		}

		// Calculate max crafts based on EMC budget
		if (totalEmcCostPerCraft > 0) {
			long emcBudgetCrafts = playerEmc.divide(BigInteger.valueOf(totalEmcCostPerCraft)).longValue();
			maxCrafts = Math.min(maxCrafts, (int) emcBudgetCrafts);
		}

		return maxCrafts;
	}

	/**
	 * Check if this menu is still valid (player hasn't moved too far away)
	 */
	@Override
	public boolean stillValid(Player player) {
		// For portable Arcane Tablet, always valid if player still holds the item
		// For block-based Alchemy Table, check distance to block
		// For now, always valid (portable behavior)
		return true;
	}
}
