package com.kegare.caveworld.packet;

import com.google.common.base.Strings;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class PlayCaveSoundPacket extends AbstractPacket
{
	private String domain;
	private String name;

	public PlayCaveSoundPacket() {}

	public PlayCaveSoundPacket(String name)
	{
		this("caveworld", name);
	}

	public PlayCaveSoundPacket(String domain, String name)
	{
		this.domain = domain;
		this.name = name;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, domain);
		ByteBufUtils.writeUTF8String(buffer, name);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		domain = ByteBufUtils.readUTF8String(buffer);
		name = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayerSP player)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc != null && !Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(domain))
		{
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation(domain, name)));
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP player) {}
}