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
import caveworld.entity.EntityCavenicCreeper;
import caveworld.entity.EntityCavenicSkeleton;
import caveworld.entity.EntityCavenicSpider;
import caveworld.entity.EntityCavenicZombie;
import caveworld.item.CaveItems;
import caveworld.plugin.CavePlugins;
import caveworld.plugin.ICavePlugin;
import caveworld.util.CaveConfiguration;
import caveworld.util.CaveLog;
import caveworld.util.CaveUtils;
import caveworld.world.ChunkProviderAquaCavern;
import caveworld.world.ChunkProviderCaveland;
import caveworld.world.ChunkProviderCavenia;
import caveworld.world.ChunkProviderCavern;
import caveworld.world.ChunkProviderCaveworld;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCaveland;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.EnumHelper;

public class Config
{
	private static final Side side = FMLCommonHandler.instance().getSide();

	public static Configuration generalCfg;
	public static Configuration mobsCfg;
	public static Configuration dimensionCfg;
	public static Configuration biomesCfg;
	public static Configuration biomesCavernCfg;
	public static Configuration biomesAquaCavernCfg;
	public static Configuration veinsCfg;
	public static Configuration veinsCavernCfg;
	public static Configuration veinsAquaCavernCfg;
	public static Configuration pluginsCfg;
	public static Configuration serverCfg;

	public static float caveMusicVolume;
	public static boolean versionNotify;
	public static boolean veinsAutoRegister;
	public static boolean deathLoseMiningPoint;
	public static int miningPointRenderType;
	public static boolean showMinerRank;
	public static String[] miningPoints;
	public static String[] miningPointValidItems;
	public static String[] randomiteDrops;
	public static int[] randomitePotions;
	public static boolean oreRenderOverlay;
	public static boolean fakeMiningPickaxe;
	public static boolean fakeLumberingAxe;
	public static boolean fakeDiggingShovel;
	public static boolean fakeFarmingHoe;
	public static int modeDisplayTime;
	public static int quickBreakLimit;
	public static boolean portalCache;

	public static boolean mossStoneCraftRecipe;

	public static boolean hardcore;
	public static int caveborn;
	public static String[] cavebornItems;

	public static boolean cauldron;
	public static boolean remoteConfig;

	public static Class<? extends IConfigEntry> selectItems;
	public static Class<? extends IConfigEntry> selectItemsWithBlocks;
	public static Class<? extends IConfigEntry> selectBiomes;
	public static Class<? extends IConfigEntry> selectMobs;
	public static Class<? extends IConfigEntry> selectPotions;
	public static Class<? extends IConfigEntry> cycleInteger;
	public static Class<? extends IConfigEntry> pointsEntry;

	@SideOnly(Side.CLIENT)
	public static KeyBinding keyBindAtCommand;

	public static final int RENDER_TYPE_PORTAL = Caveworld.proxy.getUniqueRenderType();
	public static final int RENDER_TYPE_CHEST = Caveworld.proxy.getUniqueRenderType();
	public static final int RENDER_TYPE_OVERLAY = Caveworld.proxy.getUniqueRenderType();

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

