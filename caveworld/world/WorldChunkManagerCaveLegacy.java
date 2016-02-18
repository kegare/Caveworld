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

import caveworld.api.ICaveBiomeManager;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class WorldChunkManagerCaveLegacy extends WorldChunkManager
{
	private final World worldObj;
	private final Random random;
	private final BiomeCache biomeCache;
	private final int biomeSize;
	private final ICaveBiomeManager biomeManager;

	public WorldChunkManagerCaveLegacy(World world, int biomeSize, ICaveBiomeManager manager)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
		this.biomeCache = new BiomeCache(this);
		this.biomeSize = biomeSize;
		this.biomeManager = manager;
	}

	@Override
	public List getBiomesToSpawnIn()
	{
		return biomeManager.getBiomeList();
	}

	private BiomeGenBase getCaveBiomeGenAt(int x, int z)
	{
		int chunkX = x >> 4;
		int chunkZ = z >> 4;

		if (biomeSize <= 0)
		{
			random.setSeed(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ) ^ worldObj.getSeed());
		}
		else
		{
			random.setSeed(ChunkCoordIntPair.chunkXZ2Int((chunkX + 1) / biomeSize, (chunkZ + 1) / biomeSize) ^ worldObj.getSeed());
		}

		return biomeManager.getRandomCaveBiome(random).getBiome();
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
	public float[] getRainfall(float[] rainfalls, int x, int z, int xSize, int zSize)
	{
		if (rainfalls == null || rainfalls.length < xSize * zSize)
		{
			rainfalls = new float[xSize * zSize];
		}

		Arrays.fill(rainfalls, 0.0F);

		return rainfalls;
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int xSize, int zSize)
	{
		if (biomes == null || biomes.length < xSize * zSize)
		{
			biomes = new BiomeGenBase[xSize * zSize];
		}

		Arrays.fill(biomes, getCaveBiomeGenAt(x, z));

		return biomes;
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int xSize, int zSize)
	{
		return getBiomesForGeneration(biomes, x, z, xSize, zSize);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int xSize, int zSize, boolean flag)
	{
		return getBiomesForGeneration(biomes, x, z, xSize, zSize);
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