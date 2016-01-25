/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import static caveworld.core.Caveworld.MODID;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.block.CaveBlocks;
import caveworld.entity.EntityArcherZombie;
import caveworld.entity.EntityCaveman;
import caveworld.entity.EntityCavenicSkeleton;
import caveworld.entity.EntityMasterCavenicSkeleton;
import caveworld.handler.CaveAPIHandler;
import caveworld.handler.CaveEventHooks;
import caveworld.handler.CaveFuelHandler;
import caveworld.item.CaveItems;
import caveworld.item.ItemDiggingShovel;
import caveworld.item.ItemLumberingAxe;
import caveworld.item.ItemMiningPickaxe;
import caveworld.network.client.CaverAdjustMessage;
import caveworld.network.client.CavernAdjustMessage;
import caveworld.network.client.CaveworldAdjustMessage;
import caveworld.network.client.CaveworldMenuMessage;
import caveworld.network.client.MultiBreakCountMessage;
import caveworld.network.client.OpenUrlMessage;
import caveworld.network.client.PlaySoundMessage;
import caveworld.network.common.RegenerateMessage;
import caveworld.network.server.CaveAchievementMessage;
import caveworld.network.server.PortalInventoryMessage;
import caveworld.network.server.SelectBreakableMessage;
import caveworld.plugin.advancedtools.AdvancedToolsPlugin;
import caveworld.plugin.craftguide.CraftGuidePlugin;
import caveworld.plugin.enderstorage.EnderStoragePlugin;
import caveworld.plugin.hexagonaldia.HexagonalDiamondPlugin;
import caveworld.plugin.ic2.IC2Plugin;
import caveworld.plugin.mapletree.MapleTreePlugin;
import caveworld.plugin.materialarms.MaterialArmsPlugin;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import caveworld.plugin.miningmod.MiningmodPlugin;
import caveworld.plugin.more.MOrePlugin;
import caveworld.plugin.moreinventory.MIMPlugin;
import caveworld.plugin.nei.NEIPlugin;
import caveworld.plugin.tconstruct.TinkersConstructPlugin;
import caveworld.recipe.RecipeCaveniumTool;
import caveworld.util.CaveLog;
import caveworld.util.CaveUtils;
import caveworld.util.Version;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
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
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

@Mod(modid = MODID, guiFactory = "caveworld.client.config.CaveGuiFactory")
public class Caveworld
{
	public static final String
	MODID = "caveworld",
	CONFIG_LANG = "caveworld.config.";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = "caveworld.client.ClientProxy", serverSide = "caveworld.core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static final CreativeTabCaveworld tabCaveworld = new CreativeTabCaveworld();
	public static final CreativeTabMiningPickaxe tabMiningPickaxe = new CreativeTabMiningPickaxe();
	public static final CreativeTabLumberingAxe tabLumberingAxe = new CreativeTabLumberingAxe();
	public static final CreativeTabDiggingShovel tabDiggingShovel = new CreativeTabDiggingShovel();

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		proxy.initializeConfigEntries();

		CaveworldAPI.apiHandler = new CaveAPIHandler();
		CaveworldAPI.biomeManager = new CaveBiomeManager();
		CaveworldAPI.veinManager = new CaveVeinManager();
		CaveworldAPI.biomeCavernManager = new CavernBiomeManager();
		CaveworldAPI.veinCavernManager = new CavernVeinManager();
		CaveworldAPI.caverManager = new CaverManager();

		Version.versionCheck();

		RecipeSorter.register(MODID + ":cavenium_tool", RecipeCaveniumTool.class, Category.SHAPED, "after:minecraft:shaped");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		int id = 0;

		network.registerMessage(CaveworldAdjustMessage.class, CaveworldAdjustMessage.class, id++, Side.CLIENT);
		network.registerMessage(CavernAdjustMessage.class, CavernAdjustMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaverAdjustMessage.class, CaverAdjustMessage.class, id++, Side.CLIENT);
		network.registerMessage(OpenUrlMessage.class, OpenUrlMessage.class, id++, Side.CLIENT);
		network.registerMessage(PlaySoundMessage.class, PlaySoundMessage.class, id++, Side.CLIENT);
		network.registerMessage(RegenerateMessage.class, RegenerateMessage.class, id++, Side.CLIENT);
		network.registerMessage(RegenerateMessage.class, RegenerateMessage.class, id++, Side.SERVER);
		network.registerMessage(RegenerateMessage.ProgressNotify.class, RegenerateMessage.ProgressNotify.class, id++, Side.CLIENT);
		network.registerMessage(CaveworldMenuMessage.class, CaveworldMenuMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaveAchievementMessage.class, CaveAchievementMessage.class, id++, Side.SERVER);
		network.registerMessage(SelectBreakableMessage.class, SelectBreakableMessage.class, id++, Side.SERVER);
		network.registerMessage(MultiBreakCountMessage.class, MultiBreakCountMessage.class, id++, Side.CLIENT);
		network.registerMessage(PortalInventoryMessage.class, PortalInventoryMessage.class, id++, Side.SERVER);

		Config.syncGeneralCfg();

		CaveBlocks.registerBlocks();
		CaveItems.registerItems();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		Config.syncEntitiesCfg();
		Config.syncDimensionCfg();

