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

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome.BlockEntry;
import com.kegare.caveworld.util.CaveLog;

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

	private static void initCaveBiomes()
	{
		clearCaveBiomes();

		addCaveBiome(new CaveBiome(BiomeGenBase.ocean, 30));
		addCaveBiome(new CaveBiome(BiomeGenBase.plains, 120));
		addCaveBiome(new CaveBiome(BiomeGenBase.desert, 100));
		addCaveBiome(new CaveBiome(BiomeGenBase.desertHills, 10));
		addCaveBiome(new CaveBiome(BiomeGenBase.forest, 100));
		addCaveBiome(new CaveBiome(BiomeGenBase.forestHills, 15));
		addCaveBiome(new CaveBiome(BiomeGenBase.taiga, 100));
		addCaveBiome(new CaveBiome(BiomeGenBase.taigaHills, 15));
		addCaveBiome(new CaveBiome(BiomeGenBase.jungle, 90));
		addCaveBiome(new CaveBiome(BiomeGenBase.jungleHills, 10));
		addCaveBiome(new CaveBiome(BiomeGenBase.swampland, 50));
		addCaveBiome(new CaveBiome(BiomeGenBase.extremeHills, 30));
		addCaveBiome(new CaveBiome(BiomeGenBase.extremeHillsEdge, 10));
		addCaveBiome(new CaveBiome(BiomeGenBase.icePlains, 20));
		addCaveBiome(new CaveBiome(BiomeGenBase.iceMountains, 20));
		addCaveBiome(new CaveBiome(BiomeGenBase.beach, 3));
		addCaveBiome(new CaveBiome(BiomeGenBase.river, 3));
		addCaveBiome(new CaveBiome(BiomeGenBase.mushroomIsland, 2));
		addCaveBiome(new CaveBiome(BiomeGenBase.mushroomIslandShore, 1));
		addCaveBiome(new CaveBiome(BiomeGenBase.savanna, 30));
		addCaveBiome(new CaveBiome(BiomeGenBase.mesa, 3));
		addCaveBiome(new CaveBiome(BiomeGenBase.hell, 0, new BlockEntry(Blocks.netherrack, 0)));
		addCaveBiome(new CaveBiome(BiomeGenBase.sky, 0, new BlockEntry(Blocks.end_stone, 0)));
	}

	static boolean loadCaveBiomes()
	{
		File file = Config.getConfigFile("biomes");

		try
		{
			if (file.exists() && file.canRead())
			{
				String data = Files.readLines(file, Charsets.US_ASCII, new LineProcessor<String>()
				{
					private final StringBuilder builder = new StringBuilder();

					@Override
					public boolean processLine(String line) throws IOException
					{
						if (!Strings.isNullOrEmpty(line))
						{
							line = StringUtils.deleteWhitespace(line);

							if (!line.startsWith("#"))
							{
								builder.append(line);
							}
						}

						return true;
					}

					@Override
					public String getResult()
					{
						return builder.toString();
					}
				});

				if (!Strings.isNullOrEmpty(data) && loadCaveBiomesFromString(data))
				{
					return true;
				}
			}

			return !CAVE_BIOMES.isEmpty();
		}
		catch (Exception e)
		{
			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
		}
		finally
		{
			if (CAVE_BIOMES.isEmpty())
			{
				initCaveBiomes();
			}

			for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
			{
				if (biome != null)
				{
					addCaveBiome(new CaveBiome(biome, 0));
				}
			}

			CaveLog.info("Loaded %d cave biomes", getActiveBiomeCount());

			saveCaveBiomes();
		}

		return false;
	}

	public static boolean loadCaveBiomesFromString(String data)
	{
		Map<String, Map<String, Object>> json = new Gson().fromJson(data, new TypeToken<Map<String, Map<String, Object>>>(){}.getType());

		for (String key : json.keySet())
		{
			Map<String, Object> entry = json.get(key);
			int weight = entry.containsKey("genWeight") ? ((Number)entry.get("genWeight")).intValue() : 1;
			Block block = entry.containsKey("terrainBlock") ? Block.getBlockFromName((String)entry.get("terrainBlock")) : Blocks.stone;
			int metadata = entry.containsKey("terrainBlockMetadata") ? ((Number)entry.get("terrainBlockMetadata")).intValue() : 0;

			if (block == null || block.getMaterial().isLiquid() || !block.getMaterial().isSolid() || block.getMaterial().isReplaceable())
			{
				block = Blocks.stone;
			}

			if (key.matches("^[0-9]{1,3}$"))
			{
				int id = NumberUtils.toInt(key, -1);

				if (id < 0 || id >= BiomeGenBase.getBiomeGenArray().length)
				{
					continue;
				}

				BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[id];

				if (biome != null)
				{
					addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(block, metadata)));
				}
			}
			else
			{
				Type type;

				try
				{
					type = Type.valueOf(key);
				}
				catch (Exception e)
				{
					type = null;
				}

				if (type == null)
				{
					for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
					{
						if (biome != null && biome.biomeName.equals(key))
						{
							addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(block, metadata)));
						}
					}
				}
				else
				{
					for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
					{
						addCaveBiome(new CaveBiome(biome, weight, new BlockEntry(block, metadata)));
					}
				}
			}
		}

		return !CAVE_BIOMES.isEmpty();
	}

	static boolean saveCaveBiomes()
	{
		try
		{
			File file = Config.getConfigFile("biomes");
			String dest = null;

			if (file.exists() && file.canRead())
			{
				dest = FileUtils.readFileToString(file);
			}

			StrBuilder builder = new StrBuilder(dest == null ? 2048 : dest.length());

			builder.appendln("# Configuration file - Caveworld biomes");
			builder.appendNewLine();
			builder.appendln('{');

			for (Iterator<CaveBiome> biomes = CAVE_BIOMES.iterator(); biomes.hasNext();)
			{
				CaveBiome caveBiome = biomes.next();
				BiomeGenBase biome = caveBiome.biome;

				builder.append("  # ").append(biome.biomeName);

				if (BiomeDictionary.isBiomeRegistered(biome))
				{
					Set<String> types = Sets.newHashSet();

					for (Type type : BiomeDictionary.getTypesForBiome(biome))
					{
						types.add(type.name());
					}

					builder.append(" [").append(Joiner.on(", ").skipNulls().join(types)).append(']');
				}

				builder.appendNewLine();
				builder.append("  \"").append(biome.biomeID).appendln("\": {");
				builder.append("    \"genWeight\": ").append(caveBiome.itemWeight);

				BlockEntry entry = caveBiome.terrainBlock;

				if (entry.block != Blocks.stone)
				{
					builder.appendln(',');
					builder.append("    \"terrainBlock\": \"").append(Block.blockRegistry.getNameForObject(entry.block)).append("\"");
				}

				if (entry.blockMetadata != 0)
				{
					builder.appendln(',');
					builder.append("    \"terrainBlockMetadata\": ").append(entry.blockMetadata);
				}

				builder.appendNewLine();
				builder.append("  }");

				if (biomes.hasNext())
				{
					builder.append(',');
				}

				builder.appendNewLine();
			}

			String data = builder.append('}').toString();

			if (dest != null && data.equals(dest))
			{
				return false;
			}

			FileUtils.writeStringToFile(file, data);

			return true;
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to saving cave biomes");
		}

		return false;
	}

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
		else
		{
			for (CaveBiome caveBiome : CAVE_BIOMES)
			{
				if (caveBiome.biome.biomeID == biome.biomeID)
				{
					genWeightMap.put(biome.biomeID, caveBiome.itemWeight);

					return caveBiome.itemWeight;
				}
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
		else
		{
			for (CaveBiome caveBiome : CAVE_BIOMES)
			{
				if (caveBiome.biome.biomeID == biome.biomeID)
				{
					BlockEntry entry = caveBiome.terrainBlock;
					Block block = entry.block;

					if (block == null || block.getMaterial().isLiquid() || !block.getMaterial().isSolid() || block.getMaterial().isReplaceable())
					{
						entry = new BlockEntry(Blocks.stone, 0);
					}

					terrainBlockMap.put(biome.biomeID, entry);

					return entry;
				}
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
			return "\"" + biome.biomeID + "\":{\"genWeight\":" + itemWeight + ",\"terrainBlock\":\"" + Block.blockRegistry.getNameForObject(terrainBlock.block) + "\",\"terrainBlockMetadata\":" + terrainBlock.blockMetadata + "}";
		}

		@Override
		public boolean equals(Object target)
		{
			return target instanceof CaveBiome && biome.biomeID == ((CaveBiome)target).biome.biomeID;
		}

		public static class BlockEntry
		{
			public final Block block;
			public final int blockMetadata;

			public BlockEntry(Block block, int metadata)
			{
				this.block = block;
				this.blockMetadata = metadata;
			}
		}
	}
}