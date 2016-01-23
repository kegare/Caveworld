/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import java.awt.Desktop;
import java.net.URI;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class OpenUrlMessage implements IMessage, IMessageHandler<OpenUrlMessage, IMessage>
{
	private String url;

	public OpenUrlMessage() {}

	public OpenUrlMessage(String url)
	{
		this.url = url;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		url = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, url);
	}

	@Override
	public IMessage onMessage(OpenUrlMessage message, MessageContext ctx)
	{
		try
		{
			Desktop.getDesktop().browse(new URI(message.url));
		}
		catch (Exception ignored) {}

		return null;
	}
}