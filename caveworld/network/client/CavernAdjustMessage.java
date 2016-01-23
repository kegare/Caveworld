/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.world.ChunkProviderCavern;
import caveworld.world.WorldProviderCavern;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class CavernAdjustMessage implements IMessage, IMessageHandler<CavernAdjustMessage, IMessage>
{
	private int dimensionId;
	private NBTTagCompound data;

	public CavernAdjustMessage() {}

	public CavernAdjustMessage(int dim, NBTTagCompound data)
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
	public IMessage onMessage(CavernAdjustMessage message, MessageContext ctx)
	{
		ChunkProviderCavern.dimensionId = message.dimensionId;
		WorldProviderCavern.loadDimData(message.data);

		return null;
	}
}