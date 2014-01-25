package kegare.caveworld.block;

import cpw.mods.fml.common.registry.GameRegistry;
import kegare.caveworld.item.ItemPortalCaveworld;

public class CaveBlock
{
	public static BlockPortalCaveworld portalCaveworld;

	public static void configure()
	{
		GameRegistry.registerBlock(portalCaveworld, ItemPortalCaveworld.class, "portalCaveworld");
	}
}