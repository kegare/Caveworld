/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SetBlockMessage implements IMessage, IMessageHandler<SetBlockMessage, IMessage>
{
	private int x;
	private int y;
	private int z;
	private Block block;
	private int metadata;
	private int flag;

	public SetBlockMessage() {}

	public SetBlockMessage(int x, int y, int z, Block block, int metadata, int flag)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.block = block;
		this.metadata= metadata;
		this.flag = flag;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		block = GameData.getBlockRegistry().getObject(ByteBufUtils.readUTF8String(buffer));
		metadata = buffer.readInt();
		flag = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		ByteBufUtils.writeUTF8String(buffer, GameData.getBlockRegistry().getNameForObject(block));
		buffer.writeInt(metadata);
		buffer.writeInt(flag);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(SetBlockMessage message, MessageContext ctx)
	{
		FMLClientHandler.instance().getWorldClient().setBlock(message.x, message.y, message.z, message.block, message.metadata, message.flag);

		return null;
	}
}