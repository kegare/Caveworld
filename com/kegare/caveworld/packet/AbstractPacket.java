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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class AbstractPacket
{
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer);

	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer);

	public abstract void handleClientSide(EntityPlayerSP player);

	public abstract void handleServerSide(EntityPlayerMP player);
}