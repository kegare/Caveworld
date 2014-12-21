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

import com.kegare.caveworld.util.breaker.MultiBreakExecutor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MultiBreakCountMessage implements IMessage, IMessageHandler<MultiBreakCountMessage, IMessage>
{
	private int count;

	public MultiBreakCountMessage () {}

	public MultiBreakCountMessage(int count)
	{
		this.count = count;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		count = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(count);
	}

	@Override
	public IMessage onMessage(MultiBreakCountMessage message, MessageContext ctx)
	{
		MultiBreakExecutor.positionsCount.set(message.count);

		return null;
	}
}