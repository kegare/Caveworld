package kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kegare.caveworld.core.CaveBiome;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import com.google.common.collect.Lists;

public class WorldChunkManagerCaveworld extends WorldChunkManager
{
	@Override
	public List getBiomesToSpawnIn()
	{
		return Lists.newArrayList(CaveBiome.biomeCaveworld);
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int chunkX, int chunkZ)
	{
		return CaveBiome.biomeCaveworld;
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int chunkX, int chunkZ, int width, int length, boolean flag)
	{
		return getBiomesForGeneration(biomes, chunkX, chunkZ, width, length);
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int chunkX, int chunkZ, int width, int length)
	{
		if (biomes == null || biomes.length < width * length)
		{
			biomes = new BiomeGenBase[width * length];
		}

		Arrays.fill(biomes, CaveBiome.biomeCaveworld);

		return biomes;
	}

	@Override
	public float[] getTemperatures(float[] temperatures, int chunkX, int chunkZ, int width, int length)
	{
		if (temperatures == null || temperatures.length < width * length)
		{
			temperatures = new float[width * length];
		}

		Arrays.fill(temperatures, CaveBiome.biomeCaveworld.getFloatTemperature());

		return temperatures;
	}

	@Override
	public float[] getRainfall(float[] rainfalls, int x, int z, int width, int length)
	{
		if (rainfalls == null || rainfalls.length < width * length)
		{
			rainfalls = new float[width * length];
		}

		Arrays.fill(rainfalls, CaveBiome.biomeCaveworld.getFloatRainfall());

		return rainfalls;
	}

	@Override
	public ChunkPosition findBiomePosition(int x, int y, int z, List list, Random random)
	{
		return list.contains(CaveBiome.biomeCaveworld) ? new ChunkPosition(x - z + random.nextInt(z * 2 + 1), 0, y - z + random.nextInt(z * 2 + 1)) : null;
	}

	@Override
	public boolean areBiomesViable(int x, int y, int z, List list)
	{
		return list.contains(CaveBiome.biomeCaveworld);
	}
}