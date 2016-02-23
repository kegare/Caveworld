package caveworld.api;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

/**
 * NOTE: Do NOT access to this class fields.
 * You should use API from this class methods.
 */
public final class CaverAPI
{
	public static ICaverManager caverManager;

	/**
	 * Returns mining point of the entity.
	 * @param entity The entity
	 */
	public static int getMiningPoint(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getMiningPoint(entity);
	}

	/**
	 * Sets mining point of the entity.
	 * @param entity The entity
	 * @param value The set point amount
	 */
	public static void setMiningPoint(Entity entity, int value)
	{
		if (caverManager != null)
		{
			caverManager.setMiningPoint(entity, value);
		}
	}

	/**
	 * Adds mining point of the entity.
	 * @param entity The entity
	 * @param value The additional point amount
	 */
	public static void addMiningPoint(Entity entity, int value)
	{
		if (caverManager != null)
		{
			caverManager.addMiningPoint(entity, value);
		}
	}

	/**
	 * Returns mining point amount of the block and metadata
	 * @param block The block
	 * @param metadata The block metadata
	 */
	public static int getMiningPointAmount(Block block, int metadata)
	{
		return caverManager == null ? 0 : caverManager.getMiningPointAmount(block, metadata);
	}

	/**
	 * Sets mining point amount of the block and metadata
	 * @param block The block
	 * @param metadata The block metadata
	 * @param amount The amount
	 */
	public static void setMiningPointAmount(Block block, int metadata, int amount)
	{
		if (caverManager != null)
		{
			caverManager.setMiningPointAmount(block, metadata, amount);
		}
	}

	/**
	 * Sets mining point amount of the block and metadata with the ore dictionary registered name
	 * @param oredict The ore dictionary registered name
	 * @param amount The amount
	 */
	public static void setMiningPointAmount(String oredict, int amount)
	{
		if (caverManager != null)
		{
			caverManager.setMiningPointAmount(oredict, amount);
		}
	}

	/**
	 * Returns miner rank of the entity.
	 * @param entity The entity
	 */
	public static int getMinerRank(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getMinerRank(entity);
	}

	/**
	 * Returns miner rank name of the entity.
	 * @param entity The entity
	 */
	public static String getMinerRankName(Entity entity)
	{
		return caverManager == null ? "null" : caverManager.getMinerRankName(entity);
	}

	/**
	 * Sets miner rank of the entity.
	 * @param entity The entity
	 * @param rank The set rank
	 */
	public static void setMinerRank(Entity entity, int rank)
	{
		if (caverManager != null)
		{
			caverManager.setMinerRank(entity, rank);
		}
	}

	/**
	 * Returns miner ranks.<br>
	 * <br>
	 * <b>key</b>: rank<br>
	 * <b>pair</b>: left=rank name, right=rank phase
	 */
	public static Map<Integer, Pair<String, Integer>> getMinerRanks()
	{
		if (caverManager == null)
		{
			return Maps.newHashMap();
		}

		return caverManager.getMinerRanks();
	}

	/**
	 * Returns last dimension before Caveworld dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public static int getLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getLastDimension(entity);
	}

	/**
	 * Sets the last dimension before Caveworld dimension for the entity.
	 * @param entity The entity
	 * @param dimension The last dimension
	 */
	public static void setLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setLastDimension(entity, dimension);
		}
	}

	/**
	 * Returns last dimension before Cavern dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public static int getCavernLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getCavernLastDimension(entity);
	}

	/**
	 * Sets the last dimension before Cavern dimension for the entity.
	 * @param entity The entity
	 * @param dimension The last dimension
	 */
	public static void setCavernLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setCavernLastDimension(entity, dimension);
		}
	}

	/**
	 * Returns last dimension before Aqua Cavern dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public static int getAquaCavernLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getAquaCavernLastDimension(entity);
	}

	/**
	 * Sets the last dimension before Aqua Cavern dimension for the entity.
	 * @param entity The entity
	 * @param dimension The last dimension
	 */
	public static void setAquaCavernLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setAquaCavernLastDimension(entity, dimension);
		}
	}

	/**
	 * Returns last dimension before Caveland dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public static int getCavelandLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getCavelandLastDimension(entity);
	}

	/**
	 * Sets the last dimension before Caveland dimension for the entity.
	 * @param entity The entity
	 * @param dimension The last dimension
	 */
	public static void setCavelandLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setCavelandLastDimension(entity, dimension);
		}
	}

	/**
	 * Returns last dimension before Cavenia dimension for the entity.
	 * @param entity The entity
	 * @return dimension id
	 */
	public static int getCaveniaLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getCaveniaLastDimension(entity);
	}

	/**
	 * Sets the last dimension before Cavenia dimension for the entity.
	 * @param entity The entity
	 * @param dimension The last dimension
	 */
	public static void setCaveniaLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setCaveniaLastDimension(entity, dimension);
		}
	}

	/**
	 * Returns last sleep time of the entity at current dimension.
	 * @param entity The entity
	 * @param dimension The dimension
	 */
	public static long getLastSleepTime(Entity entity)
	{
		return caverManager == null ? 0L : caverManager.getLastSleepTime(entity);
	}

	/**
	 * Returns last sleep time of the entity at the dimension.
	 * @param entity The entity
	 * @param dimension The dimension
	 */
	public static long getLastSleepTime(Entity entity, int dimension)
	{
		return caverManager == null ? 0L : caverManager.getLastSleepTime(entity, dimension);
	}

	/**
	 * Sets last sleep time of the entity at current dimension.
	 * @param entity The entity
	 * @param time The sleep time
	 */
	public static void setLastSleepTime(Entity entity, long time)
	{
		if (caverManager != null)
		{
			caverManager.setLastSleepTime(entity, time);
		}
	}

	/**
	 * Sets last sleep time of the entity at current dimension.
	 * @param entity The entity
	 * @param dimension The dimension
	 * @param time The sleep time
	 */
	public static void setLastSleepTime(Entity entity, int dimension, long time)
	{
		if (caverManager != null)
		{
			caverManager.setLastSleepTime(entity, dimension, time);
		}
	}

	public static ChunkCoordinates getLastPos(Entity entity, int type)
	{
		return caverManager == null ? null : caverManager.getLastPos(entity, type);
	}

	public static ChunkCoordinates getLastPos(Entity entity, int dimension, int type)
	{
		return caverManager == null ? null : caverManager.getLastPos(entity, dimension, type);
	}

	public static void setLastPos(Entity entity, int type, ChunkCoordinates coord)
	{
		if (caverManager != null)
		{
			caverManager.setLastPos(entity, type, coord);
		}
	}

	public static void setLastPos(Entity entity, int dimension, int type, ChunkCoordinates coord)
	{
		if (caverManager != null)
		{
			caverManager.setLastPos(entity, dimension, type, coord);
		}
	}

	public static void saveData(Entity entity, NBTTagCompound compound)
	{
		if (caverManager != null)
		{
			caverManager.saveData(entity, compound);
		}
	}

	public static void loadData(Entity entity, NBTTagCompound compound)
	{
		if (caverManager != null)
		{
			caverManager.loadData(entity, compound);
		}
	}
}