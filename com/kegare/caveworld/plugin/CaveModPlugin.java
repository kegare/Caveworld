/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.plugin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public abstract class CaveModPlugin
{
	public final String pluginModId;

	public CaveModPlugin(String modid)
	{
		this.pluginModId = modid;
	}

	protected ModContainer getPluginModContainer()
	{
		return Loader.instance().getIndexedModList().get(pluginModId);
	}

	public String getPluginModName()
	{
		ModContainer mod = getPluginModContainer();

		return mod == null ? pluginModId : mod.getName();
	}

	public String getPluginModVersion()
	{
		ModContainer mod = getPluginModContainer();

		return mod == null ? "unknown" : mod.getVersion();
	}

	public boolean isPluginEnabled()
	{
		return Loader.isModLoaded(pluginModId);
	}

	protected abstract void init();
}