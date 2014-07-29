/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.core;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import shift.mceconomy2.api.MCEconomyAPI;

import com.google.common.collect.Maps;
import com.kegare.caveworld.network.MiningSyncMessage;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;

public class CaveMiningPlayer implements IExtendedEntityProperties
{
	public static final String CAVE_MINING = "Caveworld:CaveMining";

	private static final Map<String, NBTTagCompound> extendedData = Maps.newHashMap();

	private final EntityPlayer player;

	private int miningCount;
	private int miningLevel;

	public CaveMiningPlayer(EntityPlayer player)
	{
		this.player = player;
	}

	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(CAVE_MINING, new CaveMiningPlayer(player));
	}

	public static CaveMiningPlayer get(EntityPlayer player)
	{
		if (player == null)
		{
			return null;
		}

		if (player.getExtendedProperties(CAVE_MINING) == null)
		{
			register(player);
		}

		return (CaveMiningPlayer)player.getExtendedProperties(CAVE_MINING);
	}

	public static void saveMiningData(EntityPlayer player)
	{
		CaveMiningPlayer data = get(player);
		NBTTagCompound compound = new NBTTagCompound();

		data.saveNBTData(compound);

		extendedData.put(player.getUniqueID().toString(), compound);
	}

	public static void loadMiningData(EntityPlayer player)
	{
		CaveMiningPlayer data = get(player);
		NBTTagCompound compound = extendedData.remove(player.getUniqueID().toString());

		data.loadNBTData(compound);
		data.syncMiningData();
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound data = new NBTTagCompound();

		data.setInteger("MiningCount", miningCount);
		data.setInteger("MiningLevel", miningLevel);

		compound.setTag(CAVE_MINING, data);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		if (compound == null || !compound.hasKey(CAVE_MINING))
		{
			return;
		}

		NBTTagCompound data = compound.getCompoundTag(CAVE_MINING);

		miningCount = data.getInteger("MiningCount");
		miningLevel = data.getInteger("MiningLevel");
	}

	@Override
	public void init(Entity entity, World world) {}

	public void setMiningCount(int count)
	{
		miningCount = Math.max(count, 0);

		syncMiningData();
	}

	public int getMiningCount()
	{
		return miningCount;
	}

	public void addMiningCount(int count)
	{
		setMiningCount(miningCount + count);

		if (miningCount == getNextAmount())
		{
			addMiningLevel(1);
		}

		if (miningCount > 0 && miningCount % 100 == 0)
		{
			player.addExperience(player.xpBarCap() / 2);

			if (MCEconomyPlugin.enabled())
			{
				MCEconomyAPI.addPlayerMP(player, 10 + miningCount / 100 - 1, false);
			}
		}
	}

	public int getNextAmount()
	{
		return 100 * miningLevel + 100 * (miningLevel + 1);
	}

	public void setMiningLevel(int level)
	{
		miningLevel = Math.max(level, 0);

		syncMiningData();
	}

	public int getMiningLevel()
	{
		return miningLevel;
	}

	public void addMiningLevel(int level)
	{
		setMiningLevel(miningLevel + level);

		player.addStat(CaveAchievementList.miner, level);
	}

	public void syncMiningData()
	{
		if (player instanceof EntityPlayerMP)
		{
			Caveworld.network.sendTo(new MiningSyncMessage(miningCount, miningLevel), (EntityPlayerMP)player);
		}
	}
}