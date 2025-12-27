package dev.latvian.mods.projectex.block.entity;

import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ProjectEXBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
			DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ProjectEX.MOD_ID);

	// Link Block Entities
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyLinkBlockEntity>> ENERGY_LINK =
			REGISTRY.register("energy_link", () ->
					BlockEntityType.Builder.of(EnergyLinkBlockEntity::new,
							ProjectEXBlocks.ENERGY_LINK.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PersonalLinkBlockEntity>> PERSONAL_LINK =
			REGISTRY.register("personal_link", () ->
					BlockEntityType.Builder.of(PersonalLinkBlockEntity::new,
							ProjectEXBlocks.PERSONAL_LINK.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RefinedLinkBlockEntity>> REFINED_LINK =
			REGISTRY.register("refined_link", () ->
					BlockEntityType.Builder.of(RefinedLinkBlockEntity::new,
							ProjectEXBlocks.REFINED_LINK.get()).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompressedRefinedLinkBlockEntity>> COMPRESSED_REFINED_LINK =
			REGISTRY.register("compressed_refined_link", () ->
					BlockEntityType.Builder.of(CompressedRefinedLinkBlockEntity::new,
							ProjectEXBlocks.COMPRESSED_REFINED_LINK.get()).build(null));

	// Matter-Tiered Block Entities (multiple blocks per type)
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CollectorBlockEntity>> COLLECTOR =
			REGISTRY.register("collector", () ->
					BlockEntityType.Builder.of(CollectorBlockEntity::new,
							ProjectEXBlocks.COLLECTOR.values().stream()
									.map(DeferredHolder::get)
									.toArray(Block[]::new)).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RelayBlockEntity>> RELAY =
			REGISTRY.register("relay", () ->
					BlockEntityType.Builder.of(RelayBlockEntity::new,
							ProjectEXBlocks.RELAY.values().stream()
									.map(DeferredHolder::get)
									.toArray(Block[]::new)).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PowerFlowerBlockEntity>> POWER_FLOWER =
			REGISTRY.register("power_flower", () ->
					BlockEntityType.Builder.of(PowerFlowerBlockEntity::new,
							ProjectEXBlocks.POWER_FLOWER.values().stream()
									.map(DeferredHolder::get)
									.toArray(Block[]::new)).build(null));

	// Table Block Entities
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyTableEntity>> ALCHEMY_TABLE =
			REGISTRY.register("alchemy_table", () ->
					BlockEntityType.Builder.of(AlchemyTableEntity::new,
							ProjectEXBlocks.ALCHEMY_TABLE.get()).build(null));
}
