package com.kegare.caveworld.core;

import com.kegare.caveworld.packet.AbstractPacket;
import com.kegare.caveworld.util.CaveLog;
import cpw.mods.fml.common.Loader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config
{
	public static boolean versionNotify = true;

	public static int dimensionCaveworld = -75;
	public static int subsurfaceHeight = 127;
	public static boolean generateCaves = true;
	public static boolean generateRavine = true;
	public static boolean generateMineshaft = true;
	public static boolean generateLakes = true;
	public static boolean generateDungeons = true;
	public static boolean decorateVines = true;

	private static Configuration getConfig(String name)
	{
		File dir = new File(Loader.instance().getConfigDir(), "caveworld");

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		File file = new File(dir, "caveworld-" + name + ".cfg");
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

			CaveLog.severe("A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName(), e);
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
			versionNotify = prop.getBoolean(versionNotify);
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
		config.addCustomCategoryComment("caveworld", "If multiplayer, server-side only.");

		try
		{
			prop = config.get(category, "dimensionCaveworld", dimensionCaveworld);
			prop.comment = "DimensionID - Caveworld";
			dimensionCaveworld = prop.getInt(dimensionCaveworld);
			prop = config.get(category, "subsurfaceHeight", subsurfaceHeight);
			prop.comment = "Specify the subsurface layer height of Caveworld. [63-255]";
			subsurfaceHeight = roundIntValue(prop, 63, 255).getInt();
			prop = config.get(category, "generateCaves", generateCaves);
			prop.comment = "Whether or not to generate caves to Caveworld. [true/false]";
			generateCaves = prop.getBoolean(generateCaves);
			prop = config.get(category, "generateRavine", generateRavine);
			prop.comment = "Whether or not to generate ravine to Caveworld. [true/false]";
			generateRavine = prop.getBoolean(generateRavine);
			prop = config.get(category, "generateMineshaft", generateMineshaft);
			prop.comment = "Whether or not to generate mineshaft to Caveworld. [true/false]";
			generateMineshaft = prop.getBoolean(generateMineshaft);
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

	private static Property roundIntValue(Property prop, int min, int max)
	{
		int value = Math.min(Math.max(prop.getInt(), min), max);

		if (prop.getInt() != value)
		{
			prop.set(value);
		}

		return prop;
	}

	public static class ConfigSyncPacket extends AbstractPacket
	{
		private int dimensionCaveworld;
		private int subsurfaceHeight;
		private boolean generateCaves;
		private boolean generateRavine;
		private boolean generateMineshaft;
		private boolean generateLakes;
		private boolean generateDungeons;
		private boolean decorateVines;

		public ConfigSyncPacket()
		{
			dimensionCaveworld = Config.dimensionCaveworld;
			subsurfaceHeight = Config.subsurfaceHeight;
			generateCaves = Config.generateCaves;
			generateRavine = Config.generateRavine;
			generateMineshaft = Config.generateMineshaft;
			generateLakes = Config.generateLakes;
			generateDungeons = Config.generateDungeons;
			decorateVines = Config.decorateVines;
		}

		@Override
		public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
		{
			buffer.writeInt(dimensionCaveworld);
			buffer.writeInt(subsurfaceHeight);
			buffer.writeBoolean(generateCaves);
			buffer.writeBoolean(generateRavine);
			buffer.writeBoolean(generateMineshaft);
			buffer.writeBoolean(generateLakes);
			buffer.writeBoolean(generateDungeons);
			buffer.writeBoolean(decorateVines);
		}

		@Override
		public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
		{
			dimensionCaveworld = buffer.readInt();
			subsurfaceHeight = buffer.readInt();
			generateCaves = buffer.readBoolean();
			generateRavine = buffer.readBoolean();
			generateMineshaft = buffer.readBoolean();
			generateLakes = buffer.readBoolean();
			generateDungeons = buffer.readBoolean();
			decorateVines = buffer.readBoolean();
		}

		@Override
		public void handleClientSide(EntityPlayer player)
		{
			Config.dimensionCaveworld = dimensionCaveworld;
			Config.subsurfaceHeight = subsurfaceHeight;
			Config.generateCaves = generateCaves;
			Config.generateRavine = generateRavine;
			Config.generateMineshaft = generateMineshaft;
			Config.generateLakes = generateLakes;
			Config.generateDungeons = generateDungeons;
			Config.decorateVines = decorateVines;
		}

		@Override
		public void handleServerSide(EntityPlayer player) {}
	}
}