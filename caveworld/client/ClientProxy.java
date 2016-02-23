/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client;

import org.lwjgl.input.Keyboard;

import caveworld.client.config.CycleIntegerEntry;
import caveworld.client.config.MiningPointsEntry;
import caveworld.client.config.SelectBiomeEntry;
import caveworld.client.config.SelectItemEntry;
import caveworld.client.config.SelectItemWithBlockEntry;
import caveworld.client.config.SelectMobEntry;
import caveworld.client.gui.GuiIngameCaveworldMenu;
import caveworld.client.gui.MenuType;
import caveworld.client.renderer.RenderBlockOverlay;
import caveworld.client.renderer.RenderCavePortal;
import caveworld.client.renderer.RenderCaveman;
import caveworld.client.renderer.RenderCavenicBow;
import caveworld.client.renderer.RenderCavenicCreeper;
import caveworld.client.renderer.RenderCavenicSkeleton;
import caveworld.client.renderer.RenderCavenicSpider;
import caveworld.client.renderer.RenderCavenicZombie;
import caveworld.client.renderer.RenderCaveniumTool;
import caveworld.client.renderer.RenderCrazyCavenicSkeleton;
import caveworld.client.renderer.RenderFarmingHoe;
import caveworld.client.renderer.RenderMasterCavenicCreeper;
import caveworld.client.renderer.RenderMasterCavenicSkeleton;
import caveworld.client.renderer.TileEntityUniversalChestRenderer;
import caveworld.core.CommonProxy;
import caveworld.core.Config;
import caveworld.entity.EntityArcherZombie;
import caveworld.entity.EntityCaveman;
import caveworld.entity.EntityCavenicCreeper;
import caveworld.entity.EntityCavenicSkeleton;
import caveworld.entity.EntityCavenicSpider;
import caveworld.entity.EntityCavenicZombie;
import caveworld.entity.EntityCrazyCavenicSkeleton;
import caveworld.entity.EntityMasterCavenicCreeper;
import caveworld.entity.EntityMasterCavenicSkeleton;
import caveworld.entity.TileEntityUniversalChest;
import caveworld.item.CaveItems;
import caveworld.util.breaker.MultiBreakExecutor;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void initConfigEntries()
	{
		Config.selectItems = SelectItemEntry.class;
		Config.selectItemsWithBlocks = SelectItemWithBlockEntry.class;
		Config.selectBiomes = SelectBiomeEntry.class;
		Config.selectMobs = SelectMobEntry.class;
		Config.cycleInteger = CycleIntegerEntry.class;
		Config.pointsEntry = MiningPointsEntry.class;
	}

	@Override
	public void registerKeyBindings()
	{
		Config.keyBindAtCommand = new KeyBinding("key.atCommand", Keyboard.KEY_GRAVE, "key.categories.caveworld");

		ClientRegistry.registerKeyBinding(Config.keyBindAtCommand);
	}

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(new RenderCavePortal());
		RenderingRegistry.registerBlockHandler(new RenderBlockOverlay());

		TileEntityUniversalChestRenderer chestRenderer = new TileEntityUniversalChestRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUniversalChest.class, chestRenderer);
		RenderingRegistry.registerBlockHandler(Config.RENDER_TYPE_CHEST, chestRenderer);

		IItemRenderer itemRenderer = new RenderCaveniumTool();
		MinecraftForgeClient.registerItemRenderer(CaveItems.mining_pickaxe, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.lumbering_axe, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.digging_shovel, itemRenderer);
		MinecraftForgeClient.registerItemRenderer(CaveItems.farming_hoe, new RenderFarmingHoe());
		MinecraftForgeClient.registerItemRenderer(CaveItems.cavenic_bow, new RenderCavenicBow());

		RenderingRegistry.registerEntityRenderingHandler(EntityCaveman.class, new RenderCaveman());
		RenderingRegistry.registerEntityRenderingHandler(EntityArcherZombie.class, new RenderZombie());
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSkeleton.class, new RenderCavenicSkeleton());
		RenderingRegistry.registerEntityRenderingHandler(EntityMasterCavenicSkeleton.class, new RenderMasterCavenicSkeleton());
		RenderingRegistry.registerEntityRenderingHandler(EntityCrazyCavenicSkeleton.class, new RenderCrazyCavenicSkeleton());
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicCreeper.class, new RenderCavenicCreeper());
		RenderingRegistry.registerEntityRenderingHandler(EntityMasterCavenicCreeper.class, new RenderMasterCavenicCreeper());
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicZombie.class, new RenderCavenicZombie());
		RenderingRegistry.registerEntityRenderingHandler(EntityCavenicSpider.class, new RenderCavenicSpider());
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

	@Override
	public int getMultiBreakCount(EntityPlayer player)
	{
		return MultiBreakExecutor.positionsCount.get();
	}

	@Override
	public void setDebugBoundingBox(boolean flag)
	{
		RenderManager.debugBoundingBox = flag;
	}
}