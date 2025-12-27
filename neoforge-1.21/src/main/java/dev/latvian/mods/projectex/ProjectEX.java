package dev.latvian.mods.projectex;

import dev.latvian.mods.projectex.block.ProjectEXBlocks;
import dev.latvian.mods.projectex.block.entity.ProjectEXBlockEntities;
import dev.latvian.mods.projectex.item.ProjectEXItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(ProjectEX.MOD_ID)
public class ProjectEX {
	public static final String MOD_ID = "projectex";

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
		CREATIVE_TABS.register(modEventBus);
	}
}
