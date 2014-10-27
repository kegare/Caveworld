/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.comparator;

import java.util.Comparator;

import net.minecraft.item.Item;

import com.kegare.caveworld.util.CaveUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class ItemComparator implements Comparator<Item>
{
	@Override
	public int compare(Item o1, Item o2)
	{
		int i = CaveUtils.compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			UniqueIdentifier unique1 = GameRegistry.findUniqueIdentifierFor(o1);
			UniqueIdentifier unique2 = GameRegistry.findUniqueIdentifierFor(o2);

			i = CaveUtils.compareWithNull(unique1, unique2);

			if (i == 0 && unique1 != null && unique2 != null)
			{
				i = unique1.modId.compareTo(unique2.modId);

				if (i == 0)
				{
					i = unique1.name.compareTo(unique1.name);
				}
			}
		}

		return i;
	}
}