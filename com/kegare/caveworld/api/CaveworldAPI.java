package com.kegare.caveworld.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;

/**
 * NOTE: Do NOT access to this class fields.
 * You should use API from this class methods.
 */
public final class CaveworldAPI
{
	public static ICaveAPIHandler apiHandler;
	public static ICaveBiomeManager biomeManager;
	public static ICaveVeinManager veinManager;
	public static ICaveBiomeManager biomeDeepManager;
	public static ICaveVeinManager veinDeepManager;
	public static ICaveMiningManager miningManager;

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
	 * @see ICaveAPIHandler#getDeepDimension()
	 */
	public static int getDeepDimension()
	{
		return apiHandler == null ? DimensionManager.getNextFreeDimId() : apiHandler.getDeepDimension();
	}

	/**
	 * @see ICaveAPIHandler#isDeepExist()
	 */
	public static boolean isDeepExist()
	{
		return apiHandler != null && apiHandler.isDeepExist();
	}

	/**
	 * @see ICaveAPIHandler#isEntityInCaveworld(Entity)
	 */
	public static boolean isEntityInCaveworld(Entity entity)
	{
		return apiHandler != null && apiHandler.isEntityInCaveworld(entity);
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

	@Deprecated
	public static int getBiomeGenWeight(BiomeGenBase biome)
	{
		return biomeManager == null ? 0 : biomeManager.getCaveBiome(biome).getGenWeight();
	}

	@Deprecated
	public static BlockEntry getBiomeTerrainBlock(BiomeGenBase biome)
	{
		return biomeManager == null ? new BlockEntry(Blocks.stone, 0) : biomeManager.getCaveBiome(biome).getTerrainBlock();
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
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addCaveDeepBiome(ICaveBiome biome)
	{
		return biomeDeepManager != null && biomeDeepManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeCaveDeepBiome(BiomeGenBase biome)
	{
		return biomeDeepManager != null && biomeDeepManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveDeepBiomeCount()
	{
		return biomeDeepManager == null ? 0 : biomeDeepManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiome(BiomeGenBase)
	 */
	public static ICaveBiome getCaveDeepBiome(BiomeGenBase biome)
	{
		return biomeDeepManager == null ? new EmptyCaveBiome(biome) : biomeDeepManager.getCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getRandomCaveBiome(Random)
	 */
	public static ICaveBiome getRandomCaveDeepBiome(Random random)
	{
		return biomeDeepManager == null ? null : biomeDeepManager.getRandomCaveBiome(random);
	}

	/**
	 * @see ICaveBiomeManager#getCaveBiomes()
	 */
	public static Set<ICaveBiome> getCaveDeepBiomes()
	{
		return biomeDeepManager == null ? new HashSet<ICaveBiome>() : biomeDeepManager.getCaveBiomes();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeList()
	 */
	public static List<BiomeGenBase> getDeepBiomeList()
	{
		return biomeDeepManager == null ? new ArrayList<BiomeGenBase>() : biomeDeepManager.getBiomeList();
	}

	/**
	 * @see ICaveBiomeManager#clearCaveBiomes()
	 */
	public static void clearCaveDeepBiomes()
	{
		if (biomeDeepManager != null)
		{
			biomeDeepManager.clearCaveBiomes();
		}
	}

	/**
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addCaveVein(ICaveVein vein)
	{
		return veinManager != null && veinManager.addCaveVein(vein);
	}

	@Deprecated
	public static boolean addCaveVein(String name, ICaveVein vein)
	{
		return addCaveVein(vein);
	}

	@Deprecated
	public static boolean addCaveVeinWithConfig(String name, ICaveVein vein)
	{
		return addCaveVein(vein);
	}

	@Deprecated
	public static boolean addCaveVeinFromConfig(String name)
	{
		return false;
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeCaveVeins(ICaveVein vein)
	{
		return veinManager == null ? 0 : veinManager.removeCaveVeins(vein);
	}

	@Deprecated
	public static boolean removeCaveVein(String name)
	{
		return false;
	}

	@Deprecated
	public static boolean removeCaveVeinWithConfig(String name)
	{
		return false;
	}

	@Deprecated
	public static boolean removeCaveVeinFromConfig(String name)
	{
		return false;
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

	@Deprecated
	public static ICaveVein getCaveVein(String name)
	{
		return null;
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
	 * @see ICaveVeinManager#addCaveVein(ICaveVein)
	 */
	public static boolean addCaveDeepVein(ICaveVein vein)
	{
		return veinDeepManager != null && veinDeepManager.addCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(ICaveVein)
	 */
	public static int removeCaveDeepVeins(ICaveVein vein)
	{
		return veinDeepManager == null ? 0 : veinDeepManager.removeCaveVeins(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeins(Block, int)
	 */
	public static int removeCaveDeepVeins(Block block, int metadata)
	{
		return veinDeepManager == null ? 0 : veinDeepManager.removeCaveVeins(block, metadata);
	}

	/**
	 * @see ICaveVeinManager#getRandomCaveVein(Random)
	 */
	public static ICaveVein getRandomCaveDeepVein(Random random)
	{
		return veinDeepManager == null ? null : veinDeepManager.getRandomCaveVein(random);
	}

	public static List<ICaveVein> getCaveDeepVeins()
	{
		return veinDeepManager == null ? new ArrayList<ICaveVein>() : veinDeepManager.getCaveVeins();
	}

	/**
	 * @see ICaveVeinManager#clearCaveVeins()
	 */
	public static void clearCaveDeepVeins()
	{
		if (veinDeepManager != null)
		{
			veinDeepManager.clearCaveVeins();
		}
	}

	/**
	 * @see ICaveMiningManager#getMiningPoint(EntityPlayer)
	 */
	public static int getMiningPoint(EntityPlayer player)
	{
		return miningManager == null ? 0 : miningManager.getMiningPoint(player);
	}

	/**
	 * @see ICaveMiningManager#setMiningPoint(EntityPlayer, int)
	 */
	public static void setMiningPoint(EntityPlayer player, int value)
	{
		if (miningManager != null)
		{
			miningManager.setMiningPoint(player, value);
		}
	}

	/**
	 * @see ICaveMiningManager#addMiningPoint(EntityPlayer, int)
	 */
	public static void addMiningPoint(EntityPlayer player, int value)
	{
		if (miningManager != null)
		{
			miningManager.addMiningPoint(player, value);
		}
	}

	/**
	 * @see ICaveMiningManager#getMiningPointAmount(Block, int)
	 */
	public static int getMiningPointAmount(Block block, int metadata)
	{
		return miningManager == null ? 0 : miningManager.getMiningPointAmount(block, metadata);
	}

	/**
	 * @see ICaveMiningManager#setMiningPointAmount(Block, int, int)
	 */
	public static void setMiningPointAmount(Block block, int metadata, int amount)
	{
		if (miningManager != null)
		{
			miningManager.setMiningPointAmount(block, metadata, amount);
		}
	}

	/**
	 * @see ICaveMiningManager#setMiningPointAmount(String, int)
	 */
	public static void setMiningPointAmount(String oredict, int amount)
	{
		if (miningManager != null)
		{
			miningManager.setMiningPointAmount(oredict, amount);
		}
	}

	/**
	 * @see ICaveMiningManager#saveMiningData(EntityPlayer, NBTTagCompound)
	 */
	public static void saveMiningData(EntityPlayer player, NBTTagCompound compound)
	{
		if (miningManager != null)
		{
			miningManager.saveMiningData(player, compound);
		}
	}

	/**
	 * @see ICaveMiningManager#loadMiningData(EntityPlayer, NBTTagCompound)
	 */
	public static void loadMiningData(EntityPlayer player, NBTTagCompound compound)
	{
		if (miningManager != null)
		{
			miningManager.loadMiningData(player, compound);
		}
	}
}