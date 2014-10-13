/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Objects;

import cpw.mods.fml.common.registry.GameData;

public class ItemEntry
{
	public Item item;
	public int damage;

	private ItemStack itemstack;

	public ItemEntry(Item item, int damage)
	{
		this.item = item;
		this.damage = damage;
	}

	public ItemEntry(String name, int damage)
	{
		this(GameData.getItemRegistry().getObject(name), damage);
	}

	public ItemStack getItemStack()
	{
		if (itemstack == null || itemstack.getItem() != item || itemstack.getItemDamage() != damage)
		{
			itemstack = new ItemStack(item, 1, damage);
		}

		return itemstack;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ItemEntry)
		{
			ItemEntry entry = (ItemEntry)obj;

			return item == entry.item && damage == entry.damage;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		if (item == null)
		{
			return Objects.hashCode(damage);
		}

		return Objects.hashCode(GameData.getItemRegistry().getNameForObject(item), damage);
	}
}