/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.network;

import io.netty.buffer.ByteBuf;

import com.kegare.caveworld.core.CaveMiningPlayer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MiningSyncMessage implements IMessage, IMessageHandler<MiningSyncMessage, IMessage>
{
	private int count;
	private int level;

	public MiningSyncMessage() {}

	public MiningSyncMessage(int count, int level)
	{
		this.count = count;
		this.level = level;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		count = buffer.readInt();
		level = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(count);
		buffer.writeInt(level);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MiningSyncMessage message, MessageContext ctx)
	{
		CaveMiningPlayer data = CaveMiningPlayer.get(FMLClientHandler.instance().getClientPlayerEntity());

		if (data != null)
		{
			data.setMiningCount(message.count);
			data.setMiningLevel(message.level);
		}

		return null;
	}
}