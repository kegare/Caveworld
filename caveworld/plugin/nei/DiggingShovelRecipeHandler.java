/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.nei;

import caveworld.item.CaveItems;
import caveworld.item.ICaveniumTool;
import net.minecraft.item.Item;

public class DiggingShovelRecipeHandler extends CaveniumToolRecipeHandler
{
	@Override
	public ICaveniumTool getTool()
	{
		return CaveItems.digging_shovel;
	}

	@Override
	public Item getToolItem()
	{
		return CaveItems.digging_shovel;
	}
}