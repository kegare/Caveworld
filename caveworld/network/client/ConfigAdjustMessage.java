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
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class ConfigAdjustMessage implements IMessage, IMessageHandler<ConfigAdjustMessage, IMessage>
{
	private int caveworld;
	private int cavern;
	private int aqua;
	private int caveland;
	private int cavenia;

	public ConfigAdjustMessage() {}

	public ConfigAdjustMessage(int caveworld, int cavern, int aqua, int caveland, int cavenia)
	{
		this.caveworld = caveworld;
		this.cavern = cavern;
		this.aqua = aqua;
		this.caveland = caveland;
		this.cavenia = cavenia;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		caveworld = buffer.readInt();
		cavern = buffer.readInt();
		aqua = buffer.readInt();
		caveland = buffer.readInt();
		cavenia = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(caveworld);
		buffer.writeInt(cavern);
		buffer.writeInt(aqua);
		buffer.writeInt(caveland);
		buffer.writeInt(cavenia);
	}

	@Override
	public IMessage onMessage(ConfigAdjustMessage message, MessageContext ctx)
	{
		ChunkProviderCaveworld.dimensionId = message.caveworld;
		ChunkProviderCavern.dimensionId = message.cavern;
		ChunkProviderAquaCavern.dimensionId = message.aqua;
		ChunkProviderCaveland.dimensionId = message.caveland;
		ChunkProviderCavenia.dimensionId = message.cavenia;

		return null;
	}
}