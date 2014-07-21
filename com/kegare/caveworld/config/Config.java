/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.config;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.config.CaveGuiFactory.CaveConfigGui.VeinsEntry.VeinConfigEntry;
import com.kegare.caveworld.config.impl.NumberSliderEntry;
import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.core.CaveOreManager;
import com.kegare.caveworld.core.CaveOreManager.CaveOre;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.BlockEntry;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.Version;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;

public class Config
{
	private static final Side side = FMLCommonHandler.instance().getSide();

	public static Configuration generalCfg;
	public static Configuration blocksCfg;
	public static Configuration dimensionCfg;
	public static Configuration biomesCfg;
	public static Configuration veinsCfg;

	private static final Map<Integer, Integer> biomesDefaultMap = Maps.newHashMap();

	public static boolean versionNotify;
	public static boolean deathLoseMiningCount;

	public static boolean portalCraftRecipe;
	public static boolean mossStoneCraftRecipe;

	public static boolean hardcore;

	public static boolean rope;

	public static int dimensionCaveworld;
	public static int subsurfaceHeight;
	public static boolean generateCaves;
	public static boolean generateRavine;
	public static boolean generateMineshaft;
	public static boolean generateStronghold;
	public static boolean generateLakes;
	public static boolean generateDungeons;
	public static boolean decorateVines;

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

