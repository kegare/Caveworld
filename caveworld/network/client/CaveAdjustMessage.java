/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.world.ChunkProviderAquaCavern;
import caveworld.world.ChunkProviderCaveland;
import caveworld.world.ChunkProviderCavenia;
import caveworld.world.ChunkProviderCavern;
import caveworld.world.ChunkProviderCaveworld;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCaveland;
import caveworld.world.WorldProviderCavenia;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class CaveAdjustMessage implements IMessage, IMessageHandler<CaveAdjustMessage, IMessage>
{
	private int type;
	private int dimensionId;
	private NBTTagCompound data;

	public CaveAdjustMessage() {}

	public CaveAdjustMessage(int type, int dim, NBTTagCompound data)
	{
		this.type = type;
		this.dimensionId = dim;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		type = buffer.readInt();
		dimensionId = buffer.readInt();
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(type);
		buffer.writeInt(dimensionId);
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	public IMessage onMessage(CaveAdjustMessage message, MessageContext ctx)
	{
		int dim = message.dimensionId;
		NBTTagCompound dat = message.data;

		switch (message.type)
		{
			case WorldProviderCaveworld.TYPE:
				ChunkProviderCaveworld.dimensionId = dim;
				WorldProviderCaveworld.loadDimData(dat);
				break;
			case WorldProviderCavern.TYPE:
				ChunkProviderCavern.dimensionId = dim;
				WorldProviderCavern.loadDimData(dat);
				break;
			case WorldProviderAquaCavern.TYPE:
				ChunkProviderAquaCavern.dimensionId = dim;
				WorldProviderAquaCavern.loadDimData(dat);
				break;
			case WorldProviderCaveland.TYPE:
				ChunkProviderCaveland.dimensionId = dim;
				WorldProviderCaveland.loadDimData(dat);
				break;
			case WorldProviderCavenia.TYPE:
				ChunkProviderCavenia.dimensionId = dim;
				WorldProviderCavenia.loadDimData(dat);
				break;
		}

		return null;
	}
}