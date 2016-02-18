/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.api.EmptyCaveBiome;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveBiomeManager;
import caveworld.util.CaveUtils;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;

public class CaveBiomeManager implements ICaveBiomeManager
{
	private final Map<BiomeGenBase, ICaveBiome> CAVE_BIOMES = Maps.newHashMap();

	private boolean readOnly;

	public static final Map<BiomeGenBase, ICaveBiome> presets = Maps.newHashMap();

	static
	{
		presets.put(BiomeGenBase.ocean, new CaveBiome(BiomeGenBase.ocean, 15));
		presets.put(BiomeGenBase.plains, new CaveBiome(BiomeGenBase.plains, 100));
		presets.put(BiomeGenBase.desert, new CaveBiome(BiomeGenBase.desert, 70));
		presets.put(BiomeGenBase.desertHills, new CaveBiome(BiomeGenBase.desertHills, 10));
		presets.put(BiomeGenBase.forest, new CaveBiome(BiomeGenBase.forest, 80));
		presets.put(BiomeGenBase.forestHills, new CaveBiome(BiomeGenBase.forestHills, 10));
		presets.put(BiomeGenBase.taiga, new CaveBiome(BiomeGenBase.taiga, 80));
		presets.put(BiomeGenBase.taigaHills, new CaveBiome(BiomeGenBase.taigaHills, 10));
		presets.put(BiomeGenBase.jungle, new CaveBiome(BiomeGenBase.jungle, 80));
		presets.put(BiomeGenBase.jungleHills, new CaveBiome(BiomeGenBase.jungleHills, 10));
		presets.put(BiomeGenBase.swampland, new CaveBiome(BiomeGenBase.swampland, 60));
		presets.put(BiomeGenBase.extremeHills, new CaveBiome(BiomeGenBase.extremeHills, 30));
		presets.put(BiomeGenBase.icePlains, new CaveBiome(BiomeGenBase.icePlains, 15));
		presets.put(BiomeGenBase.iceMountains, new CaveBiome(BiomeGenBase.iceMountains, 15));
		presets.put(BiomeGenBase.mushroomIsland, new CaveBiome(BiomeGenBase.mushroomIsland, 10));
		presets.put(BiomeGenBase.savanna, new CaveBiome(BiomeGenBase.savanna, 50));
		presets.put(BiomeGenBase.mesa, new CaveBiome(BiomeGenBase.mesa, 50));
		presets.put(BiomeGenBase.hell, new CaveBiome(BiomeGenBase.hell, 0, new BlockEntry(Blocks.netherrack, 0), null));
		presets.put(BiomeGenBase.sky, new CaveBiome(BiomeGenBase.sky, 0, new BlockEntry(Blocks.end_stone, 0), null));
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
	public int getType()
	{
		return WorldProviderCaveworld.TYPE;
	}

	@Override
	public boolean isReadOnly()
	{
		return readOnly;
	}

	@Override
	public ICaveBiomeManager setReadOnly(boolean flag)
	{
		readOnly = flag;

		return this;
	}

	@Override
	public boolean addCaveBiome(ICaveBiome biome)
	{
		if (isReadOnly())
		{
			return false;
		}

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
		return !isReadOnly() && getRaw().remove(biome) != null;
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
		if (!isReadOnly())
		{
			getRaw().clear();
		}
	}

	@Override
	public NBTTagList saveNBTData()
	{
		NBTTagList list = new NBTTagList();

		for (ICaveBiome biome : getRaw().values())
		{
			list.appendTag(biome.saveNBTData());
		}

		return list;
	}

	@Override
	public void loadNBTData(NBTTagList list)
	{
		if (!isReadOnly())
		{
			for (int i = 0; i < list.tagCount(); ++i)
			{
				addCaveBiome(new CaveBiome(list.getCompoundTagAt(i)));
			}
		}
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

		public CaveBiome()
		{
			super(0);
		}

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

		public CaveBiome(NBTTagCompound data)
		{
			this();
			this.loadNBTData(data);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			else if (obj == null || !(obj instanceof ICaveBiome))
			{
				return false;
			}

			ICaveBiome biome = (ICaveBiome)obj;

			return getBiome().biomeID == biome.getBiome().biomeID;
		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(getBiome().biomeID);
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

		@Override
		public NBTTagCompound saveNBTData()
		{
			NBTTagCompound data = new NBTTagCompound();

			data.setInteger("BiomeID", getBiome().biomeID);
			data.setInteger("Weight", getGenWeight());
			data.setString("TerrainBlock", GameData.getBlockRegistry().getNameForObject(getTerrainBlock().getBlock()));
			data.setInteger("TerrainBlockMeta", getTerrainBlock().getMetadata());
			data.setString("TopBlock", GameData.getBlockRegistry().getNameForObject(getTopBlock().getBlock()));
			data.setInteger("TopBlockMeta", getTopBlock().getMetadata());

			return data;
		}

		@Override
		public void loadNBTData(NBTTagCompound data)
		{
			if (data == null || data.hasNoTags())
			{
				return;
			}

			biome = BiomeGenBase.getBiome(data.getInteger("BiomeID"));
			setGenWeight(data.getInteger("Weight"));
			setTerrainBlock(new BlockEntry(data.getString("TerrainBlock"), data.getInteger("TerrainBlockMeta")));
			setTopBlock(new BlockEntry(data.getString("TopBlock"), data.getInteger("TopBlockMeta")));
		}
	}
}