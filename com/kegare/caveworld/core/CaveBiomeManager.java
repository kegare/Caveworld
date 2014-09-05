/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.EmptyCaveBiome;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveBiomeManager;
import com.kegare.caveworld.util.CaveBiomeComparator;

public class CaveBiomeManager implements ICaveBiomeManager, Function<ICaveBiome, BiomeGenBase>
{
	private final Set<ICaveBiome> CAVE_BIOMES = Sets.newTreeSet(new CaveBiomeComparator());

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

	public static final Map<BiomeGenBase, ICaveBiome> defaultMapping = Maps.newHashMap();

	static
	{
		defaultMapping.put(BiomeGenBase.ocean, new CaveBiome(BiomeGenBase.ocean, 15));
		defaultMapping.put(BiomeGenBase.plains, new CaveBiome(BiomeGenBase.plains, 100));
		defaultMapping.put(BiomeGenBase.desert, new CaveBiome(BiomeGenBase.desert, 70));
		defaultMapping.put(BiomeGenBase.desertHills, new CaveBiome(BiomeGenBase.desertHills, 10));
		defaultMapping.put(BiomeGenBase.forest, new CaveBiome(BiomeGenBase.forest, 80));
		defaultMapping.put(BiomeGenBase.forestHills, new CaveBiome(BiomeGenBase.forestHills, 10));
		defaultMapping.put(BiomeGenBase.taiga, new CaveBiome(BiomeGenBase.taiga, 80));
		defaultMapping.put(BiomeGenBase.taigaHills, new CaveBiome(BiomeGenBase.taigaHills, 10));
		defaultMapping.put(BiomeGenBase.jungle, new CaveBiome(BiomeGenBase.jungle, 80));
		defaultMapping.put(BiomeGenBase.jungleHills, new CaveBiome(BiomeGenBase.jungleHills, 10));
		defaultMapping.put(BiomeGenBase.swampland, new CaveBiome(BiomeGenBase.swampland, 60));
		defaultMapping.put(BiomeGenBase.extremeHills, new CaveBiome(BiomeGenBase.extremeHills, 30));
		defaultMapping.put(BiomeGenBase.icePlains, new CaveBiome(BiomeGenBase.icePlains, 15));
		defaultMapping.put(BiomeGenBase.iceMountains, new CaveBiome(BiomeGenBase.iceMountains, 15));
		defaultMapping.put(BiomeGenBase.mushroomIsland, new CaveBiome(BiomeGenBase.mushroomIsland, 10));
		defaultMapping.put(BiomeGenBase.savanna, new CaveBiome(BiomeGenBase.savanna, 50));
		defaultMapping.put(BiomeGenBase.mesa, new CaveBiome(BiomeGenBase.mesa, 50));
		defaultMapping.put(BiomeGenBase.hell, new CaveBiome(BiomeGenBase.hell, 0, new BlockEntry(Blocks.netherrack, 0)));
		defaultMapping.put(BiomeGenBase.sky, new CaveBiome(BiomeGenBase.sky, 0, new BlockEntry(Blocks.end_stone, 0)));
	}

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

			return CAVE_BIOMES.add(biome);
		}
		finally
		{
			terrainBlockCache.invalidate(biome.getBiome().biomeID);
		}
	}

	@Override
	public boolean removeCaveBiome(BiomeGenBase biome)
	{
		for (Iterator<ICaveBiome> biomes = CAVE_BIOMES.iterator(); biomes.hasNext();)
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
			return terrainBlockCache.get(biome.biomeID);
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
	public BiomeGenBase apply(ICaveBiome input)
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