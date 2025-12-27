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

import java.math.BigInteger;
import java.util.UUID;

public class PowerFlowerBlockEntity extends BlockEntity {
	public UUID owner = Util.NIL_UUID;
	public String ownerName = "";
	public int tick = 0;
	public BigInteger storedEMC = BigInteger.ZERO;

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

		if (tick >= 20) {
			tick = 0;

			BlockState state = getBlockState();

			if (state.getBlock() instanceof PowerFlowerBlock powerFlower) {
				long gen = powerFlower.matter.getPowerFlowerOutput();

				ServerPlayer player = level.getServer().getPlayerList().getPlayer(owner);

				if (player != null) {
					IKnowledgeProvider provider = level.getCapability(PECapabilities.KNOWLEDGE,
							player.blockPosition(),
							null,
							player,
							null);

					if (provider != null) {
						provider.setEmc(provider.getEmc().add(BigInteger.valueOf(gen)));

						if (!storedEMC.equals(BigInteger.ZERO)) {
							provider.setEmc(provider.getEmc().add(storedEMC));
							storedEMC = BigInteger.ZERO;
							setChanged();
						}
					} else {
						storedEMC = storedEMC.add(BigInteger.valueOf(gen));
						setChanged();
					}
				}
			}
		}
	}
}
