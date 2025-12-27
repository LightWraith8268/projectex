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

package dev.latvian.mods.projectex.item;

import dev.latvian.mods.projectex.Star;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.world.item.ItemStack;

public class MagnumStarItem extends ItemPE implements IItemEmcHolder, IBarHelper {
	public static final long[] STAR_EMC = new long[12];

	static {
		long emc = 204800000L;

		for (int i = 0; i < STAR_EMC.length; i++) {
			STAR_EMC[i] = emc;
			emc *= 4L;
		}
	}

	public final Star tier;

	public MagnumStarItem(Star t) {
		super(new Properties().stacksTo(1));
		tier = t;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return !stack.isEmpty() && getStoredEmc(stack) > 0;
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		long starEmc = getStoredEmc(stack);
		return (float) (starEmc == 0L ? 1D : 1D - (double) starEmc / (double) getMaximumEmc(stack));
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return this.getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return this.getColorForBar(stack);
	}

	@Override
	public long insertEmc(ItemStack stack, long toInsert, IEmcStorage.EmcAction action) {
		if (toInsert < 0L) {
			return this.extractEmc(stack, -toInsert, action);
		} else {
			long toAdd = Math.min(this.getNeededEmc(stack), toInsert);
			if (action.execute()) {
				addEmcToStack(stack, toAdd);
			}
			return toAdd;
		}
	}

	@Override
	public long extractEmc(ItemStack stack, long toExtract, IEmcStorage.EmcAction action) {
		if (toExtract < 0L) {
			return this.insertEmc(stack, -toExtract, action);
		} else {
			long storedEmc = this.getStoredEmc(stack);
			long toRemove = Math.min(storedEmc, toExtract);
			if (action.execute()) {
				setEmc(stack, storedEmc - toRemove);
			}
			return toRemove;
		}
	}

	@Override
	public long getStoredEmc(ItemStack stack) {
		return getEmc(stack);
	}

	@Override
	public long getMaximumEmc(ItemStack stack) {
		return STAR_EMC[tier.ordinal()];
	}
}
