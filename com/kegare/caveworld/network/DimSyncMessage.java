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

import com.google.common.base.Optional;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DimSyncMessage implements IMessage, IMessageHandler<DimSyncMessage, IMessage>
{
	private long dimensionSeed;
	private int subsurfaceHeight;

	public DimSyncMessage()
	{
		dimensionSeed = WorldProviderCaveworld.dimensionSeed.or(0L);
		subsurfaceHeight = WorldProviderCaveworld.subsurfaceHeight.or(127);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		dimensionSeed = buffer.readLong();
		subsurfaceHeight = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(dimensionSeed);
		buffer.writeInt(subsurfaceHeight);
	}

	@Override
	public IMessage onMessage(DimSyncMessage message, MessageContext ctx)
	{
		WorldProviderCaveworld.dimensionSeed = Optional.of(message.dimensionSeed);
		WorldProviderCaveworld.subsurfaceHeight = Optional.of(message.subsurfaceHeight);

		return null;
	}
}