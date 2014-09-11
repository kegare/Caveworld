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

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeComparator implements Comparator<BiomeGenBase>
{
	@Override
	public int compare(BiomeGenBase o1, BiomeGenBase o2)
	{
		int i = CaveUtils.compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			return Integer.compare(o1.biomeID, o2.biomeID);
		}

		return i;
	}
}