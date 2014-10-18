/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.io.File;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.entity.EntityCaveman;
import com.kegare.caveworld.util.CaveConfiguration;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

public class Config
{
	private static final Side side = FMLCommonHandler.instance().getSide();

	public static Configuration generalCfg;
	public static Configuration blocksCfg;
	public static Configuration itemsCfg;
	public static Configuration dimensionCfg;
	public static Configuration biomesCfg;
	public static Configuration entitiesCfg;
	public static Configuration veinsCfg;

	public static boolean versionNotify;
	public static boolean veinsAutoRegister;
	public static boolean deathLoseMiningPoint;

	public static boolean portalCraftRecipe;
	public static boolean mossStoneCraftRecipe;

	public static boolean hardcore;
	public static boolean caveborn;

	public static boolean rope;
	public static boolean oreCavenium;

	public static boolean cavenium;
	public static boolean pickaxeMining;

	public static int cavemanSpawnWeight;
	public static int cavemanSpawnMinHeight;
	public static int cavemanSpawnMaxHeight;
	public static int cavemanSpawnInChunks;
	public static int[] cavemanSpawnBiomes;
	public static int cavemanCreatureType;
	public static boolean cavemanShowHealthBar;

	public static int dimensionCaveworld;
	public static int subsurfaceHeight;
	public static boolean generateCaves;
	public static boolean generateExtremeCaves;
	public static boolean generateRavine;
	public static boolean generateExtremeRavine;
	public static boolean generateMineshaft;
	public static boolean generateStronghold;
	public static boolean generateLakes;
	public static boolean generateDungeons;
	public static boolean generateAnimalDungeons;
	public static boolean decorateVines;

	public static final int RENDER_TYPE_PORTAL = Caveworld.proxy.getUniqueRenderType();

	public static Class selectBlockEntryClass;
	public static Class selectItemEntryClass;
	public static Class selectBiomeEntryClass;

