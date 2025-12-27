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
import net.minecraft.world.item.ItemStack;

public class ColossalStarItem extends MagnumStarItem {
	public ColossalStarItem(Star t) {
		super(t);
	}

	@Override
	public long getMaximumEmc(ItemStack stack) {
		return STAR_EMC[tier.ordinal() + 6];
	}
}
