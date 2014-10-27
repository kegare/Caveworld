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
import net.minecraft.item.ItemStack;

import com.kegare.caveworld.util.CaveUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class ItemStackComparator implements Comparator<ItemStack>
{
	@Override
	public int compare(ItemStack o1, ItemStack o2)
	{
		int i = CaveUtils.compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			Item item1 = o1.getItem();
			Item item2 = o2.getItem();

			i = CaveUtils.compareWithNull(item1, item2);

			if (i == 0 && item1 != null && item2 != null)
			{
				UniqueIdentifier unique1 = GameRegistry.findUniqueIdentifierFor(item1);
				UniqueIdentifier unique2 = GameRegistry.findUniqueIdentifierFor(item2);

				i = CaveUtils.compareWithNull(unique1, unique2);

				if (i == 0 && unique1 != null && unique2 != null)
				{
					i = unique1.modId.compareTo(unique2.modId);

					if (i == 0)
					{
						i = unique1.name.compareTo(unique1.name);

						if (i == 0)
						{
							i = Integer.compare(o1.getItemDamage(), o2.getItemDamage());

							if (i == 0)
							{
								i = Integer.compare(o1.stackSize, o2.stackSize);
							}
						}
					}
				}
			}
		}

		return i;
	}
}