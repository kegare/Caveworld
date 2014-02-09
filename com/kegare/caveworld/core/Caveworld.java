package com.kegare.caveworld.core;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.packet.PacketPipeline;
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

@Mod(modid = "kegare.caveworld")
public class Caveworld
{
	public static final PacketPipeline packetPipeline = new PacketPipeline();

	@Metadata("kegare.caveworld")
	public static ModMetadata metadata;

	@SidedProxy(modId = "kegare.caveworld", clientSide = "com.kegare.caveworld.proxy.ClientProxy", serverSide = "com.kegare.caveworld.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Version.versionCheck();

		Config.buildConfig();

		CaveBlocks.configure();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerRenderers();

		DimensionManager.registerProviderType(Config.dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(Config.dimensionCaveworld, Config.dimensionCaveworld);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		packetPipeline.init("kegare.caveworld");
		packetPipeline.registerPacket(Config.ConfigSyncPacket.class);
		packetPipeline.registerPacket(WorldProviderCaveworld.DataSyncPacket.class);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		CaveBiomeManager.loadCaveBiomes();

		packetPipeline.postInit();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCaveworld());

		if (event.getSide().isServer() && (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo("A new Caveworld version is available : " + Version.LATEST.or(Version.CURRENT.orNull()));
		}
	}
}