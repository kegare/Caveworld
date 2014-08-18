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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class VeinsSyncMessage implements IMessage, IMessageHandler<VeinsSyncMessage, IMessage>
{
	private String data;

	public VeinsSyncMessage() {}

	public VeinsSyncMessage(Map<String, ICaveVein> veins)
	{
		List<String> dat = Lists.newArrayList();
		List<String> list = Lists.newArrayList();
		Set<String> biomes = Sets.newHashSet();
		ICaveVein vein;

		for (Entry<String, ICaveVein> entry : veins.entrySet())
		{
			vein = entry.getValue();
			list.clear();
			list.add(Block.blockRegistry.getNameForObject(vein.getBlock().getBlock()));
			list.add(Integer.toString(vein.getBlock().getMetadata()));
			list.add(Integer.toString(vein.getGenBlockCount()));
			list.add(Integer.toString(vein.getGenWeight()));
			list.add(Integer.toString(vein.getGenMinHeight()));
			list.add(Integer.toString(vein.getGenMaxHeight()));
			list.add(Block.blockRegistry.getNameForObject(vein.getGenTargetBlock().getBlock()));
			list.add(Integer.toString(vein.getGenTargetBlock().getMetadata()));

			if (vein.getGenBiomes().length > 0)
			{
				biomes.clear();

				for (int id : vein.getGenBiomes())
				{
					biomes.add(Integer.toString(id));
				}

				list.add(Joiner.on('.').join(biomes));
			}

			dat.add(entry.getKey() + "#" + Joiner.on(',').join(list));
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
	public IMessage onMessage(VeinsSyncMessage message, MessageContext ctx)
	{
		CaveworldAPI.clearCaveVeins();

		try
		{
			List<String> list;
			String[] dat;
			int metadata;
			int count;
			int weight;
			int min;
			int max;
			int target;
			int[] biomes;

			for (String entry : Splitter.on('&').splitToList(message.data))
			{
				dat = entry.split("#");
				list = Splitter.on(',').splitToList(dat[1]);
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

				CaveworldAPI.addCaveVein(dat[0], new CaveVein(new BlockEntry(list.get(0), metadata), count, weight, min, max, new BlockEntry(list.get(6), target), biomes));
			}

			CaveLog.info("Loaded %d cave veins from server", CaveworldAPI.getCaveVeins().size());
		}
		catch (Exception e)
		{
			CaveLog.log(Level.WARN, e, "An error occurred trying to loading cave veins from server");
		}

		return null;
	}
}