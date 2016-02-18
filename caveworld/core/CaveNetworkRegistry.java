/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import caveworld.network.client.BiomeAdjustMessage;
import caveworld.network.client.CaveAdjustMessage;
import caveworld.network.client.CaveMusicMessage;
import caveworld.network.client.CaverAdjustMessage;
import caveworld.network.client.CaveworldMenuMessage;
import caveworld.network.client.MultiBreakCountMessage;
import caveworld.network.client.OpenUrlMessage;
import caveworld.network.client.VeinAdjustMessage;
import caveworld.network.common.RegenerateMessage;
import caveworld.network.server.CaveAchievementMessage;
import caveworld.network.server.PortalInventoryMessage;
import caveworld.network.server.SelectBreakableMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;

public class CaveNetworkRegistry
{
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(Caveworld.MODID);

	public static int messageId;

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		network.registerMessage(messageHandler, requestMessageType, messageId++, side);
	}

	public static Packet getPacket(IMessage message)
	{
		return network.getPacketFrom(message);
	}

	public static void sendToAll(IMessage message)
	{
		network.sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player)
	{
		network.sendTo(message, player);
	}

	public static void sendToDimension(IMessage message, int dimensionId)
	{
		network.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message)
	{
		network.sendToServer(message);
	}

	public static void registerMessages()
	{
		registerMessage(CaveAdjustMessage.class, CaveAdjustMessage.class, Side.CLIENT);
		registerMessage(CaverAdjustMessage.class, CaverAdjustMessage.class, Side.CLIENT);
		registerMessage(BiomeAdjustMessage.class, BiomeAdjustMessage.class, Side.CLIENT);
		registerMessage(VeinAdjustMessage.class, VeinAdjustMessage.class, Side.CLIENT);
		registerMessage(OpenUrlMessage.class, OpenUrlMessage.class, Side.CLIENT);
		registerMessage(CaveMusicMessage.class, CaveMusicMessage.class, Side.CLIENT);
		registerMessage(RegenerateMessage.class, RegenerateMessage.class, Side.CLIENT);
		registerMessage(RegenerateMessage.class, RegenerateMessage.class, Side.SERVER);
		registerMessage(RegenerateMessage.ProgressNotify.class, RegenerateMessage.ProgressNotify.class, Side.CLIENT);
		registerMessage(CaveworldMenuMessage.class, CaveworldMenuMessage.class, Side.CLIENT);
		registerMessage(CaveAchievementMessage.class, CaveAchievementMessage.class, Side.SERVER);
		registerMessage(SelectBreakableMessage.class, SelectBreakableMessage.class, Side.SERVER);
		registerMessage(MultiBreakCountMessage.class, MultiBreakCountMessage.class, Side.CLIENT);
		registerMessage(PortalInventoryMessage.class, PortalInventoryMessage.class, Side.SERVER);
	}
}