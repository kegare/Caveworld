package com.kegare.caveworld.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
 * NOTE: Do not access to this class fields.
 */
public final class CaveworldAPI
{
	public static ICaveAPIHandler apiHandler;
	public static ICaveBiomeManager biomeManager;
	public static ICaveVeinManager veinManager;
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
	 * @see ICaveAPIHandler#isEntityInCaveworld(Entity)
	 */
	public static boolean isEntityInCaveworld(Entity entity)
	{
		return apiHandler == null ? false : apiHandler.isEntityInCaveworld(entity);
	}

	/**
	 * @see ICaveBiomeManager#addCaveBiome(ICaveBiome)
	 */
	public static boolean addCaveBiome(ICaveBiome biome)
	{
		return biomeManager == null ? false : biomeManager.addCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#removeCaveBiome(BiomeGenBase)
	 */
	public static boolean removeCaveBiome(BiomeGenBase biome)
	{
		return biomeManager == null ? false : biomeManager.removeCaveBiome(biome);
	}

	/**
	 * @see ICaveBiomeManager#getActiveBiomeCount()
	 */
	public static int getActiveBiomeCount()
	{
		return biomeManager == null ? 0 : biomeManager.getActiveBiomeCount();
	}

	/**
	 * @see ICaveBiomeManager#getBiomeGenWeight(BiomeGenBase)
	 */
	public static int getBiomeGenWeight(BiomeGenBase biome)
	{
		return biomeManager == null ? 0 : biomeManager.getBiomeGenWeight(biome);
	}

	/**
	 * @see ICaveBiomeManager#getBiomeTerrainBlock(BiomeGenBase)
	 */
	public static BlockEntry getBiomeTerrainBlock(BiomeGenBase biome)
	{
		return biomeManager == null ? new BlockEntry(Blocks.stone, 0) : biomeManager.getBiomeTerrainBlock(biome);
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
	 * @see ICaveVeinManager#addCaveVein(String, ICaveVein)
	 */
	public static boolean addCaveVein(String name, ICaveVein vein)
	{
		return veinManager == null ? false : veinManager.addCaveVein(name, vein);
	}

	/**
	 * @see ICaveVeinManager#addCaveVeinWithConfig(String, ICaveVein)
	 */
	public static boolean addCaveVeinWithConfig(String name, ICaveVein vein)
	{
		return veinManager == null ? false : veinManager.addCaveVeinWithConfig(name, vein);
	}

	/**
	 * @see ICaveVeinManager#addCaveVeinFromConfig(String)
	 */
	public static boolean addCaveVeinFromConfig(String name)
	{
		return veinManager == null ? false : veinManager.addCaveVeinFromConfig(name);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVein(String)
	 */
	public static boolean removeCaveVein(String name)
	{
		return veinManager == null ? false : veinManager.removeCaveVein(name);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeinWithConfig(String)
	 */
	public static boolean removeCaveVeinWithConfig(String name)
	{
		return veinManager == null ? false : veinManager.removeCaveVeinWithConfig(name);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeinFromConfig(String)
	 */
	public static boolean removeCaveVeinFromConfig(String name)
	{
		return veinManager == null ? false : veinManager.removeCaveVeinFromConfig(name);
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

	/**
	 * @see ICaveVeinManager#getCaveVein(String)
	 */
	public static ICaveVein getCaveVein(String name)
	{
		return veinManager == null ? null : veinManager.getCaveVein(name);
	}

	/**
	 * @see ICaveVeinManager#getCaveVeins()
	 */
	public static Map<String, ICaveVein> getCaveVeins()
	{
		return veinManager == null ? new HashMap<String, ICaveVein>() : veinManager.getCaveVeins();
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