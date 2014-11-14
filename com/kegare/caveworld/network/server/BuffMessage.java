/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

import com.kegare.caveworld.api.CaveworldAPI;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BuffMessage implements IMessage, IMessageHandler<BuffMessage, IMessage>
{
	private PotionEffect effect;

	public BuffMessage() {}

	public BuffMessage(PotionEffect effect)
	{
		this.effect = effect;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		effect = new PotionEffect(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(effect.getPotionID());
		buffer.writeInt(effect.getDuration());
		buffer.writeInt(effect.getAmplifier());
		buffer.writeBoolean(effect.getIsAmbient());
	}

	@Override
	public IMessage onMessage(BuffMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;

		CaveworldAPI.addMiningPoint(player, -message.effect.getDuration() / 20);

		player.addPotionEffect(message.effect);

		return null;
	}
}