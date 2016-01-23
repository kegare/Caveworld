/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.materialarms;

import caveworld.util.CaveUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameData;
import ma.api.item.ICustomableItemMA;
import ma.api.item.IDamageableItemMA;
import ma.api.item.IEnchantableItemMA;
import net.minecraft.item.Item;

public class MaterialArmsPlugin
{
	public static final String MODID = "MaterialArms";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			if (item instanceof IDamageableItemMA || item instanceof ICustomableItemMA || item instanceof IEnchantableItemMA)
			{
				CaveUtils.excludeItems.add(item);
			}
		}
	}
}