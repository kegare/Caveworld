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

import com.kegare.caveworld.world.ChunkProviderAquaCaveworld;
import com.kegare.caveworld.world.WorldProviderAquaCaveworld;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class DimAquaSyncMessage implements IMessage, IMessageHandler<DimAquaSyncMessage, IMessage>
{
	private int dimensionId;
	private NBTTagCompound data;
	private boolean livingAssist;

	public DimAquaSyncMessage() {}

	public DimAquaSyncMessage(int dim, NBTTagCompound data, boolean assist)
	{
		this.dimensionId = dim;
		this.data = data;
		this.livingAssist = assist;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		dimensionId = buffer.readInt();
		data = ByteBufUtils.readTag(buffer);
		livingAssist = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(dimensionId);
		ByteBufUtils.writeTag(buffer, data);
		buffer.writeBoolean(livingAssist);
	}

	@Override
	public IMessage onMessage(DimAquaSyncMessage message, MessageContext ctx)
	{
		ChunkProviderAquaCaveworld.dimensionId = message.dimensionId;
		WorldProviderAquaCaveworld.loadDimData(message.data);
		ChunkProviderAquaCaveworld.aquaLivingAssist = message.livingAssist;

		return null;
	}
}