/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.world;

import com.google.common.collect.Maps;
import com.kegare.caveworld.core.CaveBiomeManager;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldChunkManagerCaveworld extends WorldChunkManager
{
	private final World worldObj;
	private final Random random;

	static final Map<Long, BiomeGenBase> biomeMap = Maps.newHashMap();

	public WorldChunkManagerCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public List getBiomesToSpawnIn()
	{
		return CaveBiomeManager.getBiomeList();
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		BiomeGenBase biome;
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		long chunkSeed = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);

		if (biomeMap.containsKey(chunkSeed))
		{
			biome = biomeMap.get(chunkSeed);
		}
		else
		{
			random.setSeed(chunkX + chunkZ ^ worldObj.getSeed());

			biome = CaveBiomeManager.getRandomBiome(random);

			if (biome != null)
			{
				biomeMap.put(chunkSeed, biome);
			}
		}

		return biome == null ? BiomeGenBase.plains : biome;
	}

	@Override
	public float[] getRainfall(float[] rainfalls, int x, int z, int width, int length)
	{
		if (rainfalls == null || rainfalls.length < width * length)
		{
			rainfalls = new float[width * length];
		}

		Arrays.fill(rainfalls, 0.0F);

		return rainfalls;
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int length)
	{
		if (biomes == null || biomes.length < width * length)
		{
			biomes = new BiomeGenBase[width * length];
		}

		Arrays.fill(biomes, getBiomeGenAt(x, z));

		return biomes;
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int width, int length)
	{
		return getBiomesForGeneration(biomes, x, z, width, length);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int width, int length, boolean flag)
	{
		return getBiomesForGeneration(biomes, x, z, width, length);
	}

	@Override
	public boolean areBiomesViable(int x, int y, int z, List list)
	{
		return list.contains(getBiomeGenAt(x, z));
	}

	@Override
	public ChunkPosition findBiomePosition(int x, int y, int z, List list, Random random)
	{
		return list.contains(getBiomeGenAt(x, z)) ? new ChunkPosition(x - z + random.nextInt(z * 2 + 1), 0, y - z + random.nextInt(z * 2 + 1)) : null;
	}
}