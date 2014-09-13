package com.kegare.caveworld.api;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

public interface ICaveBiomeManager
{
	/**
	 * Add a cave biome.
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveBiome(ICaveBiome biome);

	/**
	 * Remove a cave biome.
	 * @param biome The removing biome
	 * @return <tt>true</tt> if has been removed successfully.
	 */
	public boolean removeCaveBiome(BiomeGenBase biome);

	public int getActiveBiomeCount();

	public ICaveBiome getCaveBiome(BiomeGenBase biome);

	public ICaveBiome getRandomCaveBiome(Random random);

	public Set<ICaveBiome> getCaveBiomes();

	public List<BiomeGenBase> getBiomeList();

	/**
	 * Remove all cave biomes.
	 */
	public void clearCaveBiomes();
}