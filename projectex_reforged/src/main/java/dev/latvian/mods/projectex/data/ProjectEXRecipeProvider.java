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

package dev.latvian.mods.projectex.data;

import dev.latvian.mods.projectex.Matter;
import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.Star;
import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import dev.latvian.mods.projectex.item.ProjectEXItems;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ProjectEXRecipeProvider extends RecipeProvider {
	public ProjectEXRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		// ===== MATTER ITEM RECIPES =====
		// Horizontal and vertical patterns using Aeternalis Fuel
		Ingredient afuel = Ingredient.of(PEItems.AETERNALIS_FUEL);

		for (Matter matter : Matter.VALUES) {
			if (matter.hasMatterItem && matter.getPrev() != null) {
				Item prevMatterItem = matter.getPrev().getItem().get();

				// Horizontal pattern: FFF/MMM/FFF
				ShapedRecipeBuilder.shaped(RecipeCategory.MISC, matter.getItem().get())
					.pattern("FFF")
					.pattern("MMM")
					.pattern("FFF")
					.define('F', afuel)
					.define('M', prevMatterItem)
					.group("projectex:matter/" + matter.name)
					.unlockedBy("has_item", has(prevMatterItem))
					.save(output, ProjectEX.MOD_ID + ":matter_h/" + matter.name);

				// Vertical pattern: FMF/FMF/FMF
				ShapedRecipeBuilder.shaped(RecipeCategory.MISC, matter.getItem().get())
					.pattern("FMF")
					.pattern("FMF")
					.pattern("FMF")
					.define('F', afuel)
					.define('M', prevMatterItem)
					.group("projectex:matter/" + matter.name)
					.unlockedBy("has_item", has(prevMatterItem))
					.save(output, ProjectEX.MOD_ID + ":matter_v/" + matter.name);
			}
		}

		// ===== MATTER BLOCK RECIPES =====
		// 4 matter items -> 1 matter block (2x2 crafting)
		// 1 matter block -> 4 matter items (unpacking)
		// Skip DARK and RED - ProjectE already has those blocks
		for (Matter matter : Matter.VALUES) {
			if (matter != Matter.DARK && matter != Matter.RED) {
				Item matterItem = matter.getItem().get();

				// Only create recipes if we actually registered a matter block for this tier
				if (ProjectEXBlocks.MATTER_BLOCK.containsKey(matter)) {
					Item matterBlock = ProjectEXBlocks.MATTER_BLOCK.get(matter).get().asItem();

					// Crafting: 4 matter items -> 1 matter block (2x2)
					ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, matterBlock)
						.pattern("MM")
						.pattern("MM")
						.define('M', matterItem)
						.group("projectex:matter_block")
						.unlockedBy("has_matter_item", has(matterItem))
						.save(output, ProjectEX.MOD_ID + ":matter_block/" + matter.name);

					// Unpacking: 1 matter block -> 4 matter items
					ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, matterItem, 4)
						.requires(matterBlock)
						.group("projectex:matter_item_from_block")
						.unlockedBy("has_matter_block", has(matterBlock))
						.save(output, ProjectEX.MOD_ID + ":matter_item_from_block/" + matter.name);
				}
			}
		}

		// ===== BASE RECIPES FOR BASIC TIER =====
		// Basic Collector: 8 ProjectE Collector MK1 + 1 Obsidian (center)
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.COLLECTOR.get(Matter.BASIC).get())
			.pattern("CCC")
			.pattern("COC")
			.pattern("CCC")
			.define('C', PEBlocks.COLLECTOR.asItem())
			.define('O', Items.OBSIDIAN)
			.group("projectex:collector")
			.unlockedBy("has_collector_mk1", has(PEBlocks.COLLECTOR.asItem()))
			.save(output, ProjectEX.MOD_ID + ":collector/basic_base");

		// Basic Relay: 8 ProjectE Relay MK1 + 1 Obsidian (center)
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.RELAY.get(Matter.BASIC).get())
			.pattern("RRR")
			.pattern("ROR")
			.pattern("RRR")
			.define('R', PEBlocks.RELAY.asItem())
			.define('O', Items.OBSIDIAN)
			.group("projectex:relay")
			.unlockedBy("has_relay_mk1", has(PEBlocks.RELAY.asItem()))
			.save(output, ProjectEX.MOD_ID + ":relay/basic_base");

		// ===== COLLECTOR, RELAY, POWER FLOWER, COMPRESSED COLLECTOR RECIPES =====
		for (Matter matter : Matter.VALUES) {
			Item collector = ProjectEXBlocks.COLLECTOR.get(matter).get().asItem();
			Item relay = ProjectEXBlocks.RELAY.get(matter).get().asItem();
			Item powerFlower = ProjectEXBlocks.POWER_FLOWER.get(matter).get().asItem();
			Item compressedCollector = ProjectEXItems.COMPRESSED_COLLECTOR.get(matter).get();

			Matter prev = matter.getPrev();

			// Collector tier upgrade (shapeless: previous collector + matter item)
			if (prev != null) {
				Item matterItem = matter.getItem().get();

				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, collector)
					.requires(ProjectEXBlocks.COLLECTOR.get(prev).get())
					.requires(matterItem)
					.group("projectex:collector")
					.unlockedBy("has_matter", has(matterItem))
					.save(output, ProjectEX.MOD_ID + ":collector/" + matter.name);

				// Relay tier upgrade (shapeless: previous relay + matter item)
				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, relay)
					.requires(ProjectEXBlocks.RELAY.get(prev).get())
					.requires(matterItem)
					.group("projectex:relay")
					.unlockedBy("has_matter", has(matterItem))
					.save(output, ProjectEX.MOD_ID + ":relay/" + matter.name);
			}

			// Compressed Collector (9 collectors in 3x3 grid)
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, compressedCollector)
				.pattern("CCC")
				.pattern("CCC")
				.pattern("CCC")
				.define('C', collector)
				.group("projectex:compressed_collector")
				.unlockedBy("has_collector", has(collector))
				.save(output, ProjectEX.MOD_ID + ":compressed_collector/" + matter.name);

			// Power Flower (2 compressed collectors + energy link + 6 relays: CLC/RRR/RRR)
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, powerFlower)
				.pattern("CLC")
				.pattern("RRR")
				.pattern("RRR")
				.define('C', compressedCollector)
				.define('L', ProjectEXBlocks.ENERGY_LINK.get(matter).get())
				.define('R', relay)
				.group("projectex:power_flower")
				.unlockedBy("has_compressed_collector", has(compressedCollector))
				.save(output, ProjectEX.MOD_ID + ":power_flower/" + matter.name);
		}

		// ===== STAR RECIPES =====
		// Base star recipes: 4x Klein Star Omega -> Magnum Star Ein
		Item startMagnum = PEItems.KLEIN_STAR_OMEGA.get();
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXItems.MAGNUM_STAR.get(Star.EIN).get())
			.requires(startMagnum)
			.requires(startMagnum)
			.requires(startMagnum)
			.requires(startMagnum)
			.group("projectex:magnum_star")
			.unlockedBy("has_klein_star_omega", has(startMagnum))
			.save(output, ProjectEX.MOD_ID + ":magnum_star/ein");

		// 4x Magnum Star Omega -> Colossal Star Ein
		Item startColossal = ProjectEXItems.MAGNUM_STAR.get(Star.OMEGA).get();
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXItems.COLOSSAL_STAR.get(Star.EIN).get())
			.requires(startColossal)
			.requires(startColossal)
			.requires(startColossal)
			.requires(startColossal)
			.group("projectex:colossal_star")
			.unlockedBy("has_magnum_star_omega", has(startColossal))
			.save(output, ProjectEX.MOD_ID + ":colossal_star/ein");

		// Magnum Star tier upgrades (4x previous tier -> 1x next tier)
		Star prevStar = null;
		for (Star star : Star.VALUES) {
			if (prevStar != null) {
				Item prevMagnum = ProjectEXItems.MAGNUM_STAR.get(prevStar).get();

				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXItems.MAGNUM_STAR.get(star).get())
					.requires(prevMagnum)
					.requires(prevMagnum)
					.requires(prevMagnum)
					.requires(prevMagnum)
					.group("projectex:magnum_star")
					.unlockedBy("has_prev_star", has(prevMagnum))
					.save(output, ProjectEX.MOD_ID + ":magnum_star/" + star.name);
			}
			prevStar = star;
		}

		// Colossal Star tier upgrades (4x previous tier -> 1x next tier)
		prevStar = null;
		for (Star star : Star.VALUES) {
			if (prevStar != null) {
				Item prevColossal = ProjectEXItems.COLOSSAL_STAR.get(prevStar).get();

				ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ProjectEXItems.COLOSSAL_STAR.get(star).get())
					.requires(prevColossal)
					.requires(prevColossal)
					.requires(prevColossal)
					.requires(prevColossal)
					.group("projectex:colossal_star")
					.unlockedBy("has_prev_star", has(prevColossal))
					.save(output, ProjectEX.MOD_ID + ":colossal_star/" + star.name);
			}
			prevStar = star;
		}

		// ===== TABLE AND UTILITY RECIPES =====
		// Stone Table: 8 stone bricks + (transmutation table OR philosopher's stone)
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ProjectEXBlocks.STONE_TABLE.get())
			.pattern("SSS")
			.pattern("STS")
			.pattern("SSS")
			.define('S', ItemTags.STONE_BRICKS)
			.define('T', Ingredient.of(
				PEBlocks.TRANSMUTATION_TABLE.asItem(),
				PEItems.PHILOSOPHERS_STONE.get()
			))
			.group("projectex")
			.unlockedBy("has_transmutation_table", has(PEBlocks.TRANSMUTATION_TABLE.asItem()))
			.save(output);

		// Alchemy Table: 123/TST/LDL
		// 1=low covalence, 2=medium covalence, 3=high covalence, T=torch, S=stone table, L=wooden rod, D=diamond
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ProjectEXBlocks.ALCHEMY_TABLE.get())
			.pattern("123")
			.pattern("TST")
			.pattern("LDL")
			.define('1', PEItems.LOW_COVALENCE_DUST.get())
			.define('2', PEItems.MEDIUM_COVALENCE_DUST.get())
			.define('3', PEItems.HIGH_COVALENCE_DUST.get())
			.define('T', Items.TORCH)
			.define('S', ProjectEXBlocks.STONE_TABLE.get())
			.define('L', Tags.Items.RODS_WOODEN)
			.define('D', Tags.Items.GEMS_DIAMOND)
			.group("projectex")
			.unlockedBy("has_stone_table", has(ProjectEXBlocks.STONE_TABLE.get()))
			.save(output);

		// Arcane Tablet: TWT/MSM/TCT
		// T=stone table, W=crafting table, M=magenta matter OR transmutation tablet, S=magnum star ein, C=chest
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ProjectEXItems.ARCANE_TABLET.get())
			.pattern("TWT")
			.pattern("MSM")
			.pattern("TCT")
			.define('T', ProjectEXBlocks.STONE_TABLE.get())
			.define('W', Items.CRAFTING_TABLE)
			.define('M', Ingredient.of(
				Matter.MAGENTA.getItem().get(),
				PEItems.TRANSMUTATION_TABLET.get()
			))
			.define('S', ProjectEXItems.MAGNUM_STAR.get(Star.EIN).get())
			.define('C', Tags.Items.CHESTS)
			.group("projectex")
			.unlockedBy("has_stone_table", has(ProjectEXBlocks.STONE_TABLE.get()))
			.save(output);

		// ===== LINK BLOCK RECIPES =====
		// Energy Link: LMH/SRS/HML
		// L=low covalence, M=medium covalence, H=high covalence, S=stone, R=red matter
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.ENERGY_LINK.get(Matter.BASIC).get())
			.pattern("LMH")
			.pattern("SRS")
			.pattern("HML")
			.define('L', PEItems.LOW_COVALENCE_DUST.get())
			.define('M', PEItems.MEDIUM_COVALENCE_DUST.get())
			.define('H', PEItems.HIGH_COVALENCE_DUST.get())
			.define('S', Tags.Items.STONES)
			.define('R', PEItems.RED_MATTER.get())
			.group("projectex:link")
			.unlockedBy("has_red_matter", has(PEItems.RED_MATTER.get()))
			.save(output);

		// Tiered Energy Link Upgrades (previous tier + 4 Matter Blocks in + pattern)
		for (Matter matter : Matter.VALUES) {
			Matter prev = matter.getPrev();
			if (prev != null) {
				// Pattern: _M_/MEM/_M_ where M = matter block, E = previous energy link
				// Use ProjectE blocks for DARK and RED, ProjectEX blocks for others
				Item matterBlock;
				if (matter == Matter.DARK) {
					matterBlock = PEBlocks.DARK_MATTER.asItem();
				} else if (matter == Matter.RED) {
					matterBlock = PEBlocks.RED_MATTER.asItem();
				} else {
					matterBlock = ProjectEXBlocks.MATTER_BLOCK.get(matter).get().asItem();
				}

				ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.ENERGY_LINK.get(matter).get())
					.pattern(" M ")
					.pattern("MEM")
					.pattern(" M ")
					.define('M', matterBlock)
					.define('E', ProjectEXBlocks.ENERGY_LINK.get(prev).get())
					.group("projectex:energy_link")
					.unlockedBy("has_" + prev.name + "_energy_link", has(ProjectEXBlocks.ENERGY_LINK.get(prev).get()))
					.save(output, ProjectEX.MOD_ID + ":energy_link/" + matter.name);
			}
		}

		// Personal Link: RBR/BCB/RBR
		// B=energy link, R=red matter block, C=condenser mk2
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.PERSONAL_LINK.get())
			.pattern("RBR")
			.pattern("BCB")
			.pattern("RBR")
			.define('B', ProjectEXBlocks.ENERGY_LINK.get(Matter.BASIC).get())
			.define('R', PEBlocks.RED_MATTER.asItem())
			.define('C', PEBlocks.CONDENSER_MK2.asItem())
			.group("projectex:link")
			.unlockedBy("has_energy_link", has(ProjectEXBlocks.ENERGY_LINK.get(Matter.BASIC).get()))
			.save(output);

		// Only generate Refined Link recipes if storage mod is loaded
		if (ProjectEXBlocks.REFINED_LINK != null) {
			// Refined Link: 9 personal links in 3x3
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.REFINED_LINK.get())
				.pattern("LLL")
				.pattern("LLL")
				.pattern("LLL")
				.define('L', ProjectEXBlocks.PERSONAL_LINK.get())
				.group("projectex:link")
				.unlockedBy("has_personal_link", has(ProjectEXBlocks.PERSONAL_LINK.get()))
				.save(output);
		}

		if (ProjectEXBlocks.COMPRESSED_REFINED_LINK != null) {
			// Compressed Refined Link: 6 refined links in 2x3
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXBlocks.COMPRESSED_REFINED_LINK.get())
				.pattern("LLL")
				.pattern("LLL")
				.define('L', ProjectEXBlocks.REFINED_LINK.get())
				.group("projectex:link")
				.unlockedBy("has_refined_link", has(ProjectEXBlocks.REFINED_LINK.get()))
				.save(output);
		}

		// ===== FINAL TIER ITEMS =====
		// Final Star: 8 final power flowers + dragon egg
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXItems.FINAL_STAR.get())
			.pattern("SSS")
			.pattern("SES")
			.pattern("SSS")
			.define('S', ProjectEXBlocks.POWER_FLOWER.get(Matter.FINAL).get())
			.define('E', Items.DRAGON_EGG)
			.group("projectex:star")
			.unlockedBy("has_final_power_flower", has(ProjectEXBlocks.POWER_FLOWER.get(Matter.FINAL).get()))
			.save(output);

		// Final Star Shard: 8 colossal star omega + nether star
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXItems.FINAL_STAR_SHARD.get())
			.pattern("SSS")
			.pattern("SNS")
			.pattern("SSS")
			.define('S', ProjectEXItems.COLOSSAL_STAR.get(Star.OMEGA).get())
			.define('N', Items.NETHER_STAR)
			.group("projectex:star")
			.unlockedBy("has_colossal_star_omega", has(ProjectEXItems.COLOSSAL_STAR.get(Star.OMEGA).get()))
			.save(output);

		// Knowledge Sharing Book: RNR/NBN/RNR
		// R=violet matter, N=nether star, B=writable book
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ProjectEXItems.KNOWLEDGE_SHARING_BOOK.get())
			.pattern("RNR")
			.pattern("NBN")
			.pattern("RNR")
			.define('R', Matter.VIOLET.getItem().get())
			.define('N', Items.NETHER_STAR)
			.define('B', Items.WRITABLE_BOOK)
			.group("projectex:tome")
			.unlockedBy("has_violet_matter", has(Matter.VIOLET.getItem().get()))
			.save(output);
	}
}
