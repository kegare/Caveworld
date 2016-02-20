package caveworld.api;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

public interface ICaveVeinManager
{
	public Configuration getConfig();

	public int getType();

	public boolean isReadOnly();

	public ICaveVeinManager setReadOnly(boolean flag);

	/**
	 * Adds a cave vein.
	 * @param vein The additional vein
	 * @return <tt>true</tt> if has been added successfully.
	 */
	public boolean addCaveVein(ICaveVein vein);

	/**
	 * Removes cave veins.
	 * @param vein The removing vein
	 * @return Removed vein count
	 */
	public int removeCaveVeins(ICaveVein vein);

	/**
	 * Removes cave veins.
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
	 * Removes all cave veins.
	 */
	public void clearCaveVeins();
}