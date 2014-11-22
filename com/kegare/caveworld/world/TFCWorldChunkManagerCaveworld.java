/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;

import com.bioxx.tfc.WorldGen.TFCBiome;
import com.bioxx.tfc.WorldGen.TFCWorldChunkManager;
import com.kegare.caveworld.api.ICaveBiomeManager;

public class TFCWorldChunkManagerCaveworld extends TFCWorldChunkManager
{
	private final World worldObj;
	private final Random random;
	private final BiomeCache biomeCache;
	private final int biomeSize;
	private final ICaveBiomeManager biomeManager;

	public TFCWorldChunkManagerCaveworld(World world, int biomeSize, ICaveBiomeManager manager)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
		this.biomeCache = new BiomeCache(this);
		this.biomeSize = biomeSize;
		this.biomeManager = manager;
	}

	public static TFCBiome convertToTFCBiome(final BiomeGenBase biome)
	{
		if (biome == null || biome.biomeID < 0 || biome.biomeID >= TFCBiome.getBiomeGenArray().length)
		{
			return TFCBiome.getBiome(1);
		}

		if (TFCBiome.getBiomeGenArray()[biome.biomeID] == null)
		{
			new TFCBiome(biome.biomeID)
			{
				{
					biomeName = biome.biomeName;
					color = biomeColor = biome.color;
					field_150609_ah = biome.field_150609_ah;
					topBlock = biome.topBlock;
					field_150604_aj = biome.field_150604_aj;
					fillerBlock = biome.fillerBlock;
					field_76754_C = biome.field_76754_C;
					temperature = temperatureTFC = biome.temperature;
					rainfall = biome.rainfall;
					waterColorMultiplier = biome.waterColorMultiplier;
					spawnableMonsterList.clear();
					spawnableMonsterList.addAll(biome.getSpawnableList(EnumCreatureType.monster));
					spawnableCreatureList.clear();
					spawnableCreatureList.addAll(biome.getSpawnableList(EnumCreatureType.creature));
					spawnableWaterCreatureList.clear();
					spawnableWaterCreatureList.addAll(biome.getSpawnableList(EnumCreatureType.waterCreature));
					spawnableCaveCreatureList.clear();
					spawnableCaveCreatureList.addAll(biome.getSpawnableList(EnumCreatureType.ambient));
				}

				@Override
				public void decorate(World world, Random random, int chunkX, int chunkZ) {}
			};
		}

		TFCBiome result = TFCBiome.getBiomeGenArray()[biome.biomeID];

		return result == null ? TFCBiome.getBiome(1) : result;
	}

	@Override
	public List getBiomesToSpawnIn()
	{
		return null;
	}

	private TFCBiome getCaveBiomeGenAt(int x, int z)
	{
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		int size = Math.max(biomeSize, 1);

		random.setSeed(ChunkCoordIntPair.chunkXZ2Int((chunkX + 1) / size, (chunkZ + 1) / size) ^ worldObj.getSeed());

		return convertToTFCBiome(biomeManager.getRandomCaveBiome(random).getBiome());
	}

	@Override
	public TFCBiome getBiomeGenAt(int x, int z)
	{
		BiomeGenBase biome = biomeCache.getBiomeGenAt(x, z);

		if (biome == null)
		{
			biome = getCaveBiomeGenAt(x, z);
		}
		else
		{
			biome = convertToTFCBiome(biome);
		}

		return biome == null ? TFCBiome.getBiome(1) : (TFCBiome)biome;
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
	public TFCBiome[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int length)
	{
		if (biomes == null || !(biomes instanceof TFCBiome[]) || biomes.length < width * length)
		{
			biomes = new TFCBiome[width * length];
		}

		Arrays.fill(biomes, getCaveBiomeGenAt(x, z));

		return (TFCBiome[])biomes;
	}

	@Override
	public TFCBiome[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int width, int length)
	{
		return getBiomesForGeneration(biomes, x, z, width, length);
	}

	@Override
	public TFCBiome[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int width, int length, boolean flag)
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