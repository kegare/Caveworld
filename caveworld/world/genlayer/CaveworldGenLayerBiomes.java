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

	public CaveworldGenLayerBiomes(long seed, GenLayer layer, ICaveBiomeManager manager)
	{
		super(seed);
		this.biomeManager = manager;
		this.allowedBiomes = biomeManager.getBiomeList().toArray(new BiomeGenBase[0]);
		this.parent = layer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ)
	{
		parent.getInts(x, z, sizeX, sizeZ);
		int[] ints = IntCache.getIntCache(sizeX * sizeZ);

		for (int dz = 0; dz < sizeZ; ++dz)
		{
			for (int dx = 0; dx < sizeX; ++dx)
			{
				initChunkSeed(dx + x, dz + z);

				ints[dx + dz * sizeX] = allowedBiomes[nextInt(allowedBiomes.length)].biomeID;
			}
		}

		return ints;
	}
}