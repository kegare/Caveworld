package kegare.caveworld.core;

import java.io.File;

import kegare.caveworld.util.CaveLog;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Loader;

public class Config
{
	public static int dimensionCaveworld = -75;
	public static int biomeCaveworld = 75;

	public static boolean generateCaves = true;
	public static boolean generateRavine = true;
	public static boolean generateMineshaft = true;

	public static int portalCaveworld = 750;

	public static boolean versionCheck = true;

	public static void buildConfiguration()
	{
		Configuration cfg = new Configuration(new File(Loader.instance().getConfigDir(), "Caveworld.cfg"));

		try
		{
			cfg.load();

			dimensionCaveworld = cfg.get("caveworld", "dimensionCaveworld", dimensionCaveworld).getInt();
			biomeCaveworld = cfg.get("caveworld", "biomeCaveworld", biomeCaveworld).getInt();

			generateCaves = cfg.get("caveworld", "generateCaves", generateCaves).getBoolean(generateCaves);
			generateRavine = cfg.get("caveworld", "generateRavine", generateRavine).getBoolean(generateRavine);
			generateMineshaft = cfg.get("caveworld", "generateMineshaft", generateMineshaft).getBoolean(generateMineshaft);

			portalCaveworld = cfg.getBlock("portalCaveworld", portalCaveworld).getInt();

			versionCheck = cfg.get("config", "versionCheck", versionCheck).getBoolean(versionCheck);
		}
		catch (Exception e)
		{
			CaveLog.exception(e);
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