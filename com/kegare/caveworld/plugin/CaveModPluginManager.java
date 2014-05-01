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

import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.kegare.caveworld.util.CaveLog;

public class CaveModPluginManager
{
	private static final Set<CaveModPlugin> CAVE_PLUGINS = Sets.newHashSet();

	public static boolean registerPlugin(Class<? extends CaveModPlugin> clazz)
	{
		CaveModPlugin entry = null;

		try
		{
			entry = clazz.newInstance();
		}
		catch (Exception e)
		{
			entry = null;
		}

		if (entry == null || entry.pluginModId.isEmpty())
		{
			return false;
		}

		for (CaveModPlugin plugin : CAVE_PLUGINS)
		{
			if (plugin.pluginModId.equals(entry.pluginModId))
			{
				return false;
			}
		}

		return CAVE_PLUGINS.add(entry);
	}

	public static void unregisterPlugin(Class<? extends CaveModPlugin> clazz)
	{
		for (Iterator<CaveModPlugin> plugins = CAVE_PLUGINS.iterator(); plugins.hasNext();)
		{
			if (plugins.next().getClass() == clazz)
			{
				plugins.remove();
			}
		}
	}

	public static void unregisterPlugin(String modid)
	{
		for (Iterator<CaveModPlugin> plugins = CAVE_PLUGINS.iterator(); plugins.hasNext();)
		{
			if (plugins.next().pluginModId.equals(modid))
			{
				plugins.remove();
			}
		}
	}

	public static boolean isPluginRegistered(Class<? extends CaveModPlugin> clazz)
	{
		for (CaveModPlugin plugin : CAVE_PLUGINS)
		{
			if (plugin.getClass() == clazz)
			{
				return true;
			}
		}

		return false;
	}

	public static boolean isPluginRegistered(String modid)
	{
		for (CaveModPlugin plugin : CAVE_PLUGINS)
		{
			if (plugin.pluginModId.equals(modid))
			{
				return true;
			}
		}

		return false;
	}

	public static ImmutableSet<CaveModPlugin> getModPlugins()
	{
		return new ImmutableSet.Builder<CaveModPlugin>().addAll(CAVE_PLUGINS).build();
	}

	public static void initPlugins()
	{
		for (Iterator<CaveModPlugin> plugins = CAVE_PLUGINS.iterator(); plugins.hasNext();)
		{
			CaveModPlugin plugin = plugins.next();
			boolean flag = false;

			try
			{
				if (plugin.isPluginEnabled())
				{
					plugin.init();
				}
				else
				{
					flag = true;
				}
			}
			catch (Exception e)
			{
				CaveLog.log(Level.WARN, e, "An error occurred trying to initialize %s plugin", plugin.getPluginModName());

				flag = true;
			}
			finally
			{
				if (flag)
				{
					plugins.remove();
				}
			}
		}
	}
}