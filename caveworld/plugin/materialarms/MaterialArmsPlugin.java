/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.materialarms;

import caveworld.plugin.ICavePlugin;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameData;
import ma.api.item.ICustomableItemMA;
import ma.api.item.IDamageableItemMA;
import ma.api.item.IEnchantableItemMA;
import net.minecraft.item.Item;

public class MaterialArmsPlugin implements ICavePlugin
{
	public static final String MODID = "MaterialArms";

	public static boolean pluginState = true;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Method(modid = MODID)
	@Override
	public void invoke()
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