	public static Configuration loadConfig(String name)
	{
		File file = getConfigFile(name);
		Configuration config = new Configuration(file, null, true);

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

	public static final String LANG_KEY = "caveworld.configgui.";

	public static void syncConfig()
	{
		syncGeneralCfg();
		syncBlocksCfg();
		syncDimensionCfg();
	}

	public static void syncPostConfig()
	{
		syncBiomesCfg();
		syncVeinsCfg();
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
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, does not have to match client-side and server-side.";
		propOrder.add(prop.getName());
		versionNotify = prop.getBoolean(versionNotify);
		prop = generalCfg.get(category, "deathLoseMiningCount", false);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		deathLoseMiningCount = prop.getBoolean(deathLoseMiningCount);

		generalCfg.setCategoryPropertyOrder(category, propOrder);

		category = "recipes";
		prop = generalCfg.get(category, "portalCraftRecipe", Version.DEV_DEBUG);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		portalCraftRecipe = prop.getBoolean(portalCraftRecipe);
		prop = generalCfg.get(category, "mossStoneCraftRecipe", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		mossStoneCraftRecipe = prop.getBoolean(mossStoneCraftRecipe);

		generalCfg.setCategoryPropertyOrder(category, propOrder);
		generalCfg.setCategoryRequiresMcRestart(category, true);

		category = "options";
		prop = generalCfg.get(category, "hardcore", false);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		hardcore = prop.getBoolean(hardcore);

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
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		rope = prop.getBoolean(rope);

		blocksCfg.setCategoryPropertyOrder(category, propOrder);
		blocksCfg.setCategoryRequiresMcRestart(category, true);

		if (blocksCfg.hasChanged())
		{
			blocksCfg.save();
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
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		dimensionCaveworld = prop.getInt(dimensionCaveworld);

		if (DimensionManager.isDimensionRegistered(dimensionCaveworld))
		{
			dimensionCaveworld = DimensionManager.getNextFreeDimId();

			prop.set(dimensionCaveworld);
		}

		prop = dimensionCfg.get(category, "subsurfaceHeight", 127);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		subsurfaceHeight = prop.getInt(subsurfaceHeight);
		prop = dimensionCfg.get(category, "generateCaves", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);
		prop = dimensionCfg.get(category, "generateRavine", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateRavine = prop.getBoolean(generateRavine);
		prop = dimensionCfg.get(category, "generateMineshaft", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateMineshaft = prop.getBoolean(generateMineshaft);
		prop = dimensionCfg.get(category, "generateStronghold", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateStronghold = prop.getBoolean(generateStronghold);
		prop = dimensionCfg.get(category, "generateLakes", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateLakes = prop.getBoolean(generateLakes);
		prop = dimensionCfg.get(category, "generateDungeons", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		generateDungeons = prop.getBoolean(generateDungeons);
		prop = dimensionCfg.get(category, "decorateVines", true);
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
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
		Class clazz;

		if (biomesCfg == null)
		{
			biomesCfg = loadConfig(category);
		}

		CaveBiomeManager.clearCaveBiomes();

		if (biomesDefaultMap.isEmpty())
		{
			biomesDefaultMap.put(BiomeGenBase.ocean.biomeID, 15);
			biomesDefaultMap.put(BiomeGenBase.plains.biomeID, 100);
			biomesDefaultMap.put(BiomeGenBase.desert.biomeID, 70);
			biomesDefaultMap.put(BiomeGenBase.desertHills.biomeID, 10);
			biomesDefaultMap.put(BiomeGenBase.forest.biomeID, 80);
			biomesDefaultMap.put(BiomeGenBase.forestHills.biomeID, 10);
			biomesDefaultMap.put(BiomeGenBase.taiga.biomeID, 80);
			biomesDefaultMap.put(BiomeGenBase.taigaHills.biomeID, 10);
			biomesDefaultMap.put(BiomeGenBase.jungle.biomeID, 80);
			biomesDefaultMap.put(BiomeGenBase.jungleHills.biomeID, 10);
			biomesDefaultMap.put(BiomeGenBase.swampland.biomeID, 60);
			biomesDefaultMap.put(BiomeGenBase.extremeHills.biomeID, 30);
			biomesDefaultMap.put(BiomeGenBase.icePlains.biomeID, 15);
			biomesDefaultMap.put(BiomeGenBase.iceMountains.biomeID, 15);
			biomesDefaultMap.put(BiomeGenBase.mushroomIsland.biomeID, 10);
			biomesDefaultMap.put(BiomeGenBase.savanna.biomeID, 50);
			biomesDefaultMap.put(BiomeGenBase.mesa.biomeID, 50);
		}

		String name;
		int weight;
		String block;
		int metadata;

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
		{
			if (biome == null)
			{
				continue;
			}

			propOrder.clear();
			name = String.valueOf(biome.biomeID);
			prop = biomesCfg.get(name, "genWeight", biomesDefaultMap.containsKey(biome.biomeID) ? biomesDefaultMap.get(biome.biomeID) : 0);
			clazz = side.isClient() ? NumberSliderEntry.class : null;
			prop.setMinValue(0).setMaxValue(100).setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setConfigEntryClass(clazz);
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			weight = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
			prop = biomesCfg.get(name, "terrainBlock", Block.blockRegistry.getNameForObject(Blocks.stone));
			prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			block = prop.getString();
			prop = biomesCfg.get(name, "terrainBlockMetadata", 0);
			prop.setMinValue(0).setMaxValue(15).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			metadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));

			if (BiomeDictionary.isBiomeRegistered(biome))
			{
				Set<String> types = Sets.newHashSet();

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
				CaveBiomeManager.addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(block, metadata, Blocks.stone)));
			}
		}

		if (biomesCfg.hasChanged())
		{
			biomesCfg.save();
		}
	}

	public static void syncVeinsCfg()
	{
		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();
		Class clazz;

		if (veinsCfg == null)
		{
			veinsCfg = loadConfig(category);
		}

		CaveOreManager.clearCaveOres();

		String block;
		int blockMetadata;
		int count;
		int weight;
		int min;
		int max;
		String target;
		int targetMetadata;
		int[] biomes;

		if (veinsCfg.getCategoryNames().isEmpty())
		{
			Map<String, CaveOre> ores = Maps.newHashMap();

			ores.put("Coal Ore Vein", new CaveOre(new BlockEntry(Blocks.coal_ore, 0), 16, 20, 0, 255));
			ores.put("Iron Ore Vein", new CaveOre(new BlockEntry(Blocks.iron_ore, 0), 10, 28, 0, 255));
			ores.put("Gold Ore Vein", new CaveOre(new BlockEntry(Blocks.gold_ore, 0), 8, 2, 0, 127));
			ores.put("Redstone Ore Vein", new CaveOre(new BlockEntry(Blocks.redstone_ore, 0), 7, 8, 0, 40));
			ores.put("Lapis Ore Vein", new CaveOre(new BlockEntry(Blocks.lapis_ore, 0), 5, 1, 0, 40));
			ores.put("Diamond Ore Vein", new CaveOre(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 0, 20));
			ores.put("Emerald Ore Vein", new CaveOre(new BlockEntry(Blocks.emerald_ore, 0), 5, 3, 50, 255, null, Type.MOUNTAIN, Type.HILLS));
			ores.put("Quartz Ore Vein", new CaveOre(new BlockEntry(Blocks.quartz_ore, 0), 10, 16, 0, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			ores.put("Dirt Vein", new CaveOre(new BlockEntry(Blocks.dirt, 0), 24, 18, 0, 255));
			ores.put("Gravel Vein", new CaveOre(new BlockEntry(Blocks.gravel, 0), 20, 6, 0, 255));
			ores.put("Sand Vein, 0", new CaveOre(new BlockEntry(Blocks.sand, 0), 20, 8, 0, 255, null, Type.SANDY));
			ores.put("Sand Vein, 1", new CaveOre(new BlockEntry(Blocks.sand, 0), 20, 8, 0, 20, new BlockEntry(Blocks.gravel, 0), Type.SANDY));
			ores.put("Soul Sand Vein", new CaveOre(new BlockEntry(Blocks.soul_sand, 0), 20, 10, 0, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			ores.put("Hardened Clay Vein, 0", new CaveOre(new BlockEntry(Blocks.hardened_clay, 1), 24, 20, 0, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			ores.put("Hardened Clay Vein, 1", new CaveOre(new BlockEntry(Blocks.hardened_clay, 12), 24, 14, 0, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));

			String name;
			CaveOre ore;

			for (Map.Entry<String, CaveOre> entry : ores.entrySet())
			{
				name = entry.getKey();
				ore = entry.getValue();
				block = Block.blockRegistry.getNameForObject(ore.getBlock().getBlock());
				blockMetadata = ore.getBlock().getMetadata();
				count = ore.getGenBlockCount();
				weight = ore.getGenWeight();
				min = ore.getGenMinHeight();
				max = ore.getGenMaxHeight();
				target = Block.blockRegistry.getNameForObject(ore.getGenTargetBlock().getBlock());
				targetMetadata = ore.getGenTargetBlock().getMetadata();
				biomes = ore.getGenBiomes();

				addVeinEntry(name, block, blockMetadata, count, weight, min, max, target, targetMetadata, biomes);

				CaveOreManager.addCaveOre(ore);
			}
		}
		else
		{
			for (String name : veinsCfg.getCategoryNames())
			{
				propOrder.clear();
				prop = veinsCfg.get(name, "block", Block.blockRegistry.getNameForObject(Blocks.stone));
				prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				propOrder.add(prop.getName());
				block = prop.getString();
				prop = veinsCfg.get(name, "blockMetadata", 0);
				prop.setMinValue(0).setMaxValue(15).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				blockMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
				prop = veinsCfg.get(name, "genBlockCount", 1);
				prop.setMinValue(1).setMaxValue(100).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				count = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
				prop = veinsCfg.get(name, "genWeight", 1);
				clazz = side.isClient() ? VeinConfigEntry.class : null;
				prop.setMinValue(1).setMaxValue(100).setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setConfigEntryClass(clazz);
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				weight = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
				prop = veinsCfg.get(name, "genMinHeight", 0);
				prop.setMinValue(0).setMaxValue(254).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				min = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
				prop = veinsCfg.get(name, "genMaxHeight", 255);
				prop.setMinValue(1).setMaxValue(255).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				max = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
				prop = veinsCfg.get(name, "genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone));
				prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				target = prop.getString();
				prop = veinsCfg.get(name, "genTargetBlockMetadata", 0);
				prop.setMinValue(0).setMaxValue(15).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
				propOrder.add(prop.getName());
				targetMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
				prop = veinsCfg.get(name, "genBiomes", new int[] {});
				prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
				prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
				propOrder.add(prop.getName());
				biomes = prop.getIntList();

				veinsCfg.setCategoryPropertyOrder(name, propOrder);

				CaveOreManager.addCaveOre(new CaveOre(new BlockEntry(block, blockMetadata, Blocks.stone), count, weight, min, max, new BlockEntry(target, targetMetadata, Blocks.stone), biomes));
			}
		}

		if (veinsCfg.hasChanged())
		{
			veinsCfg.save();
		}
	}

	public static void addVeinEntry(String name, String block, int blockMetadata, int count, int weight, int min, int max, String target, int targetMetadata, int[] biomes)
	{
		String category = "veins";
		List<String> propOrder = Lists.newArrayList();
		Property prop;
		Class clazz;

		prop = veinsCfg.get(name, "block", Block.blockRegistry.getNameForObject(Blocks.stone));
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.set(block);
		propOrder.add(prop.getName());
		block = prop.getString();
		prop = veinsCfg.get(name, "blockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.set(blockMetadata);
		propOrder.add(prop.getName());
		blockMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = veinsCfg.get(name, "genBlockCount", 1);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.set(count);
		propOrder.add(prop.getName());
		count = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = veinsCfg.get(name, "genWeight", 1);
		clazz = side.isClient() ? VeinConfigEntry.class : null;
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(LANG_KEY + category + '.' + prop.getName()).setConfigEntryClass(clazz);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.set(weight);
		propOrder.add(prop.getName());
		weight = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = veinsCfg.get(name, "genMinHeight", 0);
		prop.setMinValue(0).setMaxValue(254).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.set(min);
		propOrder.add(prop.getName());
		min = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = veinsCfg.get(name, "genMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.set(max);
		propOrder.add(prop.getName());
		max = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = veinsCfg.get(name, "genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone));
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.set(target);
		propOrder.add(prop.getName());
		target = prop.getString();
		prop = veinsCfg.get(name, "genTargetBlockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.set(targetMetadata);
		propOrder.add(prop.getName());
		targetMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = veinsCfg.get(name, "genBiomes", new int[] {});
		prop.setLanguageKey(LANG_KEY + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.set(biomes);
		propOrder.add(prop.getName());
		biomes = prop.getIntList();

		Config.veinsCfg.setCategoryPropertyOrder(name, propOrder);
	}
}