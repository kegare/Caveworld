/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.block;

import com.kegare.caveworld.item.ItemPortalCaveworld;
import cpw.mods.fml.common.registry.GameRegistry;

public class CaveBlocks
{
	public static final BlockPortalCaveworld caveworld_portal = new BlockPortalCaveworld("portalCaveworld");

	public static void configure()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemPortalCaveworld.class, "caveworld_portal");
	}
}