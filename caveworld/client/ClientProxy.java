/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client;

import caveworld.client.config.CycleIntegerEntry;
import caveworld.client.config.MiningPointsEntry;
import caveworld.client.config.SelectBiomeEntry;
import caveworld.client.config.SelectItemEntry;
import caveworld.client.gui.GuiIngameCaveworldMenu;
import caveworld.client.gui.MenuType;
import caveworld.client.renderer.RenderCaveman;
import caveworld.client.renderer.RenderCavenicBow;
import caveworld.client.renderer.RenderCavenicSkeleton;
import caveworld.client.renderer.RenderCaveniumTool;
import caveworld.client.renderer.RenderMasterCavenicSkeleton;
import caveworld.client.renderer.RenderOreOverlay;
import caveworld.client.renderer.RenderPortalCaveworld;
import caveworld.client.renderer.TileEntityUniversalChestRenderer;
import caveworld.core.CommonProxy;
import caveworld.core.Config;
import caveworld.entity.EntityArcherZombie;
import caveworld.entity.EntityCaveman;
import caveworld.entity.EntityCavenicSkeleton;
import caveworld.entity.EntityMasterCavenicSkeleton;
import caveworld.entity.TileEntityUniversalChest;
import caveworld.item.CaveItems;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void initializeConfigEntries()
	{
		Config.selectItems = SelectItemEntry.class;
		Config.selectBiomes = SelectBiomeEntry.class;
		Config.cycleInteger = CycleIntegerEntry.class;
		Config.pointsEntry = MiningPointsEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(new RenderPortalCaveworld());
		RenderingRegistry.registerBlockHandler(new RenderOreOverlay());

		TileEntityUniversalChestRenderer chestRenderer = new TileEntityUniversalChestRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUniversalChest.class, chestRenderer);
		RenderingRegistry.registerBlockHandler(Config.RENDER_TYPE_CHEST, chestRenderer);

		IItemRenderer itemRenderer = new RenderCaveniumTool();
		MinecraftForgeClient.registerItemRenderer(CaveItems.mining_pickaxe, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.lumbering_axe, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.digging_shovel, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.cavenic_bow, new RenderCavenicBow());

		RenderingRegistry.registerEntityRenderingHandler(EntityCaveman.class, new RenderCaveman());
		RenderingRegistry.registerEntityRenderingHandler(EntityArcherZombie.class, new RenderZombie());
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSkeleton.class, new RenderCavenicSkeleton());
		RenderingRegistry.registerEntityRenderingHandler(EntityMasterCavenicSkeleton.class, new RenderMasterCavenicSkeleton());
	}

	@Override
	public int getUniqueRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void displayMenu(MenuType type)
	{
		FMLClientHandler.instance().showGuiScreen(new GuiIngameCaveworldMenu().setMenuType(type));
	}

	@Override
	public void displayPortalMenu(MenuType type, int x, int y, int z)
	{
		FMLClientHandler.instance().showGuiScreen(new GuiIngameCaveworldMenu().setMenuType(type).setPortalCoord(x, y, z));
	}
}