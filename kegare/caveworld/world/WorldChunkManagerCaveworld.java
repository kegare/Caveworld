package kegare.caveworld.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import kegare.caveworld.core.Caveworld;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import com.google.common.collect.Sets;

public class WorldChunkManagerCaveworld extends WorldChunkManager
{
	private final World worldObj;
	private final Random random;

	private final Set<BiomeGenBase> biomeList = Sets.newLinkedHashSet();

	protected WorldChunkManagerCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public List getBiomesToSpawnIn()
	{
		return allowedBiomes;
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		if (biomeList.size() <= 0)
		{
			for (int biomeID : Caveworld.genBiomes)
			{
				if (biomeID >= 0 && biomeID <= 255)
				{
					BiomeGenBase biome = BiomeGenBase.biomeList[biomeID];

					if (biome != null)
					{
						biomeList.add(biome);
					}
				}
			}
		}

		long worldSeed = worldObj.getSeed();
		random.setSeed(worldSeed);
		long xSeed = random.nextLong() >> 2 + 1L;
		long zSeed = random.nextLong() >> 2 + 1L;
		random.setSeed((xSeed * (x >> 4) + zSeed * (z >> 4)) ^ worldSeed);
		BiomeGenBase biome = biomeList.toArray(new BiomeGenBase[biomeList.size()])[random.nextInt(biomeList.size())];

		return biome != null ? biome : BiomeGenBase.plains;
	}

	@Override
	public float[] getRainfall(float[] rainfalls, int x, int z, int width, int length)
	{
		if (rainfalls == null || rainfalls.length < width * length)
		{
			rainfalls = new float[width * length];
		}

		Arrays.fill(rainfalls, getBiomeGenAt(x, z).getFloatRainfall());

		return rainfalls;
	}

	@Override
	public float[] getTemperatures(float[] temperatures, int x, int z, int width, int length)
	{
		if (temperatures == null || temperatures.length < width * length)
		{
			temperatures = new float[width * length];
		}

		Arrays.fill(temperatures, getBiomeGenAt(x, z).getFloatTemperature());

		return temperatures;
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
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int width, int depth)
	{
		return getBiomeGenAt(biomes, x, z, width, depth, true);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int width, int length, boolean flag)
	{
		if (biomes == null || biomes.length < width * length)
		{
			biomes = new BiomeGenBase[width * length];
		}

		Arrays.fill(biomes, getBiomeGenAt(x, z));

		return biomes;
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