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
import net.minecraft.nbt.NBTTagCompound;

import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DimSyncMessage implements IMessage, IMessageHandler<DimSyncMessage, IMessage>
{
	private NBTTagCompound data;

	public DimSyncMessage() {}

	public DimSyncMessage(NBTTagCompound compound)
	{
		this.data = compound;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	public IMessage onMessage(DimSyncMessage message, MessageContext ctx)
	{
		WorldProviderCaveworld.loadDimData(message.data);

		return null;
	}
}