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

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncMessage implements IMessage, IMessageHandler<ConfigSyncMessage, IMessage>
{
	private String data;

	public ConfigSyncMessage()
	{
		StringBuilder builder = new StringBuilder();

		builder.append(Config.hardcoreEnabled).append(';');
		builder.append(Config.deathLoseMiningCount).append(';');
		builder.append(Config.dimensionCaveworld).append(';');
		builder.append(Config.subsurfaceHeight).append(';');
		builder.append(Config.generateCaves).append(';');
		builder.append(Config.generateRavine).append(';');
		builder.append(Config.generateMineshaft).append(';');
		builder.append(Config.generateStronghold).append(';');
		builder.append(Config.generateLakes).append(';');
		builder.append(Config.generateDungeons).append(';');
		builder.append(Config.decorateVines);

		this.data = builder.toString();
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
	public IMessage onMessage(ConfigSyncMessage message, MessageContext ctx)
	{
		String[] buffer = message.data.split(";");
		byte i = 0;

		try
		{
			Config.hardcoreEnabled = Boolean.valueOf(buffer[i++]);
			Config.deathLoseMiningCount = Boolean.valueOf(buffer[i++]);
			Config.dimensionCaveworld = Integer.valueOf(buffer[i++]);
			Config.subsurfaceHeight = Integer.valueOf(buffer[i++]);
			Config.generateCaves = Boolean.valueOf(buffer[i++]);
			Config.generateRavine = Boolean.valueOf(buffer[i++]);
			Config.generateMineshaft = Boolean.valueOf(buffer[i++]);
			Config.generateStronghold = Boolean.valueOf(buffer[i++]);
			Config.generateLakes = Boolean.valueOf(buffer[i++]);
			Config.generateDungeons = Boolean.valueOf(buffer[i++]);
			Config.decorateVines = Boolean.valueOf(buffer[i++]);
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading config values from server");
		}

		return null;
	}
}