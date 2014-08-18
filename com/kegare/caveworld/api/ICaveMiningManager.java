package com.kegare.caveworld.api;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface ICaveMiningManager
{
	/**
	 * Returns mining point of the player.
	 * @param player The player
	 */
	public int getMiningPoint(EntityPlayer player);

	/**
	 * Sets mining point of the player.
	 * @param player The player
	 * @param value The set point amount
	 */
	public void setMiningPoint(EntityPlayer player, int value);

	/**
	 * Adds mining point of the player.
	 * @param player The player
	 * @param value The additional point amount
	 */
	public void addMiningPoint(EntityPlayer player, int value);

	/**
	 * Returns mining point amount of the block and metadata
	 * @param block The block
	 * @param metadata The block metadata
	 */
	public int getMiningPointAmount(Block block, int metadata);

	/**
	 * Sets mining point amount of the block and metadata
	 * @param block The block
	 * @param metadata The block metadata
	 * @param amount The amount
	 */
	public void setMiningPointAmount(Block block, int metadata, int amount);

	public void saveMiningData(EntityPlayer player, NBTTagCompound compound);

	public void loadMiningData(EntityPlayer player, NBTTagCompound compound);
}