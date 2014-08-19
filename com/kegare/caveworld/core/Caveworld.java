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

import java.lang.reflect.Field;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.handler.CaveFuelHandler;
import com.kegare.caveworld.network.CaveSoundMessage;
import com.kegare.caveworld.network.ConfigSyncMessage;
import com.kegare.caveworld.network.DimSyncMessage;
import com.kegare.caveworld.network.MiningSyncMessage;
import com.kegare.caveworld.plugin.CaveModPlugin;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;

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
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
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
	CONFIG_LANG = "caveworld.configgui.";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = MOD_PACKAGE + ".client.ClientProxy", serverSide = MOD_PACKAGE + ".core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MODID);

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		Version.versionCheck();

		try
		{
			for (Field field : CaveworldAPI.class.getDeclaredFields())
			{
				switch (field.getName())
				{
					case "biomeManager":
						field.setAccessible(true);
						field.set(null, new CaveBiomeManager());
						break;
					case "veinManager":
						field.setAccessible(true);
						field.set(null, new CaveVeinManager());
						break;
					case "miningManager":
						field.setAccessible(true);
						field.set(null, new CaveMiningManager());
						break;
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to initialize api instance");
		}

		CaveModPlugin.initializePlugins(event.getASMHarvestedData());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.syncConfig();

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

		CaveworldAPI.setMiningPointAmount(Blocks.emerald_ore, 0, 2);
		CaveworldAPI.setMiningPointAmount(Blocks.diamond_ore, 0, 3);
	}

	private void registerMessages()
	{
		byte id = 0;

		network.registerMessage(ConfigSyncMessage.class, ConfigSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(DimSyncMessage.class, DimSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(MiningSyncMessage.class, MiningSyncMessage.class, id++, Side.CLIENT);
		network.registerMessage(CaveSoundMessage.class, CaveSoundMessage.class, id++, Side.CLIENT);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		Config.syncPostConfig();

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