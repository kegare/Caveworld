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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.Level;

public class CaveBiomeSyncPacket extends AbstractPacket
{
	private String data;

	public CaveBiomeSyncPacket()
	{
		StringBuilder builder = new StringBuilder(1024);

		builder.append('{');

		for (CaveBiome biome : CaveBiomeManager.getCaveBiomes())
		{
			if (biome.itemWeight <= 0)
			{
				continue;
			}

			builder.append(biome).append(',');
		}

		this.data = builder.deleteCharAt(builder.lastIndexOf(",")).append('}').toString();
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
	public void handleClientSide(EntityPlayerSP player)
	{
		if (!Strings.isNullOrEmpty(data))
		{
			CaveBiomeManager.clearCaveBiomes();

			try
			{
				if (CaveBiomeManager.loadCaveBiomesFromString(data))
				{
					CaveLog.info("Loaded %d cave biomes from server", CaveBiomeManager.getActiveBiomeCount());
				}
			}
			catch (Exception e)
			{
				CaveLog.log(Level.WARN, e, "An error occurred trying to loading cave biomes from server");
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP player) {}
}