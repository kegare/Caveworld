package caveworld.api;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public interface ICaverManager
{
	/**
	 * Returns mining point of the entity.
	 * @param entity The entity
	 */
	public int getMiningPoint(Entity entity);

	/**
	 * Sets mining point of the entity.
	 * @param entity The entity
	 * @param value The set point amount
	 */
	public void setMiningPoint(Entity entity, int value);

	/**
	 * Adds mining point of the entity.
	 * @param entity The entity
	 * @param value The additional point amount
	 */
	public void addMiningPoint(Entity entity, int value);

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

	/**
	 * Sets mining point amount of the block and metadata with the ore dictionary registered name
	 * @param oredict The ore dictionary registered name
	 * @param amount The amount
	 */
	public void setMiningPointAmount(String oredict, int amount);

	public void clearMiningPointAmounts();

	public int getLastDimension(Entity entity);

	public void setLastDimension(Entity entity, int dimension);

	public int getCavernLastDimension(Entity entity);

	public void setCavernLastDimension(Entity entity, int dimension);

	public void saveData(Entity entity, NBTTagCompound compound);

	public void loadData(Entity entity, NBTTagCompound compound);
}