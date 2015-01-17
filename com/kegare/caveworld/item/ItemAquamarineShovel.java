/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import net.minecraft.item.ItemSpade;

import com.kegare.caveworld.core.Caveworld;

public class ItemAquamarineShovel extends ItemSpade implements IAquamarineTool
{
	public ItemAquamarineShovel(String name)
	{
		super(CaveItems.AQUAMARINE);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:aquamarine_shovel");
		this.setCreativeTab(Caveworld.tabCaveworld);
	}
}