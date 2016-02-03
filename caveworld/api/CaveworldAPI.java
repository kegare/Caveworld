package caveworld.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
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
	API_VERSION = "2.0.5";

	public static ICaveAPIHandler apiHandler;
	public static ICaveBiomeManager biomeManager;
	public static ICaveVeinManager veinManager;
	public static ICaveBiomeManager biomeCavernManager;
	public static ICaveVeinManager veinCavernManager;
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
		return biomeCavernManager == null ? new HashSet<ICaveBiome>() : biomeManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getCavernBiomeList()
	{
		return biomeCavernManager == null ? new ArrayList<BiomeGenBase>() : biomeManager.getBiomeList();
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