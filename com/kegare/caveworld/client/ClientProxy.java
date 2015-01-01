/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client;

import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import com.kegare.caveworld.client.config.CycleIntegerEntry;
import com.kegare.caveworld.client.config.SelectBiomeEntry;
import com.kegare.caveworld.client.config.SelectItemEntry;
import com.kegare.caveworld.client.renderer.RenderCaveman;
import com.kegare.caveworld.client.renderer.RenderCaveniumTool;
import com.kegare.caveworld.client.renderer.RenderPortalCaveworld;
import com.kegare.caveworld.client.renderer.TileEntityUniversalChestRenderer;
import com.kegare.caveworld.core.CommonProxy;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.EntityArcherZombie;
import com.kegare.caveworld.entity.EntityCaveman;
import com.kegare.caveworld.entity.TileEntityUniversalChest;
import com.kegare.caveworld.item.CaveItems;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void initializeConfigEntries()
	{
		Config.selectItems = SelectItemEntry.class;
		Config.selectBiomes = SelectBiomeEntry.class;
		Config.cycleInteger = CycleIntegerEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(new RenderPortalCaveworld());

		TileEntityUniversalChestRenderer chestRenderer = new TileEntityUniversalChestRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUniversalChest.class, chestRenderer);
		RenderingRegistry.registerBlockHandler(Config.RENDER_TYPE_CHEST, chestRenderer);

		IItemRenderer itemRenderer = new RenderCaveniumTool();
		MinecraftForgeClient.registerItemRenderer(CaveItems.mining_pickaxe, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.lumbering_axe, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.digging_shovel, itemRenderer);

		RenderingRegistry.registerEntityRenderingHandler(EntityCaveman.class, new RenderCaveman());
		RenderingRegistry.registerEntityRenderingHandler(EntityArcherZombie.class, new RenderZombie());
	}

	@Override
	public int getUniqueRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
}