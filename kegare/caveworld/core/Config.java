package kegare.caveworld.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

public class Config
{
	public static boolean versionNotify = true;

	public static int dimensionCaveworld = -75;
	public static int biomeCaveworld = 75;
	public static boolean generateCaves = true;
	public static boolean generateLakes = true;
	public static boolean generateRavine = true;
	public static boolean generateMineshaft = true;
	public static boolean generateDungeon = true;

	public static int portalCaveworld = 750;

	public static void buildConfiguration()
	{
		Configuration cfg = new Configuration(new File(Loader.instance().getConfigDir(), "Caveworld.cfg"));

		try
		{
			cfg.load();

			versionNotify = cfg.get(Configuration.CATEGORY_GENERAL, "versionNotify", versionNotify, "Notifies when a new version is available").getBoolean(versionNotify);

			dimensionCaveworld = cfg.get("caveworld", "dimensionCaveworld", dimensionCaveworld, "Caveworld Dimension ID (Default: -75)").getInt(dimensionCaveworld);
			biomeCaveworld = cfg.get("caveworld", "biomeCaveworld", biomeCaveworld, "Caveworld Biome ID (Default: 75)").getInt(biomeCaveworld);
			generateCaves = cfg.get("caveworld", "generateCaves", generateCaves, "Whether or not to generate caves to Caveworld (Default: true)").getBoolean(generateCaves);
			generateLakes = cfg.get("caveworld", "generateLakes", generateLakes, "Whether or not to generate lakes to Caveworld (Default: true)").getBoolean(generateLakes);
			generateRavine = cfg.get("caveworld", "generateRavine", generateRavine, "Whether or not to generate ravine to Caveworld (Default: true)").getBoolean(generateRavine);
			generateMineshaft = cfg.get("caveworld", "generateMineshaft", generateMineshaft, "Whether or not to generate mineshaft to Caveworld (Default: true)").getBoolean(generateMineshaft);
			generateDungeon = cfg.get("caveworld", "generateDungeon", generateDungeon, "Whether or not to generate dungeon to Caveworld (Default: true)").getBoolean(generateDungeon);

			portalCaveworld = cfg.getBlock("portalCaveworld", portalCaveworld, "Caveworld Portal ID (Default: 750)").getInt(portalCaveworld);
		}
		finally
		{
			if (cfg.hasChanged())
			{
				cfg.save();
			}
		}
	}
}