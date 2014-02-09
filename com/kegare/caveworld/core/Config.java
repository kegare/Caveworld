package com.kegare.caveworld.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.kegare.caveworld.packet.AbstractPacket;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.Loader;

public class Config
{
	public static boolean versionNotify = true;

	public static int portalCaveworld = 750;

	public static int dimensionCaveworld = -75;
	public static int subsurfaceHeight = 127;
	public static boolean generateCaves = true;
	public static boolean generateRavine = true;
	public static boolean generateMineshaft = true;
	public static boolean generateLakes = true;
	public static boolean generateDungeons = true;
	public static boolean decorateVines = true;
	public static int genRateDirt = 18;
	public static int genRateGravel = 7;
	public static int genRateCoal = 20;
	public static int genRateIron = 28;
	public static int genRateGold = 2;
	public static int genRateRedstone = 8;
	public static int genRateLapis = 1;
	public static int genRateDiamond = 1;
	public static int genRateEmerald = 2;

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
			prop.set(Math.min(Math.max(prop.getInt(subsurfaceHeight), 63), 255));
			subsurfaceHeight = prop.getInt();
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

		config = getConfig("ores");
		config.addCustomCategoryComment("caveworld", "If multiplayer, server-side only.");

		try
		{
			prop = config.get(category, "genRateDirt", genRateDirt);
			prop.comment = "Specify the generation rate of \"Dirt\" in Caveworld. [0-50]";
			prop.set(Math.min(Math.max(prop.getInt(genRateDirt), 0), 50));
			genRateDirt = prop.getInt();
			prop = config.get(category, "genRateGravel", genRateGravel);
			prop.comment = "Specify the generation rate of \"Gravel\" in Caveworld. [0-50]";
			prop.set(Math.min(Math.max(prop.getInt(genRateGravel), 0), 50));
			genRateGravel = prop.getInt();
			prop = config.get(category, "genRateCoal", genRateCoal);
			prop.comment = "Specify the generation rate of \"Coal Ore\" in Caveworld. [0-50]";
			prop.set(Math.min(Math.max(prop.getInt(genRateCoal), 0), 50));
			genRateCoal = prop.getInt();
			prop = config.get(category, "genRateIron", genRateIron);
			prop.comment = "Specify the generation rate of \"Iron Ore\" in Caveworld. [0-50]";
			prop.set(Math.min(Math.max(prop.getInt(genRateIron), 0), 50));
			genRateIron = prop.getInt();
			prop = config.get(category, "genRateGold", genRateGold);
			prop.comment = "Specify the generation rate of \"Gold Ore\" in Caveworld. [0-20]";
			prop.set(Math.min(Math.max(prop.getInt(genRateGold), 0), 20));
			genRateGold = prop.getInt();
			prop = config.get(category, "genRateRedstone", genRateRedstone);
			prop.comment = "Specify the generation rate of \"Redstone Ore\" in Caveworld. [0-20]";
			prop.set(Math.min(Math.max(prop.getInt(genRateRedstone), 0), 20));
			genRateRedstone = prop.getInt();
			prop = config.get(category, "genRateLapis", genRateLapis);
			prop.comment = "Specify the generation rate of \"Lapis Lazuli Ore\" in Caveworld. [0-20]";
			prop.set(Math.min(Math.max(prop.getInt(genRateLapis), 0), 20));
			genRateLapis = prop.getInt();
			prop = config.get(category, "genRateDiamond", genRateDiamond);
			prop.comment = "Specify the generation rate of \"Diamond Ore\" in Caveworld. [0-10]";
			prop.set(Math.min(Math.max(prop.getInt(genRateDiamond), 0), 10));
			genRateDiamond = prop.getInt();
			prop = config.get(category, "genRateEmerald", genRateEmerald);
			prop.comment = "Specify the generation rate of \"Emerald Ore\" in Caveworld. [0-20]";
			prop.set(Math.min(Math.max(prop.getInt(genRateEmerald), 0), 20));
			genRateEmerald = prop.getInt();
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}
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
		private int genRateDirt;
		private int genRateGravel;
		private int genRateCoal;
		private int genRateIron;
		private int genRateGold;
		private int genRateRedstone;
		private int genRateLapis;
		private int genRateDiamond;
		private int genRateEmerald;

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
			genRateDirt = Config.genRateDirt;
			genRateGravel = Config.genRateGravel;
			genRateCoal = Config.genRateCoal;
			genRateIron = Config.genRateIron;
			genRateGold = Config.genRateGold;
			genRateRedstone = Config.genRateRedstone;
			genRateLapis = Config.genRateLapis;
			genRateDiamond = Config.genRateDiamond;
			genRateEmerald = Config.genRateEmerald;
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
			buffer.writeInt(genRateDirt);
			buffer.writeInt(genRateGravel);
			buffer.writeInt(genRateCoal);
			buffer.writeInt(genRateIron);
			buffer.writeInt(genRateGold);
			buffer.writeInt(genRateRedstone);
			buffer.writeInt(genRateLapis);
			buffer.writeInt(genRateDiamond);
			buffer.writeInt(genRateEmerald);
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
			genRateDirt = buffer.readInt();
			genRateGravel = buffer.readInt();
			genRateCoal = buffer.readInt();
			genRateIron = buffer.readInt();
			genRateGold = buffer.readInt();
			genRateRedstone = buffer.readInt();
			genRateLapis = buffer.readInt();
			genRateDiamond = buffer.readInt();
			genRateEmerald = buffer.readInt();
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
			Config.genRateDirt = genRateDirt;
			Config.genRateGravel = genRateGravel;
			Config.genRateCoal = genRateCoal;
			Config.genRateIron = genRateIron;
			Config.genRateGold = genRateGold;
			Config.genRateRedstone = genRateRedstone;
			Config.genRateLapis = genRateLapis;
			Config.genRateDiamond = genRateDiamond;
			Config.genRateEmerald = genRateEmerald;
		}

		@Override
		public void handleServerSide(EntityPlayer player) {}
	}
}