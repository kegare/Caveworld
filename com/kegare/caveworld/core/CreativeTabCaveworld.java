/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.item.CaveItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabCaveworld extends CreativeTabs
{
	@SideOnly(Side.CLIENT)
	public ItemStack tabIconItem;

	public CreativeTabCaveworld()
	{
		super(Caveworld.MODID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getTabLabel()
	{
		return "Caveworld";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getTranslatedTabLabel()
	{
		return getTabLabel();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getTabIconItem()
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getIconItemStack()
	{
		if (tabIconItem == null)
		{
			Random random = new Random();

			switch (random.nextInt(5))
			{
				case 1:
					if (Config.rope)
					{
						tabIconItem = new ItemStack(CaveBlocks.rope);
						break;
					}
				case 2:
					if (Config.oreCavenium)
					{
						int i = random.nextInt(2);

						if (random.nextBoolean())
						{
							tabIconItem = new ItemStack(CaveBlocks.cavenium_ore, 1, i);
						}
						else
						{
							tabIconItem = new ItemStack(CaveBlocks.cavenium_ore, 1, 2 + i);
						}

						break;
					}
				case 3:
					if (Config.cavenium)
					{
						tabIconItem = new ItemStack(CaveItems.cavenium, 1, random.nextInt(2));
						break;
					}
				case 4:
					if (Config.oreCompass)
					{
						tabIconItem = new ItemStack(CaveItems.ore_compass);
						break;
					}
				default:
					tabIconItem = new ItemStack(CaveBlocks.caveworld_portal);
					break;
			}
		}

		return tabIconItem;
	}
}