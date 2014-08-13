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
import com.kegare.caveworld.api.ICaveMiningManager;
import com.kegare.caveworld.network.MiningSyncMessage;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;

public class CaveMiningManager implements ICaveMiningManager
{
	public static final String MINING_TAG = "Caveworld:CaveMining";

	private static final Map<String, NBTTagCompound> miningData = Maps.newHashMap();

	private MiningPlayer getMiningPlayer(EntityPlayer player)
	{
		if (player.getExtendedProperties(MINING_TAG) == null)
		{
			player.registerExtendedProperties(MINING_TAG, new MiningPlayer(player));
		}

		return (MiningPlayer)player.getExtendedProperties(MINING_TAG);
	}

	@Override
	public void setMiningCount(EntityPlayer player, int count)
	{
		getMiningPlayer(player).setMiningCount(count);
	}

	@Override
	public int getMiningCount(EntityPlayer player)
	{
		return getMiningPlayer(player).getMiningCount();
	}

	@Override
	public void addMiningCount(EntityPlayer player, int count)
	{
		getMiningPlayer(player).addMiningCount(count);
	}

	@Override
	public int getNextAmount(EntityPlayer player)
	{
		return getMiningPlayer(player).getNextAmount();
	}

	@Override
	public void setMiningLevel(EntityPlayer player, int level)
	{
		getMiningPlayer(player).setMiningLevel(level);
	}

	@Override
	public int getMiningLevel(EntityPlayer player)
	{
		return getMiningPlayer(player).getMiningLevel();
	}

	@Override
	public void addMiningLevel(EntityPlayer player, int level)
	{
		getMiningPlayer(player).addMiningLevel(level);
	}

	@Override
	public void saveMiningData(EntityPlayer player, NBTTagCompound compound)
	{
		MiningPlayer mining = getMiningPlayer(player);

		if (compound == null)
		{
			NBTTagCompound data = new NBTTagCompound();

			mining.saveNBTData(data);

			miningData.put(player.getUniqueID().toString(), data);
		}
		else
		{
			mining.saveNBTData(compound);
		}
	}

	@Override
	public void loadMiningData(EntityPlayer player, NBTTagCompound compound)
	{
		MiningPlayer mining = getMiningPlayer(player);

		if (compound == null)
		{
			mining.loadNBTData(miningData.remove(player.getUniqueID().toString()));
		}
		else
		{
			mining.loadNBTData(compound);
		}

		mining.syncMiningData();
	}

	public static class MiningPlayer implements IExtendedEntityProperties
	{
		private final EntityPlayer player;

		private int miningCount;
		private int miningLevel;

		public MiningPlayer(EntityPlayer player)
		{
			this.player = player;
		}

		@Override
		public void saveNBTData(NBTTagCompound compound)
		{
			NBTTagCompound data = new NBTTagCompound();

			data.setInteger("MiningCount", miningCount);
			data.setInteger("MiningLevel", miningLevel);

			compound.setTag(MINING_TAG, data);
		}

		@Override
		public void loadNBTData(NBTTagCompound compound)
		{
			if (compound == null || !compound.hasKey(MINING_TAG))
			{
				return;
			}

			NBTTagCompound data = compound.getCompoundTag(MINING_TAG);

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
				Caveworld.network.sendTo(new MiningSyncMessage(player), (EntityPlayerMP)player);
			}
		}
	}
}