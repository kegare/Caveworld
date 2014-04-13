/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.core;

import java.io.File;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.Version;

import cpw.mods.fml.common.Loader;

public class Config
{
	public static boolean hardcoreEnabled = false;

	public static boolean versionNotify = true;
	public static boolean deathLoseMiningCount = false;

	public static boolean portalCraftRecipe = false;
	public static boolean mossStoneCraftRecipe = true;

	public static boolean rope = true;

	public static int dimensionCaveworld = -75;
	public static int subsurfaceHeight = 127;
	public static boolean generateCaves = true;
	public static boolean generateRavine = true;
	public static boolean generateMineshaft = true;
	public static boolean generateStronghold = true;
	public static boolean generateLakes = true;
	public static boolean generateDungeons = true;
	public static boolean decorateVines = true;

	public static final int RENDER_TYPE_PORTAL = Caveworld.proxy.getUniqueRenderType();

	public static File getConfigFile(String name)
	{
		File dir = new File(Loader.instance().getConfigDir(), "caveworld");

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, "caveworld-" + name + ".cfg");
	}

	private static Configuration getConfig(String name)
	{
		File file = getConfigFile(name);
		Configuration config = new Configuration(file);

		try
		{
			config.load();
		}
		catch (Exception e)
		{
			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
		}

		return config;
	}

	protected static void buildConfig()
	{
		String category;
		Configuration config;
		Property prop;

		category = Configuration.CATEGORY_GENERAL;
		config = getConfig(category);

		try
		{
			prop = config.get(category, "versionNotify", versionNotify);
			prop.comment = "Whether or not to notify when a new Caveworld version is available. [true/false]";
			prop.comment += Configuration.NEW_LINE;
			prop.comment += "Note: If multiplayer, does not have to match client-side and server-side.";
			versionNotify = prop.getBoolean(versionNotify);
			prop = config.get(category, "deathLoseMiningCount", deathLoseMiningCount);
			prop.comment = "Whether or not to lose mining count on death. [true/false]";
			prop.comment += Configuration.NEW_LINE;
			prop.comment += "Note: If multiplayer, server-side only.";
			deathLoseMiningCount = prop.getBoolean(deathLoseMiningCount);
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}

		category = "recipes";
		config.addCustomCategoryComment(category, "If multiplayer, values must match on client-side and server-side.");

		try
		{
			prop = config.get(category, "portalCraftRecipe", Version.DEV_DEBUG);
			prop.comment = "Whether or not to add crafting recipe of Caveworld Portal Block. [true/false]";
			portalCraftRecipe = prop.getBoolean(portalCraftRecipe);
			prop = config.get(category, "mossStoneCraftRecipe", mossStoneCraftRecipe);
			prop.comment = "Whether or not to add crafting recipe of Moss Stone. [true/false]";
			mossStoneCraftRecipe = prop.getBoolean(mossStoneCraftRecipe);
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}

		category = "blocks";
		config = getConfig(category);
		config.addCustomCategoryComment(category, "If multiplayer, values must match on client-side and server-side.");

		try
		{
			prop = config.get(category, "Rope", rope);
			prop.comment = "Whether or not to add Rope. [true/false]";
			rope = prop.getBoolean(rope);
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}

		category = "caveworld";
		config = getConfig("dimension");
		config.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		try
		{
			prop = config.get(category, "dimensionCaveworld", dimensionCaveworld);
			prop.comment = "DimensionID - Caveworld";
			dimensionCaveworld = prop.getInt(dimensionCaveworld);

			if (DimensionManager.isDimensionRegistered(dimensionCaveworld))
			{
				dimensionCaveworld = DimensionManager.getNextFreeDimId();

				prop.set(dimensionCaveworld);
			}

			prop = config.get(category, "subsurfaceHeight", subsurfaceHeight);
			prop.comment = "Specify the subsurface layer height of Caveworld. [63-255]";
			subsurfaceHeight = getIntBounded(prop, subsurfaceHeight, 63, 255);
			prop = config.get(category, "generateCaves", generateCaves);
			prop.comment = "Whether or not to generate caves to Caveworld. [true/false]";
			generateCaves = prop.getBoolean(generateCaves);
			prop = config.get(category, "generateRavine", generateRavine);
			prop.comment = "Whether or not to generate ravine to Caveworld. [true/false]";
			generateRavine = prop.getBoolean(generateRavine);
			prop = config.get(category, "generateMineshaft", generateMineshaft);
			prop.comment = "Whether or not to generate mineshaft to Caveworld. [true/false]";
			generateMineshaft = prop.getBoolean(generateMineshaft);
			prop = config.get(category, "generateStronghold", generateStronghold);
			prop.comment = "Whether or not to generate stronghold to Caveworld. [true/false]";
			generateStronghold = prop.getBoolean(generateStronghold);
			prop = config.get(category, "generateLakes", generateLakes);
			prop.comment = "Whether or not to generate lakes to Caveworld. [true/false]";
			generateLakes = prop.getBoolean(generateLakes);
			prop = config.get(category, "generateDungeons", generateDungeons);
			prop.comment = "Whether or not to generate dungeons to Caveworld. [true/false]";
			generateDungeons = prop.getBoolean(generateDungeons);
			prop = config.get(category, "decorateVines", decorateVines);
			prop.comment = "Whether or not to decorate vines to Caveworld. [true/false]";
			decorateVines = prop.getBoolean(decorateVines);
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}
	}

	private static int getIntBounded(Property prop, int num, int min, int max)
	{
		int value = MathHelper.clamp_int(prop.getInt(num), min, max);

		if (prop.getInt(num) != value)
		{
			prop.set(value);
		}

		return prop.getInt(value);
	}
}