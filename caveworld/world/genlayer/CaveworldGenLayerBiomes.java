/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world.genlayer;

import caveworld.api.ICaveBiomeManager;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class CaveworldGenLayerBiomes extends GenLayer
{
	private final ICaveBiomeManager biomeManager;

	protected BiomeGenBase[] allowedBiomes;

	public CaveworldGenLayerBiomes(long seed, ICaveBiomeManager manager)
	{
		super(seed);
		this.biomeManager = manager;
		this.allowedBiomes = biomeManager.getBiomeList().toArray(new BiomeGenBase[0]);
	}

	public CaveworldGenLayerBiomes(long seed, GenLayer layer, ICaveBiomeManager manager)
	{
		this(seed, manager);
		this.parent = layer;
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth)
	{
		int[] dest = IntCache.getIntCache(width * depth);

		for (int dz = 0; dz < depth; dz++)
		{
			for (int dx = 0; dx < width; dx++)
			{
				initChunkSeed(dx + x, dz + z);

				dest[dx + dz * width] = allowedBiomes[nextInt(allowedBiomes.length)].biomeID;
			}
		}

		return dest;
	}
}