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

import java.util.Collection;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.util.BlockEntry;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BiomeSyncMessage implements IMessage, IMessageHandler<BiomeSyncMessage, IMessage>
{
	private String data;

	public BiomeSyncMessage() {}

	public BiomeSyncMessage(Collection<CaveBiome> biomes)
	{
		List<String> dat = Lists.newArrayList();

		for (CaveBiome biome : biomes)
		{
			if (biome.itemWeight <= 0)
			{
				continue;
			}

			dat.add(biome.toString());
		}

		this.data = Joiner.on('&').join(dat);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		data = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, data);
	}

	@Override
	public IMessage onMessage(BiomeSyncMessage message, MessageContext ctx)
	{
		CaveBiomeManager.clearCaveBiomes();

		try
		{
			List<String> list;
			BiomeGenBase biome;
			int weight;
			int metadata;

			for (String entry : Splitter.on('&').splitToList(message.data))
			{
				list = Splitter.on(',').splitToList(entry);
				biome = BiomeGenBase.getBiome(NumberUtils.toInt(list.get(0), BiomeGenBase.plains.biomeID));
				weight = NumberUtils.toInt(list.get(1));
				metadata = NumberUtils.toInt(list.get(3));

				if (biome == null)
				{
					continue;
				}

				CaveBiomeManager.addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(list.get(2), metadata, Blocks.stone)));
			}

			CaveLog.info("Loaded %d cave biomes from server", CaveBiomeManager.getActiveBiomeCount());
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading cave biomes from server");
		}

		return null;
	}
}