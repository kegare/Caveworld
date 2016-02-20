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
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.block.CaveBlocks;
import caveworld.entity.CaveEntityRegistry;
import caveworld.handler.CaveAPIHandler;
import caveworld.handler.CaveEventHooks;
import caveworld.handler.CaveFuelHandler;
import caveworld.handler.CaveGuiHandler;
import caveworld.item.CaveItems;
import caveworld.item.ItemDiggingShovel;
import caveworld.item.ItemLumberingAxe;
import caveworld.item.ItemMiningPickaxe;
import caveworld.network.CaveNetworkRegistry;
import caveworld.plugin.CavePlugins;
import caveworld.recipe.RecipeCaveniumTool;
import caveworld.util.CaveLog;
import caveworld.util.CaveUtils;
import caveworld.util.SubItemHelper;
import caveworld.util.Version;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCaveland;
import caveworld.world.WorldProviderCavenia;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockClay;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid = Caveworld.MODID, guiFactory = "caveworld.client.config.CaveGuiFactory")
public class Caveworld
{
	public static final String
	MODID = "caveworld",
	CONFIG_LANG = "caveworld.config.";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@Instance(MODID)
	public static Caveworld instance;

	@SidedProxy(modId = MODID, clientSide = "caveworld.client.ClientProxy", serverSide = "caveworld.core.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabCaveworld tabCaveworld = new CreativeTabCaveworld();
	public static final CreativeTabMiningPickaxe tabMiningPickaxe = new CreativeTabMiningPickaxe();
	public static final CreativeTabLumberingAxe tabLumberingAxe = new CreativeTabLumberingAxe();
	public static final CreativeTabDiggingShovel tabDiggingShovel = new CreativeTabDiggingShovel();

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		proxy.initConfigEntries();

		CaveworldAPI.apiHandler = new CaveAPIHandler();
		CaveworldAPI.biomeManager = new CaveBiomeManager();
		CaveworldAPI.veinManager = new CaveVeinManager();
		CaveworldAPI.biomeCavernManager = new CavernBiomeManager();
		CaveworldAPI.veinCavernManager = new CavernVeinManager();
		CaveworldAPI.biomeAquaCavernManager = new AquaCavernBiomeManager();
		CaveworldAPI.veinAquaCavernManager = new AquaCavernVeinManager();
		CaveworldAPI.caverManager = new CaverManager();

		Version.versionCheck();

		RecipeSorter.register(MODID + ":cavenium_tool", RecipeCaveniumTool.class, Category.SHAPED, "after:minecraft:shaped");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CaveNetworkRegistry.registerMessages();

		Config.syncGeneralCfg();

		if (event.getSide().isServer())
		{
			Config.syncServerCfg();
		}

		CaveBlocks.registerBlocks();
		CaveItems.registerItems();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());

		proxy.registerKeyBindings();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CaveEntityRegistry.registerEntities();
		CaveEntityRegistry.addVallilaSpawns();

		proxy.registerRenderers();

		Config.syncMobsCfg();
		Config.syncDimensionCfg();

