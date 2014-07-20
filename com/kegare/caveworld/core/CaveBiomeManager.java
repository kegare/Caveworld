/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.util.BlockEntry;

public class CaveBiomeManager
{
	private static final SortedSet<CaveBiome> CAVE_BIOMES = Sets.newTreeSet(new Comparator<CaveBiome>()
	{
		@Override
		public int compare(CaveBiome o1, CaveBiome o2)
		{
			return Integer.valueOf(o1.biome.biomeID).compareTo(o2.biome.biomeID);
		}
	});

	private static final Map<Integer, Integer> genWeightMap = Maps.newHashMap();
	private static final Map<Integer, BlockEntry> terrainBlockMap = Maps.newHashMap();

	public static boolean addCaveBiome(CaveBiome biome)
	{
		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			if (caveBiome.biome.biomeID == biome.biome.biomeID)
			{
				caveBiome.itemWeight += biome.itemWeight;

				return false;
			}
		}

		return CAVE_BIOMES.add(biome);
	}

	public static boolean removeCaveBiome(BiomeGenBase biome)
	{
		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			if (caveBiome.biome.biomeID == biome.biomeID)
			{
				return CAVE_BIOMES.remove(caveBiome);
			}
		}

		return false;
	}

	public static int getActiveBiomeCount()
	{
		int count = 0;

		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			if (caveBiome.itemWeight > 0)
			{
				++count;
			}
		}

		return count;
	}

	public static int getBiomeGenWeight(BiomeGenBase biome)
	{
		if (genWeightMap.containsKey(biome.biomeID))
		{
			return genWeightMap.get(biome.biomeID);
		}

		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			if (caveBiome.biome.biomeID == biome.biomeID)
			{
				genWeightMap.put(biome.biomeID, caveBiome.itemWeight);

				return caveBiome.itemWeight;
			}
		}

		return 0;
	}

	public static BlockEntry getBiomeTerrainBlock(BiomeGenBase biome)
	{
		if (terrainBlockMap.containsKey(biome.biomeID))
		{
			return terrainBlockMap.get(biome.biomeID);
		}

		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			if (caveBiome.biome.biomeID == biome.biomeID)
			{
				BlockEntry entry = caveBiome.terrainBlock;
				Block block = entry.getBlock();

				if (block == null || block.getMaterial().isLiquid() || !block.getMaterial().isSolid() || block.getMaterial().isReplaceable())
				{
					entry = new BlockEntry(Blocks.stone, 0);
				}

				terrainBlockMap.put(biome.biomeID, entry);

				return entry;
			}
		}

		return new BlockEntry(Blocks.stone, 0);
	}

	public static BiomeGenBase getRandomCaveBiome(Random random)
	{
		try
		{
			return ((CaveBiome)WeightedRandom.getRandomItem(random, CAVE_BIOMES)).biome;
		}
		catch (Exception e)
		{
			return BiomeGenBase.plains;
		}
	}

	public static void clearCaveBiomes()
	{
		CAVE_BIOMES.clear();
	}

	public static ImmutableSet<CaveBiome> getCaveBiomes()
	{
		return new ImmutableSet.Builder<CaveBiome>().addAll(CAVE_BIOMES).build();
	}

	public static ImmutableList<BiomeGenBase> getBiomeList()
	{
		Set<BiomeGenBase> biomes = Sets.newHashSet();

		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			biomes.add(caveBiome.biome);
		}

		return new ImmutableList.Builder<BiomeGenBase>().addAll(biomes).build();
	}

	public static class CaveBiome extends WeightedRandom.Item
	{
		public final BiomeGenBase biome;
		public final BlockEntry terrainBlock;

		public CaveBiome(BiomeGenBase biome, int weight)
		{
			this(biome, weight, new BlockEntry(Blocks.stone, 0));
		}

		public CaveBiome(BiomeGenBase biome, int weight, BlockEntry block)
		{
			super(weight);
			this.biome = biome;
			this.terrainBlock = block;
		}

		@Override
		public String toString()
		{
			List<String> list = Lists.newArrayList();
			list.add(Integer.toString(biome.biomeID));
			list.add(Integer.toString(itemWeight));
			list.add(Block.blockRegistry.getNameForObject(terrainBlock.getBlock()));
			list.add(Integer.toString(terrainBlock.getMetadata()));

			return Joiner.on(',').join(list);
		}
	}
}