		int id = 0;
		EntityRegistry.registerGlobalEntityID(EntityCaveman.class, "Caveman", EntityRegistry.findGlobalUniqueEntityId(), 0xAAAAAA, 0xCCCCCC);
		EntityRegistry.registerModEntity(EntityCaveman.class, "Caveman", id++, this, 128, 1, true);
		EntityRegistry.registerGlobalEntityID(EntityArcherZombie.class, "ArcherZombie", EntityRegistry.findGlobalUniqueEntityId(), 0x00A0A0, 0xAAAAAA);
		EntityRegistry.registerModEntity(EntityArcherZombie.class, "ArcherZombie", id++, this, 128, 1, true);
		EntityRegistry.registerGlobalEntityID(EntityCavenicSkeleton.class, "CavenicSkeleton", EntityRegistry.findGlobalUniqueEntityId(), 0xAAAAAA, 0xDDDDDD);
		EntityRegistry.registerModEntity(EntityCavenicSkeleton.class, "CavenicSkeleton", id++, this, 128, 1, true);
		EntityRegistry.registerGlobalEntityID(EntityMasterCavenicSkeleton.class, "MasterCavenicSkeleton", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityMasterCavenicSkeleton.class, "MasterCavenicSkeleton", id, this, 128, 1, true);

		proxy.registerRenderers();

		id = CaveworldAPI.getDimension();
		DimensionManager.registerProviderType(id, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(id, id);

		id = CaveworldAPI.getCavernDimension();
		DimensionManager.registerProviderType(id, WorldProviderCavern.class, true);
		DimensionManager.registerDimension(id, id);

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);

		CaveAchievementList.registerAchievements();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Config.syncBiomesCfg();
		Config.syncBiomesCavernCfg();
		Config.syncVeinsCfg();
		Config.syncVeinsCavernCfg();

		CaveUtils.getPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				List list = Lists.newArrayList();

				for (Block block : GameData.getBlockRegistry().typeSafeIterable())
				{
					try
					{
						Item item = Item.getItemFromBlock(block);

						list.clear();

						CreativeTabs tab = block.getCreativeTabToDisplayOn();

						if (tab == null)
						{
							tab = CreativeTabs.tabAllSearch;
						}

						if (item != null)
						{
							block.getSubBlocks(item, tab, list);
						}

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
						else for (Object obj : list)
						{
							ItemStack itemstack = (ItemStack)obj;

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
			}
		});

		try
		{
			if (CraftGuidePlugin.enabled())
			{
				CraftGuidePlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: CraftGuidePlugin");
		}

		try
		{
			if (event.getSide().isClient() && NEIPlugin.enabled())
			{
				NEIPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: NEIPlugin");
		}

		try
		{
			if (IC2Plugin.enabled())
			{
				IC2Plugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: IC2Plugin");
		}

		try
		{
			if (MCEconomyPlugin.enabled())
			{
				MCEconomyPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: MCEconomyPlugin");
		}

		try
		{
			if (MOrePlugin.enabled())
			{
				MOrePlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: MOrePlugin");
		}

		try
		{
			if (MiningmodPlugin.enabled())
			{
				MiningmodPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: MiningmodPlugin");
		}

		try
		{
			if (AdvancedToolsPlugin.enabled())
			{
				AdvancedToolsPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: AdvancedToolsPlugin");
		}

		try
		{
			if (TinkersConstructPlugin.enabled())
			{
				TinkersConstructPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: TinkersConstructPlugin");
		}

		try
		{
			if (MIMPlugin.enabled())
			{
				MIMPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: MIMPlugin");
		}

		try
		{
			if (MapleTreePlugin.enabled())
			{
				MapleTreePlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: MapleTreePlugin");
		}

		try
		{
			if (EnderStoragePlugin.enabled())
			{
				EnderStoragePlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: EnderStoragePlugin");
		}

		try
		{
			if (HexagonalDiamondPlugin.enabled())
			{
				HexagonalDiamondPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: HexagonalDiamondPlugin");
		}

		try
		{
			if (MaterialArmsPlugin.enabled())
			{
				MaterialArmsPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: MaterialArmsPlugin");
		}

		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			CaveUtils.isItemPickaxe(item);
			CaveUtils.isItemAxe(item);
			CaveUtils.isItemShovel(item);
		}
	}

	@EventHandler
	public void loaded(FMLLoadCompleteEvent event)
	{
		CaveUtils.getPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				Set<String> items = Sets.newTreeSet();

				for (Item item : CaveUtils.pickaxeItems)
				{
					items.add(GameData.getItemRegistry().getNameForObject(item));
				}

				Config.miningPointValidItemsDefault = items.toArray(new String[items.size()]);

				Property prop = Config.generalCfg.getCategory(Configuration.CATEGORY_GENERAL).get("miningPointValidItems");
				prop.setDefaultValues(Config.miningPointValidItemsDefault);

				if (prop.getStringList() == null || prop.getStringList().length <= 0)
				{
					prop.setToDefault();
				}

				if (Config.generalCfg.hasChanged())
				{
					Config.generalCfg.save();
				}
			}
		});

		CaveUtils.getPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
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
				CaveworldAPI.setMiningPointAmount("oreCavenium", 2);
				CaveworldAPI.setMiningPointAmount("caveniumOre", 2);
				CaveworldAPI.setMiningPointAmount("oreAquamarine", 2);
				CaveworldAPI.setMiningPointAmount("aquamarineOre", 2);
				CaveworldAPI.setMiningPointAmount("glowstone", 2);

				for (Block block : GameData.getBlockRegistry().typeSafeIterable())
				{
					for (int i = 0; i < 16; ++i)
					{
						if (CaveworldAPI.getMiningPointAmount(block, i) > 0)
						{
							ItemMiningPickaxe.defaultBreakables.add(CaveUtils.toStringHelper(block, i));
						}
					}
				}
			}
		});
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo(String.format(StatCollector.translateToLocal("caveworld.version.message"), "Caveworld") + ": " + Version.getLatest());
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