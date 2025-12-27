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

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;
import java.util.UUID;

public abstract class LinkBaseBlockEntity extends BlockEntity implements IEmcStorage {
	public UUID owner = Util.NIL_UUID;
	public String ownerName = "";
	public int tick = 0;
	public BigInteger storedEMC = BigInteger.ZERO;

	public LinkBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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

			ServerPlayer player = level.getServer().getPlayerList().getPlayer(owner);

			if (player != null && !storedEMC.equals(BigInteger.ZERO)) {
				IKnowledgeProvider provider = level.getCapability(PECapabilities.KNOWLEDGE,
						player.blockPosition(),
						null,
						player,
						null);

				if (provider != null) {
					provider.setEmc(provider.getEmc().add(storedEMC));
					storedEMC = BigInteger.ZERO;
					setChanged();
					provider.syncEmc(player);
				}
			}
		}
	}

	@Override
	public long getStoredEmc() {
		return 0L;
	}

	@Override
	public long getMaximumEmc() {
		return Long.MAX_VALUE;
	}

	@Override
	public long extractEmc(long emc, EmcAction action) {
		return emc < 0L ? insertEmc(-emc, action) : 0L;
	}

	@Override
	public long insertEmc(long emc, EmcAction action) {
		if (emc > 0L) {
			if (action.execute()) {
				storedEMC = storedEMC.add(BigInteger.valueOf(emc));
			}
			return emc;
		}
		return 0L;
	}
}
