package com.kegare.caveworld.api;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;

public interface ICaveVeinManager
{
	/**
	 * Add a cave vein.
	 * @param vein The additional vein
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVein(ICaveVein vein);

	/**
	 * Remove cave veins.
	 * @param vein The removing vein
	 * @return Removed vein count
	 */
	public int removeCaveVeins(ICaveVein vein);

	/**
	 * Remove cave veins.
	 * @param block The removing vein block
	 * @param metadata The removing vein block metadata
	 * @return Removed vein count
	 */
	public int removeCaveVeins(Block block, int metadata);

	/**
	 * Returns random cave vein.
	 */
	public ICaveVein getRandomCaveVein(Random random);

	public List<ICaveVein> getCaveVeins();

	/**
	 * Remove all cave veins.
	 */
	public void clearCaveVeins();
}