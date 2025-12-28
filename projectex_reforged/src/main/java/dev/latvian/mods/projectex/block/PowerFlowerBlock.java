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
import dev.latvian.mods.projectex.block.entity.PowerFlowerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class PowerFlowerBlock extends BaseEntityBlock {
	public static final MapCodec<PowerFlowerBlock> CODEC = simpleCodec(properties -> new PowerFlowerBlock(Matter.BASIC));
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,###,###");

	public static final VoxelShape SHAPE = Shapes.or(
			box(0, 0, 0, 16, 1, 16),
			box(3.5, 4, 6.5, 12.5, 13, 9.5),
			box(6.5, 1, 6.5, 9.5, 16, 9.5),
			box(6.5, 4, 3.5, 9.5, 13, 12.5),
			box(6.5, 7, 0.5, 9.5, 10, 15.5),
			box(3.5, 7, 3.5, 12.5, 10, 12.5),
			box(0.5, 7, 6.5, 15.5, 10, 9.5)
	);

	public final Matter matter;

	public PowerFlowerBlock(Matter m) {
		super(BlockBehaviour.Properties.of()
				.strength(1F)
				.sound(SoundType.STONE)
				.noOcclusion()
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
		return new PowerFlowerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (level1, blockPos, blockState, t) -> {
			if (!level1.isClientSide && t instanceof PowerFlowerBlockEntity tile) {
				tile.tick();
			}
		};
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof PowerFlowerBlockEntity powerFlower) {
				player.displayClientMessage(Component.literal(powerFlower.ownerName), true);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		if (entity != null) {
			BlockEntity e = level.getBlockEntity(pos);
			if (e instanceof PowerFlowerBlockEntity powerFlower) {
				powerFlower.owner = entity.getUUID();
				powerFlower.ownerName = entity.getScoreboardName();
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		tooltipComponents.add(Component.translatable("block.projectex.collector.tooltip")
				.withStyle(ChatFormatting.GRAY));
		tooltipComponents.add(Component.translatable("block.projectex.collector.emc_produced",
						DECIMAL_FORMAT.format(matter.getPowerFlowerOutput()))
				.withStyle(ChatFormatting.GRAY));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
