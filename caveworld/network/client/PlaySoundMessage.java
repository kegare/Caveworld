/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

public class PlaySoundMessage implements IMessage, IMessageHandler<PlaySoundMessage, IMessage>
{
	private ResourceLocation resource;

	public PlaySoundMessage() {}

	public PlaySoundMessage(ResourceLocation resource)
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

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(PlaySoundMessage message, MessageContext ctx)
	{
		SoundHandler handler = FMLClientHandler.instance().getClient().getSoundHandler();
		ISound sound = PositionedSoundRecord.func_147673_a(message.resource);

		if (!handler.isSoundPlaying(sound))
		{
			handler.playSound(sound);
		}

		return null;
	}
}