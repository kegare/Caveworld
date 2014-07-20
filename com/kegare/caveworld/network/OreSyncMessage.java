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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.kegare.caveworld.core.CaveOreManager;
import com.kegare.caveworld.core.CaveOreManager.CaveOre;
import com.kegare.caveworld.util.BlockEntry;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class OreSyncMessage implements IMessage, IMessageHandler<OreSyncMessage, IMessage>
{
	private String data;

	public OreSyncMessage() {}

	public OreSyncMessage(Collection<CaveOre> ores)
	{
		List<String> dat = Lists.newArrayList();

		for (CaveOre ore : ores)
		{
			dat.add(ore.toString());
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
	public IMessage onMessage(OreSyncMessage message, MessageContext ctx)
	{
		CaveOreManager.clearCaveOres();

		try
		{
			List<String> list;
			int metadata;
			int count;
			int weight;
			int min;
			int max;
			int target;
			int[] biomes;

			for (String entry : Splitter.on('&').splitToList(message.data))
			{
				list = Splitter.on(',').splitToList(entry);
				metadata = NumberUtils.toInt(list.get(1));
				count = NumberUtils.toInt(list.get(2));
				weight = NumberUtils.toInt(list.get(3));
				min = NumberUtils.toInt(list.get(4));
				max = NumberUtils.toInt(list.get(5));
				target = NumberUtils.toInt(list.get(7));
				biomes = new int[] {};

				if (list.size() > 8)
				{
					for (String biome : Splitter.on('.').splitToList(list.get(8)))
					{
						biomes = ArrayUtils.add(biomes, NumberUtils.toInt(biome, -1));
					}
				}

				CaveOreManager.addCaveOre(new CaveOre(new BlockEntry(list.get(0), metadata, Blocks.stone), count, weight, min, max, new BlockEntry(list.get(6), target, Blocks.stone), biomes));
			}

			CaveLog.info("Loaded %d cave ores from server", CaveOreManager.getCaveOres().size());
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading cave ores from server");
		}

		return null;
	}
}