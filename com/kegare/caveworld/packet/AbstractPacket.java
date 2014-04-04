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