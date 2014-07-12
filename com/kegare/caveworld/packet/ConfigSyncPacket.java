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

import com.kegare.caveworld.core.Config;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncPacket implements IMessage, IMessageHandler<ConfigSyncPacket, IMessage>
{
	private boolean hardcoreEnabled;
	private boolean deathLoseMiningCount;
	private int dimensionCaveworld;
	private int subsurfaceHeight;
	private boolean generateCaves;
	private boolean generateRavine;
	private boolean generateMineshaft;
	private boolean generateStronghold;
	private boolean generateLakes;
	private boolean generateDungeons;
	private boolean decorateVines;

	public ConfigSyncPacket()
	{
		hardcoreEnabled = Config.hardcoreEnabled;
		deathLoseMiningCount = Config.deathLoseMiningCount;
		dimensionCaveworld = Config.dimensionCaveworld;
		subsurfaceHeight = Config.subsurfaceHeight;
		generateCaves = Config.generateCaves;
		generateRavine = Config.generateRavine;
		generateMineshaft = Config.generateMineshaft;
		generateStronghold = Config.generateStronghold;
		generateLakes = Config.generateLakes;
		generateDungeons = Config.generateDungeons;
		decorateVines = Config.decorateVines;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		hardcoreEnabled = buffer.readBoolean();
		deathLoseMiningCount = buffer.readBoolean();
		dimensionCaveworld = buffer.readInt();
		subsurfaceHeight = buffer.readInt();
		generateCaves = buffer.readBoolean();
		generateRavine = buffer.readBoolean();
		generateMineshaft = buffer.readBoolean();
		generateStronghold = buffer.readBoolean();
		generateLakes = buffer.readBoolean();
		generateDungeons = buffer.readBoolean();
		decorateVines = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(hardcoreEnabled);
		buffer.writeBoolean(deathLoseMiningCount);
		buffer.writeInt(dimensionCaveworld);
		buffer.writeInt(subsurfaceHeight);
		buffer.writeBoolean(generateCaves);
		buffer.writeBoolean(generateRavine);
		buffer.writeBoolean(generateMineshaft);
		buffer.writeBoolean(generateStronghold);
		buffer.writeBoolean(generateLakes);
		buffer.writeBoolean(generateDungeons);
		buffer.writeBoolean(decorateVines);
	}

	@Override
	public IMessage onMessage(ConfigSyncPacket message, MessageContext ctx)
	{
		Config.hardcoreEnabled = message.hardcoreEnabled;
		Config.deathLoseMiningCount = message.deathLoseMiningCount;
		Config.dimensionCaveworld = message.dimensionCaveworld;
		Config.subsurfaceHeight = message.subsurfaceHeight;
		Config.generateCaves = message.generateCaves;
		Config.generateRavine = message.generateRavine;
		Config.generateMineshaft = message.generateMineshaft;
		Config.generateStronghold = message.generateStronghold;
		Config.generateLakes = message.generateLakes;
		Config.generateDungeons = message.generateDungeons;
		Config.decorateVines = message.decorateVines;

		return null;
	}
}