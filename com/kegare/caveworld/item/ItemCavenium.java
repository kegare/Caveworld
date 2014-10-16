/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCavenium extends Item
{
	public ItemCavenium(String name)
	{
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:cavenium");
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}
}