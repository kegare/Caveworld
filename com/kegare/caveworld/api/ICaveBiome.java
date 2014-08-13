package com.kegare.caveworld.api;

import net.minecraft.world.biome.BiomeGenBase;

public interface ICaveBiome
{
	public BiomeGenBase getBiome();

	public int getGenWeight();

	public BlockEntry getTerrainBlock();

	public void setGenWeight(int weight);
}