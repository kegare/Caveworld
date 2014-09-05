/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util;

import java.util.Comparator;

import com.kegare.caveworld.api.ICaveBiome;

public class CaveBiomeComparator implements Comparator<ICaveBiome>
{
	@Override
	public int compare(ICaveBiome o1, ICaveBiome o2)
	{
		int i = CaveUtils.compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			i = Integer.compare(o1.getBiome().biomeID, o2.getBiome().biomeID);
		}

		return i;
	}
}