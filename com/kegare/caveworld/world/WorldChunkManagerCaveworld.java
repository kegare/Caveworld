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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import com.kegare.caveworld.api.CaveworldAPI;

public class WorldChunkManagerCaveworld extends WorldChunkManager
{
	private final World worldObj;
	private final Random random;
	private final BiomeCache biomeCache;

	public WorldChunkManagerCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
		this.biomeCache = new BiomeCache(this);
	}

	@Override
	public List getBiomesToSpawnIn()
	{
		return CaveworldAPI.getBiomeList();
	}

	private BiomeGenBase getCaveBiomeGenAt(int x, int z)
	{
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		long chunkSeed = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);

		random.setSeed(chunkSeed ^ worldObj.getSeed());

		return CaveworldAPI.getRandomBiome(random);
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		BiomeGenBase biome = biomeCache.getBiomeGenAt(x, z);

		if (biome == null)
		{
			biome = getCaveBiomeGenAt(x, z);
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

		Arrays.fill(biomes, getCaveBiomeGenAt(x, z));

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
		return new ChunkPosition(x - z + random.nextInt(z * 2 + 1), 0, y - z + random.nextInt(z * 2 + 1));
	}

	@Override
	public void cleanupCache()
	{
		biomeCache.cleanupCache();
	}
}