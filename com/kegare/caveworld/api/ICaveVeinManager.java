package com.kegare.caveworld.api;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;

public interface ICaveVeinManager
{
	/**
	 * Add a cave vein.
	 * @param name The vein name
	 * @param vein The additional vein
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVein(String name, ICaveVein vein);

	/**
	 * Add a cave vein with config.
	 * @see #addCaveVein(String, ICaveVein)
	 */
	public boolean addCaveVeinWithConfig(String name, ICaveVein vein);

	/**
	 * Add a cave vein from config.
	 * @param name The vein name
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVeinFromConfig(String name);

	/**
	 * Remove a cave vein.
	 * @param name The removing vein name
	 * @return <tt>true</tt> if has been removed successfully.
	 */
	public boolean removeCaveVein(String name);

	/**
	 * Remove a cave vein with config.
	 * @see #removeCaveVein(String)
	 */
	public boolean removeCaveVeinWithConfig(String name);

	/**
	 * Remove a cave vein from config.
	 * @see #removeCaveVein(String)
	 */
	public boolean removeCaveVeinFromConfig(String name);

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

	/**
	 * Returns cave vein of the vein name.
	 * @param name The vein name
	 */
	public ICaveVein getCaveVein(String name);

	public Map<String, ICaveVein> getCaveVeins();

	/**
	 * Remove all cave veins.
	 */
	public void clearCaveVeins();
}