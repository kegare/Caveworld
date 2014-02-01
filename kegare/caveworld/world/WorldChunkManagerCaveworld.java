package kegare.caveworld.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import kegare.caveworld.core.Config;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WorldChunkManagerCaveworld extends WorldChunkManager
{
	private final World worldObj;
	private final Random random;

	private static final Set<BiomeGenBase> biomeSet = Sets.newHashSet();
	protected static final Map<Long, BiomeGenBase> biomeMap = Maps.newHashMap();

	public WorldChunkManagerCaveworld(World world)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());

		if (biomeSet.isEmpty())
		{
			for (int i : Config.genBiomes)
			{
				if (i >= 0 && i < BiomeGenBase.biomeList.length && BiomeGenBase.biomeList[i] != null)
				{
					biomeSet.add(BiomeGenBase.biomeList[i]);
				}
			}
		}
	}

	@Override
	public List getBiomesToSpawnIn()
	{
		return Lists.newArrayList(biomeSet);
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int x, int z)
	{
		BiomeGenBase biome;
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		long chunkSeed = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);

		if (!biomeMap.containsKey(chunkSeed))
		{
			long worldSeed = worldObj.getSeed();
			random.setSeed(worldSeed);
			long xSeed = random.nextLong() >> 2 + 1L;
			long zSeed = random.nextLong() >> 2 + 1L;
			random.setSeed(chunkX * xSeed + chunkZ * zSeed ^ worldSeed);

			biome = Lists.newArrayList(biomeSet).get(random.nextInt(biomeSet.size()));

			if (biome != null) biomeMap.put(chunkSeed, biome);
		}
		else
		{
			biome = biomeMap.get(chunkSeed);
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