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
import java.util.Random;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.EmptyCaveBiome;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveBiomeManager;

public class CaveBiomeManager implements ICaveBiomeManager, Function<CaveBiomeManager.CaveBiome, BiomeGenBase>
{
	private final SortedSet<CaveBiome> CAVE_BIOMES = Sets.newTreeSet(new Comparator<CaveBiome>()
	{
		@Override
		public int compare(CaveBiome o1, CaveBiome o2)
		{
			return Integer.compare(o1.getBiome().biomeID, o2.getBiome().biomeID);
		}
	});

	private final LoadingCache<Integer, BlockEntry> terrainBlockCache = CacheBuilder.newBuilder()
		.maximumSize(BiomeGenBase.getBiomeGenArray().length).expireAfterWrite(3, TimeUnit.MINUTES).build(
			new CacheLoader<Integer, BlockEntry>()
			{
				@Override
				public BlockEntry load(Integer key) throws Exception
				{
					for (ICaveBiome entry : CAVE_BIOMES)
					{
						if (entry.getBiome().biomeID == key.intValue())
						{
							BlockEntry terrain = entry.getTerrainBlock();
							Block block = terrain.getBlock();

							if (block == null || block.getMaterial().isLiquid() || !block.getMaterial().isSolid() || block.getMaterial().isReplaceable())
							{
								terrain = new BlockEntry(Blocks.stone, 0);
							}

							return terrain;
						}
					}

					return new BlockEntry(Blocks.stone, 0);
				}
			});

	@Override
	public boolean addCaveBiome(ICaveBiome biome)
	{
		try
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
		finally
		{
			terrainBlockCache.invalidate(biome.getBiome().biomeID);
		}
	}

	@Override
	public boolean removeCaveBiome(BiomeGenBase biome)
	{
		for (Iterator<CaveBiome> biomes = CAVE_BIOMES.iterator(); biomes.hasNext();)
		{
			if (biomes.next().getBiome().biomeID == biome.biomeID)
			{
				biomes.remove();

				terrainBlockCache.invalidate(biome.biomeID);

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
		for (ICaveBiome entry : CAVE_BIOMES)
		{
			if (entry.getBiome().biomeID == biome.biomeID)
			{
				return entry.getGenWeight();
			}
		}

		return 0;
	}

	@Override
	public BlockEntry getBiomeTerrainBlock(BiomeGenBase biome)
	{
		try
		{
			return terrainBlockCache.get(Integer.valueOf(biome.biomeID));
		}
		catch (ExecutionException e)
		{
			return new BlockEntry(Blocks.stone, 0);
		}
	}

	@Override
	public ICaveBiome getRandomCaveBiome(Random random)
	{
		try
		{
			return (ICaveBiome)WeightedRandom.getRandomItem(random, CAVE_BIOMES);
		}
		catch (Exception e)
		{
			return new EmptyCaveBiome();
		}
	}

	@Override
	public ImmutableSet<ICaveBiome> getCaveBiomes()
	{
		return new ImmutableSet.Builder<ICaveBiome>().addAll(CAVE_BIOMES).build();
	}

	@Override
	public List<BiomeGenBase> getBiomeList()
	{
		return Lists.transform(Lists.newArrayList(CAVE_BIOMES), this);
	}

	@Override
	public void clearCaveBiomes()
	{
		CAVE_BIOMES.clear();

		terrainBlockCache.invalidateAll();
	}

	@Override
	public BiomeGenBase apply(CaveBiome input)
	{
		return input.getBiome();
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