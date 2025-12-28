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

package dev.latvian.mods.projectex.block;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.projectex.Matter;
import dev.latvian.mods.projectex.block.entity.RelayBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class RelayBlock extends BaseEntityBlock {
	public static final MapCodec<RelayBlock> CODEC = simpleCodec(properties -> new RelayBlock(Matter.BASIC));
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,###,###");

	public final Matter matter;

	public RelayBlock(Matter m) {
		super(BlockBehaviour.Properties.of()
				.strength(5F)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops());
		matter = m;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RelayBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (level1, blockPos, blockState, t) -> {
			if (!level.isClientSide && t instanceof RelayBlockEntity tile) {
				tile.tick();
			}
		};
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		tooltipComponents.add(Component.translatable("block.projectex.relay.tooltip")
				.withStyle(ChatFormatting.GRAY));
		tooltipComponents.add(Component.translatable("block.projectex.relay.relay_bonus",
						DECIMAL_FORMAT.format(matter.relayBonus))
				.withStyle(ChatFormatting.GRAY));
		tooltipComponents.add(Component.translatable("block.projectex.relay.max_transfer",
						DECIMAL_FORMAT.format(matter.relayTransfer))
				.withStyle(ChatFormatting.GRAY));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
