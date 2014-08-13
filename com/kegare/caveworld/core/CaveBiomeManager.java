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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.ConfigCategory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveBiomeManager;

public class CaveBiomeManager implements ICaveBiomeManager
{
	private final SortedSet<CaveBiome> CAVE_BIOMES = Sets.newTreeSet(new Comparator<CaveBiome>()
	{
		@Override
		public int compare(CaveBiome o1, CaveBiome o2)
		{
			return Integer.valueOf(o1.getBiome().biomeID).compareTo(o2.getBiome().biomeID);
		}
	});

	private final Map<Integer, Integer> genWeightMap = Maps.newHashMap();
	private final Map<Integer, BlockEntry> terrainBlockMap = Maps.newHashMap();

	@Override
	public boolean addCaveBiome(ICaveBiome biome)
	{
		for (ICaveBiome entry : CAVE_BIOMES)
		{
			if (entry.getBiome().biomeID == biome.getBiome().biomeID)
			{
				entry.setGenWeight(entry.getGenWeight() + biome.getGenWeight());

				return false;
			}
		}

		return CAVE_BIOMES.add((CaveBiome)biome);
	}

	@Override
	public boolean removeCaveBiome(BiomeGenBase biome)
	{
		for (Iterator<CaveBiome> biomes = CAVE_BIOMES.iterator(); biomes.hasNext();)
		{
			if (biomes.next().getBiome().biomeID == biome.biomeID)
			{
				biomes.remove();

				return true;
			}
		}

		return false;
	}

	@Override
	public int getActiveBiomeCount()
	{
		int count = 0;

		for (ICaveBiome entry : CAVE_BIOMES)
		{
			if (entry.getGenWeight() > 0)
			{
				++count;
			}
		}

		return count;
	}

	@Override
	public int getBiomeGenWeight(BiomeGenBase biome)
	{
		if (genWeightMap.containsKey(biome.biomeID))
		{
			return genWeightMap.get(biome.biomeID);
		}

		for (ICaveBiome entry : CAVE_BIOMES)
		{
			if (entry.getBiome().biomeID == biome.biomeID)
			{
				genWeightMap.put(biome.biomeID, entry.getGenWeight());

				return entry.getGenWeight();
			}
		}

		return 0;
	}

	@Override
	public BlockEntry getBiomeTerrainBlock(BiomeGenBase biome)
	{
		if (terrainBlockMap.containsKey(biome.biomeID))
		{
			return terrainBlockMap.get(biome.biomeID);
		}

		for (ICaveBiome entry : CAVE_BIOMES)
		{
			if (entry.getBiome().biomeID == biome.biomeID)
			{
				BlockEntry terrainBlock = entry.getTerrainBlock();
				Block block = terrainBlock.getBlock();

				if (block == null || block.getMaterial().isLiquid() || !block.getMaterial().isSolid() || block.getMaterial().isReplaceable())
				{
					terrainBlock = new BlockEntry(Blocks.stone, 0);
				}

				terrainBlockMap.put(biome.biomeID, terrainBlock);

				return terrainBlock;
			}
		}

		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public BiomeGenBase getRandomBiome(Random random)
	{
		try
		{
			return ((CaveBiome)WeightedRandom.getRandomItem(random, CAVE_BIOMES)).getBiome();
		}
		catch (Exception e)
		{
			return BiomeGenBase.plains;
		}
	}

	@Override
	public ImmutableSet<ICaveBiome> getCaveBiomes()
	{
		return new ImmutableSet.Builder<ICaveBiome>().addAll(CAVE_BIOMES).build();
	}

	@Override
	public ImmutableList<BiomeGenBase> getBiomeList()
	{
		Set<BiomeGenBase> biomes = Sets.newHashSet();

		for (ICaveBiome entry : CAVE_BIOMES)
		{
			biomes.add(entry.getBiome());
		}

		return new ImmutableList.Builder<BiomeGenBase>().addAll(biomes).build();
	}

	@Override
	public void clearCaveBiomes()
	{
		CAVE_BIOMES.clear();
	}

	public static List<ConfigCategory> getBiomeCategories()
	{
		List<ConfigCategory> list = Lists.newArrayList();
		SortedSet<String> entries = Sets.newTreeSet(new Comparator<String>()
		{
			@Override
			public int compare(String o1, String o2)
			{
				return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
			}
		});

		entries.addAll(Config.biomesCfg.getCategoryNames());

		for (String name : entries)
		{
			list.add(Config.biomesCfg.getCategory(name));
		}

		return list;
	}

	public static class CaveBiome extends WeightedRandom.Item implements ICaveBiome
	{
		private BiomeGenBase biome;
		private BlockEntry terrainBlock;

		public CaveBiome(BiomeGenBase biome, int weight)
		{
			this(biome, weight, new BlockEntry(Blocks.stone, 0));
		}

		public CaveBiome(BiomeGenBase biome, int weight, BlockEntry terrain)
		{
			super(weight);
			this.biome = biome;
			this.terrainBlock = terrain;
		}

		@Override
		public BiomeGenBase getBiome()
		{
			return biome == null ? BiomeGenBase.plains : biome;
		}

		@Override
		public int getGenWeight()
		{
			return itemWeight;
		}

		@Override
		public BlockEntry getTerrainBlock()
		{
			return terrainBlock == null ? new BlockEntry(Blocks.stone, 0) : terrainBlock;
		}

		@Override
		public void setGenWeight(int weight)
		{
			itemWeight = weight;
		}
	}
}