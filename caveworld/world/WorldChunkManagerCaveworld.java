/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiomeManager;
import caveworld.world.genlayer.CaveworldGenLayer;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManagerCaveworld extends WorldChunkManager
{
	public static final List<BiomeGenBase> spawnInBiomes = Lists.newArrayList();

	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;
	private List<BiomeGenBase> biomesToSpawnIn;

	public WorldChunkManagerCaveworld()
	{
		if (spawnInBiomes.isEmpty())
		{
			spawnInBiomes.addAll(CaveworldAPI.getBiomeList());
		}

		this.biomeCache = new BiomeCache(this);
		this.biomesToSpawnIn = Lists.newArrayList();
		this.biomesToSpawnIn.addAll(spawnInBiomes);
	}

	public WorldChunkManagerCaveworld(long seed, WorldType worldType, ICaveBiomeManager manager)
	{
		this();
		this.biomeIndexLayer = CaveworldGenLayer.makeWorldLayers(seed, worldType, manager)[1];
	}

	public WorldChunkManagerCaveworld(World world, ICaveBiomeManager manager)
	{
		this(world.getSeed(), world.getWorldInfo().getTerrainType(), manager);
	}

	@Override
	public List<BiomeGenBase> getBiomesToSpawnIn()
	{
		return biomesToSpawnIn;
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		return biomeCache.getBiomeGenAt(x, z);
	}

	@Override
	public float[] getRainfall(float[] rainfalls, int x, int z, int sizeX, int sizeZ)
	{
		if (rainfalls == null || rainfalls.length < sizeX * sizeZ)
		{
			rainfalls = new float[sizeX * sizeZ];
		}

		Arrays.fill(rainfalls, 0, sizeX * sizeZ, 0.0F);

		return rainfalls;
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int sizeX, int sizeZ)
	{
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < sizeX * sizeZ)
		{
			biomes = new BiomeGenBase[sizeX * sizeZ];
		}

		int[] biomeArray = biomeIndexLayer.getInts(x, z, sizeX, sizeZ);

		for (int i = 0; i < sizeX * sizeZ; ++i)
		{
			biomes[i] = BiomeGenBase.getBiome(biomeArray[i]);
		}

		return biomes;
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int sizeX, int sizeY)
	{
		return getBiomeGenAt(biomes, x, z, sizeX, sizeY, true);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int sizeX, int sizeY, boolean cache)
	{
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < sizeX * sizeY)
		{
			biomes = new BiomeGenBase[sizeX * sizeY];
		}

		if (cache && sizeX == 16 && sizeY == 16 && (x & 15) == 0 && (z & 15) == 0)
		{
			BiomeGenBase[] cachedBiomes = biomeCache.getCachedBiomes(x, z);
			System.arraycopy(cachedBiomes, 0, biomes, 0, sizeX * sizeY);

			return biomes;
		}

		int[] biomeArray = biomeIndexLayer.getInts(x, z, sizeX, sizeY);

		for (int i = 0; i < sizeX * sizeY; ++i)
		{
				biomes[i] = BiomeGenBase.getBiome(biomeArray[i]);
		}

		return biomes;
	}

	@Override
	public boolean areBiomesViable(int x, int z, int radius, List list)
	{
		IntCache.resetIntCache();
		int minX = x - radius >> 2;
		int minZ = z - radius >> 2;
		int maxX = x + radius >> 2;
		int maxZ = z + radius >> 2;
		int sizeX = maxX - minX + 1;
		int sizeZ = maxZ - minZ + 1;
		int[] biomes = biomeIndexLayer.getInts(minX, minZ, sizeX, sizeZ);

		for (int i = 0; i < sizeX * sizeZ; ++i)
		{
			if (!list.contains(BiomeGenBase.getBiome(biomes[i])))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public ChunkPosition findBiomePosition(int x, int z, int radius, List list, Random random)
	{
		IntCache.resetIntCache();
		int minX = x - radius >> 2;
		int minZ = z - radius >> 2;
		int maxX = x + radius >> 2;
		int maxZ = z + radius >> 2;
		int sizeX = maxX - minX + 1;
		int sizeZ = maxZ - minZ + 1;
		int[] biomes = biomeIndexLayer.getInts(minX, minZ, sizeX, sizeZ);
		ChunkPosition pos = null;
		int attempts = 0;

		for (int i = 0; i < sizeX * sizeZ; ++i)
		{
			int finalX = minX + i % sizeX << 2;
			int finalZ = minZ + i / sizeX << 2;
			BiomeGenBase biome = BiomeGenBase.getBiome(biomes[i]);

			if (list.contains(biome) && (pos == null || random.nextInt(attempts + 1) == 0))
			{
				pos = new ChunkPosition(finalX, 0, finalZ);

				++attempts;
			}
		}

		return pos;
	}

	@Override
	public void cleanupCache()
	{
		biomeCache.cleanupCache();
	}
}