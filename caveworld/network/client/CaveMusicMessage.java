/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.core.Config;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

public class CaveMusicMessage implements IMessage, IMessageHandler<CaveMusicMessage, IMessage>
{
	@SideOnly(Side.CLIENT)
	public static ISound prevMusic;

	private String name;
	private boolean stop;

	public CaveMusicMessage() {}

	public CaveMusicMessage(String name)
	{
		this.name = name;
		this.stop = true;
	}

	public CaveMusicMessage(String name, boolean stop)
	{
		this(name);
		this.stop = stop;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		name = ByteBufUtils.readUTF8String(buffer);
		stop = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, name);
		buffer.writeBoolean(stop);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaveMusicMessage message, MessageContext ctx)
	{
		SoundHandler handler = FMLClientHandler.instance().getClient().getSoundHandler();

		if (prevMusic != null)
		{
			if (message.stop)
			{
				handler.stopSound(prevMusic);

				prevMusic = null;
			}
			else if (handler.isSoundPlaying(prevMusic))
			{
				return null;
			}
		}

		if (Config.caveMusicVolume > 0.0D)
		{
			ISound sound = PositionedSoundRecord.func_147673_a(new ResourceLocation("caveworld", message.name));
			ObfuscationReflectionHelper.setPrivateValue(PositionedSound.class, (PositionedSound)sound, Config.caveMusicVolume, "volume", "field_147662_b");

			handler.playSound(sound);

			prevMusic = sound;
		}

		return null;
	}
}