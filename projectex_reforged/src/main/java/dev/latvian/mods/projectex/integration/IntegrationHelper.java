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

package dev.latvian.mods.projectex.integration;

import net.neoforged.fml.ModList;

/**
 * Helper class for detecting optional mod dependencies
 */
public class IntegrationHelper {
	/**
	 * Refined Storage 2 mod ID
	 */
	public static final String REFINEDSTORAGE_MOD_ID = "refinedstorage";

	/**
	 * Applied Energistics 2 mod ID
	 */
	public static final String AE2_MOD_ID = "ae2";

	private static Boolean refinedStorageLoaded = null;
	private static Boolean ae2Loaded = null;

	/**
	 * Check if Refined Storage 2 is loaded
	 */
	public static boolean isRefinedStorageLoaded() {
		if (refinedStorageLoaded == null) {
			refinedStorageLoaded = ModList.get().isLoaded(REFINEDSTORAGE_MOD_ID);
		}
		return refinedStorageLoaded;
	}

	/**
	 * Check if Applied Energistics 2 is loaded
	 */
	public static boolean isAE2Loaded() {
		if (ae2Loaded == null) {
			ae2Loaded = ModList.get().isLoaded(AE2_MOD_ID);
		}
		return ae2Loaded;
	}

	/**
	 * Check if ANY storage integration mod is loaded (RS2 or AE2)
	 * Used to determine if Refined Link blocks should be registered
	 */
	public static boolean isAnyStorageModLoaded() {
		return isRefinedStorageLoaded() || isAE2Loaded();
	}
}
