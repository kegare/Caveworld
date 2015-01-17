/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import net.minecraft.item.ItemPickaxe;

import com.kegare.caveworld.core.Caveworld;

public class ItemAquamarinePickaxe extends ItemPickaxe implements IAquamarineTool
{
	public ItemAquamarinePickaxe(String name)
	{
		super(CaveItems.AQUAMARINE);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:aquamarine_pickaxe");
		this.setCreativeTab(Caveworld.tabCaveworld);
	}
}