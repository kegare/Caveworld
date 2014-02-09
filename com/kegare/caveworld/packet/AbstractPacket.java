package com.kegare.caveworld.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractPacket
{
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
	public abstract void handleClientSide(EntityPlayer player);
	public abstract void handleServerSide(EntityPlayer player);
}