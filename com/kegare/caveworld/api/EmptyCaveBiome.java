package com.kegare.caveworld.api;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class EmptyCaveBiome implements ICaveBiome
{
	@Override
	public BiomeGenBase getBiome()
	{
		return BiomeGenBase.plains;
	}

	@Override
	public int getGenWeight()
	{
		return 0;
	}

	@Override
	public BlockEntry getTerrainBlock()
	{
		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public void setGenWeight(int weight) {}
}