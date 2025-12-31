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

import dev.latvian.mods.projectex.block.PowerFlowerBlock;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.UUID;

public class PowerFlowerBlockEntity extends BlockEntity {
	private static final Logger LOGGER = LoggerFactory.getLogger(PowerFlowerBlockEntity.class);

	public UUID owner = Util.NIL_UUID;
	public String ownerName = "";
	public int tick = 0;
	public BigInteger storedEMC = BigInteger.ZERO;
	private int debugLogCounter = 0; // Log every 100 ticks (5 seconds) to avoid spam

	public PowerFlowerBlockEntity(BlockPos pos, BlockState state) {
		super(ProjectEXBlockEntities.POWER_FLOWER.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		owner = tag.getUUID("Owner");
		ownerName = tag.getString("OwnerName");
		tick = tag.getByte("Tick") & 0xFF;
		String s = tag.getString("StoredEMC");
		storedEMC = s.equals("0") ? BigInteger.ZERO : new BigInteger(s);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putUUID("Owner", owner);
		tag.putString("OwnerName", ownerName);
		tag.putByte("Tick", (byte) tick);
		tag.putString("StoredEMC", storedEMC.toString());
	}

	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		tick++;
		debugLogCounter++;

		if (tick >= 20) {
			tick = 0;

			BlockState state = getBlockState();

			if (state.getBlock() instanceof PowerFlowerBlock powerFlower) {
				long gen = powerFlower.matter.getPowerFlowerOutput();

				// Log every 100 ticks (5 seconds)
				if (debugLogCounter >= 100) {
					debugLogCounter = 0;
					LOGGER.info("[PowerFlowerBlockEntity] @ {} - Tier: {}, Owner: {} ({}), Generating: {} EMC/s, Stored: {} EMC",
							worldPosition,
							powerFlower.matter.name,
							ownerName,
							owner,
							gen,
							storedEMC);
				}

				ServerPlayer player = level.getServer().getPlayerList().getPlayer(owner);

				if (player != null) {
					IKnowledgeProvider provider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);

					if (provider != null) {
						BigInteger previousEmc = provider.getEmc();
						provider.setEmc(provider.getEmc().add(BigInteger.valueOf(gen)));

						if (debugLogCounter == 0) {
							LOGGER.info("[PowerFlowerBlockEntity] @ {} - Successfully added {} EMC to player {}'s knowledge (was: {}, now: {})",
									worldPosition,
									gen,
									ownerName,
									previousEmc,
									provider.getEmc());
						}

						if (!storedEMC.equals(BigInteger.ZERO)) {
							provider.setEmc(provider.getEmc().add(storedEMC));
							if (debugLogCounter == 0) {
								LOGGER.info("[PowerFlowerBlockEntity] @ {} - Released {} stored EMC to player {}",
										worldPosition,
										storedEMC,
										ownerName);
							}
							storedEMC = BigInteger.ZERO;
							setChanged();
						}
					} else {
						storedEMC = storedEMC.add(BigInteger.valueOf(gen));
						if (debugLogCounter == 0) {
							LOGGER.warn("[PowerFlowerBlockEntity] @ {} - Player {} online but has NO KNOWLEDGE CAPABILITY! Storing EMC: {}",
									worldPosition,
									ownerName,
									storedEMC);
						}
						setChanged();
					}
				} else {
					storedEMC = storedEMC.add(BigInteger.valueOf(gen));
					if (debugLogCounter == 0) {
						LOGGER.warn("[PowerFlowerBlockEntity] @ {} - Owner {} (UUID: {}) NOT ONLINE! Storing EMC: {}",
								worldPosition,
								ownerName,
								owner,
								storedEMC);
					}
					setChanged();
				}
			}
		}
	}
}
