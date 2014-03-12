/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.packet;

import com.kegare.caveworld.world.WorldProviderCaveworld;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;

public class DataSyncPacket extends AbstractPacket
{
	private long dimensionSeed;
	private int subsurfaceHeight;

	public DataSyncPacket()
	{
		dimensionSeed = WorldProviderCaveworld.dimensionSeed;
		subsurfaceHeight = WorldProviderCaveworld.subsurfaceHeight;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeLong(dimensionSeed);
		buffer.writeInt(subsurfaceHeight);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		dimensionSeed = buffer.readLong();
		subsurfaceHeight = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayerSP player)
	{
		WorldProviderCaveworld.dimensionSeed = dimensionSeed;
		WorldProviderCaveworld.subsurfaceHeight = subsurfaceHeight;
	}

	@Override
	public void handleServerSide(EntityPlayerMP player) {}
}