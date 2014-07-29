/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.core;

import static com.kegare.caveworld.core.Caveworld.*;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.config.Config;
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.handler.CaveFuelHandler;
import com.kegare.caveworld.network.BiomesSyncMessage;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.ConfigSyncMessage;
import com.kegare.caveworld.network.DimSyncMessage;
import com.kegare.caveworld.network.MiningSyncMessage;
import com.kegare.caveworld.network.VeinsSyncMessage;
import com.kegare.caveworld.plugin.CaveModPlugin;
import com.kegare.caveworld.proxy.CommonProxy;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod
(
	modid = MODID,
	acceptedMinecraftVersions = "[1.7.10,)",
	guiFactory = MOD_PACKAGE + ".config.CaveGuiFactory"
)
public class Caveworld
{
	public static final String
	MODID = "kegare.caveworld",
	MOD_PACKAGE = "com.kegare.caveworld",
	CONFIG_LANG = "caveworld.configgui.";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = MOD_PACKAGE + ".proxy.ClientProxy", serverSide = MOD_PACKAGE + ".proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Version.versionCheck();

		Config.syncConfig();

		CaveBlocks.initializeBlocks();
		CaveBlocks.registerBlocks();

		CaveAchievementList.registerAchievements();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		registerMessages();

		proxy.registerRenderers();
		proxy.registerRecipes();

		DimensionManager.registerProviderType(Config.dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(Config.dimensionCaveworld, Config.dimensionCaveworld);

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);
	}

	private void registerMessages()
	{
		byte id = 0;

		network.registerMessage(DimSyncMessage.class, DimSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(ConfigSyncMessage.class, ConfigSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(BiomesSyncMessage.class, BiomesSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(VeinsSyncMessage.class, VeinsSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(MiningSyncMessage.class, MiningSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaveSoundMessage.class, CaveSoundMessage.class, id++, Side.CLIENT);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Config.syncPostConfig();

		CaveModPlugin.registerPlugins();
		CaveModPlugin.invokePlugins();
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
}