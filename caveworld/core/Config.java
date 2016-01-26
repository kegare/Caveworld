/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveVein;
import caveworld.block.CaveBlocks;
import caveworld.core.CaveBiomeManager.CaveBiome;
import caveworld.core.CaveVeinManager.CaveVein;
import caveworld.entity.EntityArcherZombie;
import caveworld.entity.EntityCaveman;
import caveworld.entity.EntityCavenicSkeleton;
import caveworld.util.CaveConfiguration;
import caveworld.util.CaveLog;
import caveworld.util.CaveUtils;
import caveworld.util.Version;
import caveworld.world.ChunkProviderCavern;
import caveworld.world.ChunkProviderCaveworld;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config
{
	private static final Side side = FMLCommonHandler.instance().getSide();

	public static Configuration generalCfg;
	public static Configuration entitiesCfg;
	public static Configuration dimensionCfg;
	public static Configuration biomesCfg;
	public static Configuration biomesCavernCfg;
	public static Configuration veinsCfg;
	public static Configuration veinsCavernCfg;

	public static boolean versionNotify;
	public static boolean veinsAutoRegister;
	public static boolean deathLoseMiningPoint;
	public static int miningPointRenderType;
	public static String[] miningPoints;
	public static String[] miningPointsDefault;
	public static String[] miningPointValidItems;
	public static String[] miningPointValidItemsDefault;
	public static boolean fakeMiningPickaxe;
	public static boolean fakeLumberingAxe;
	public static boolean fakeDiggingShovel;
	public static int modeDisplayTime;
	public static int quickBreakLimit;

	public static boolean portalCraftRecipe;
	public static boolean mossStoneCraftRecipe;
	public static boolean refinedCaveniumCraftRecipe;

	public static boolean hardcore;
	public static boolean caveborn;

	public static Class<? extends IConfigEntry> selectItems;
	public static Class<? extends IConfigEntry> selectBiomes;
	public static Class<? extends IConfigEntry> cycleInteger;
	public static Class<? extends IConfigEntry> pointsEntry;

	public static final int RENDER_TYPE_PORTAL = Caveworld.proxy.getUniqueRenderType();
	public static final int RENDER_TYPE_CHEST = Caveworld.proxy.getUniqueRenderType();

	public static File getConfigDir()
	{
		return new File(Loader.instance().getConfigDir(), "caveworld");
	}

	public static File getConfigFile(String name)
	{
		File dir = getConfigDir();

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, "caveworld-" + name + ".cfg");
	}

	public static Configuration loadConfig(String name)
	{
		File file = getConfigFile(name);
		Configuration config = new CaveConfiguration(file, true);

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

	public static File getConfigFile(String name, String category)
	{
		File dir = getConfigDir();

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return new File(dir, name + "-" + category + ".cfg");
	}

	public static Configuration loadConfig(String name, String category)
	{
		File file = getConfigFile(name, category);
		Configuration config = new CaveConfiguration(file, true);

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

	public static void syncGeneralCfg()
	{
		String category = Configuration.CATEGORY_GENERAL;
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (generalCfg == null)
		{
			generalCfg = loadConfig(category);
		}

		prop = generalCfg.get(category, "versionNotify", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, does not have to match client-side and server-side.";
		propOrder.add(prop.getName());
		versionNotify = prop.getBoolean(versionNotify);
		prop = generalCfg.get(category, "veinsAutoRegister", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		veinsAutoRegister = prop.getBoolean(veinsAutoRegister);
		prop = generalCfg.get(category, "deathLoseMiningPoint", false);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		deathLoseMiningPoint = prop.getBoolean(deathLoseMiningPoint);

		if (side.isClient())
		{
			prop = generalCfg.get(category, "miningPointRenderType", 0);
			prop.setMinValue(0).setMaxValue(4).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(cycleInteger);
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

			for (int i = Integer.parseInt(prop.getMinValue()); i <= Integer.parseInt(prop.getMaxValue()); ++i)
			{
				prop.comment += Configuration.NEW_LINE;

				if (i == Integer.parseInt(prop.getMaxValue()))
				{
					prop.comment += i + ": " + StatCollector.translateToLocal(prop.getLanguageKey() + "." + i);
				}
				else
				{
					prop.comment += i + ": " + StatCollector.translateToLocal(prop.getLanguageKey() + "." + i) + ", ";
				}
			}

			propOrder.add(prop.getName());
			miningPointRenderType = MathHelper.clamp_int(prop.getInt(miningPointRenderType), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		}

		prop = generalCfg.get(category, "miningPoints", miningPointsDefault == null ? new String[0] : miningPointsDefault);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(pointsEntry);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		miningPoints = prop.getStringList();

		prop = generalCfg.get(category, "miningPointValidItems", miningPointValidItemsDefault == null ? new String[0] : miningPointValidItemsDefault);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectItems);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		miningPointValidItems = prop.getStringList();

		if (side.isClient())
		{
			prop = generalCfg.get(category, "fakeMiningPickaxe", false);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			fakeMiningPickaxe = prop.getBoolean(fakeMiningPickaxe);
			prop = generalCfg.get(category, "fakeLumberingAxe", false);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			fakeLumberingAxe = prop.getBoolean(fakeLumberingAxe);
			prop = generalCfg.get(category, "fakeDiggingShovel", false);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			fakeDiggingShovel = prop.getBoolean(fakeDiggingShovel);
			prop = generalCfg.get(category, "modeDisplayTime", 2200);
			prop.setMinValue(0).setMaxValue(Integer.MAX_VALUE).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			modeDisplayTime = MathHelper.clamp_int(prop.getInt(modeDisplayTime), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		}

		prop = generalCfg.get(category, "quickBreakLimit", 100);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		quickBreakLimit = MathHelper.clamp_int(prop.getInt(quickBreakLimit), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		generalCfg.setCategoryPropertyOrder(category, propOrder);

		category = "recipes";
		prop = generalCfg.get(category, "portalCraftRecipe", Version.DEV_DEBUG);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		portalCraftRecipe = prop.getBoolean(portalCraftRecipe);
		prop = generalCfg.get(category, "mossStoneCraftRecipe", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		mossStoneCraftRecipe = prop.getBoolean(mossStoneCraftRecipe);
		prop = generalCfg.get(category, "refinedCaveniumCraftRecipe", Version.DEV_DEBUG);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		refinedCaveniumCraftRecipe = prop.getBoolean(refinedCaveniumCraftRecipe);

		generalCfg.setCategoryPropertyOrder(category, propOrder);
		generalCfg.setCategoryRequiresMcRestart(category, true);

		category = "options";
		prop = generalCfg.get(category, "hardcore", false);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		hardcore = prop.getBoolean(hardcore);
		prop = generalCfg.get(category, "caveborn", false);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		caveborn = prop.getBoolean(caveborn);

		generalCfg.setCategoryPropertyOrder(category, propOrder);

		if (generalCfg.hasChanged())
		{
			generalCfg.save();
		}
	}

	public static void refreshMiningPoints()
	{
		CaveUtils.getPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				CaveworldAPI.caverManager.clearMiningPointAmounts();

				for (String str : miningPoints)
				{
					if (!Strings.isNullOrEmpty(str) && str.contains(",") && str.contains(":"))
					{
						str = str.trim();

						int i = str.indexOf(',');
						String str2 = str.substring(0, i);
						int point = Integer.parseInt(str.substring(i + 1));

						i = str2.lastIndexOf(':');
						Block block = GameData.getBlockRegistry().getObject(str2.substring(0, i));
						int meta = Integer.parseInt(str2.substring(i + 1));

						if (block != null && block != Blocks.air)
						{
							CaveworldAPI.setMiningPointAmount(block, meta, point);
						}
					}
				}
			}
		});
	}

	public static void syncEntitiesCfg()
	{
		String category = "entities";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (entitiesCfg == null)
		{
			entitiesCfg = loadConfig(category);
		}

		category = "Caveman";
		prop = entitiesCfg.get(category, "spawnWeight", 50);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMinHeight", 10);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnInChunks", 1);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCaveman.spawnBiomes = prop.getIntList();
		EntityCaveman.refreshSpawn();
		prop = entitiesCfg.get(category, "creatureType", 1);
		prop.setMinValue(0).setMaxValue(1).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName()).setConfigEntryClass(cycleInteger);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		for (int i = Integer.parseInt(prop.getMinValue()); i <= Integer.parseInt(prop.getMaxValue()); ++i)
		{
			prop.comment += Configuration.NEW_LINE;

			if (i == Integer.parseInt(prop.getMaxValue()))
			{
				prop.comment += i + ": " + StatCollector.translateToLocal(prop.getLanguageKey() + "." + i);
			}
			else
			{
				prop.comment += i + ": " + StatCollector.translateToLocal(prop.getLanguageKey() + "." + i) + ", ";
			}
		}

		propOrder.add(prop.getName());
		EntityCaveman.creatureType = MathHelper.clamp_int(prop.getInt(EntityCaveman.creatureType), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		entitiesCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		entitiesCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "ArcherZombie";
		prop = entitiesCfg.get(category, "spawnWeight", 100);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMinHeight", 10);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnInChunks", 4);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnBiomes = prop.getIntList();
		EntityArcherZombie.refreshSpawn();

		entitiesCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		entitiesCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "CavenicSkeleton";
		prop = entitiesCfg.get(category, "spawnWeight", 50);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMinHeight", 100);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMaxHeight", 200);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnInChunks", 2);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnBiomes = prop.getIntList();
		EntityCavenicSkeleton.refreshSpawn();

		entitiesCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		entitiesCfg.setCategoryPropertyOrder(category, propOrder);

		if (entitiesCfg.hasChanged())
		{
			entitiesCfg.save();
		}
	}

	public static void syncDimensionCfg()
	{
		String category = "Caveworld";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (dimensionCfg == null)
		{
			dimensionCfg = loadConfig("dimension");
		}

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimension", -5);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.dimensionId = prop.getInt(ChunkProviderCaveworld.dimensionId);

		if (ChunkProviderCaveworld.dimensionId == 0)
		{
			prop.set(DimensionManager.getNextFreeDimId());

			ChunkProviderCaveworld.dimensionId = prop.getInt();
		}

		prop = dimensionCfg.get(category, "subsurfaceHeight", 255);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.subsurfaceHeight = MathHelper.clamp_int(prop.getInt(ChunkProviderCaveworld.subsurfaceHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = dimensionCfg.get(category, "generateCaves", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateCaves = prop.getBoolean(ChunkProviderCaveworld.generateCaves);
		prop = dimensionCfg.get(category, "generateRavine", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateRavine = prop.getBoolean(ChunkProviderCaveworld.generateRavine);
		prop = dimensionCfg.get(category, "generateUnderCaves", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateUnderCaves = prop.getBoolean(ChunkProviderCaveworld.generateUnderCaves);
		prop = dimensionCfg.get(category, "generateExtremeCaves", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateExtremeCaves = prop.getBoolean(ChunkProviderCaveworld.generateExtremeCaves);
		prop = dimensionCfg.get(category, "generateExtremeRavine", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateExtremeRavine = prop.getBoolean(ChunkProviderCaveworld.generateExtremeRavine);
		prop = dimensionCfg.get(category, "generateMineshaft", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateMineshaft = prop.getBoolean(ChunkProviderCaveworld.generateMineshaft);
		prop = dimensionCfg.get(category, "generateStronghold", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateStronghold = prop.getBoolean(ChunkProviderCaveworld.generateStronghold);
		prop = dimensionCfg.get(category, "generateLakes", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateLakes = prop.getBoolean(ChunkProviderCaveworld.generateLakes);
		prop = dimensionCfg.get(category, "generateDungeons", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateDungeons = prop.getBoolean(ChunkProviderCaveworld.generateDungeons);
		prop = dimensionCfg.get(category, "generateAnimalDungeons", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.generateAnimalDungeons = prop.getBoolean(ChunkProviderCaveworld.generateAnimalDungeons);
		prop = dimensionCfg.get(category, "decorateVines", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.decorateVines = prop.getBoolean(ChunkProviderCaveworld.decorateVines);

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "Cavern";

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimension", -6);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCavern.dimensionId = prop.getInt(ChunkProviderCavern.dimensionId);

		if (ChunkProviderCavern.dimensionId == 0)
		{
			prop.set(DimensionManager.getNextFreeDimId());

			ChunkProviderCavern.dimensionId = prop.getInt();
		}

		prop = dimensionCfg.get(category, "subsurfaceHeight", 127);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCavern.subsurfaceHeight = MathHelper.clamp_int(prop.getInt(ChunkProviderCavern.subsurfaceHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = dimensionCfg.get(category, "generateCaves", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCavern.generateCaves = prop.getBoolean(ChunkProviderCavern.generateCaves);
		prop = dimensionCfg.get(category, "generateLakes", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCavern.generateLakes = prop.getBoolean(ChunkProviderCavern.generateLakes);
		prop = dimensionCfg.get(category, "caveType", 0);
		prop.setMinValue(0).setMaxValue(1).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setConfigEntryClass(cycleInteger);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		for (int i = Integer.parseInt(prop.getMinValue()); i <= Integer.parseInt(prop.getMaxValue()); ++i)
		{
			prop.comment += Configuration.NEW_LINE;

			if (i == Integer.parseInt(prop.getMaxValue()))
			{
				prop.comment += i + ": " + StatCollector.translateToLocal(prop.getLanguageKey() + "." + i);
			}
			else
			{
				prop.comment += i + ": " + StatCollector.translateToLocal(prop.getLanguageKey() + "." + i) + ", ";
			}
		}

		propOrder.add(prop.getName());
		ChunkProviderCavern.caveType = MathHelper.clamp_int(prop.getInt(ChunkProviderCavern.caveType), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		if (dimensionCfg.hasChanged())
		{
			dimensionCfg.save();
		}
	}

	public static void syncBiomesCfg()
	{
		String category = "biomes";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (biomesCfg == null)
		{
			biomesCfg = loadConfig(category);
		}
		else
		{
			CaveworldAPI.clearCaveBiomes();
		}

		String name, terrainBlock, topBlock;
		int weight, terrainMeta, topMeta;

		for (BiomeGenBase biome : CaveUtils.getBiomes())
		{
			name = Integer.toString(biome.biomeID);

			if (CaveBiomeManager.presets.containsKey(biome))
			{
				ICaveBiome entry = CaveBiomeManager.presets.get(biome);

				weight = entry.getGenWeight();
				terrainBlock = GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock());
				terrainMeta = entry.getTerrainBlock().getMetadata();
				topBlock = GameData.getBlockRegistry().getNameForObject(entry.getTopBlock().getBlock());
				topMeta = entry.getTopBlock().getMetadata();
			}
			else
			{
				weight = 0;
				terrainBlock = GameData.getBlockRegistry().getNameForObject(Blocks.stone);
				terrainMeta = 0;
				topBlock = terrainBlock;
				topMeta = terrainMeta;
			}

			propOrder = Lists.newArrayList();
			prop = biomesCfg.get(name, "genWeight", weight);
			prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesCfg.get(name, "terrainBlock", terrainBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			terrainBlock = prop.getString();
			if (!GameData.getBlockRegistry().containsKey(terrainBlock)) prop.setToDefault();
			prop = biomesCfg.get(name, "terrainBlockMetadata", terrainMeta);
			prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			terrainMeta = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesCfg.get(name, "topBlock", topBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			topBlock = prop.getString();
			if (!GameData.getBlockRegistry().containsKey(topBlock)) prop.setToDefault();
			prop = biomesCfg.get(name, "topBlockMetadata", topMeta);
			prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			topMeta = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

			if (BiomeDictionary.isBiomeRegistered(biome))
			{
				Set<String> types = Sets.newTreeSet();

				for (Type type : BiomeDictionary.getTypesForBiome(biome))
				{
					types.add(type.name());
				}

				biomesCfg.addCustomCategoryComment(name, biome.biomeName + ": " + Joiner.on(", ").skipNulls().join(types));
			}
			else
			{
				biomesCfg.addCustomCategoryComment(name, biome.biomeName);
			}

			biomesCfg.setCategoryPropertyOrder(name, propOrder);

			if (weight > 0)
			{
				CaveworldAPI.addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(terrainBlock, terrainMeta), new BlockEntry(topBlock, topMeta)));
			}
		}

		if (biomesCfg.hasChanged())
		{
			biomesCfg.save();
		}
	}

	public static void syncBiomesCavernCfg()
	{
		String category = "biomes";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (biomesCavernCfg == null)
		{
			biomesCavernCfg = loadConfig("cavern", category);
		}
		else
		{
			CaveworldAPI.clearCavernBiomes();
		}

		String name, terrainBlock, topBlock;
		int weight, terrainMeta, topMeta;

		for (BiomeGenBase biome : CaveUtils.getBiomes())
		{
			name = Integer.toString(biome.biomeID);

			if (CaveBiomeManager.presets.containsKey(biome))
			{
				ICaveBiome entry = CaveBiomeManager.presets.get(biome);

				weight = entry.getGenWeight();
				terrainBlock = GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock());
				terrainMeta = entry.getTerrainBlock().getMetadata();
				topBlock = GameData.getBlockRegistry().getNameForObject(entry.getTopBlock().getBlock());
				topMeta = entry.getTopBlock().getMetadata();
			}
			else
			{
				weight = 0;
				terrainBlock = GameData.getBlockRegistry().getNameForObject(Blocks.stone);
				terrainMeta = 0;
				topBlock = terrainBlock;
				topMeta = terrainMeta;
			}

			propOrder = Lists.newArrayList();
			prop = biomesCavernCfg.get(name, "genWeight", weight);
			prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesCavernCfg.get(name, "terrainBlock", terrainBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			terrainBlock = prop.getString();
			if (!GameData.getBlockRegistry().containsKey(terrainBlock)) prop.setToDefault();
			prop = biomesCavernCfg.get(name, "terrainBlockMetadata", terrainMeta);
			prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			terrainMeta = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesCavernCfg.get(name, "topBlock", topBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			topBlock = prop.getString();
			if (!GameData.getBlockRegistry().containsKey(topBlock)) prop.setToDefault();
			prop = biomesCavernCfg.get(name, "topBlockMetadata", topMeta);
			prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			topMeta = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

			if (BiomeDictionary.isBiomeRegistered(biome))
			{
				Set<String> types = Sets.newTreeSet();

				for (Type type : BiomeDictionary.getTypesForBiome(biome))
				{
					types.add(type.name());
				}

				biomesCavernCfg.addCustomCategoryComment(name, biome.biomeName + ": " + Joiner.on(", ").skipNulls().join(types));
			}
			else
			{
				biomesCavernCfg.addCustomCategoryComment(name, biome.biomeName);
			}

			biomesCavernCfg.setCategoryPropertyOrder(name, propOrder);

			if (weight > 0)
			{
				CaveworldAPI.addCavernBiome(new CaveBiome(biome, weight, new BlockEntry(terrainBlock, terrainMeta), new BlockEntry(topBlock, topMeta)));
			}
		}

		if (biomesCavernCfg.hasChanged())
		{
			biomesCavernCfg.save();
		}
	}

	public static void syncVeinsCfg()
	{
		if (veinsCfg == null)
		{
			veinsCfg = loadConfig("veins");
		}
		else
		{
			CaveworldAPI.clearCaveVeins();
		}

		if (veinsCfg.getCategoryNames().isEmpty())
		{
			List<ICaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 5, 8, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 20, 1, 8, 128, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 1), 2, 4, 100, 150, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 17, 20, 100, 0, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 85, 1, 10, 0, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 18, 20, 100, 200, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 10, 28, 100, 0, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 50, 1, 10, 0, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 12, 25, 100, 200, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 3, 100, 0, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 24, 1, 8, 0, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 7, 8, 100, 0, 40));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 30, 1, 8, 0, 40));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 5, 2, 100, 0, 50));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 18, 1, 8, 0, 50));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 100, 0, 20));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 15, 1, 5, 0, 20));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 3, 100, 50, 255, null, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 16, 1, 6, 50, 255, null, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockEntry(Blocks.quartz_ore, 0), 10, 16, 100, 0, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.dirt, 0), 25, 20, 100, 0, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gravel, 0), 20, 6, 100, 0, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 100, 0, 255, null, Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 100, 0, 20, new BlockEntry(Blocks.gravel, 0), Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.soul_sand, 0), 20, 10, 100, 0, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));

			List<ICaveVein> entries = Lists.newArrayList(veins);
			Set<BlockEntry> temp = Sets.newHashSet();

			for (ICaveVein entry : entries)
			{
				if (!temp.contains(entry.getBlock()))
				{
					ICaveVein vein = new CaveVein(entry);
					vein.setGenWeight(vein.getGenWeight() * 2);
					vein.setGenRate(Math.min(vein.getGenRate() * 2, 100));
					vein.setGenMinHeight(0);
					vein.setGenMaxHeight(15);
					vein.setGenBiomes(null);

					veins.add(vein);

					vein = new CaveVein(entry);
					vein.setGenBlockCount(vein.getGenBlockCount() * 2);
					vein.setGenMinHeight(150);
					vein.setGenMaxHeight(255);
					vein.setGenBiomes(null);

					veins.add(vein);
				}
			}

			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 1), 24, 20, 100, 0, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 12), 24, 14, 100, 0, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 0), 10, 15, 100, 0, 50));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 5, 20, 100, 50, 150));

			for (ICaveVein entry : veins)
			{
				CaveworldAPI.addCaveVein(entry);
			}
		}
		else
		{
			int i = 0;

			for (String name : veinsCfg.getCategoryNames())
			{
				if (NumberUtils.isNumber(name))
				{
					CaveworldAPI.addCaveVein(null);
				}
				else ++i;
			}

			if (i > 0)
			{
				try
				{
					FileUtils.forceDelete(new File(veinsCfg.toString()));

					CaveworldAPI.clearCaveVeins();

					veinsCfg = null;
					syncVeinsCfg();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		if (veinsCfg.hasChanged())
		{
			veinsCfg.save();
		}
	}

	public static void syncVeinsCavernCfg()
	{
		if (veinsCavernCfg == null)
		{
			veinsCavernCfg = loadConfig("cavern", "veins");
		}
		else
		{
			CaveworldAPI.clearCavernVeins();
		}

		if (veinsCavernCfg.getCategoryNames().isEmpty())
		{
			List<ICaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 5, 8, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 17, 20, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 10, 28, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 3, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 7, 8, 100, 1, 40));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 5, 2, 100, 1, 50));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 100, 1, 20));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 3, 100, 50, 127, null, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockEntry(Blocks.quartz_ore, 0), 10, 16, 100, 1, 127, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.dirt, 0), 25, 20, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gravel, 0), 20, 6, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 100, 1, 127, null, Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 100, 1, 127, new BlockEntry(Blocks.gravel, 0), Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.soul_sand, 0), 20, 10, 100, 1, 127, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 1), 24, 20, 100, 1, 127, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 12), 24, 14, 100, 1, 127, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 0), 10, 15, 100, 100, 127));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 5, 20, 100, 1, 127));

			for (ICaveVein entry : veins)
			{
				CaveworldAPI.addCavernVein(entry);
			}
		}
		else
		{
			int i = 0;

			for (String name : veinsCavernCfg.getCategoryNames())
			{
				if (NumberUtils.isNumber(name))
				{
					CaveworldAPI.addCavernVein(null);
				}
				else ++i;
			}

			if (i > 0)
			{
				try
				{
					FileUtils.forceDelete(new File(veinsCavernCfg.toString()));

					CaveworldAPI.clearCavernVeins();

					veinsCavernCfg = null;
					syncVeinsCavernCfg();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		if (veinsCavernCfg.hasChanged())
		{
			veinsCavernCfg.save();
		}
	}
}