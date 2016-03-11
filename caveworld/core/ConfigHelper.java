/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import caveworld.api.CaverAPI;
import caveworld.block.CaveBlocks;
import caveworld.handler.CaveEventHooks;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ConfigHelper
{
	public static boolean isMiningPointValidItem(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
		{
			String name = GameData.getItemRegistry().getNameForObject(itemstack.getItem());

			if (!itemstack.isItemStackDamageable() && (itemstack.getHasSubtypes() || itemstack.getItemDamage() > 0))
			{
				name += ":" + itemstack.getItemDamage();
			}

			return Config.miningPointValidItems != null && ArrayUtils.contains(Config.miningPointValidItems, name);
		}

		return false;
	}

	public static Collection<ItemStack> getItemsFromStrings(String... strings)
	{
		return getItemsFromStrings(new ArrayList<ItemStack>(), strings);
	}

	public static Collection<ItemStack> getItemsFromStrings(Collection<ItemStack> list, String... strings)
	{
		for (String str : strings)
		{
			if (!Strings.isNullOrEmpty(str))
			{
				str = str.trim();

				if (!str.contains(":"))
				{
					str = "minecraft:" + str;
				}

				if (str.indexOf(':') != str.lastIndexOf(':'))
				{
					int i = str.lastIndexOf(':');
					Item item = GameData.getItemRegistry().getObject(str.substring(0, i));

					if (item != null)
					{
						list.add(new ItemStack(item, 1, Integer.parseInt(str.substring(i + 1))));
					}
				}
				else
				{
					Item item = GameData.getItemRegistry().getObject(str);

					if (item != null)
					{
						list.add(new ItemStack(item));
					}
				}
			}
		}

		return list;
	}

	public static String[] getStringsFromItems(Collection<ItemStack> items)
	{
		Set<String> ret = Sets.newLinkedHashSet();

		for (ItemStack itemstack : items)
		{
			if (itemstack != null && itemstack.getItem() != null)
			{
				String name = GameData.getItemRegistry().getNameForObject(itemstack.getItem());
				int damage = itemstack.getItemDamage();

				if (itemstack.isItemStackDamageable())
				{
					ret.add(name);
				}
				else if (itemstack.getHasSubtypes() || damage > 0)
				{
					ret.add(name + ":" + damage);
				}
				else
				{
					ret.add(name);
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	public static boolean refreshMiningPoints()
	{
		if (Config.miningPoints == null)
		{
			return false;
		}

		CaverAPI.caverManager.clearMiningPointAmounts();

		for (String value : Config.miningPoints)
		{
			if (!Strings.isNullOrEmpty(value) && value.contains(","))
			{
				value = value.trim();

				int i = value.indexOf(',');
				String str = value.substring(0, i);
				int point = NumberUtils.toInt(value.substring(i + 1));

				if (str.contains(":"))
				{
					i = str.lastIndexOf(':');
					Block block = GameData.getBlockRegistry().getObject(str.substring(0, i));

					if (block != null && block != Blocks.air)
					{
						int meta = NumberUtils.toInt(str.substring(i + 1));

						CaverAPI.setMiningPointAmount(block, meta, point);
					}
				}
				else
				{
					CaverAPI.setMiningPointAmount(str, point);
				}
			}
		}

		return true;
	}

	public static boolean refreshRandomiteDrops()
	{
		if (Config.randomiteDrops == null)
		{
			return false;
		}

		CaveBlocks.gem_ore.randomiteDrops.clear();

		getItemsFromStrings(CaveBlocks.gem_ore.randomiteDrops, Config.randomiteDrops);

		return true;
	}

	public static boolean refreshCavebornItems()
	{
		if (Config.cavebornItems == null)
		{
			return false;
		}

		CaveEventHooks.cavebornItems.clear();

		getItemsFromStrings(CaveEventHooks.cavebornItems, Config.cavebornItems);

		return true;
	}
}