		int id = CaveworldAPI.getDimension();
		DimensionManager.registerProviderType(id, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(id, id);

		id = CaveworldAPI.getCavernDimension();
		DimensionManager.registerProviderType(id, WorldProviderCavern.class, true);
		DimensionManager.registerDimension(id, id);

		id = CaveworldAPI.getAquaCavernDimension();
		DimensionManager.registerProviderType(id, WorldProviderAquaCavern.class, true);
		DimensionManager.registerDimension(id, id);

		id = CaveworldAPI.getCavelandDimension();
		DimensionManager.registerProviderType(id, WorldProviderCaveland.class, true);
		DimensionManager.registerDimension(id, id);

		id = CaveworldAPI.getCaveniaDimension();
		DimensionManager.registerProviderType(id, WorldProviderCavenia.class, true);
		DimensionManager.registerDimension(id, id);

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new CaveGuiHandler());

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);

		CaveAchievementList.registerAchievements();
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent event)
	{
		SubItemHelper.cacheSubBlocks(event.getSide());
		SubItemHelper.cacheSubItems(event.getSide());

		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			CaveUtils.isItemPickaxe(item);
			CaveUtils.isItemAxe(item);
			CaveUtils.isItemShovel(item);
		}

		for (Block block : GameData.getBlockRegistry().typeSafeIterable())
		{
			try
			{
				List<ItemStack> list = SubItemHelper.getSubBlocks(block);

				if (list.isEmpty())
				{
					if (Strings.nullToEmpty(block.getHarvestTool(0)).equalsIgnoreCase("pickaxe") || CaveItems.mining_pickaxe.func_150897_b(block) ||
						block instanceof BlockOre || block instanceof BlockRedstoneOre || block instanceof BlockGlowstone)
					{
						ItemMiningPickaxe.breakableBlocks.addIfAbsent(new BlockEntry(block, 0));

						if (block instanceof BlockRotatedPillar)
						{
							ItemMiningPickaxe.breakableBlocks.addIfAbsent(new BlockEntry(block, 4));
							ItemMiningPickaxe.breakableBlocks.addIfAbsent(new BlockEntry(block, 8));
						}
					}

					if (CaveUtils.isWood(block, 0))
					{
						ItemLumberingAxe.breakableBlocks.addIfAbsent(new BlockEntry(block, 0));

						if (block instanceof BlockRotatedPillar)
						{
							ItemLumberingAxe.breakableBlocks.addIfAbsent(new BlockEntry(block, 4));
							ItemLumberingAxe.breakableBlocks.addIfAbsent(new BlockEntry(block, 8));
						}
					}

					if (Strings.nullToEmpty(block.getHarvestTool(0)).equalsIgnoreCase("shovel") || CaveItems.digging_shovel.func_150897_b(block) ||
						block.getMaterial() == Material.ground || block.getMaterial() == Material.grass || block.getMaterial() == Material.sand || block.getMaterial() == Material.snow || block.getMaterial() == Material.craftedSnow)
					{
						ItemDiggingShovel.breakableBlocks.addIfAbsent(new BlockEntry(block, 0));

						if (block instanceof BlockRotatedPillar)
						{
							ItemDiggingShovel.breakableBlocks.addIfAbsent(new BlockEntry(block, 4));
							ItemDiggingShovel.breakableBlocks.addIfAbsent(new BlockEntry(block, 8));
						}
					}
				}
				else for (ItemStack itemstack : list)
				{
					if (itemstack != null && itemstack.getItem() != null)
					{
						Block sub = Block.getBlockFromItem(itemstack.getItem());

						if (sub == Blocks.air)
						{
							continue;
						}

						int meta = itemstack.getItemDamage();

						if (Strings.nullToEmpty(sub.getHarvestTool(meta)).equalsIgnoreCase("pickaxe") ||
							CaveItems.mining_pickaxe.func_150897_b(sub) || sub instanceof BlockOre || sub instanceof BlockRedstoneOre || block instanceof BlockGlowstone)
						{
							ItemMiningPickaxe.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta));

							if (sub instanceof BlockRotatedPillar)
							{
								ItemMiningPickaxe.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta + 4));
								ItemMiningPickaxe.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta + 8));
							}
						}

						if (CaveUtils.isWood(sub, meta))
						{
							ItemLumberingAxe.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta));

							if (sub instanceof BlockRotatedPillar)
							{
								ItemLumberingAxe.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta + 4));
								ItemLumberingAxe.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta + 8));
							}
						}

						if (Strings.nullToEmpty(sub.getHarvestTool(meta)).equalsIgnoreCase("shovel") || CaveItems.digging_shovel.func_150897_b(sub) ||
							sub.getMaterial() == Material.ground || sub.getMaterial() == Material.grass || sub.getMaterial() == Material.sand || sub.getMaterial() == Material.snow || sub.getMaterial() == Material.craftedSnow)
						{
							ItemDiggingShovel.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta));

							if (sub instanceof BlockRotatedPillar)
							{
								ItemDiggingShovel.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta + 4));
								ItemDiggingShovel.breakableBlocks.addIfAbsent(new BlockEntry(sub, meta + 8));
							}
						}
					}
				}
			}
			catch (Throwable e) {}
		}

		CaveworldAPI.setMiningPointAmount("oreCoal", 1);
		CaveworldAPI.setMiningPointAmount("oreIron", 1);
		CaveworldAPI.setMiningPointAmount("oreGold", 1);
		CaveworldAPI.setMiningPointAmount("oreRedstone", 2);
		CaveworldAPI.setMiningPointAmount(Blocks.lit_redstone_ore, 0, 2);
		CaveworldAPI.setMiningPointAmount("oreLapis", 2);
		CaveworldAPI.setMiningPointAmount("oreEmerald", 2);
		CaveworldAPI.setMiningPointAmount("oreDiamond", 3);
		CaveworldAPI.setMiningPointAmount("oreQuartz", 1);
		CaveworldAPI.setMiningPointAmount("oreCopper", 1);
		CaveworldAPI.setMiningPointAmount("copperOre", 1);
		CaveworldAPI.setMiningPointAmount("oreTin", 1);
		CaveworldAPI.setMiningPointAmount("tinOre", 1);
		CaveworldAPI.setMiningPointAmount("oreLead", 1);
		CaveworldAPI.setMiningPointAmount("leadOre", 1);
		CaveworldAPI.setMiningPointAmount("oreSilver", 1);
		CaveworldAPI.setMiningPointAmount("silverOre", 1);
		CaveworldAPI.setMiningPointAmount("oreAdamantium", 1);
		CaveworldAPI.setMiningPointAmount("adamantiumOre", 1);
		CaveworldAPI.setMiningPointAmount("oreAluminum", 1);
		CaveworldAPI.setMiningPointAmount("aluminumOre", 1);
		CaveworldAPI.setMiningPointAmount("oreApatite", 1);
		CaveworldAPI.setMiningPointAmount("apatiteOre", 1);
		CaveworldAPI.setMiningPointAmount("oreMythril", 1);
		CaveworldAPI.setMiningPointAmount("mythrilOre", 1);
		CaveworldAPI.setMiningPointAmount("oreOnyx", 1);
		CaveworldAPI.setMiningPointAmount("onyxOre", 1);
		CaveworldAPI.setMiningPointAmount("oreUranium", 2);
		CaveworldAPI.setMiningPointAmount("uraniumOre", 2);
		CaveworldAPI.setMiningPointAmount("oreSapphire", 2);
		CaveworldAPI.setMiningPointAmount("sapphireOre", 2);
		CaveworldAPI.setMiningPointAmount("oreRuby", 2);
		CaveworldAPI.setMiningPointAmount("rubyOre", 2);
		CaveworldAPI.setMiningPointAmount("oreTopaz", 2);
		CaveworldAPI.setMiningPointAmount("topazOre", 2);
		CaveworldAPI.setMiningPointAmount("oreChrome", 1);
		CaveworldAPI.setMiningPointAmount("chromeOre", 1);
		CaveworldAPI.setMiningPointAmount("orePlatinum", 1);
		CaveworldAPI.setMiningPointAmount("platinumOre", 1);
		CaveworldAPI.setMiningPointAmount("oreTitanium", 1);
		CaveworldAPI.setMiningPointAmount("titaniumOre", 1);
		CaveworldAPI.setMiningPointAmount("oreTofu", 1);
		CaveworldAPI.setMiningPointAmount("tofuOre", 1);
		CaveworldAPI.setMiningPointAmount("oreTofuDiamond", 3);
		CaveworldAPI.setMiningPointAmount("tofuDiamondOre", 3);
		CaveworldAPI.setMiningPointAmount("oreSulfur", 1);
		CaveworldAPI.setMiningPointAmount("sulfurOre", 1);
		CaveworldAPI.setMiningPointAmount("oreSaltpeter", 1);
		CaveworldAPI.setMiningPointAmount("saltpeterOre", 1);
		CaveworldAPI.setMiningPointAmount("oreFirestone", 2);
		CaveworldAPI.setMiningPointAmount("firestoneOre", 2);
		CaveworldAPI.setMiningPointAmount("oreSalt", 1);
		CaveworldAPI.setMiningPointAmount("saltOre", 1);
		CaveworldAPI.setMiningPointAmount("oreJade", 1);
		CaveworldAPI.setMiningPointAmount("jadeOre", 1);
		CaveworldAPI.setMiningPointAmount("oreManganese", 1);
		CaveworldAPI.setMiningPointAmount("manganeseOre", 1);
		CaveworldAPI.setMiningPointAmount("oreLanite", 1);
		CaveworldAPI.setMiningPointAmount("laniteOre", 1);
		CaveworldAPI.setMiningPointAmount("oreMeurodite", 1);
		CaveworldAPI.setMiningPointAmount("meuroditeOre", 1);
		CaveworldAPI.setMiningPointAmount("oreSoul", 1);
		CaveworldAPI.setMiningPointAmount("soulOre", 1);
		CaveworldAPI.setMiningPointAmount("oreSunstone", 1);
		CaveworldAPI.setMiningPointAmount("sunstoneOre", 1);
		CaveworldAPI.setMiningPointAmount("oreZinc", 1);
		CaveworldAPI.setMiningPointAmount("zincOre", 1);
		CaveworldAPI.setMiningPointAmount("oreCrocoite", 3);
		CaveworldAPI.setMiningPointAmount("crocoiteOre", 3);
		CaveworldAPI.setMiningPointAmount("oreCavenium", 2);
		CaveworldAPI.setMiningPointAmount("caveniumOre", 2);
		CaveworldAPI.setMiningPointAmount("oreAquamarine", 2);
		CaveworldAPI.setMiningPointAmount("aquamarineOre", 2);
		CaveworldAPI.setMiningPointAmount("glowstone", 2);

		Config.syncBiomesCfg();
		Config.syncBiomesCavernCfg();
		Config.syncBiomesAquaCavernCfg();
		Config.syncVeinsCfg();
		Config.syncVeinsCavernCfg();
		Config.syncVeinsAquaCavernCfg();

		CavePlugins.registerPlugins();
		Config.syncPluginsCfg();

		CavePlugins.invokePlugins();

		if (event.getSide().isClient())
		{
			CavePlugins.invokeClientPlugins();
		}
	}

	@EventHandler
	public void loaded(FMLLoadCompleteEvent event)
	{
		Set<String> entries = Sets.newTreeSet();

		for (Block block : GameData.getBlockRegistry().typeSafeIterable())
		{
			for (int i = 0; i < 16; ++i)
			{
				int point = CaveworldAPI.getMiningPointAmount(block, i);

				if (point > 0)
				{
					entries.add(GameData.getBlockRegistry().getNameForObject(block) + ":" + i + "," + point);

					ItemMiningPickaxe.defaultBreakables.add(CaveUtils.toStringHelper(block, i));
				}
			}
		}

		CaveworldAPI.caverManager.clearMiningPointAmounts();

		ConfigCategory category = Config.generalCfg.getCategory(Configuration.CATEGORY_GENERAL);
		Property prop = category.get("miningPoints");

		if (prop.getStringList() == null || prop.getStringList().length <= 0)
		{
			prop.set(entries.toArray(new String[entries.size()]));
		}

		prop = category.get("miningPointValidItems");

		if (prop.getStringList() == null || prop.getStringList().length <= 0)
		{
			entries.clear();

			for (Item item : CaveUtils.pickaxeItems)
			{
				entries.add(GameData.getItemRegistry().getNameForObject(item));
			}

			prop.set(entries.toArray(new String[entries.size()]));
		}

		prop = category.get("randomiteDrops");

		if (prop.getStringList() == null || prop.getStringList().length <= 0)
		{
			entries.clear();

			for (Item item : GameData.getItemRegistry().typeSafeIterable())
			{
				if (entries.size() >= 100)
				{
					break;
				}

				String name = GameData.getItemRegistry().getNameForObject(item);

				if (name != null && name.startsWith("minecraft"))
				{
					boolean flag = false;

					if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemHoe || item instanceof ItemBow || item instanceof ItemArmor || item instanceof ItemSeeds || item instanceof ItemFood)
					{
						flag = true;
					}
					else if (item instanceof ItemBlock)
					{
						Block block = Block.getBlockFromItem(item);

						if (block instanceof BlockSand || block instanceof BlockGrass || block instanceof BlockGlass || block instanceof BlockClay || block instanceof BlockLog ||
							block instanceof BlockReed || block instanceof BlockTorch || block instanceof BlockCocoa)
						{
							flag = true;
						}
					}
					else if (item == Items.diamond || item == Items.emerald || item == Items.iron_ingot || item == Items.gold_ingot || item == Items.ender_pearl || item == Items.blaze_rod)
					{
						flag = true;
					}

					if (flag)
					{
						if (item.isDamageable())
						{
							entries.add(name);
						}
						else
						{
							List<ItemStack> list = SubItemHelper.getSubItems(item);

							for (ItemStack itemstack : list)
							{
								int i = itemstack.getItemDamage();

								if (i <= 0)
								{
									entries.add(name);
								}
								else
								{
									entries.add(name + ":" + i);
								}
							}
						}
					}
				}
			}

			prop.set(entries.toArray(new String[entries.size()]));
		}

		Config.saveConfig(Config.generalCfg);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		Config.refreshMiningPoints();
		Config.refreshRandomiteDrops();

		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo(String.format(StatCollector.translateToLocal("caveworld.version.message"), metadata.name) + ": " + Version.getLatest());
		}
	}

	@EventHandler
	public void serverStopping(FMLServerStoppedEvent event)
	{
		CaveEventHooks.firstJoinPlayers.clear();
		ItemMiningPickaxe.BreakMode.executors.clear();
		ItemLumberingAxe.BreakMode.executors.clear();
		ItemDiggingShovel.BreakMode.executors.clear();

		File dir = Config.getConfigDir();

		if (dir != null && dir.exists())
		{
			try (FileOutputStream output = new FileOutputStream(new File(dir, "UniversalChest.dat")))
			{
				NBTTagCompound data = CaveBlocks.universal_chest.getData();

				data.setTag("ChestItems", CaveBlocks.universal_chest.inventory.saveInventoryToNBT());

				CompressedStreamTools.writeCompressed(data, output);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to writing Universal Chest data");
			}
		}
	}

	@EventHandler
	public void missingMappings(FMLMissingMappingsEvent event)
	{
		event.applyModContainer(new DummyModContainer()
		{
			@Override
			public String getModId()
			{
				return "kegare.caveworld";
			}
		});

		for (MissingMapping missing : event.get())
		{
			String name = missing.name;
			Object obj = missing.type.getRegistry().getObject(MODID + name.substring(name.indexOf(':')));

			if (obj != null)
			{
				if (obj instanceof Block)
				{
					missing.remap((Block)obj);
				}
				else if (obj instanceof Item)
				{
					missing.remap((Item)obj);
				}
			}
		}
	}
}