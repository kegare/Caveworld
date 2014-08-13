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

import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
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
		List<String> dat = Lists.newArrayList();

		dat.add(Boolean.toString(Config.deathLoseMiningCount));
		dat.add(Boolean.toString(Config.hardcore));
		dat.add(Integer.toString(Config.dimensionCaveworld));
		dat.add(Integer.toString(Config.subsurfaceHeight));
		dat.add(Boolean.toString(Config.generateCaves));
		dat.add(Boolean.toString(Config.generateRavine));
		dat.add(Boolean.toString(Config.generateMineshaft));
		dat.add(Boolean.toString(Config.generateStronghold));
		dat.add(Boolean.toString(Config.generateLakes));
		dat.add(Boolean.toString(Config.generateDungeons));
		dat.add(Boolean.toString(Config.decorateVines));

		this.data = Joiner.on(',').join(dat);
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
		List<String> dat = Splitter.on(',').splitToList(message.data);
		byte i = 0;

		try
		{
			Config.deathLoseMiningCount = Boolean.valueOf(dat.get(i++));
			Config.hardcore = Boolean.valueOf(dat.get(i++));
			Config.dimensionCaveworld = Integer.valueOf(dat.get(i++));
			Config.subsurfaceHeight = Integer.valueOf(dat.get(i++));
			Config.generateCaves = Boolean.valueOf(dat.get(i++));
			Config.generateRavine = Boolean.valueOf(dat.get(i++));
			Config.generateMineshaft = Boolean.valueOf(dat.get(i++));
			Config.generateStronghold = Boolean.valueOf(dat.get(i++));
			Config.generateLakes = Boolean.valueOf(dat.get(i++));
			Config.generateDungeons = Boolean.valueOf(dat.get(i++));
			Config.decorateVines = Boolean.valueOf(dat.get(i++));
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading config values from server");
		}

		return null;
	}
}