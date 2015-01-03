/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.kegare.caveworld.item.CaveItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabDiggingShovel extends CreativeTabs
{
	public CreativeTabDiggingShovel()
	{
		super(Caveworld.MODID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getTabLabel()
	{
		return CaveItems.digging_shovel.getUnlocalizedName() + ".name";
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
		return CaveItems.digging_shovel;
	}
}