	public static void saveConfig(final Configuration config)
	{
		if (config.hasChanged())
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					config.save();
				}
			});
		}
	}

	public static void saveAllConfigs()
	{
		saveConfig(generalCfg);
		saveConfig(mobsCfg);
		saveConfig(dimensionCfg);
		saveConfig(biomesCfg);
		saveConfig(biomesCavernCfg);
		saveConfig(biomesAquaCavernCfg);
		saveConfig(veinsCfg);
		saveConfig(veinsCavernCfg);
		saveConfig(veinsAquaCavernCfg);
		saveConfig(pluginsCfg);
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

		if (side.isClient())
		{
			prop = generalCfg.get(category, "caveMusicVolume", Float.toString(0.5F));
			prop.setMinValue(0.0F).setMaxValue(1.0F).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			caveMusicVolume = Float.parseFloat(prop.getString()) < 0.0F ? 0.0F : Float.parseFloat(prop.getString()) > 1.0F ? 1.0F : Float.parseFloat(prop.getString());
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

		prop = generalCfg.get(category, "showMinerRank", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, does not have to match client-side and server-side.";
		propOrder.add(prop.getName());
		showMinerRank = prop.getBoolean(showMinerRank);
		prop = generalCfg.get(category, "miningPoints", new String[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(pointsEntry);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		miningPoints = prop.getStringList();
		prop = generalCfg.get(category, "miningPointValidItems", new String[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectItems);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		miningPointValidItems = prop.getStringList();
		prop = generalCfg.get(category, "randomiteDrops", new String[0]);
		prop.setMaxListLength(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectItemsWithBlocks);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		randomiteDrops = prop.getStringList();
		prop = generalCfg.get(category, "randomitePotions", new int[0]);
		prop.setMaxListLength(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectPotions);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		randomitePotions = prop.getIntList();

		if (side.isClient())
		{
			prop = generalCfg.get(category, "oreRenderOverlay", false);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			oreRenderOverlay = prop.getBoolean(oreRenderOverlay);
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
			prop = generalCfg.get(category, "fakeFarmingHoe", false);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			fakeFarmingHoe = prop.getBoolean(fakeFarmingHoe);
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
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		quickBreakLimit = MathHelper.clamp_int(prop.getInt(quickBreakLimit), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = generalCfg.get(category, "portalCache", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		portalCache = prop.getBoolean(portalCache);

		generalCfg.setCategoryPropertyOrder(category, propOrder);

		category = "recipes";
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
		prop = generalCfg.get(category, "caveborn", 0);
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

		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		caveborn = MathHelper.clamp_int(prop.getInt(caveborn), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		boolean flag = !generalCfg.getCategory(category).containsKey("cavebornItems");
		prop = generalCfg.get(category, "cavebornItems", new String[0]);
		prop.setMaxListLength(36).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(selectItemsWithBlocks);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		cavebornItems = prop.getStringList();

		if (flag)
		{
			List<ItemStack> items = Lists.newArrayList();

			items.add(new ItemStack(Items.stone_pickaxe));
			items.add(new ItemStack(Items.stone_sword));
			items.add(new ItemStack(Blocks.torch));
			items.add(new ItemStack(Items.bread));

			for (int i = 0; i < 3; ++i)
			{
				items.add(new ItemStack(CaveBlocks.perverted_sapling, 1, i));
			}

			items.add(new ItemStack(Blocks.crafting_table));
			items.add(new ItemStack(Blocks.dirt));
			items.add(new ItemStack(CaveItems.acresia));

			cavebornItems = ConfigHelper.getStringsFromItems(items);
			prop.set(cavebornItems);
		}

		generalCfg.setCategoryPropertyOrder(category, propOrder);

		saveConfig(generalCfg);
	}

	public static void syncMobsCfg()
	{
		String category = "mobs";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (mobsCfg == null)
		{
			mobsCfg = loadConfig(category);
		}

		category = "Caveman";
		prop = mobsCfg.get(category, "spawnWeight", 100);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMinHeight", 1);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnInChunks", 2);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCaveman.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCaveman.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCaveman.spawnBiomes = prop.getIntList();
		EntityCaveman.refreshSpawn();
		prop = mobsCfg.get(category, "canDespawn", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCaveman.despawn = prop.getBoolean(EntityCaveman.despawn);

		mobsCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		mobsCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "ArcherZombie";
		prop = mobsCfg.get(category, "spawnWeight", 100);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMinHeight", 1);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnInChunks", 4);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityArcherZombie.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityArcherZombie.spawnBiomes = prop.getIntList();
		EntityArcherZombie.refreshSpawn();
		prop = mobsCfg.get(category, "canDespawn", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityArcherZombie.despawn = prop.getBoolean(EntityArcherZombie.despawn);

		mobsCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		mobsCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "CavenicSkeleton";
		prop = mobsCfg.get(category, "spawnWeight", 70);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMinHeight", 1);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnInChunks", 3);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCavenicSkeleton.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.spawnBiomes = prop.getIntList();
		EntityCavenicSkeleton.refreshSpawn();
		prop = mobsCfg.get(category, "canDespawn", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicSkeleton.despawn = prop.getBoolean(EntityCavenicSkeleton.despawn);

		mobsCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		mobsCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "CavenicCreeper";
		prop = mobsCfg.get(category, "spawnWeight", 70);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicCreeper.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCavenicCreeper.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMinHeight", 1);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicCreeper.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicCreeper.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicCreeper.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicCreeper.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnInChunks", 3);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicCreeper.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCavenicCreeper.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicCreeper.spawnBiomes = prop.getIntList();
		EntityCavenicCreeper.refreshSpawn();
		prop = mobsCfg.get(category, "canDespawn", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicCreeper.despawn = prop.getBoolean(EntityCavenicCreeper.despawn);

		mobsCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		mobsCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "CavenicZombie";
		prop = mobsCfg.get(category, "spawnWeight", 70);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicZombie.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCavenicZombie.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMinHeight", 1);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicZombie.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicZombie.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicZombie.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicZombie.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnInChunks", 3);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicZombie.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCavenicZombie.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicZombie.spawnBiomes = prop.getIntList();
		EntityCavenicZombie.refreshSpawn();
		prop = mobsCfg.get(category, "canDespawn", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicZombie.despawn = prop.getBoolean(EntityCavenicZombie.despawn);

		mobsCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		mobsCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "CavenicSpider";
		prop = mobsCfg.get(category, "spawnWeight", 70);
		prop.setMinValue(0).setMaxValue(1000).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSpider.spawnWeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSpider.spawnWeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMinHeight", 1);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSpider.spawnMinHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSpider.spawnMinHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSpider.spawnMaxHeight = MathHelper.clamp_int(prop.getInt(EntityCavenicSpider.spawnMaxHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnInChunks", 3);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		EntityCavenicSpider.spawnInChunks = MathHelper.clamp_int(prop.getInt(EntityCavenicSpider.spawnInChunks), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = mobsCfg.get(category, "spawnBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName()).setConfigEntryClass(selectBiomes);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicSpider.spawnBiomes = prop.getIntList();
		EntityCavenicSpider.refreshSpawn();
		prop = mobsCfg.get(category, "canDespawn", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "mobs.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		EntityCavenicSpider.despawn = prop.getBoolean(EntityCavenicSpider.despawn);

		mobsCfg.setCategoryLanguageKey(category, Caveworld.CONFIG_LANG + category);
		mobsCfg.setCategoryPropertyOrder(category, propOrder);

		saveConfig(mobsCfg);
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

		prop = dimensionCfg.get(category, "dimension", -30);
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

		prop = dimensionCfg.get(category, "subsurfaceHeight", 200);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.subsurfaceHeight = MathHelper.clamp_int(prop.getInt(ChunkProviderCaveworld.subsurfaceHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		WorldProviderCaveworld.saveHandler.setSubsurfaceHeight(ChunkProviderCaveworld.subsurfaceHeight);
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

		Class[] classes =
		{
			EntityZombie.class, EntitySkeleton.class, EntitySpider.class, EntityCaveSpider.class,
			EntityCreeper.class, EntityEnderman.class, EntitySilverfish.class, EntityBat.class, EntitySnowman.class,
			EntityArcherZombie.class, EntityCavenicSkeleton.class, EntityCavenicCreeper.class, EntityCavenicZombie.class, EntityCavenicSpider.class
		};

		Set<String> mobs = Sets.newTreeSet();

		for (Class clazz : classes)
		{
			mobs.add((String)EntityList.classToStringMapping.get(clazz));
		}

		prop = dimensionCfg.get(category, "spawnerMobs", mobs.toArray(new String[mobs.size()]));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setConfigEntryClass(selectMobs);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.spawnerMobs = prop.getStringList();
		prop = dimensionCfg.get(category, "caveMonsterSpawn", 300);
		prop.setMinValue(0).setMaxValue(5000).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.caveMonsterSpawn = MathHelper.clamp_int(prop.getInt(ChunkProviderCaveworld.caveMonsterSpawn), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		ChunkProviderCaveworld.caveMonster = EnumHelper.addCreatureType("CaveworldMonster", IMob.class, ChunkProviderCaveworld.caveMonsterSpawn, Material.air, false, false);
		prop = dimensionCfg.get(category, "caveBrightness", 0.005D);
		prop.setMinValue(0.0D).setMaxValue(1.0D).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCaveworld.caveBrightness = (float)MathHelper.clamp_double(prop.getDouble(ChunkProviderCaveworld.caveBrightness), Float.parseFloat(prop.getMinValue()), Float.parseFloat(prop.getMaxValue()));

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "Cavern";

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimension", -31);
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
		WorldProviderCavern.saveHandler.setSubsurfaceHeight(ChunkProviderCavern.subsurfaceHeight);
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
		prop = dimensionCfg.get(category, "caveMonsterSpawn", 100);
		prop.setMinValue(0).setMaxValue(5000).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCavern.caveMonsterSpawn = MathHelper.clamp_int(prop.getInt(ChunkProviderCavern.caveMonsterSpawn), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		ChunkProviderCavern.caveMonster = EnumHelper.addCreatureType("CavernMonster", IMob.class, ChunkProviderCavern.caveMonsterSpawn, Material.air, false, false);
		prop = dimensionCfg.get(category, "caveBrightness", 0.005D);
		prop.setMinValue(0.0D).setMaxValue(1.0D).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCavern.caveBrightness = (float)MathHelper.clamp_double(prop.getDouble(ChunkProviderCavern.caveBrightness), Float.parseFloat(prop.getMinValue()), Float.parseFloat(prop.getMaxValue()));

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "Aqua Cavern";

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimension", -32);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderAquaCavern.dimensionId = prop.getInt(ChunkProviderAquaCavern.dimensionId);

		if (ChunkProviderAquaCavern.dimensionId == 0)
		{
			prop.set(DimensionManager.getNextFreeDimId());

			ChunkProviderAquaCavern.dimensionId = prop.getInt();
		}

		prop = dimensionCfg.get(category, "subsurfaceHeight", 127);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderAquaCavern.subsurfaceHeight = MathHelper.clamp_int(prop.getInt(ChunkProviderAquaCavern.subsurfaceHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		WorldProviderAquaCavern.saveHandler.setSubsurfaceHeight(ChunkProviderAquaCavern.subsurfaceHeight);
		prop = dimensionCfg.get(category, "generateRavine", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderAquaCavern.generateRavine = prop.getBoolean(ChunkProviderAquaCavern.generateRavine);
		prop = dimensionCfg.get(category, "caveMonsterSpawn", 100);
		prop.setMinValue(0).setMaxValue(5000).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderAquaCavern.caveMonsterSpawn = MathHelper.clamp_int(prop.getInt(ChunkProviderAquaCavern.caveMonsterSpawn), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		ChunkProviderAquaCavern.caveMonster = EnumHelper.addCreatureType("AquaCavernMonster", IMob.class, ChunkProviderAquaCavern.caveMonsterSpawn, Material.air, false, false);
		prop = dimensionCfg.get(category, "caveBrightness", 0.05D);
		prop.setMinValue(0.0D).setMaxValue(1.0D).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderAquaCavern.caveBrightness = (float)MathHelper.clamp_double(prop.getDouble(ChunkProviderAquaCavern.caveBrightness), Float.parseFloat(prop.getMinValue()), Float.parseFloat(prop.getMaxValue()));

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "Caveland";

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimension", -33);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveland.dimensionId = prop.getInt(ChunkProviderCaveland.dimensionId);

		if (ChunkProviderCaveland.dimensionId == 0)
		{
			prop.set(DimensionManager.getNextFreeDimId());

			ChunkProviderCaveland.dimensionId = prop.getInt();
		}

		prop = dimensionCfg.get(category, "subsurfaceHeight", 127);
		prop.setMinValue(63).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveland.subsurfaceHeight = MathHelper.clamp_int(prop.getInt(ChunkProviderCaveland.subsurfaceHeight), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		WorldProviderCaveland.saveHandler.setSubsurfaceHeight(ChunkProviderCaveland.subsurfaceHeight);
		prop = dimensionCfg.get(category, "generateLakes", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveland.generateLakes = prop.getBoolean(ChunkProviderCaveland.generateLakes);
		prop = dimensionCfg.get(category, "generateAnimalDungeons", true);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCaveland.generateAnimalDungeons = prop.getBoolean(ChunkProviderCaveland.generateAnimalDungeons);
		prop = dimensionCfg.get(category, "caveMonsterSpawn", 150);
		prop.setMinValue(0).setMaxValue(5000).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCaveland.caveMonsterSpawn = MathHelper.clamp_int(prop.getInt(ChunkProviderCaveland.caveMonsterSpawn), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		ChunkProviderCaveland.caveMonster = EnumHelper.addCreatureType("CavelandMonster", IMob.class, ChunkProviderCaveland.caveMonsterSpawn, Material.air, false, false);
		prop = dimensionCfg.get(category, "caveBrightness", 0.075D);
		prop.setMinValue(0.0D).setMaxValue(1.0D).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCaveland.caveBrightness = (float)MathHelper.clamp_double(prop.getDouble(ChunkProviderCaveland.caveBrightness), Float.parseFloat(prop.getMinValue()), Float.parseFloat(prop.getMaxValue()));

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		propOrder = Lists.newArrayList();
		category = "Cavenia";

		dimensionCfg.addCustomCategoryComment(category, "If multiplayer, server-side only.");

		prop = dimensionCfg.get(category, "dimension", -34);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		propOrder.add(prop.getName());
		ChunkProviderCavenia.dimensionId = prop.getInt(ChunkProviderCavenia.dimensionId);

		if (ChunkProviderCavenia.dimensionId == 0)
		{
			prop.set(DimensionManager.getNextFreeDimId());

			ChunkProviderCavenia.dimensionId = prop.getInt();
		}

		prop = dimensionCfg.get(category, "caveMonsterSpawn", 100);
		prop.setMinValue(0).setMaxValue(5000).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName()).setRequiresMcRestart(true);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCavenia.caveMonsterSpawn = MathHelper.clamp_int(prop.getInt(ChunkProviderCavenia.caveMonsterSpawn), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		ChunkProviderCavenia.caveMonster = EnumHelper.addCreatureType("CaveniaMonster", IMob.class, ChunkProviderCavenia.caveMonsterSpawn, Material.air, false, false);
		prop = dimensionCfg.get(category, "caveBrightness", 0.125D);
		prop.setMinValue(0.0D).setMaxValue(1.0D).setLanguageKey(Caveworld.CONFIG_LANG + "dimension.entry." + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.comment += Configuration.NEW_LINE;
		prop.comment += "Note: If multiplayer, server-side only.";
		propOrder.add(prop.getName());
		ChunkProviderCavenia.caveBrightness = (float)MathHelper.clamp_double(prop.getDouble(ChunkProviderCavenia.caveBrightness), Float.parseFloat(prop.getMinValue()), Float.parseFloat(prop.getMaxValue()));

		dimensionCfg.setCategoryPropertyOrder(category, propOrder);

		saveConfig(dimensionCfg);
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

		saveConfig(biomesCfg);
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

		saveConfig(biomesCavernCfg);
	}

	public static void syncBiomesAquaCavernCfg()
	{
		String category = "biomes";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (biomesAquaCavernCfg == null)
		{
			biomesAquaCavernCfg = loadConfig("aquacavern", category);
		}
		else
		{
			CaveworldAPI.clearAquaCavernBiomes();
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
			prop = biomesAquaCavernCfg.get(name, "genWeight", weight);
			prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesAquaCavernCfg.get(name, "terrainBlock", terrainBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			terrainBlock = prop.getString();
			if (!GameData.getBlockRegistry().containsKey(terrainBlock)) prop.setToDefault();
			prop = biomesAquaCavernCfg.get(name, "terrainBlockMetadata", terrainMeta);
			prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			terrainMeta = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
			prop = biomesAquaCavernCfg.get(name, "topBlock", topBlock);
			prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
			prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
			prop.comment += " [default: " + prop.getDefault() + "]";
			propOrder.add(prop.getName());
			topBlock = prop.getString();
			if (!GameData.getBlockRegistry().containsKey(topBlock)) prop.setToDefault();
			prop = biomesAquaCavernCfg.get(name, "topBlockMetadata", topMeta);
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

				biomesAquaCavernCfg.addCustomCategoryComment(name, biome.biomeName + ": " + Joiner.on(", ").skipNulls().join(types));
			}
			else
			{
				biomesAquaCavernCfg.addCustomCategoryComment(name, biome.biomeName);
			}

			biomesAquaCavernCfg.setCategoryPropertyOrder(name, propOrder);

			if (weight > 0)
			{
				CaveworldAPI.addAquaCavernBiome(new CaveBiome(biome, weight, new BlockEntry(terrainBlock, terrainMeta), new BlockEntry(topBlock, topMeta)));
			}
		}

		saveConfig(biomesAquaCavernCfg);
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

			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 5, 12, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 5, 10, 100, 64, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 20, 1, 8, 64, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 1), 3, 8, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 1), 2, 6, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 16, 15, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 13, 25, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 85, 1, 10, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 15, 25, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 12, 40, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 10, 32, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 50, 1, 10, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 12, 35, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 6, 30, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 8, 20, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 15, 8, 8, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 8, 15, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 12, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 6, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 24, 1, 8, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 6, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 6, 20, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 7, 12, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 30, 1, 8, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 4, 10, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 5, 4, 100, 1, 70));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 18, 1, 8, 1, 70));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 5, 2, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 100, 1, 25));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 18, 1, 5, 1, 25));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 3, 3, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 5, 3, 100, 1, 25));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 10, 1, 5, 1, 25));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 5, 2, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 0), 8, 35, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 0), 10, 15, 100, 1, 63));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 7, 18, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 5, 22, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 7), 1, 3, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 6, 12, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 6, 100, 50, 255, null, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 16, 1, 6, 50, 255, null, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 5, 100, 128, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.quartz_ore, 0), 10, 16, 100, 1, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.dirt, 0), 28, 35, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.dirt, 0), 25, 24, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gravel, 0), 23, 10, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.gravel, 0), 20, 8, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.clay, 0), 12, 20, 100, 1, 15));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 18, 100, 1, 255, null, Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.soul_sand, 0), 20, 10, 100, 1, 255, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 1), 24, 25, 100, 1, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 12), 24, 20, 100, 1, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));

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

		saveConfig(veinsCfg);
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
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 8, 20, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 3, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 7, 8, 100, 1, 40));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 5, 2, 100, 1, 50));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 1, 100, 1, 20));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 5, 2, 100, 1, 20));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 0), 10, 15, 100, 100, 127));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 5, 20, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 3, 100, 50, 127, null, Type.MOUNTAIN, Type.HILLS));
			veins.add(new CaveVein(new BlockEntry(Blocks.quartz_ore, 0), 10, 16, 100, 1, 127, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.dirt, 0), 25, 20, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.gravel, 0), 20, 6, 100, 1, 127));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 100, 1, 127, null, Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 8, 100, 1, 127, new BlockEntry(Blocks.gravel, 0), Type.SANDY));
			veins.add(new CaveVein(new BlockEntry(Blocks.soul_sand, 0), 20, 10, 100, 1, 127, new BlockEntry(Blocks.netherrack, 0), Type.NETHER));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 1), 24, 20, 100, 1, 127, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 12), 24, 14, 100, 1, 127, new BlockEntry(Blocks.dirt, 0), Type.MESA));

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

		saveConfig(veinsCavernCfg);
	}

	public static void syncVeinsAquaCavernCfg()
	{
		if (veinsAquaCavernCfg == null)
		{
			veinsAquaCavernCfg = loadConfig("aquacavern", "veins");
		}
		else
		{
			CaveworldAPI.clearAquaCavernVeins();
		}

		if (veinsAquaCavernCfg.getCategoryNames().isEmpty())
		{
			List<ICaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 5, 10, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 0), 20, 3, 8, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.cavenium_ore, 1), 2, 4, 100, 127, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 17, 30, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.coal_ore, 0), 85, 2, 10, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 10, 45, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.iron_ore, 0), 50, 2, 10, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 8, 35, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 3), 24, 10, 1, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 8, 7, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gold_ore, 0), 24, 2, 8, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 7, 18, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.redstone_ore, 0), 30, 2, 8, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 5, 10, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.lapis_ore, 0), 18, 2, 8, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 8, 3, 100, 1, 20));
			veins.add(new CaveVein(new BlockEntry(Blocks.diamond_ore, 0), 15, 2, 5, 1, 20));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 6, 4, 100, 1, 20));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 5), 12, 3, 5, 1, 20));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 0), 30, 35, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 2), 10, 32, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(CaveBlocks.gem_ore, 7), 1, 3, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 5, 7, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.emerald_ore, 0), 16, 2, 6, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.dirt, 0), 25, 20, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.gravel, 0), 20, 30, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.sand, 0), 20, 20, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.clay, 0), 20, 20, 100, 1, 255));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 1), 24, 20, 100, 1, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));
			veins.add(new CaveVein(new BlockEntry(Blocks.stained_hardened_clay, 12), 24, 14, 100, 1, 255, new BlockEntry(Blocks.dirt, 0), Type.MESA));

			for (ICaveVein entry : veins)
			{
				CaveworldAPI.addAquaCavernVein(entry);
			}
		}
		else
		{
			int i = 0;

			for (String name : veinsAquaCavernCfg.getCategoryNames())
			{
				if (NumberUtils.isNumber(name))
				{
					CaveworldAPI.addAquaCavernVein(null);
				}
				else ++i;
			}

			if (i > 0)
			{
				try
				{
					FileUtils.forceDelete(new File(veinsAquaCavernCfg.toString()));

					CaveworldAPI.clearAquaCavernVeins();

					veinsAquaCavernCfg = null;
					syncVeinsAquaCavernCfg();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		saveConfig(veinsAquaCavernCfg);
	}

	public static void syncPluginsCfg()
	{
		String category = "plugins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (pluginsCfg == null)
		{
			pluginsCfg = loadConfig(category);
		}

		for (ICavePlugin plugin : CavePlugins.PLUGINS)
		{
			if (Loader.isModLoaded(plugin.getModId()))
			{
				ModContainer mod = Loader.instance().getIndexedModList().get(plugin.getModId());
				String name = mod == null ? plugin.getModId() : mod.getName();

				prop = pluginsCfg.get(category, plugin.getModId(), true);
				prop.setLanguageKey(Caveworld.CONFIG_LANG + category + ".state");
				prop.comment = plugin.getClass().getSimpleName();
				prop.comment += " : ";
				prop.comment += StatCollector.translateToLocalFormatted(prop.getLanguageKey() + ".tooltip", name);
				propOrder.add(prop.getName());
				plugin.setPluginState(prop.getBoolean(plugin.getPluginState()));
			}
		}

		if (side.isClient())
		{
			for (ICavePlugin plugin : CavePlugins.CLIENT_PLUGINS)
			{
				if (Loader.isModLoaded(plugin.getModId()))
				{
					ModContainer mod = Loader.instance().getIndexedModList().get(plugin.getModId());
					String name = mod == null ? plugin.getModId() : mod.getName();

					prop = pluginsCfg.get(category, plugin.getModId(), true);
					prop.setLanguageKey(Caveworld.CONFIG_LANG + category + ".state");
					prop.comment = plugin.getClass().getSimpleName();
					prop.comment += " : ";
					prop.comment += StatCollector.translateToLocalFormatted(prop.getLanguageKey() + ".tooltip", name);
					propOrder.add(prop.getName());
					plugin.setPluginState(prop.getBoolean(plugin.getPluginState()));
				}
			}
		}

		pluginsCfg.setCategoryPropertyOrder(category, propOrder);
		pluginsCfg.setCategoryRequiresMcRestart(category, true);

		saveConfig(pluginsCfg);
	}

	public static void syncServerCfg()
	{
		String category = "server";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		if (serverCfg == null)
		{
			serverCfg = loadConfig(category);
		}

		prop = serverCfg.get(category, "cauldron", Loader.isModLoaded("kimagine"));
		prop.comment = "Whether the server is Cauldron.";
		propOrder.add(prop.getName());
		cauldron = prop.getBoolean(cauldron);
		prop = serverCfg.get(category, "remoteConfig", false);
		prop.comment = "Whether or not to enable remote edit config for operators.";
		propOrder.add(prop.getName());
		remoteConfig = prop.getBoolean(remoteConfig);

		saveConfig(serverCfg);
	}
}