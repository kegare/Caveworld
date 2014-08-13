package com.kegare.caveworld.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface ICaveMiningManager
{
	/**
	 * Sets mining count of the player.
	 * @param player The player
	 * @param count The set count amount
	 */
	public void setMiningCount(EntityPlayer player, int count);

	/**
	 * Returns mining count of the player.
	 * @param player The player
	 */
	public int getMiningCount(EntityPlayer player);

	/**
	 * Adds mining count of the player.
	 * @param player The player
	 * @param level The additional count amount
	 */
	public void addMiningCount(EntityPlayer player, int count);

	/**
	 * Returns next level-up requirement mining count amount of the player.
	 * @param player The player
	 */
	public int getNextAmount(EntityPlayer player);

	/**
	 * Sets mining level of the player.
	 * @param player The player
	 * @param count The set level amount
	 */
	public void setMiningLevel(EntityPlayer player, int level);

	/**
	 * Returns mining level of the player.
	 * @param player The player
	 */
	public int getMiningLevel(EntityPlayer player);

	/**
	 * Adds mining level of the player.
	 * @param player The player
	 * @param level The additional level amount
	 */
	public void addMiningLevel(EntityPlayer player, int level);

	public void saveMiningData(EntityPlayer player, NBTTagCompound compound);

	public void loadMiningData(EntityPlayer player, NBTTagCompound compound);
}