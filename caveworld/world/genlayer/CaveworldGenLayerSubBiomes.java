/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world.genlayer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class CaveworldGenLayerSubBiomes extends CaveworldGenLayer
{
	public CaveworldGenLayerSubBiomes(long seed, GenLayer layer)
	{
		super(seed);
		this.parent = layer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ)
	{
		int[] currentBiomes = parent.getInts(x - 2, z - 2, sizeX + 4, sizeZ + 4);
		int[] biomes = IntCache.getIntCache(sizeX * sizeZ);

		for (int dz = 0; dz < sizeZ; ++dz)
		{
			for (int dx = 0; dx < sizeX; ++dx)
			{
				initChunkSeed(dx + x, dz + z);

				biomes[dx + dz * sizeX] = currentBiomes[dx + 2 + (dz + 2) * (sizeX + 4)];
			}
		}

		initChunkSeed(x, z);

		return biomes;
	}
}