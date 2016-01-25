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
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public abstract class CaveworldGenLayer extends GenLayer
{
	public CaveworldGenLayer(long seed)
	{
		super(seed);
	}

	public static GenLayer[] makeWorldLayers(long seed, WorldType type, ICaveBiomeManager manager)
	{
		GenLayer genLayer = new GenLayerIsland(1L);
		genLayer = new GenLayerFuzzyZoom(2000L, genLayer);

		genLayer = new CaveworldGenLayerBiomes(100L, genLayer, manager);
		genLayer = GenLayerZoom.magnify(2000L, genLayer, 1);

		genLayer = new CaveworldGenLayerSubBiomes(101L, genLayer);
		genLayer = GenLayerZoom.magnify(2100L, genLayer, 2);

		genLayer = new GenLayerVoronoiZoom(10L, genLayer);
		genLayer.initWorldGenSeed(seed);

		return new GenLayer[] {null, genLayer, null};
	}
}