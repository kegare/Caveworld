/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.packet;

import io.netty.buffer.ByteBuf;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.kegare.caveworld.core.CaveOreManager;
import com.kegare.caveworld.core.CaveOreManager.CaveOre;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CaveOreSyncPacket implements IMessage, IMessageHandler<CaveOreSyncPacket, IMessage>
{
	private String data;

	public CaveOreSyncPacket() {}

	public CaveOreSyncPacket(Collection<CaveOre> ores)
	{
		StringBuilder builder = new StringBuilder(1024);

		builder.append('[');

		for (CaveOre ore : ores)
		{
			builder.append(ore).append(',');
		}

		builder.deleteCharAt(builder.lastIndexOf(",")).append(']');

		this.data = StringUtils.deleteWhitespace(builder.toString());
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		data = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, data);
	}

	@Override
	public IMessage onMessage(CaveOreSyncPacket message, MessageContext ctx)
	{
		CaveOreManager.clearCaveOres();

		try
		{
			if (CaveOreManager.loadCaveOresFromString(message.data))
			{
				CaveLog.info("Loaded %d cave ores from server", CaveOreManager.getCaveOres().size());
			}
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading cave ores from server");
		}

		return null;
	}
}