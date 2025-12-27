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

import dev.latvian.mods.projectex.ProjectEX;
import dev.latvian.mods.projectex.block.CollectorBlock;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CollectorBlockEntity extends BlockEntity implements IEmcStorage {
	public int tick = 0;
	public long storedEMC = 0L;

	public CollectorBlockEntity(BlockPos pos, BlockState state) {
		super(ProjectEXBlockEntities.COLLECTOR.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		tick = tag.getByte("Tick") & 0xFF;
		storedEMC = tag.getLong("StoredEMC");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putByte("Tick", (byte) tick);
		tag.putLong("StoredEMC", storedEMC);
	}

	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		tick++;

		if (tick >= 20) {
			tick = 0;

			BlockState state = getBlockState();

			if (state.getBlock() instanceof CollectorBlock collector) {
				storedEMC += collector.matter.collectorOutput;

				List<IEmcStorage> temp = new ArrayList<>(1);

				for (Direction direction : ProjectEX.DIRECTIONS) {
					BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));

					if (blockEntity != null) {
						IEmcStorage storage = blockEntity.getLevel()
								.getCapability(PECapabilities.EMC_STORAGE_BLOCK,
										blockEntity.getBlockPos(),
										blockEntity.getBlockState(),
										blockEntity,
										direction.getOpposite());

						if (storage != null && storage.insertEmc(1L, IEmcStorage.EmcAction.SIMULATE) > 0L) {
							temp.add(storage);

							if (blockEntity instanceof RelayMK1BlockEntity relay) {
								for (int i = 0; i < 20; i++) {
									relay.addBonus();
								}
								blockEntity.setChanged();
							} else if (blockEntity instanceof RelayBlockEntity relay) {
								relay.addBonus();
								blockEntity.setChanged();
							}
						}
					}
				}

				if (!temp.isEmpty() && storedEMC >= temp.size()) {
					long s = storedEMC / temp.size();

					for (IEmcStorage storage : temp) {
						long a = storage.insertEmc(s, EmcAction.EXECUTE);

						if (a > 0L) {
							storedEMC -= a;
							setChanged();

							if (storedEMC < s) {
								break;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public long getStoredEmc() {
		return storedEMC;
	}

	@Override
	public long getMaximumEmc() {
		return Long.MAX_VALUE;
	}

	@Override
	public long extractEmc(long emc, EmcAction action) {
		long e = Math.min(storedEMC, emc);

		if (e < 0L) {
			return insertEmc(-e, action);
		} else if (action.execute()) {
			storedEMC -= e;
		}

		return e;
	}

	@Override
	public long insertEmc(long l, EmcAction emcAction) {
		return 0L;
	}
}
