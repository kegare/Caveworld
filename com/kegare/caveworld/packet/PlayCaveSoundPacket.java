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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Strings;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PlayCaveSoundPacket implements IMessage, IMessageHandler<PlayCaveSoundPacket, IMessage>
{
	private String name;

	public PlayCaveSoundPacket() {}

	public PlayCaveSoundPacket(String name)
	{
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		name = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, name);
	}

	@Override
	public IMessage onMessage(PlayCaveSoundPacket message, MessageContext ctx)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc != null && !Strings.isNullOrEmpty(message.name))
		{
			SoundHandler handler = mc.getSoundHandler();
			ISound sound = PositionedSoundRecord.func_147673_a(new ResourceLocation(message.name));

			if (handler == null || sound == null)
			{
				return null;
			}

			if (!handler.isSoundPlaying(sound))
			{
				handler.playSound(sound);
			}
		}

		return null;
	}
}