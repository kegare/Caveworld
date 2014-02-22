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

import com.google.common.base.Strings;
import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.util.CaveLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Iterator;
import java.util.Set;

public class CaveBiomeSyncPacket extends AbstractPacket
{
	private String data;

	public CaveBiomeSyncPacket()
	{
		data = "";

		Iterator<CaveBiome> biomes = CaveBiomeManager.getCaveBiomes().iterator();

		while (biomes.hasNext())
		{
			data += biomes.next();

			if (biomes.hasNext())
			{
				data += ",";
			}
		}
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, data);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		data = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayerSP player)
	{
		if (!Strings.isNullOrEmpty(data))
		{
			CaveBiomeManager.clearCaveBiomes();
			CaveBiomeManager.loadCaveBiomesFromString(data);

			Set<CaveBiome> biomes = CaveBiomeManager.getCaveBiomes();

			if (!biomes.isEmpty())
			{
				CaveLog.info("Loaded %d cave biomes from server", biomes.size());
			}
		}
	}

	@Override
	@SideOnly(Side.SERVER)
	public void handleServerSide(EntityPlayerMP player) {}
}