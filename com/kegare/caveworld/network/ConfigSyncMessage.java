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
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ConfigSyncMessage implements IMessage, IMessageHandler<ConfigSyncMessage, IMessage>
{
	private String data;

	public ConfigSyncMessage() {}

	public ConfigSyncMessage(Object trigger)
	{
		List<String> dat = Lists.newArrayList();
		List<String> list = Lists.newArrayList();

		list.add(Boolean.toString(Config.deathLoseMiningPoint));
		list.add(Boolean.toString(Config.hardcore));
		list.add(Integer.toString(Config.dimensionCaveworld));
		list.add(Integer.toString(Config.subsurfaceHeight));
		list.add(Boolean.toString(Config.generateCaves));
		list.add(Boolean.toString(Config.generateRavine));
		list.add(Boolean.toString(Config.generateMineshaft));
		list.add(Boolean.toString(Config.generateStronghold));
		list.add(Boolean.toString(Config.generateLakes));
		list.add(Boolean.toString(Config.generateDungeons));
		list.add(Boolean.toString(Config.decorateVines));
		dat.add(Joiner.on(',').join(list));

		List<String> entries = Lists.newArrayList();

		for (ICaveBiome biome : CaveworldAPI.getCaveBiomes())
		{
			if (biome.getGenWeight() <= 0)
			{
				continue;
			}

			list.clear();
			list.add(Integer.toString(biome.getBiome().biomeID));
			list.add(Integer.toString(biome.getGenWeight()));
			list.add(Block.blockRegistry.getNameForObject(biome.getTerrainBlock().getBlock()));
			list.add(Integer.toString(biome.getTerrainBlock().getMetadata()));

			entries.add(Joiner.on(',').join(list));
		}

		dat.add(Joiner.on('&').join(entries));
		entries.clear();

		Set<String> biomes = Sets.newHashSet();
		ICaveVein vein;

		for (Entry<String, ICaveVein> entry : CaveworldAPI.getCaveVeins().entrySet())
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

			entries.add(entry.getKey() + "#" + Joiner.on(',').join(list));
		}

		dat.add(Joiner.on('&').join(entries));

		this.data = Joiner.on('|').join(dat);
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
	public IMessage onMessage(ConfigSyncMessage message, MessageContext ctx)
	{
		final List<String> dat = Splitter.on('|').splitToList(message.data);
		ExecutorService pool = Executors.newCachedThreadPool();

		pool.execute(new Runnable()
		{
			@Override
			public void run()
			{
				List<String> list = Splitter.on(',').splitToList(dat.get(0));
				byte i = 0;

				Config.deathLoseMiningPoint = Boolean.valueOf(list.get(i++));
				Config.hardcore = Boolean.valueOf(list.get(i++));
				Config.dimensionCaveworld = Integer.valueOf(list.get(i++));
				Config.subsurfaceHeight = Integer.valueOf(list.get(i++));
				Config.generateCaves = Boolean.valueOf(list.get(i++));
				Config.generateRavine = Boolean.valueOf(list.get(i++));
				Config.generateMineshaft = Boolean.valueOf(list.get(i++));
				Config.generateStronghold = Boolean.valueOf(list.get(i++));
				Config.generateLakes = Boolean.valueOf(list.get(i++));
				Config.generateDungeons = Boolean.valueOf(list.get(i++));
				Config.decorateVines = Boolean.valueOf(list.get(i++));
			}
		});

		pool.execute(new Runnable()
		{
			@Override
			public void run()
			{
				List<String> list;
				BiomeGenBase biome;
				int weight;
				int metadata;

				CaveworldAPI.clearCaveBiomes();

				for (String entry : Splitter.on('&').splitToList(dat.get(1)))
				{
					list = Splitter.on(',').splitToList(entry);
					biome = BiomeGenBase.getBiome(NumberUtils.toInt(list.get(0), BiomeGenBase.plains.biomeID));
					weight = NumberUtils.toInt(list.get(1));
					metadata = NumberUtils.toInt(list.get(3));

					if (biome == null)
					{
						continue;
					}

					CaveworldAPI.addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(list.get(2), metadata)));
				}

				CaveLog.info("Loaded %d cave biomes from server", CaveworldAPI.getActiveBiomeCount());
			}
		});

		pool.execute(new Runnable()
		{
			@Override
			public void run()
			{
				String[] temp;
				List<String> list;
				int metadata;
				int count;
				int weight;
				int min;
				int max;
				int target;
				int[] biomes;

				CaveworldAPI.clearCaveVeins();

				for (String entry : Splitter.on('&').splitToList(dat.get(2)))
				{
					temp = entry.split("#");
					list = Splitter.on(',').splitToList(temp[1]);
					metadata = NumberUtils.toInt(list.get(1));
					count = NumberUtils.toInt(list.get(2));
					weight = NumberUtils.toInt(list.get(3));
					min = NumberUtils.toInt(list.get(4));
					max = NumberUtils.toInt(list.get(5));
					target = NumberUtils.toInt(list.get(7));
					biomes = new int[] {};

					if (list.size() > 8)
					{
						for (String id : Splitter.on('.').splitToList(list.get(8)))
						{
							biomes = ArrayUtils.add(biomes, NumberUtils.toInt(id, -1));
						}
					}

					CaveworldAPI.addCaveVein(temp[0], new CaveVein(new BlockEntry(list.get(0), metadata), count, weight, min, max, new BlockEntry(list.get(6), target), biomes));
				}

				CaveLog.info("Loaded %d cave veins from server", CaveworldAPI.getCaveVeins().size());
			}
		});

		pool.shutdown();

		return null;
	}
}