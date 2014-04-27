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
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.handler.CaveFuelHandler;
import com.kegare.caveworld.packet.CaveBiomeSyncPacket;
import com.kegare.caveworld.packet.CaveDimSyncPacket;
import com.kegare.caveworld.packet.CaveMiningSyncPacket;
import com.kegare.caveworld.packet.CaveNotifyPacket;
import com.kegare.caveworld.packet.CaveOreSyncPacket;
import com.kegare.caveworld.packet.ConfigSyncPacket;
import com.kegare.caveworld.packet.PacketPipeline;
import com.kegare.caveworld.packet.PlayCaveSoundPacket;
import com.kegare.caveworld.plugin.CaveModPluginManager;
import com.kegare.caveworld.plugin.thaumcraft.ThaumcraftPlugin;
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
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MODID, acceptedMinecraftVersions = "[1.7,)")
public class Caveworld
{
	public static final String
	MODID = "kegare.caveworld",
	PACKAGE_NAME = "com.kegare.caveworld";

	public static final PacketPipeline packetPipeline = new PacketPipeline();

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = PACKAGE_NAME + ".proxy.ClientProxy", serverSide = PACKAGE_NAME + ".proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Version.versionCheck();

		Config.buildConfig();

		CaveBlocks.initialize();
		CaveBlocks.register();

		CaveAchievementList.register();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		proxy.registerRecipes();

		DimensionManager.registerProviderType(Config.dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(Config.dimensionCaveworld, Config.dimensionCaveworld);

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);

		packetPipeline.init(MODID);
		packetPipeline.registerPacket(ConfigSyncPacket.class);
		packetPipeline.registerPacket(CaveDimSyncPacket.class);
		packetPipeline.registerPacket(CaveBiomeSyncPacket.class);
		packetPipeline.registerPacket(CaveOreSyncPacket.class);
		packetPipeline.registerPacket(CaveMiningSyncPacket.class);
		packetPipeline.registerPacket(CaveNotifyPacket.class);
		packetPipeline.registerPacket(PlayCaveSoundPacket.class);

		CaveModPluginManager.registerPlugin(ThaumcraftPlugin.class);
		CaveModPluginManager.initPlugins();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		CaveBiomeManager.loadCaveBiomes();
		CaveOreManager.loadCaveOres();

		packetPipeline.postInit();
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