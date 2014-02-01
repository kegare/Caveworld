package kegare.caveworld.core;

import cpw.mods.fml.common.Loader;
import kegare.caveworld.block.BlockPortalCaveworld;
import kegare.caveworld.block.CaveBlock;
import kegare.caveworld.util.CaveLog;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class Config
{
	private static File cfgFile;
	private static Configuration config;

	public static boolean versionNotify;

	public static int portalCaveworld;

	public static int dimensionCaveworld;
	public static int subsurfaceHeight;
	public static int[] genBiomes;
	public static boolean generateCaves;
	public static boolean generateLakes;
	public static boolean generateRavine;
	public static boolean generateMineshaft;
	public static boolean generateDungeon;
	public static boolean decorateVines;
	public static int genRateDirt;
	public static int genRateGravel;
	public static int genRateCoal;
	public static int genRateIron;
	public static int genRateGold;
	public static int genRateRedstone;
	public static int genRateLapis;
	public static int genRateDiamond;
	public static int genRateEmerald;

	public static void buildConfig()
	{
		cfgFile = new File(Loader.instance().getConfigDir(), "Caveworld.cfg");
		config = new Configuration(cfgFile);

		try
		{
			config.load();
		}
		catch (Exception e)
		{
			File dest = new File(cfgFile.getParentFile(), "Caveworld.cfg.bak");

			if (dest.exists())
			{
				dest.delete();
			}

			cfgFile.renameTo(dest);

			CaveLog.severe("A critical error occured reading the Caveworld.cfg file, defaults will be used - the invalid file is backed up at Caveworld.cfg.bak", e);
		}

		config.addCustomCategoryComment(Configuration.CATEGORY_BLOCK, "If multiplayer, values must match on client-side and server-side.");
		config.addCustomCategoryComment("caveworld", "If multiplayer, server-side only.");

		versionNotify = config.get("general", "versionNotify", true, "Whether or not to notify when a new Caveworld version is available. [true/false]").getBoolean(true);

		portalCaveworld = config.getBlock("portalCaveworld", 750, "BlockID - Block of Caveworld Portal").getInt(750);

		dimensionCaveworld = config.get("caveworld", "dimensionCaveworld", -75, "DimensionID - Caveworld").getInt(-75);
		subsurfaceHeight = Math.min(Math.max(config.get("caveworld", "subsurfaceHeight", 127, "Specify the subsurface layer height of Caveworld. [63-255]").getInt(127), 63), 255);
		genBiomes = config.get("caveworld", "genBiomes", new int[]{0, 1, 2, 3, 4, 5, 6, 7, 12, 13, 16, 17, 18, 19, 21, 22}, "Biomes to generate in Caveworld. Specify in BiomeIDs. [0-255]").getIntList();
		generateCaves = config.get("caveworld", "generateCaves", true, "Whether or not to generate caves to Caveworld. [true/false]").getBoolean(true);
		generateLakes = config.get("caveworld", "generateLakes", true, "Whether or not to generate lakes to Caveworld. [true/false]").getBoolean(true);
		generateRavine = config.get("caveworld", "generateRavine", true, "Whether or not to generate ravine to Caveworld. [true/false]").getBoolean(true);
		generateMineshaft = config.get("caveworld", "generateMineshaft", true, "Whether or not to generate mineshaft to Caveworld. [true/false]").getBoolean(true);
		generateDungeon = config.get("caveworld", "generateDungeon", true, "Whether or not to generate dungeon to Caveworld. [true/false]").getBoolean(true);
		decorateVines = config.get("caveworld", "decorateVines", true, "Whether or not to decorate vines to Caveworld. [true/false]").getBoolean(true);
		genRateDirt = Math.min(Math.max(config.get("caveworld", "genRateDirt", 20, "Specify the generate rate of \"Dirt\" in Caveworld. [0-50]").getInt(20), 0), 50);
		genRateGravel = Math.min(Math.max(config.get("caveworld", "genRateGravel", 15, "Specify the generate rate of \"Gravel\" in Caveworld. [0-50]").getInt(15), 0), 50);
		genRateCoal = Math.min(Math.max(config.get("caveworld", "genRateCoal", 20, "Specify the generate rate of \"Coal Ore\" in Caveworld. [0-50]").getInt(20), 0), 50);
		genRateIron = Math.min(Math.max(config.get("caveworld", "genRateIron", 28, "Specify the generate rate of \"Iron Ore\" in Caveworld. [0-50]").getInt(28), 0), 50);
		genRateGold = Math.min(Math.max(config.get("caveworld", "genRateGold", 2, "Specify the generate rate of \"Gold Ore\" in Caveworld. [0-20]").getInt(2), 0), 20);
		genRateRedstone = Math.min(Math.max(config.get("caveworld", "genRateRedstone", 8, "Specify the generate rate of \"Redstone Ore\" in Caveworld. [0-20]").getInt(8), 0), 20);
		genRateLapis = Math.min(Math.max(config.get("caveworld", "genRateLapis", 1, "Specify the generate rate of \"Lapis Lazuli Ore\" in Caveworld. [0-20]").getInt(1), 0), 20);
		genRateDiamond = Math.min(Math.max(config.get("caveworld", "genRateDiamond", 1, "Specify the generate rate of \"Diamond Ore\" in Caveworld. [0-10]").getInt(1), 0), 10);
		genRateEmerald = Math.min(Math.max(config.get("caveworld", "genRateEmerald", 2, "Specify the generate rate of \"Emerald Ore\" in Caveworld. [0-20]").getInt(2), 0), 20);
	}

	public static boolean saveConfig(boolean forced)
	{
		if (cfgFile != null && config != null)
		{
			if (forced || !cfgFile.exists() || config.hasChanged())
			{
				config.save();

				return true;
			}
		}

		return false;
	}

	public static void initialize()
	{
		CaveBlock.portalCaveworld = new BlockPortalCaveworld(portalCaveworld, "portalCaveworld");
	}
}