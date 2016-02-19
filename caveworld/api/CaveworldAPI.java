package caveworld.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;

/**
 * NOTE: Do NOT access to this class fields.
 * You should use API from this class methods.
 */
public final class CaveworldAPI
{
	public static final String
	MODID = "caveworld",
	API_VERSION = "2.1.5";

	public static ICaveAPIHandler apiHandler;
	public static ICaveBiomeManager biomeManager;
	public static ICaveVeinManager veinManager;
	public static ICaveBiomeManager biomeCavernManager;
	public static ICaveVeinManager veinCavernManager;
	public static ICaveBiomeManager biomeAquaCavernManager;
	public static ICaveVeinManager veinAquaCavernManager;
	public static ICaverManager caverManager;

	private CaveworldAPI() {}

	/**
	 * @see ICaveAPIHandler#getVersion()
	 */
	public static String getVersion()
	{
		return apiHandler == null ? null : apiHandler.getVersion();
	}

	/**
	 * @see ICaveAPIHandler#getDimension()
	 */
	public static int getDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getDimension();
	}

	/**
	 * @see ICaveAPIHandler#getCavernDimension()
	 */
	public static int getCavernDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getCavernDimension();
	}

	/**
	 * @see ICaveAPIHandler#getAquaCavernDimension()
	 */
	public static int getAquaCavernDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getAquaCavernDimension();
	}

	/**
	 * @see ICaveAPIHandler#getCavelandDimension()
	 */
	public static int getCavelandDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getCavelandDimension();
	}

	/**
	 * @see ICaveAPIHandler#getCaveniaDimension()
	 */
	public static int getCaveniaDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getCaveniaDimension();
	}

	/**
	 * @see ICaveAPIHandler#isEntityInCaveworld(Entity)
	 */
	public static boolean isEntityInCaveworld(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaveworld(entity);
	}

	/**
	 * @see ICaveAPIHandler#isEntityInCavern(Entity)
	 */
	public static boolean isEntityInCavern(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCavern(entity);
	}

	/**
	 * @see ICaveAPIHandler#isEntityInAquaCavern(Entity)
	 */
	public static boolean isEntityInAquaCavern(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInAquaCavern(entity);
	}

	/**
	 * @see ICaveAPIHandler#isEntityInCaveland(Entity)
	 */
	public static boolean isEntityInCaveland(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaveland(entity);
	}

	/**
	 * @see ICaveAPIHandler#isEntityInCavenia(Entity)
	 */
	public static boolean isEntityInCavenia(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCavenia(entity);
	}

	/**
	 * @see ICaveAPIHandler#isEntityInCaves(Entity)
	 */
	public static boolean isEntityInCaves(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaves(entity);
	}

	/**
	 * @see ICaveAPIHandler#isCaveDimensions(int)
	 */
	public static boolean isCaveDimensions(int dim)
	{
		return apiHandler != null && apiHandler.isCaveDimensions(dim);
	}

	/**
	 * @see ICaveAPIHandler#isHardcore()
	 */
	public static boolean isHardcore()
	{
		return apiHandler != null && apiHandler.isHardcore();
	}

	public static boolean isCaveborn()
	{
		return getCaveborn() > 0;
	}

	/**
	 * @see ICaveAPIHandler#getCaveborn()
	 */
	public static int getCaveborn()
	{
		return apiHandler == null ? 0 : apiHandler.getCaveborn();
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addCaveBiome(ICaveBiome biome)
	{
		return biomeManager != null && biomeManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeCaveBiome(BiomeGenBase biome)
	{
		return biomeManager != null && biomeManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveBiomeCount()
	{
		return biomeManager == null ? 0 : biomeManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getCaveBiome(BiomeGenBase biome)
	{
		return biomeManager == null ? new EmptyCaveBiome(biome) : biomeManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomCaveBiome(Random random)
	{
		return biomeManager == null ? null : biomeManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getCaveBiomes()
	{
		return biomeManager == null ? new HashSet<ICaveBiome>() : biomeManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getBiomeList()
	{
		return biomeManager == null ? new ArrayList<BiomeGenBase>() : biomeManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearCaveBiomes()
	{
		if (biomeManager != null)
		{
			biomeManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addCaveVein(ICaveVein vein)
	{
		return veinManager != null && veinManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeCaveVeins(ICaveVein vein)
	{
		return veinManager == null ? 0 : veinManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeCaveVeins(Block block, int metadata)
	{
		return veinManager == null ? 0 : veinManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomCaveVein(Random random)
	{
		return veinManager == null ? null : veinManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getCaveVeins()
	{
		return veinManager == null ? new ArrayList<ICaveVein>() : veinManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearCaveVeins()
	{
		if (veinManager != null)
		{
			veinManager.clearCaveVeins();
		}
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addCavernBiome(ICaveBiome biome)
	{
		return biomeCavernManager != null && biomeCavernManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeCavernBiome(BiomeGenBase biome)
	{
		return biomeCavernManager != null && biomeCavernManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveCavernBiomeCount()
	{
		return biomeCavernManager == null ? 0 : biomeCavernManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getCavernBiome(BiomeGenBase biome)
	{
		return biomeCavernManager == null ? new EmptyCaveBiome(biome) : biomeCavernManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomCavernBiome(Random random)
	{
		return biomeCavernManager == null ? null : biomeCavernManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getCavernBiomes()
	{
		return biomeCavernManager == null ? new HashSet<ICaveBiome>() : biomeCavernManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getCavernBiomeList()
	{
		return biomeCavernManager == null ? new ArrayList<BiomeGenBase>() : biomeCavernManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearCavernBiomes()
	{
		if (biomeCavernManager != null)
		{
			biomeCavernManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addCavernVein(ICaveVein vein)
	{
		return veinCavernManager != null && veinCavernManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeCavernVeins(ICaveVein vein)
	{
		return veinCavernManager == null ? 0 : veinCavernManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeCavernVeins(Block block, int metadata)
	{
		return veinCavernManager == null ? 0 : veinCavernManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomCavernVein(Random random)
	{
		return veinCavernManager == null ? null : veinCavernManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getCavernVeins()
	{
		return veinCavernManager == null ? new ArrayList<ICaveVein>() : veinCavernManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearCavernVeins()
	{
		if (veinCavernManager != null)
		{
			veinCavernManager.clearCaveVeins();
		}
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addAquaCavernBiome(ICaveBiome biome)
	{
		return biomeAquaCavernManager != null && biomeAquaCavernManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeAquaCavernBiome(BiomeGenBase biome)
	{
		return biomeAquaCavernManager != null && biomeAquaCavernManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveAquaCavernBiomeCount()
	{
		return biomeAquaCavernManager == null ? 0 : biomeAquaCavernManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getAquaCavernBiome(BiomeGenBase biome)
	{
		return biomeAquaCavernManager == null ? new EmptyCaveBiome(biome) : biomeAquaCavernManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomAquaCavernBiome(Random random)
	{
		return biomeAquaCavernManager == null ? null : biomeAquaCavernManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getAquaCavernBiomes()
	{
		return biomeAquaCavernManager == null ? new HashSet<ICaveBiome>() : biomeAquaCavernManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getAquaCavernBiomeList()
	{
		return biomeAquaCavernManager == null ? new ArrayList<BiomeGenBase>() : biomeAquaCavernManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearAquaCavernBiomes()
	{
		if (biomeAquaCavernManager != null)
		{
			biomeAquaCavernManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addAquaCavernVein(ICaveVein vein)
	{
		return veinAquaCavernManager != null && veinAquaCavernManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeAquaCavernVeins(ICaveVein vein)
	{
		return veinAquaCavernManager == null ? 0 : veinAquaCavernManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeAquaCavernVeins(Block block, int metadata)
	{
		return veinAquaCavernManager == null ? 0 : veinAquaCavernManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomAquaCavernVein(Random random)
	{
		return veinAquaCavernManager == null ? null : veinAquaCavernManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getAquaCavernVeins()
	{
		return veinAquaCavernManager == null ? new ArrayList<ICaveVein>() : veinAquaCavernManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearAquaCavernVeins()
	{
		if (veinAquaCavernManager != null)
		{
			veinAquaCavernManager.clearCaveVeins();
		}
	}

	public static void addCavesVein(ICaveVein vein)
	{
		addCaveVein(vein);
		addCavernVein(vein);
		addAquaCavernVein(vein);
	}

	/**
	 * @see ICaverManager#getMiningPoint(Entity)
	 */
	public static int getMiningPoint(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getMiningPoint(entity);
	}

	/**
	 * @see ICaverManager#setMiningPoint(Entity, int)
	 */
	public static void setMiningPoint(Entity entity, int value)
	{
		if (caverManager != null)
		{
			caverManager.setMiningPoint(entity, value);
		}
	}

	/**
	 * @see ICaverManager#addMiningPoint(Entity, int)
	 */
	public static void addMiningPoint(Entity entity, int value)
	{
		if (caverManager != null)
		{
			caverManager.addMiningPoint(entity, value);
		}
	}

	/**
	 * @see ICaverManager#getMiningPointAmount(Block, int)
	 */
	public static int getMiningPointAmount(Block block, int metadata)
	{
		return caverManager == null ? 0 : caverManager.getMiningPointAmount(block, metadata);
	}

	/**
	 * @see ICaverManager#setMiningPointAmount(Block, int, int)
	 */
	public static void setMiningPointAmount(Block block, int metadata, int amount)
	{
		if (caverManager != null)
		{
			caverManager.setMiningPointAmount(block, metadata, amount);
		}
	}

	/**
	 * @see ICaverManager#setMiningPointAmount(String, int)
	 */
	public static void setMiningPointAmount(String oredict, int amount)
	{
		if (caverManager != null)
		{
			caverManager.setMiningPointAmount(oredict, amount);
		}
	}

	/**
	 * @see ICaverManager#getMinerRank(Entity)
	 */
	public static int getMinerRank(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getMinerRank(entity);
	}

	/**
	 * @see ICaverManager#getMinerRankName(Entity)
	 */
	public static String getMinerRankName(Entity entity)
	{
		return caverManager == null ? "null" : caverManager.getMinerRankName(entity);
	}

	/**
	 * @see ICaverManager#setMinerRank(Entity, int)
	 */
	public static void setMinerRank(Entity entity, int rank)
	{
		if (caverManager != null)
		{
			caverManager.setMinerRank(entity, rank);
		}
	}

	/**
	 * @see ICaverManager#getMinerRanks()
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
	 * @see ICaverManager#getLastDimension(Entity)
	 */
	public static int getLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getLastDimension(entity);
	}

	/**
	 * @see ICaverManager#setLastDimension(Entity, int)
	 */
	public static void setLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setLastDimension(entity, dimension);
		}
	}

	/**
	 * @see ICaverManager#getCavernLastDimension(Entity)
	 */
	public static int getCavernLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getCavernLastDimension(entity);
	}

	/**
	 * @see ICaverManager#setCavernLastDimension(Entity, int)
	 */
	public static void setCavernLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setCavernLastDimension(entity, dimension);
		}
	}

	/**
	 * @see ICaverManager#getAquaCavernLastDimension(Entity)
	 */
	public static int getAquaCavernLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getAquaCavernLastDimension(entity);
	}

	/**
	 * @see ICaverManager#setAquaCavernLastDimension(Entity, int)
	 */
	public static void setAquaCavernLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setAquaCavernLastDimension(entity, dimension);
		}
	}

	/**
	 * @see ICaverManager#getCavelandLastDimension(Entity)
	 */
	public static int getCavelandLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getCavelandLastDimension(entity);
	}

	/**
	 * @see ICaverManager#setCavelandLastDimension(Entity, int)
	 */
	public static void setCavelandLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setCavelandLastDimension(entity, dimension);
		}
	}

	/**
	 * @see ICaverManager#getCaveniaLastDimension(Entity)
	 */
	public static int getCaveniaLastDimension(Entity entity)
	{
		return caverManager == null ? 0 : caverManager.getCaveniaLastDimension(entity);
	}

	/**
	 * @see ICaverManager#setCaveniaLastDimension(Entity, int)
	 */
	public static void setCaveniaLastDimension(Entity entity, int dimension)
	{
		if (caverManager != null)
		{
			caverManager.setCaveniaLastDimension(entity, dimension);
		}
	}

	/**
	 * @see ICaverManager#getLastSleepTime(Entity)
	 */
	public static long getLastSleepTime(Entity entity)
	{
		return caverManager == null ? 0L : caverManager.getLastSleepTime(entity);
	}

	/**
	 * @see ICaverManager#getLastSleepTime(Entity, int)
	 */
	public static long getLastSleepTime(Entity entity, int dimension)
	{
		return caverManager == null ? 0L : caverManager.getLastSleepTime(entity, dimension);
	}

	/**
	 * @see ICaverManager#setLastSleepTime(Entity, long)
	 */
	public static void setLastSleepTime(Entity entity, long time)
	{
		if (caverManager != null)
		{
			caverManager.setLastSleepTime(entity, time);
		}
	}

	/**
	 * @see ICaverManager#setLastSleepTime(Entity, int, long)
	 */
	public static void setLastSleepTime(Entity entity, int dimension, long time)
	{
		if (caverManager != null)
		{
			caverManager.setLastSleepTime(entity, dimension, time);
		}
	}

	/**
	 * @see ICaverManager#getLastPos(Entity, int)
	 */
	public static ChunkCoordinates getLastPos(Entity entity, int type)
	{
		return caverManager == null ? null : caverManager.getLastPos(entity, type);
	}

	/**
	 * @see ICaverManager#getLastPos(Entity, int, int)
	 */
	public static ChunkCoordinates getLastPos(Entity entity, int dimension, int type)
	{
		return caverManager == null ? null : caverManager.getLastPos(entity, dimension, type);
	}

	/**
	 * @see ICaverManager#setLastPos(Entity, int, ChunkCoordinates)
	 */
	public static void setLastPos(Entity entity, int type, ChunkCoordinates coord)
	{
		if (caverManager != null)
		{
			caverManager.setLastPos(entity, type, coord);
		}
	}

	/**
	 * @see ICaverManager#setLastPos(Entity, int, int, ChunkCoordinates)
	 */
	public static void setLastPos(Entity entity, int dimension, int type, ChunkCoordinates coord)
	{
		if (caverManager != null)
		{
			caverManager.setLastPos(entity, dimension, type, coord);
		}
	}

	/**
	 * @see ICaverManager#saveData(Entity, NBTTagCompound)
	 */
	public static void saveData(Entity entity, NBTTagCompound compound)
	{
		if (caverManager != null)
		{
			caverManager.saveData(entity, compound);
		}
	}

	/**
	 * @see ICaverManager#loadData(Entity, NBTTagCompound)
	 */
	public static void loadData(Entity entity, NBTTagCompound compound)
	{
		if (caverManager != null)
		{
			caverManager.loadData(entity, compound);
		}
	}
}