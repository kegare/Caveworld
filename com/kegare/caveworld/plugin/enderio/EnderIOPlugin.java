/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.enderio;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.kegare.caveworld.client.config.CaveConfigGui;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameData;

public class EnderIOPlugin
{
	public static final String MODID = "EnderIO";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		for (Object obj : GameData.getBlockRegistry().getKeys())
		{
			String key = (String)obj;
			Block block = GameData.getBlockRegistry().getObject(key);

			if (key.startsWith(MODID) && !block.renderAsNormalBlock())
			{
				Item item = Item.getItemFromBlock(block);

				if (item != null)
				{
					CaveConfigGui.renderIgnored.add(item);
				}
			}
		}
	}
}