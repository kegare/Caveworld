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

import com.kegare.caveworld.world.ChunkProviderDeepCaveworld;
import com.kegare.caveworld.world.WorldProviderDeepCaveworld;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DimDeepSyncMessage implements IMessage, IMessageHandler<DimDeepSyncMessage, IMessage>
{
	private int dimensionId;
	private NBTTagCompound data;

	public DimDeepSyncMessage() {}

	public DimDeepSyncMessage(int dim, NBTTagCompound data)
	{
		this.dimensionId = dim;
		this.data = data;
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
	public IMessage onMessage(DimDeepSyncMessage message, MessageContext ctx)
	{
		ChunkProviderDeepCaveworld.dimensionId = message.dimensionId;
		WorldProviderDeepCaveworld.loadDimData(message.data);

		return null;
	}
}