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