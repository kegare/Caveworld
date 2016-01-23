/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import caveworld.api.ICaveBiomeManager;
import caveworld.world.genlayer.CaveworldGenLayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
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
	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;
	private List<BiomeGenBase> biomesToSpawnIn;

	public WorldChunkManagerCaveworld()
	{
		this.biomeCache = new BiomeCache(this);
		this.biomesToSpawnIn = Lists.newArrayList();
		this.biomesToSpawnIn.addAll(allowedBiomes);
	}

	public WorldChunkManagerCaveworld(long seed, WorldType worldType, ICaveBiomeManager manager)
	{
		this();
		GenLayer[] layers = CaveworldGenLayer.makeWorldLayers(seed, worldType, manager);
		layers = getModdedBiomeGenerators(worldType, seed, layers);
		this.genBiomes = layers[0];
		this.biomeIndexLayer = layers[1];
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
	public float[] getRainfall(float[] downfalls, int x, int z, int width, int length)
	{
		IntCache.resetIntCache();

		if (downfalls == null || downfalls.length < width * length)
		{
			downfalls = new float[width * length];
		}

		int[] aint = biomeIndexLayer.getInts(x, z, width, length);

		for (int i = 0; i < width * length; ++i)
		{
			try
			{
				float f = BiomeGenBase.getBiome(aint[i]).getIntRainfall() / 65536.0F;

				if (f > 1.0F)
				{
					f = 1.0F;
				}

				downfalls[i] = f;
			}
			catch (Throwable throwable)
			{
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory category = crashreport.makeCategory("DownfallBlock");
				category.addCrashSection("biome id", Integer.valueOf(i));
				category.addCrashSection("downfalls[] size", Integer.valueOf(downfalls.length));
				category.addCrashSection("x", Integer.valueOf(x));
				category.addCrashSection("z", Integer.valueOf(z));
				category.addCrashSection("w", Integer.valueOf(width));
				category.addCrashSection("h", Integer.valueOf(length));
				throw new ReportedException(crashreport);
			}
		}

		return downfalls;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getTemperatureAtHeight(float temperature, int y)
	{
		return temperature;
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int depth)
	{
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < width * depth)
		{
			biomes = new BiomeGenBase[width * depth];
		}

		int[] aint = this.genBiomes.getInts(x, z, width, depth);

		try
		{
			for (int i = 0; i < width * depth; ++i)
			{
				biomes[i] = BiomeGenBase.getBiome(aint[i]);
			}

			return biomes;
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory category = crashreport.makeCategory("RawBiomeBlock");
			category.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
			category.addCrashSection("x", Integer.valueOf(x));
			category.addCrashSection("z", Integer.valueOf(z));
			category.addCrashSection("w", Integer.valueOf(width));
			category.addCrashSection("h", Integer.valueOf(depth));
			throw new ReportedException(crashreport);
		}
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int width, int depth)
	{
		return getBiomeGenAt(biomes, x, z, width, depth, true);
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int z, int width, int length, boolean cache)
	{
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < width * length)
		{
			biomes = new BiomeGenBase[width * length];
		}

		if (cache && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
		{
			BiomeGenBase[] cachedBiomes = biomeCache.getCachedBiomes(x, z);
			System.arraycopy(cachedBiomes, 0, biomes, 0, width * length);

			return biomes;
		}

		int[] aint = biomeIndexLayer.getInts(x, z, width, length);

		for (int i = 0; i < width * length; ++i)
		{
				biomes[i] = BiomeGenBase.getBiome(aint[i]);
		}

		return biomes;
	}

	@Override
	public boolean areBiomesViable(int x, int y, int z, List list)
	{
		IntCache.resetIntCache();
		int bx = x - z >> 2;
		int bz = y - z >> 2;
		int var1 = x + z >> 2;
		int var2 = y + z >> 2;
		int width = var1 - bx + 1;
		int depth = var2 - bz + 1;
		int[] aint = this.genBiomes.getInts(bx, bz, width, depth);

		try
		{
			for (int i = 0; i < width * depth; ++i)
			{
				BiomeGenBase biome = BiomeGenBase.getBiome(aint[i]);

				if (!list.contains(biome))
				{
					return false;
				}
			}

			return true;
		}
		catch (Throwable throwable)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
			crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
			crashreportcategory.addCrashSection("x", Integer.valueOf(x));
			crashreportcategory.addCrashSection("z", Integer.valueOf(y));
			crashreportcategory.addCrashSection("radius", Integer.valueOf(z));
			crashreportcategory.addCrashSection("allowed", list);
			throw new ReportedException(crashreport);
		}
	}

	@Override
	public ChunkPosition findBiomePosition(int x, int y, int z, List list, Random random)
	{
		IntCache.resetIntCache();
		int bx = x - z >> 2;
		int bz = y - z >> 2;
		int var1 = x + z >> 2;
		int var2 = y + z >> 2;
		int width = var1 - bx + 1;
		int depth = var2 - bz + 1;
		int[] aint = this.genBiomes.getInts(bx, bz, width, depth);
		ChunkPosition pos = null;
		int count = 0;

		for (int i = 0; i < width * depth; ++i)
		{
			int j = bx + i % width << 2;
			int k = bz + i / width << 2;
			BiomeGenBase biome = BiomeGenBase.getBiome(aint[i]);

			if (list.contains(biome) && (pos == null || random.nextInt(count + 1) == 0))
			{
				pos = new ChunkPosition(j, 0, k);

				++count;
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