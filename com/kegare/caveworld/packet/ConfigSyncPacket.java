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
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import com.kegare.caveworld.core.Config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigSyncPacket extends AbstractPacket
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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
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
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
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
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer player)
	{
		Config.hardcoreEnabled = hardcoreEnabled;
		Config.deathLoseMiningCount = deathLoseMiningCount;
		Config.dimensionCaveworld = dimensionCaveworld;
		Config.subsurfaceHeight = subsurfaceHeight;
		Config.generateCaves = generateCaves;
		Config.generateRavine = generateRavine;
		Config.generateMineshaft = generateMineshaft;
		Config.generateStronghold = generateStronghold;
		Config.generateLakes = generateLakes;
		Config.generateDungeons = generateDungeons;
		Config.decorateVines = decorateVines;
	}

	@Override
	public void handleServerSide(EntityPlayer player) {}
}