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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameData;

public class InventoryComparator implements Comparator<ItemStack>
{
	private static final int DEFAULT_PRIORITY = 7;

	protected final Map<KeyPair, Integer> priorityMap = Maps.newHashMap();

	@Override
	public int compare(ItemStack o1, ItemStack o2)
	{
		int i = CaveUtils.compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			ItemStack itemstack1 = o1;
			ItemStack itemstack2 = o2;

			i = compareWithPriority(itemstack1, itemstack2);

			if (i == 0)
			{
				i = compareWithItemAndDamage(itemstack1, itemstack2);

				if (i == 0)
				{
					i = compareWithNBTId(itemstack1, itemstack2);
				}
			}
		}

		return i;
	}

	private int compareWithPriority(ItemStack itemstack1, ItemStack itemstack2)
	{
		return Integer.compare(getPriority(itemstack1), getPriority(itemstack2));
	}

	public int getPriority(ItemStack itemstack)
	{
		KeyPair key = new KeyPair(itemstack);

		if (!priorityMap.containsKey(key))
		{
			return DEFAULT_PRIORITY;
		}

		return priorityMap.get(key);
	}

	public void setPriority(ItemStack itemstack, int priority)
	{
		if (itemstack == null)
		{
			return;
		}

		KeyPair key = new KeyPair(itemstack);

		if (priority == DEFAULT_PRIORITY || priority < 0)
		{
			priorityMap.remove(key);
		}
		else
		{
			priorityMap.put(key, priority);
		}
	}

	protected int compareWithItemAndDamage(ItemStack itemstack1, ItemStack itemstack2)
	{
		if (itemstack1.getItem() == itemstack2.getItem())
		{
			return itemstack1.getItemDamage() - itemstack2.getItemDamage();
		}

		return GameData.getItemRegistry().getNameForObject(itemstack1.getItem()).compareTo(GameData.getItemRegistry().getNameForObject(itemstack2.getItem()));
	}

	protected int compareWithNBTId(ItemStack itemstack1, ItemStack itemstack2)
	{
		if (itemstack1.stackTagCompound == null && itemstack2.stackTagCompound == null)
		{
			return 0;
		}
		else if (itemstack1.stackTagCompound == null)
		{
			return -1;
		}
		else if (itemstack2.stackTagCompound == null)
		{
			return 1;
		}

		return Integer.compare(itemstack1.stackTagCompound.getId(), itemstack2.stackTagCompound.getId());
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = (NBTTagList)nbt.getTag("Priorities");

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			Block block = Block.getBlockFromName(tag.getString("Name"));
			int damage = tag.getInteger("Damage");
			int priority = tag.getInteger("Priority");

			setPriority(new ItemStack(block, 0, damage), priority);
		}
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();
		Set set = priorityMap.entrySet();

		for (Iterator<Entry> iterator = set.iterator(); iterator.hasNext();)
		{
			Entry entry = iterator.next();
			NBTTagCompound tag = new NBTTagCompound();

			tag.setString("Name", ((KeyPair)entry.getKey()).name);
			tag.setInteger("Damage", ((KeyPair)entry.getKey()).damage);
			tag.setInteger("Priority", (Integer)entry.getValue());
			list.appendTag(tag);
		}

		nbt.setTag("Priorities", list);
	}

	private class KeyPair
	{
		private String name;
		private int damage;

		public KeyPair(ItemStack itemstack)
		{
			if (itemstack == null)
			{
				this.name = null;
				this.damage = -1;
			}
			else
			{
				this.name = GameData.getItemRegistry().getNameForObject(itemstack.getItem());

				if (itemstack.isItemStackDamageable())
				{
					this.damage = 0;
				}
				else
				{
					this.damage = itemstack.getItemDamage();
				}
			}
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;

			result = prime * result + getOuterType().hashCode();
			result = prime * result + damage;
			result = prime * result + (name == null ? -1 : name.hashCode());

			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			else if (obj == null || getClass() != obj.getClass())
			{
				return false;
			}

			KeyPair other = (KeyPair)obj;

			return getOuterType().equals(other.getOuterType()) && damage == other.damage && name.equals(other.name);
		}

		private InventoryComparator getOuterType()
		{
			return InventoryComparator.this;
		}
	}
}