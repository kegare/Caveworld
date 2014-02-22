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

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.kegare.caveworld.util.CaveLog;
import cpw.mods.fml.common.Loader;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import org.apache.logging.log4j.Level;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CaveBiomeManager
{
	private static final Set<CaveBiome> CAVE_BIOMES = Sets.newHashSet();

	private static void initCaveBiomes()
	{
		clearCaveBiomes();

		addCaveBiome(BiomeGenBase.ocean, 30);
		addCaveBiome(BiomeGenBase.plains, 100);
		addCaveBiome(BiomeGenBase.desert, 90);
		addCaveBiome(BiomeGenBase.desertHills, 10);
		addCaveBiome(BiomeGenBase.forest, 100);
		addCaveBiome(BiomeGenBase.forestHills, 15);
		addCaveBiome(BiomeGenBase.taiga, 100);
		addCaveBiome(BiomeGenBase.taigaHills, 15);
		addCaveBiome(BiomeGenBase.jungle, 90);
		addCaveBiome(BiomeGenBase.jungleHills, 10);
		addCaveBiome(BiomeGenBase.swampland, 50);
		addCaveBiome(BiomeGenBase.extremeHills, 30);
		addCaveBiome(BiomeGenBase.extremeHillsEdge, 10);
		addCaveBiome(BiomeGenBase.icePlains, 20);
		addCaveBiome(BiomeGenBase.iceMountains, 20);
		addCaveBiome(BiomeGenBase.beach, 3);
		addCaveBiome(BiomeGenBase.river, 3);
		addCaveBiome(BiomeGenBase.mushroomIsland, 2);
		addCaveBiome(BiomeGenBase.mushroomIslandShore, 1);
		addCaveBiome(BiomeGenBase.savanna, 30);
		addCaveBiome(BiomeGenBase.mesa, 3);
	}

	static void loadCaveBiomes()
	{
		try
		{
			File dir = new File(Loader.instance().getConfigDir(), "caveworld");

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			File file = new File(dir, "caveworld-biomes.cfg");

			if (file.createNewFile())
			{
				initCaveBiomes();

				BufferedWriter buffer = Files.newWriter(file, Charsets.UTF_8);

				try
				{
					buffer.write("# Caveworld biomes");
					buffer.newLine();
					buffer.write("#  Specify the biomes to generate in Caveworld, and rarity.");
					buffer.newLine();
					buffer.write("#  Format: {BiomeID}={Rarity[1-100]}");
					buffer.newLine();
					buffer.write("#  Note: If specify 0 for rarity, it will not be generated.");
					buffer.newLine();
					buffer.newLine();

					for (int i = 0; i < 256; ++i)
					{
						BiomeGenBase biome = BiomeGenBase.getBiome(i);

						if (biome != null)
						{
							buffer.write("# " + biome.biomeName);

							if (BiomeDictionary.isBiomeRegistered(biome))
							{
								Set<String> types = Sets.newHashSet();

								for (Type type : BiomeDictionary.getTypesForBiome(biome))
								{
									types.add(type.toString());
								}

								buffer.write(" [" + Joiner.on(", ").skipNulls().join(types) + "]");
							}

							buffer.newLine();
							buffer.write(biome.biomeID + "=" + getBiomeRarity(biome));
							buffer.newLine();
							buffer.newLine();
						}
					}
				}
				finally
				{
					buffer.close();
				}
			}
			else if (file.exists() && file.canRead())
			{
				String data = Files.readLines(file, Charsets.UTF_8, new LineProcessor<String>()
				{
					private final StringBuilder builder = new StringBuilder();

					@Override
					public boolean processLine(String line) throws IOException
					{
						if (!Strings.isNullOrEmpty(line) && !line.startsWith("#"))
						{
							builder.append(line.trim());
							builder.append(',');
						}

						return true;
					}

					@Override
					public String getResult()
					{
						return builder.toString();
					}
				});

				if (!Strings.isNullOrEmpty(data))
				{
					loadCaveBiomesFromString(data);
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to loading cave biomes");
		}
		finally
		{
			if (CAVE_BIOMES.isEmpty())
			{
				initCaveBiomes();
			}

			CaveLog.info("Loaded %d cave biomes", CAVE_BIOMES.size());
		}
	}

	public static void loadCaveBiomesFromString(String data)
	{
		try
		{
			for (String entry : Splitter.on(',').omitEmptyStrings().trimResults().split(data))
			{
				int id = Integer.valueOf(entry.split("=")[0]);
				int rarity = Integer.valueOf(entry.split("=")[1]);

				if (id >= 0 && id < 256)
				{
					addCaveBiome(BiomeGenBase.getBiome(id), rarity);
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to loading cave biomes from string");
		}
	}

	public static int addCaveBiome(BiomeGenBase biome, int rarity)
	{
		if (biome != null && rarity > 0)
		{
			for (CaveBiome caveBiome : CAVE_BIOMES)
			{
				if (caveBiome.biome.isEqualTo(biome))
				{
					return caveBiome.itemWeight += rarity;
				}
			}

			CAVE_BIOMES.add(new CaveBiome(biome, Math.min(Math.max(rarity, 1), 100)));
		}

		return rarity;
	}

	public static int removeCaveBiome(BiomeGenBase biome)
	{
		if (biome != null)
		{
			for (CaveBiome caveBiome : CAVE_BIOMES)
			{
				if (caveBiome.biome.isEqualTo(biome))
				{
					CAVE_BIOMES.remove(caveBiome);

					return caveBiome.itemWeight;
				}
			}
		}

		return 0;
	}

	public static int getBiomeRarity(BiomeGenBase biome)
	{
		if (biome != null)
		{
			for (CaveBiome caveBiome : CAVE_BIOMES)
			{
				if (caveBiome.biome.biomeID == biome.biomeID)
				{
					return caveBiome.itemWeight;
				}
			}
		}

		return 0;
	}

	public static BiomeGenBase getRandomBiome(Random random)
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

	public static List<BiomeGenBase> getBiomeList()
	{
		Set<BiomeGenBase> biomes = Sets.newHashSet();

		for (CaveBiome caveBiome : CAVE_BIOMES)
		{
			biomes.add(caveBiome.biome);
		}

		return Lists.newArrayList(biomes);
	}

	public static class CaveBiome extends WeightedRandom.Item
	{
		public final BiomeGenBase biome;

		public CaveBiome(BiomeGenBase biome, int rarity)
		{
			super(rarity);
			this.biome = biome;
		}

		@Override
		public String toString()
		{
			return biome.biomeID + "=" + itemWeight;
		}

		@Override
		public boolean equals(Object target)
		{
			return target instanceof CaveBiome && biome.biomeID == ((CaveBiome)target).biome.biomeID;
		}
	}
}