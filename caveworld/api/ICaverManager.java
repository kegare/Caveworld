package caveworld.api;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

public interface ICaverManager
{
	public int getMiningPoint(Entity entity);

	public void setMiningPoint(Entity entity, int value);

	public void addMiningPoint(Entity entity, int value);

	public int getMiningPointAmount(Block block, int metadata);

	public void setMiningPointAmount(Block block, int metadata, int amount);

	public void setMiningPointAmount(String oredict, int amount);

	public void clearMiningPointAmounts();

	public int getMinerRank(Entity entity);

	public String getMinerRankName(Entity entity);

	public void setMinerRank(Entity entity, int rank);

	public Map<Integer, Pair<String, Integer>> getMinerRanks();

	public int getLastDimension(Entity entity);

	public void setLastDimension(Entity entity, int dimension);

	public int getCavernLastDimension(Entity entity);

	public void setCavernLastDimension(Entity entity, int dimension);

	public int getAquaCavernLastDimension(Entity entity);

	public void setAquaCavernLastDimension(Entity entity, int dimension);

	public int getCavelandLastDimension(Entity entity);

	public void setCavelandLastDimension(Entity entity, int dimension);

	public int getCaveniaLastDimension(Entity entity);

	public void setCaveniaLastDimension(Entity entity, int dimension);

	public long getLastSleepTime(Entity entity);

	public long getLastSleepTime(Entity entity, int dimension);

	public void setLastSleepTime(Entity entity, long time);

	public void setLastSleepTime(Entity entity, int dimension, long time);

	public ChunkCoordinates getLastPos(Entity entity, int type);

	public ChunkCoordinates getLastPos(Entity entity, int dimension, int type);

	public void setLastPos(Entity entity, int type, ChunkCoordinates coord);

	public void setLastPos(Entity entity, int dimension, int type, ChunkCoordinates coord);

	public void saveData(Entity entity, NBTTagCompound compound);

	public void loadData(Entity entity, NBTTagCompound compound);
}