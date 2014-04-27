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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Strings;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayCaveSoundPacket extends AbstractPacket
{
	private String name;

	public PlayCaveSoundPacket() {}

	public PlayCaveSoundPacket(String name)
	{
		this.name = name;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, name);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		name = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer player)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc != null && !Strings.isNullOrEmpty(name))
		{
			SoundHandler handler = mc.getSoundHandler();
			ISound sound = PositionedSoundRecord.func_147673_a(new ResourceLocation(name));

			if (handler == null || sound == null)
			{
				return;
			}

			if (!handler.isSoundPlaying(sound))
			{
				handler.playSound(sound);
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {}
}