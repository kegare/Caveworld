/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

public class CaveModPlugin
{
	public static final Map<String, Class> modPlugins = Maps.newHashMap();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface ModPlugin
	{
		String modid();
	}

	public static void initializePlugins(ASMDataTable table)
	{
		try
		{
			for (ASMData data : table.getAll(ModPlugin.class.getName()))
			{
				try
				{
					modPlugins.put((String)data.getAnnotationInfo().get("modid"), Class.forName(data.getClassName()));
				}
				catch (ClassNotFoundException e)
				{
					continue;
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to register mod-plugins");
		}
	}

	public static void invokePlugins()
	{
		String modid;
		Class clazz;
		Method method;

		for (Entry<String, Class> entry : modPlugins.entrySet())
		{
			try
			{
				modid = entry.getKey();
				clazz = entry.getValue();

				if (Loader.isModLoaded(modid))
				{
					method = clazz.getDeclaredMethod("invoke");
					method.setAccessible(true);
					method.invoke(clazz.newInstance());
				}
			}
			catch (Exception e)
			{
				continue;
			}
		}
	}
}