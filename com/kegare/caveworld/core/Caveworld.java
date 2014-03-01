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

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.handler.CaveEventHooks;
import com.kegare.caveworld.packet.CaveBiomeSyncPacket;
import com.kegare.caveworld.packet.CaveOreSyncPacket;
import com.kegare.caveworld.packet.ConfigSyncPacket;
import com.kegare.caveworld.packet.DataSyncPacket;
import com.kegare.caveworld.packet.MiningCountPacket;
import com.kegare.caveworld.packet.PacketPipeline;
import com.kegare.caveworld.packet.PlayCaveSoundPacket;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

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
		initRecipes();

		proxy.registerRenderers();

		AchievementPage.registerAchievementPage(new AchievementPage("Caveworld", CaveAchievementList.getAchievementArray()));

		DimensionManager.registerProviderType(Config.dimensionCaveworld, WorldProviderCaveworld.class, true);
		DimensionManager.registerDimension(Config.dimensionCaveworld, Config.dimensionCaveworld);

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);

		FMLCommonHandler.instance().bus().register(CaveEventHooks.instance);

		packetPipeline.init("kegare.caveworld");
		packetPipeline.registerPacket(ConfigSyncPacket.class);
		packetPipeline.registerPacket(DataSyncPacket.class);
		packetPipeline.registerPacket(CaveBiomeSyncPacket.class);
		packetPipeline.registerPacket(CaveOreSyncPacket.class);
		packetPipeline.registerPacket(PlayCaveSoundPacket.class);
		packetPipeline.registerPacket(MiningCountPacket.class);
	}

	private void initRecipes()
	{
		if (Version.DEV_DEBUG || Config.portalCraftRecipe)
		{
			GameRegistry.addShapedRecipe(new ItemStack(CaveBlocks.caveworld_portal),
					" E ", "EPE", " D ",
					'E', Items.emerald, 'P', Items.ender_pearl, 'D', Items.diamond
			);
		}

		if (Version.DEV_DEBUG || Config.mossStoneCraftRecipe)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Blocks.mossy_cobblestone),
					" V ", "VCV", " V ",
					'V', Blocks.vine, 'C', Blocks.cobblestone
			);
		}
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