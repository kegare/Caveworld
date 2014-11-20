/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.EmptyCaveBiome;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.api.ICaveBiomeManager;
import com.kegare.caveworld.util.CaveUtils;

public class CaveBiomeManager implements ICaveBiomeManager
{
	private final Map<BiomeGenBase, ICaveBiome> CAVE_BIOMES = Maps.newHashMap();

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
		defaultMapping.put(BiomeGenBase.hell, new CaveBiome(BiomeGenBase.hell, 0, new BlockEntry(Blocks.netherrack, 0), null));
		defaultMapping.put(BiomeGenBase.sky, new CaveBiome(BiomeGenBase.sky, 0, new BlockEntry(Blocks.end_stone, 0), null));
	}

	public Map<BiomeGenBase, ICaveBiome> getRaw()
	{
		return CAVE_BIOMES;
	}

	@Override
	public Configuration getConfig()
	{
		return Config.biomesCfg;
	}

	@Override
	public boolean addCaveBiome(ICaveBiome biome)
	{
		for (ICaveBiome entry : getRaw().values())
		{
			if (entry.getBiome().biomeID == biome.getBiome().biomeID)
			{
				entry.setGenWeight(entry.getGenWeight() + biome.getGenWeight());

				return false;
			}
		}

		getRaw().put(biome.getBiome(), biome);

		return true;
	}

	@Override
	public boolean removeCaveBiome(BiomeGenBase biome)
	{
		return getRaw().remove(biome) != null;
	}

	@Override
	public int getActiveBiomeCount()
	{
		int count = 0;

		for (ICaveBiome entry : getRaw().values())
		{
			if (entry.getGenWeight() > 0)
			{
				++count;
			}
		}

		return count;
	}

	@Override
	public ICaveBiome getCaveBiome(BiomeGenBase biome)
	{
		return getRaw().containsKey(biome) ? getRaw().get(biome) : new EmptyCaveBiome(biome);
	}

	@Override
	public ICaveBiome getRandomCaveBiome(Random random)
	{
		try
		{
			return (ICaveBiome)WeightedRandom.getRandomItem(random, getRaw().values());
		}
		catch (Exception e)
		{
			return new EmptyCaveBiome();
		}
	}

	@Override
	public Set<ICaveBiome> getCaveBiomes()
	{
		Set<ICaveBiome> result = Sets.newTreeSet(CaveBiome.caveBiomeComparator);
		result.addAll(getRaw().values());

		return result;
	}

	@Override
	public List<BiomeGenBase> getBiomeList()
	{
		return Lists.newArrayList(getRaw().keySet());
	}

	@Override
	public void clearCaveBiomes()
	{
		getRaw().clear();
	}

	public static class CaveBiome extends WeightedRandom.Item implements ICaveBiome, Comparable
	{
		public static final Comparator<ICaveBiome> caveBiomeComparator = new Comparator<ICaveBiome>()
		{
			@Override
			public int compare(ICaveBiome o1, ICaveBiome o2)
			{
				int i = CaveUtils.compareWithNull(o1, o2);

				if (i == 0 && o1 != null && o2 != null)
				{
					i = CaveUtils.biomeComparator.compare(o1.getBiome(), o2.getBiome());

					if (i == 0)
					{
						BlockEntry block1 = o1.getTerrainBlock();
						BlockEntry block2 = o2.getTerrainBlock();

						i = CaveUtils.compareWithNull(block1, block2);

						if (i == 0 && block1 != null && block2 != null)
						{
							i = CaveUtils.blockComparator.compare(block1.getBlock(), block2.getBlock());

							if (i == 0)
							{
								i = Integer.compare(block1.getMetadata(), block2.getMetadata());

								if (i == 0)
								{
									block1 = o1.getTopBlock();
									block2 = o2.getTopBlock();

									i = CaveUtils.compareWithNull(block1, block2);

									if (i == 0 && block1 != null && block2 != null)
									{
										i = CaveUtils.blockComparator.compare(block1.getBlock(), block2.getBlock());

										if (i == 0)
										{
											i = Integer.compare(block1.getMetadata(), block2.getMetadata());
										}
									}
								}
							}
						}
					}
				}

				return i;
			}
		};

		private BiomeGenBase biome;
		private BlockEntry terrainBlock;
		private BlockEntry topBlock;

		public CaveBiome(BiomeGenBase biome, int weight)
		{
			this(biome, weight, new BlockEntry(Blocks.stone, 0), null);
		}

		public CaveBiome(BiomeGenBase biome, int weight, BlockEntry terrain, BlockEntry top)
		{
			super(weight);
			this.biome = biome;
			this.terrainBlock = terrain;
			this.topBlock = top;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ICaveBiome)
			{
				ICaveBiome biome = (ICaveBiome)obj;

				return getBiome().biomeID == biome.getBiome().biomeID &&
					getGenWeight() == biome.getGenWeight() &&
					getTerrainBlock().equals(biome.getTerrainBlock()) &&
					getTopBlock().equals(biome.getTopBlock());
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(getBiome().biomeID, getGenWeight(), getTerrainBlock());
		}

		@Override
		public int compareTo(Object obj)
		{
			return caveBiomeComparator.compare(this, (ICaveBiome)obj);
		}

		@Override
		public BiomeGenBase getBiome()
		{
			return biome == null ? BiomeGenBase.plains : biome;
		}

		@Override
		public int setGenWeight(int weight)
		{
			return itemWeight = weight;
		}

		@Override
		public int getGenWeight()
		{
			return itemWeight;
		}

		@Override
		public BlockEntry setTerrainBlock(BlockEntry entry)
		{
			return terrainBlock = entry;
		}

		@Override
		public BlockEntry getTerrainBlock()
		{
			return terrainBlock == null ? new BlockEntry(Blocks.stone, 0) : terrainBlock;
		}

		@Override
		public BlockEntry setTopBlock(BlockEntry entry)
		{
			return topBlock = entry;
		}

		@Override
		public BlockEntry getTopBlock()
		{
			return topBlock == null ? getTerrainBlock() : topBlock;
		}
	}
}