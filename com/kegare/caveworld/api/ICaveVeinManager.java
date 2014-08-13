package com.kegare.caveworld.api;

import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;

public interface ICaveVeinManager
{
	/**
	 * Add a cave vein.
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVein(ICaveVein vein);

	/**
	 * Add a cave vein with config.
	 * @param name The vein name (If <tt>null</tt> or empty, does not add with config)
	 * @see #addCaveVein(ICaveVein)
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVeinWithConfig(String name, ICaveVein vein);

	/**
	 * Add a cave vein from config.
	 * @param name The vein name (If <tt>null</tt> or empty, does not add with config)
	 * @see #addCaveVein(ICaveVein)
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVeinFromConfig(String name);

	/**
	 * Remove a cave vein.
	 * @param vein The removing vein
	 * @return <tt>true</tt> if has been removed successfully.
	 */
	public boolean removeCaveVein(ICaveVein vein);

	/**
	 * Remove a cave vein with config.
	 * @param name The removing vein name
	 * @param vein The removing vein
	 * @return <tt>true</tt> if has been removed successfully.
	 */
	public boolean removeCaveVeinWithConfig(String name, ICaveVein vein);

	/**
	 * Remove a cave vein from config.
	 * @param name The removing vein name
	 * @return <tt>true</tt> if has been removed successfully.
	 */
	public boolean removeCaveVeinFromConfig(String name);

	/**
	 * Remove cave veins.
	 * @param block The removing block
	 * @param metadata The removing block metadata
	 * @return Removed vein count
	 */
	public int removeCaveVeins(Block block, int metadata);

	public ICaveVein getRandomVein(Random random);

	public Set<ICaveVein> getCaveVeins();

	/**
	 * Remove all cave veins.
	 */
	public void clearCaveVeins();
}