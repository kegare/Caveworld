package com.kegare.caveworld.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.BiomeGenBase;

public class CaveworldAPI
{
	private static ICaveBiomeManager biomeManager;
	private static ICaveVeinManager veinManager;
	private static ICaveMiningManager miningManager;

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
	 * @see ICaveBiomeManager#getRandomBiome(Random)
	 */
	public static BiomeGenBase getRandomBiome(Random random)
	{
		return biomeManager == null ? BiomeGenBase.plains : biomeManager.getRandomBiome(random);
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
		return veinManager == null ? false : veinManager.addCaveVein(vein);
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
	 * @see ICaveVeinManager#removeCaveVein(ICaveVein)
	 */
	public static boolean removeCaveVein(ICaveVein vein)
	{
		return veinManager == null ? false : veinManager.removeCaveVein(vein);
	}

	/**
	 * @see ICaveVeinManager#removeCaveVeinWithConfig(String, ICaveVein)
	 */
	public static boolean removeCaveVeinWithConfig(String name, ICaveVein vein)
	{
		return veinManager == null ? false : veinManager.removeCaveVeinWithConfig(name, vein);
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
	 * @see ICaveVeinManager#getRandomVein(Random)
	 */
	public static ICaveVein getRandomVein(Random random)
	{
		return veinManager == null ? new ICaveVein()
		{
			@Override
			public int getGenWeight()
			{
				return 0;
			}

			@Override
			public BlockEntry getGenTargetBlock()
			{
				return new BlockEntry(Blocks.stone, 0);
			}

			@Override
			public int getGenMinHeight()
			{
				return 0;
			}

			@Override
			public int getGenMaxHeight()
			{
				return 0;
			}

			@Override
			public int getGenBlockCount()
			{
				return 0;
			}

			@Override
			public int[] getGenBiomes()
			{
				return new int[] {};
			}

			@Override
			public BlockEntry getBlock()
			{
				return new BlockEntry(Blocks.stone, 0);
			}
		} : veinManager.getRandomVein(random);
	}

	/**
	 * @see ICaveVeinManager#getCaveVeins()
	 */
	public static Set<ICaveVein> getCaveVeins()
	{
		return veinManager == null ? new HashSet<ICaveVein>() : veinManager.getCaveVeins();
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
	 * @see ICaveMiningManager#setMiningCount(EntityPlayer, int)
	 */
	public static void setMiningCount(EntityPlayer player, int count)
	{
		if (miningManager != null)
		{
			miningManager.setMiningCount(player, count);
		}
	}

	/**
	 * @see ICaveMiningManager#getMiningCount(EntityPlayer)
	 */
	public static int getMiningCount(EntityPlayer player)
	{
		return miningManager == null ? 0 : miningManager.getMiningCount(player);
	}

	/**
	 * @see ICaveMiningManager#addMiningCount(EntityPlayer, int)
	 */
	public static void addMiningCount(EntityPlayer player, int count)
	{
		if (miningManager != null)
		{
			miningManager.addMiningCount(player, count);
		}
	}

	/**
	 * @see ICaveMiningManager#getNextAmount(EntityPlayer)
	 */
	public static int getNextAmount(EntityPlayer player)
	{
		return miningManager == null ? 0 : miningManager.getNextAmount(player);
	}

	/**
	 * @see ICaveMiningManager#setMiningLevel(EntityPlayer, int)
	 */
	public static void setMiningLevel(EntityPlayer player, int level)
	{
		if (miningManager != null)
		{
			miningManager.setMiningLevel(player, level);
		}
	}

	/**
	 * @see ICaveMiningManager#getMiningLevel(EntityPlayer)
	 */
	public static int getMiningLevel(EntityPlayer player)
	{
		return miningManager == null ? 0 : miningManager.getMiningLevel(player);
	}

	/**
	 * @see ICaveMiningManager#addMiningLevel(EntityPlayer, int)
	 */
	public static void addMiningLevel(EntityPlayer player, int level)
	{
		if (miningManager != null)
		{
			miningManager.addMiningLevel(player, level);
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