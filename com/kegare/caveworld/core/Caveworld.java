/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import static com.kegare.caveworld.core.Caveworld.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import net.minecraft.block.Block;
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
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import org.apache.logging.log4j.Level;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.entity.EntityArcherZombie;
import com.kegare.caveworld.entity.EntityCaveman;
import com.kegare.caveworld.entity.EntityCavenicSkeleton;
import com.kegare.caveworld.handler.CaveAPIHandler;
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.handler.CaveFuelHandler;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.item.ItemDiggingShovel;
import com.kegare.caveworld.item.ItemLumberingAxe;
import com.kegare.caveworld.item.ItemMiningPickaxe;
import com.kegare.caveworld.network.client.CaveworldMenuMessage;
import com.kegare.caveworld.network.client.DimDeepSyncMessage;
import com.kegare.caveworld.network.client.DimSyncMessage;
import com.kegare.caveworld.network.client.MiningSyncMessage;
import com.kegare.caveworld.network.client.MultiBreakCountMessage;
import com.kegare.caveworld.network.client.OpenUrlMessage;
import com.kegare.caveworld.network.client.PlaySoundMessage;
import com.kegare.caveworld.network.common.RegenerateMessage;
import com.kegare.caveworld.network.server.CaveAchievementMessage;
import com.kegare.caveworld.network.server.SelectBreakableMessage;
import com.kegare.caveworld.plugin.advancedtools.AdvancedToolsPlugin;
import com.kegare.caveworld.plugin.craftguide.CraftGuidePlugin;
import com.kegare.caveworld.plugin.ic2.IC2Plugin;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.plugin.miningmod.MiningmodPlugin;
import com.kegare.caveworld.plugin.more.MOrePlugin;
import com.kegare.caveworld.plugin.moreinventory.MIMPlugin;
import com.kegare.caveworld.plugin.nei.NEIPlugin;
import com.kegare.caveworld.plugin.tconstruct.TinkersConstructPlugin;
import com.kegare.caveworld.plugin.thaumcraft.ThaumcraftPlugin;
import com.kegare.caveworld.recipe.RecipeCaveniumTool;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;
import com.kegare.caveworld.world.WorldProviderDeepCaveworld;
import com.kegare.caveworld.world.gen.MapGenStrongholdCaveworld;
import com.kegare.caveworld.world.gen.StructureStrongholdPiecesCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod
(
	modid = MODID,
	acceptedMinecraftVersions = "[1.7.10,)",
	guiFactory = MOD_PACKAGE + ".client.config.CaveGuiFactory"
)
public class Caveworld
{
	public static final String
	MODID = "kegare.caveworld",
	MOD_PACKAGE = "com.kegare.caveworld",
	CONFIG_LANG = "caveworld.config.";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = MOD_PACKAGE + ".client.ClientProxy", serverSide = MOD_PACKAGE + ".core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MODID);

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
		CaveworldAPI.biomeDeepManager = new CaveDeepBiomeManager();
		CaveworldAPI.veinDeepManager = new CaveDeepVeinManager();
		CaveworldAPI.miningManager = new CaveMiningManager();

		Version.versionCheck();

		RecipeSorter.register(MODID + ":cavenium_tool", RecipeCaveniumTool.class, Category.SHAPED, "after:minecraft:shaped");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		int id = 0;

		network.registerMessage(DimSyncMessage.class, DimSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(DimDeepSyncMessage.class, DimDeepSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(MiningSyncMessage.class, MiningSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(OpenUrlMessage.class, OpenUrlMessage.class, id++, Side.CLIENT);
		network.registerMessage(PlaySoundMessage.class, PlaySoundMessage.class, id++, Side.CLIENT);
		network.registerMessage(RegenerateMessage.class, RegenerateMessage.class, id++, Side.CLIENT);
		network.registerMessage(RegenerateMessage.class, RegenerateMessage.class, id++, Side.SERVER);
		network.registerMessage(RegenerateMessage.ProgressNotify.class, RegenerateMessage.ProgressNotify.class, id++, Side.CLIENT);
		network.registerMessage(CaveworldMenuMessage.class, CaveworldMenuMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaveAchievementMessage.class, CaveAchievementMessage.class, id++, Side.SERVER);
		network.registerMessage(SelectBreakableMessage.class, SelectBreakableMessage.class, id++, Side.SERVER);
		network.registerMessage(MultiBreakCountMessage.class, MultiBreakCountMessage.class, id, Side.CLIENT);

		Config.syncGeneralCfg();
		Config.syncBlocksCfg();
		Config.syncItemsCfg();

		CaveBlocks.registerBlocks();
		CaveItems.registerItems();
		CaveAchievementList.registerAchievements();

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
		EntityRegistry.registerModEntity(EntityCavenicSkeleton.class, "CavenicSkeleton", id, this, 128, 1, true);

		proxy.registerRenderers();

		id = CaveworldAPI.getDimension();
		DimensionManager.registerProviderType(id, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(id, id);
		id = CaveworldAPI.getDeepDimension();

		if (id != 0 && id != CaveworldAPI.getDimension())
		{
			DimensionManager.registerProviderType(id, WorldProviderDeepCaveworld.class, true);
			DimensionManager.registerDimension(id, id);
		}

		MapGenStructureIO.registerStructure(MapGenStrongholdCaveworld.Start.class, "Caveworld.Stronghold");
		StructureStrongholdPiecesCaveworld.registerStrongholdPieces();

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Config.syncBiomesCfg();
		Config.syncVeinsCfg();
		Config.syncBiomesDeepCfg();
		Config.syncVeinsDeepCfg();

		CaveUtils.getPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				CaveworldAPI.setMiningPointAmount("oreCoal", 1);
				CaveworldAPI.setMiningPointAmount("oreIron", 1);
				CaveworldAPI.setMiningPointAmount("oreGold", 1);
				CaveworldAPI.setMiningPointAmount("oreRedstone", 1);
				CaveworldAPI.setMiningPointAmount(Blocks.lit_redstone_ore, 0, 1);
				CaveworldAPI.setMiningPointAmount("oreLapis", 1);
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
				CaveworldAPI.setMiningPointAmount("oreCavenium", 2);
				CaveworldAPI.setMiningPointAmount("caveniumOre", 2);

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
								block instanceof BlockOre || block instanceof BlockRedstoneOre)
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
									CaveItems.mining_pickaxe.func_150897_b(sub) || sub instanceof BlockOre || sub instanceof BlockRedstoneOre)
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

		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			CaveUtils.isItemPickaxe(item);
			CaveUtils.isItemAxe(item);
			CaveUtils.isItemShovel(item);
		}

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
			if (ThaumcraftPlugin.enabled())
			{
				ThaumcraftPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: ThaumcraftPlugin");
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
}