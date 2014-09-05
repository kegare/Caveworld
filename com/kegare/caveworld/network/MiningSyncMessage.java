/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.kegare.caveworld.api.CaveworldAPI;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MiningSyncMessage implements IMessage, IMessageHandler<MiningSyncMessage, IMessage>
{
	private NBTTagCompound data = new NBTTagCompound();

	public MiningSyncMessage() {}

	public MiningSyncMessage(EntityPlayer player)
	{
		CaveworldAPI.saveMiningData(player, data);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, data);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(MiningSyncMessage message, MessageContext ctx)
	{
		CaveworldAPI.loadMiningData(FMLClientHandler.instance().getClientPlayerEntity(), message.data);

		return null;
	}
}