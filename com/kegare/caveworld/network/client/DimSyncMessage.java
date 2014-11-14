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
import net.minecraft.nbt.NBTTagCompound;

import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.world.WorldProviderCaveworld;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DimSyncMessage implements IMessage, IMessageHandler<DimSyncMessage, IMessage>
{
	private int dimensionId;
	private NBTTagCompound data;

	public DimSyncMessage() {}

	public DimSyncMessage(int dim, NBTTagCompound compound)
	{
		this.dimensionId = dim;
		this.data = compound;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		dimensionId = buffer.readInt();
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(dimensionId);
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	public IMessage onMessage(DimSyncMessage message, MessageContext ctx)
	{
		Config.dimensionCaveworld = message.dimensionId;
		WorldProviderCaveworld.loadDimData(message.data);

		return null;
	}
}