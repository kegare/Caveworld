package caveworld.api;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

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

	/**
	 * Returns miner rank of the entity.
	 * @param entity The entity
	 */
	public int getMinerRank(Entity entity);

	/**
	 * Returns miner rank name of the entity.
	 * @param entity The entity
	 */
	public String getMinerRankName(Entity entity);

	/**
	 * Sets miner rank of the entity.
	 * @param entity The entity
	 * @param rank The set rank
	 */
	public void setMinerRank(Entity entity, int rank);

	/**
	 * Returns miner ranks.<br>
	 * <br>
	 * <b>key</b>: rank<br>
	 * <b>pair</b>: left=rank name, right=rank phase
	 */
	public Map<Integer, Pair<String, Integer>> getMinerRanks();

	/**
	 * Returns last dimension before Caveworld dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public int getLastDimension(Entity entity);

	public void setLastDimension(Entity entity, int dimension);

	/**
	 * Returns last dimension before Cavern dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public int getCavernLastDimension(Entity entity);

	public void setCavernLastDimension(Entity entity, int dimension);

	/**
	 * Returns last dimension before Aqua Cavern dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public int getAquaCavernLastDimension(Entity entity);

	public void setAquaCavernLastDimension(Entity entity, int dimension);

	/**
	 * Returns last dimension before Caveland dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public int getCavelandLastDimension(Entity entity);

	public void setCavelandLastDimension(Entity entity, int dimension);

	/**
	 * Returns last dimension before Cavenia dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public int getCaveniaLastDimension(Entity entity);

	public void setCaveniaLastDimension(Entity entity, int dimension);

	public ChunkCoordinates getLastPos(Entity entity, int dimension, int type);

	public void setLastPos(Entity entity, int dimension, int type, ChunkCoordinates coord);

	public void saveData(Entity entity, NBTTagCompound compound);

	public void loadData(Entity entity, NBTTagCompound compound);
}