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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveSoundMessage implements IMessage, IMessageHandler<CaveSoundMessage, IMessage>
{
	private ResourceLocation resource;

	public CaveSoundMessage() {}

	public CaveSoundMessage(ResourceLocation resource)
	{
		this.resource = resource;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		String domain = ByteBufUtils.readUTF8String(buffer);
		String path = ByteBufUtils.readUTF8String(buffer);

		resource = new ResourceLocation(domain, path);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, resource.getResourceDomain());
		ByteBufUtils.writeUTF8String(buffer, resource.getResourcePath());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(CaveSoundMessage message, MessageContext ctx)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc != null)
		{
			SoundHandler handler = mc.getSoundHandler();
			ISound sound = PositionedSoundRecord.func_147673_a(message.resource);

			if (!handler.isSoundPlaying(sound))
			{
				handler.playSound(sound);
			}
		}

		return null;
	}
}