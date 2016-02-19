/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.util;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
		if (this == obj)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof ItemEntry))
		{
			return false;
		}

		ItemEntry entry = (ItemEntry)obj;

		return item == entry.item && damage == entry.damage;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	public String getString()
	{
		String name = GameData.getItemRegistry().getNameForObject(item);

		return damage == 0 ? name : name + ":" + damage;
	}

	@Override
	public String toString()
	{
		return GameData.getItemRegistry().getNameForObject(item) + ":" + damage;
	}
}