/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import caveworld.network.CaveNetworkRegistry;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;

public class MCEEventHooks
{
	@SideOnly(Side.CLIENT)
	public static IShopProductManager prevProductManager;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (!mc.isIntegratedServerRunning())
		{
			prevProductManager = MCEconomyPlugin.productManager;
			MCEconomyPlugin.productManager = new ShopProductManager();
			MCEconomyPlugin.swapShop(prevProductManager, MCEconomyPlugin.productManager);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientDisconnected(ClientDisconnectionFromServerEvent event)
	{
		if (prevProductManager != null)
		{
			MCEconomyPlugin.swapShop(MCEconomyPlugin.productManager, prevProductManager);
			MCEconomyPlugin.productManager = prevProductManager;
			prevProductManager = null;
		}
	}

	@SubscribeEvent
	public void onServerConnected(ServerConnectionFromClientEvent event)
	{
		NetworkManager manager = event.manager;

		if (!manager.isLocalChannel())
		{
			manager.scheduleOutboundPacket(CaveNetworkRegistry.getPacket(new ProductAdjustMessage(MCEconomyPlugin.productManager)));
		}
	}
}