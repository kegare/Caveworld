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
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class CaveworldGenLayer extends GenLayer
{
	public CaveworldGenLayer(long seed)
	{
		super(seed);
	}

	public static GenLayer[] makeWorldLayers(long seed, WorldType type, ICaveBiomeManager manager)
	{
		GenLayer biomes = new CaveworldGenLayerBiomes(1L, manager);
		biomes = new GenLayerZoom(1000L, biomes);
		biomes = new GenLayerZoom(1001L, biomes);
		biomes = new GenLayerZoom(1002L, biomes);
		biomes = new GenLayerZoom(1003L, biomes);
		GenLayer genlayer = new GenLayerVoronoiZoom(10L, biomes);
		biomes.initWorldGenSeed(seed);
		genlayer.initWorldGenSeed(seed);

		return new GenLayer[] {biomes, genlayer};
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth)
	{
		return null;
	}
}