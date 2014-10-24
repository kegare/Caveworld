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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.entity.EntityCaveman;
import com.kegare.caveworld.handler.CaveAPIHandler;
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.handler.CaveFuelHandler;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.network.BuffMessage;
import com.kegare.caveworld.network.CaveAchievementMessage;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.CaveworldMenuMessage;
import com.kegare.caveworld.network.DimSyncMessage;
import com.kegare.caveworld.network.MiningSyncMessage;
import com.kegare.caveworld.network.RegenerateMessage;
import com.kegare.caveworld.network.SelectBreakableMessage;
import com.kegare.caveworld.plugin.advancedtools.AdvancedToolsPlugin;
import com.kegare.caveworld.plugin.craftguide.CraftGuidePlugin;
import com.kegare.caveworld.plugin.enderio.EnderIOPlugin;
import com.kegare.caveworld.plugin.ic2.IC2Plugin;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;
import com.kegare.caveworld.plugin.miningmod.MiningmodPlugin;
import com.kegare.caveworld.plugin.more.MOrePlugin;
import com.kegare.caveworld.plugin.nei.NEIPlugin;
import com.kegare.caveworld.plugin.thaumcraft.ThaumcraftPlugin;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
import com.kegare.caveworld.util.breaker.RangedBreakExecutor;
import com.kegare.caveworld.world.WorldProviderCaveworld;
import com.kegare.caveworld.world.WorldProviderDeepCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
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

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		CaveworldAPI.apiHandler = new CaveAPIHandler();
		CaveworldAPI.biomeManager = new CaveBiomeManager();
		CaveworldAPI.veinManager = new CaveVeinManager();
		CaveworldAPI.miningManager = new CaveMiningManager();

		Version.versionCheck();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.initializeConfigClasses();

		Config.syncGeneralCfg();
		Config.syncBlocksCfg();
		Config.syncItemsCfg();

		CaveBlocks.registerBlocks();
		CaveItems.registerItems();
		CaveAchievementList.registerAchievements();

		proxy.registerRecipes();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		Config.syncEntitiesCfg();
		Config.syncDimensionCfg();

		int id = 0;

		network.registerMessage(DimSyncMessage.class, DimSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(MiningSyncMessage.class, MiningSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaveSoundMessage.class, CaveSoundMessage.class, id++, Side.CLIENT);
		network.registerMessage(RegenerateMessage.class, RegenerateMessage.class, id++, Side.CLIENT);
		network.registerMessage(RegenerateMessage.class, RegenerateMessage.class, id++, Side.SERVER);
		network.registerMessage(RegenerateMessage.ProgressNotify.class, RegenerateMessage.ProgressNotify.class, id++, Side.CLIENT);
		network.registerMessage(BuffMessage.class, BuffMessage.class, id++, Side.SERVER);
		network.registerMessage(CaveworldMenuMessage.class, CaveworldMenuMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaveAchievementMessage.class, CaveAchievementMessage.class, id++, Side.SERVER);
		network.registerMessage(SelectBreakableMessage.class, SelectBreakableMessage.class, id++, Side.SERVER);

		EntityRegistry.registerGlobalEntityID(EntityCaveman.class, "Caveman", EntityRegistry.findGlobalUniqueEntityId(), 0xAAAAAA, 0xCCCCCC);
		EntityRegistry.registerModEntity(EntityCaveman.class, "Caveman", 0, this, 128, 1, true);

		proxy.registerRenderers();

		id = CaveworldAPI.getDimension();
		DimensionManager.registerProviderType(id, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(id, id);
		id = CaveworldAPI.getDeepDimension();
		DimensionManager.registerProviderType(id, WorldProviderDeepCaveworld.class, true);
		DimensionManager.registerDimension(id, id);

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Config.syncBiomesCfg();
		Config.syncVeinsCfg();

		new ForkJoinPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				CaveworldAPI.setMiningPointAmount("oreCoal", 1);
				CaveworldAPI.setMiningPointAmount("oreIron", 1);
				CaveworldAPI.setMiningPointAmount("oreGold", 1);
				CaveworldAPI.setMiningPointAmount("oreRedstone", 1);
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
			if (EnderIOPlugin.enabled())
			{
				EnderIOPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			CaveLog.log(Level.WARN, e, "Failed to trying invoke plugin: EnderIOPlugin");
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
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo("A new Caveworld version is available : " + Version.getLatest());
		}
	}

	@EventHandler
	public void serverStopping(FMLServerStoppedEvent event)
	{
		CaveEventHooks.firstJoinPlayers.clear();

		MultiBreakExecutor.executors.clear();
		RangedBreakExecutor.executors.clear();
	}
}