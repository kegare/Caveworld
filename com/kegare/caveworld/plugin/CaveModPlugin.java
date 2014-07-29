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

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.collect.Sets;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.plugin.thaumcraft.ThaumcraftPlugin;

public class CaveModPlugin
{
	public static final Set<Class> modPlugins = Sets.newHashSet();

	public static void registerPlugins()
	{
		modPlugins.add(MCEconomyPlugin.class);
		modPlugins.add(ThaumcraftPlugin.class);
	}

	public static void invokePlugins()
	{
		Method method;

		for (Class clazz : modPlugins)
		{
			try
			{
				method = clazz.getDeclaredMethod("invoke");
				method.setAccessible(true);
				method.invoke(clazz.newInstance());
			}
			catch (Exception e)
			{
				continue;
			}
		}
	}
}