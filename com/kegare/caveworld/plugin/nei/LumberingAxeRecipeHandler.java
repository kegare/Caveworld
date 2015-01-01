/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.nei;

import net.minecraft.item.Item;

import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.item.ICaveniumTool;

public class LumberingAxeRecipeHandler extends CaveniumToolRecipeHandler
{
	@Override
	public ICaveniumTool getTool()
	{
		return CaveItems.lumbering_axe;
	}

	@Override
	public Item getToolItem()
	{
		return CaveItems.lumbering_axe;
	}
}