	public static File getConfigFile(String name)
	{
		File dir = new File(Loader.instance().getConfigDir(), "caveworld");

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

	public static String getConfigName(Configuration config)
	{
		String name = FilenameUtils.getBaseName(config.toString());

		if (name != null && name.startsWith("caveworld-"))
		{
			return name.substring(name.lastIndexOf('-') + 1);
		}

		return null;
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
		prop = generalCfg.get(category, "veinsAutoRegister", false);
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

	public static void syncBlocksCfg()
	{
		String category = "blocks";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (blocksCfg == null)
		{
			blocksCfg = loadConfig(category);
		}

		blocksCfg.addCustomCategoryComment(category, "If multiplayer, values must match on client-side and server-side.");

		prop = blocksCfg.get(category, "Rope", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		rope = prop.getBoolean(rope);
		prop = blocksCfg.get(category, "oreCavenium", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		oreCavenium = prop.getBoolean(oreCavenium);

		blocksCfg.setCategoryPropertyOrder(category, propOrder);
		blocksCfg.setCategoryRequiresMcRestart(category, true);

		if (blocksCfg.hasChanged())
		{
			blocksCfg.save();
		}
	}

	public static void syncItemsCfg()
	{
		String category = "items";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (itemsCfg == null)
		{
			itemsCfg = loadConfig(category);
		}

		itemsCfg.addCustomCategoryComment(category, "If multiplayer, values must match on client-side and server-side.");

		prop = itemsCfg.get(category, "cavenium", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		cavenium = prop.getBoolean(cavenium);
		prop = itemsCfg.get(category, "pickaxeMining", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		pickaxeMining = prop.getBoolean(pickaxeMining);

		itemsCfg.setCategoryPropertyOrder(category, propOrder);
		itemsCfg.setCategoryRequiresMcRestart(category, true);

		if (itemsCfg.hasChanged())
		{
			itemsCfg.save();
		}
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
		prop = entitiesCfg.get(category, "spawnWeight", 2);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		cavemanSpawnWeight = MathHelper.clamp_int(prop.getInt(cavemanSpawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMinHeight", 10);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		cavemanSpawnMinHeight = MathHelper.clamp_int(prop.getInt(cavemanSpawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		cavemanSpawnMaxHeight = MathHelper.clamp_int(prop.getInt(cavemanSpawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnInChunks", 1);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		cavemanSpawnInChunks = MathHelper.clamp_int(prop.getInt(cavemanSpawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = entitiesCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName()).setConfigEntryClass(selectBiomeEntryClass);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		cavemanSpawnBiomes = prop.getIntList();
		prop = entitiesCfg.get(category, "creatureType", 0);
		prop.setMinValue(0).setMaxValue(1).setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());;
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		cavemanCreatureType = MathHelper.clamp_int(prop.getInt(cavemanCreatureType), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		if (side.isClient())
		{
			prop = entitiesCfg.get(category, "showHealthBar", true);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + "entities.entry." + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			cavemanShowHealthBar = prop.getBoolean();
		}

		BiomeGenBase[] def = CaveUtils.getBiomes().toArray(new BiomeGenBase[0]);
		BiomeGenBase[] biomes = new BiomeGenBase[0];
		BiomeGenBase biome;

		for (int i : cavemanSpawnBiomes)
		{
			if (i >= 0 && i < BiomeGenBase.getBiomeGenArray().length)
			{
				biome = BiomeGenBase.getBiome(i);

				if (biome != null)
				{
					biomes = ArrayUtils.add(biomes, biome);
				}
			}
		}

		if (ArrayUtils.isEmpty(biomes))
		{
			biomes = def;
		}

		EntityRegistry.removeSpawn(EntityCaveman.class, EnumCreatureType.ambient, def);

		if (cavemanSpawnWeight > 0)
		{
			EntityRegistry.addSpawn(EntityCaveman.class, cavemanSpawnWeight, 1, 1, EnumCreatureType.ambient, biomes);
		}

		entitiesCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");
		entitiesCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		entitiesCfg.setCategoryPropertyOrder(category, propOrder);

		if (entitiesCfg.hasChanged())
		{
			entitiesCfg.save();
		}
	}

	public static void syncDimensionCfg()
	{
		String category = "caveworld";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (dimensionCfg == null)
		{
			dimensionCfg = loadConfig("dimension");
		}

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimensionCaveworld", -5);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		dimensionCaveworld = prop.getInt(dimensionCaveworld);

		if (dimensionCaveworld == 0)
		{
			prop.set(DimensionManager.getNextFreeDimId());

			dimensionCaveworld = prop.getInt();
		}

		prop = dimensionCfg.get(category, "subsurfaceHeight", 255);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		subsurfaceHeight = MathHelper.clamp_int(prop.getInt(subsurfaceHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = dimensionCfg.get(category, "generateCaves", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);
		prop = dimensionCfg.get(category, "generateExtremeCaves", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateExtremeCaves = prop.getBoolean(generateExtremeCaves);
		prop = dimensionCfg.get(category, "generateRavine", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateRavine = prop.getBoolean(generateRavine);
		prop = dimensionCfg.get(category, "generateExtremeRavine", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateExtremeRavine = prop.getBoolean(generateExtremeRavine);
		prop = dimensionCfg.get(category, "generateMineshaft", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateMineshaft = prop.getBoolean(generateMineshaft);
		prop = dimensionCfg.get(category, "generateStronghold", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateStronghold = prop.getBoolean(generateStronghold);
		prop = dimensionCfg.get(category, "generateLakes", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateLakes = prop.getBoolean(generateLakes);
		prop = dimensionCfg.get(category, "generateDungeons", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateDungeons = prop.getBoolean(generateDungeons);
		prop = dimensionCfg.get(category, "generateAnimalDungeons", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateAnimalDungeons = prop.getBoolean(generateAnimalDungeons);
		prop = dimensionCfg.get(category, "decorateVines", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		decorateVines = prop.getBoolean(decorateVines);

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

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
		{
			if (biome == null)
			{
				continue;
			}

			name = Integer.toString(biome.biomeID);

			if (CaveBiomeManager.defaultMapping.containsKey(biome))
			{
				ICaveBiome entry = CaveBiomeManager.defaultMapping.get(biome);

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

			propOrder.clear();
			prop = biomesCfg.get(name, "genWeight", weight);
			prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesCfg.get(name, "terrainBlock", terrainBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectBlockEntryClass);
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
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectBlockEntryClass);
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
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 4, 7, 128, 255));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 1), 1, 3, 150, 255));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 17, 20, 0, 255));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 10, 28, 0, 255));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 3, 0, 127));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 7, 8, 0, 40));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 5, 2, 0, 50));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 0, 20));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 3, 50, 255, null, Type.MOUNTAIN, Type.HILLS));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.quartz_ore, 0), 10, 16, 0, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.dirt, 0), 25, 20, 0, 255));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.gravel, 0), 20, 6, 0, 255));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 0, 255, null, Type.SANDY));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 0, 20, new BlockEntry(Blocks.gravel, 0), Type.SANDY));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.soul_sand, 0), 20, 10, 0, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.hardened_clay, 1), 24, 20, 0, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(Blocks.hardened_clay, 12), 24, 14, 0, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
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
}