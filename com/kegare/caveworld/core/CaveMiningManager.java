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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.google.common.collect.Maps;
import com.kegare.caveworld.api.ICaveMiningManager;
import com.kegare.caveworld.network.MiningSyncMessage;

public class CaveMiningManager implements ICaveMiningManager
{
	public static final String MINING_TAG = "Caveworld:CaveMining";

	private static final Map<String, Integer> pointAmounts = Maps.newHashMap();
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
	public int getMiningPoint(EntityPlayer player)
	{
		return getMiningPlayer(player).getMiningPoint();
	}

	@Override
	public void setMiningPoint(EntityPlayer player, int value)
	{
		getMiningPlayer(player).setMiningPoint(value);
	}

	@Override
	public void addMiningPoint(EntityPlayer player, int value)
	{
		getMiningPlayer(player).addMiningPoint(value);
	}

	@Override
	public int getMiningPointAmount(Block block, int metadata)
	{
		String key = Block.blockRegistry.getNameForObject(block) + "," + metadata;

		return pointAmounts.containsKey(key) ? pointAmounts.get(key) : 0;
	}

	@Override
	public void setMiningPointAmount(Block block, int metadata, int amount)
	{
		pointAmounts.put(Block.blockRegistry.getNameForObject(block) + "," + metadata, amount);
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

		private int point;

		public MiningPlayer(EntityPlayer player)
		{
			this.player = player;
		}

		@Override
		public void saveNBTData(NBTTagCompound compound)
		{
			NBTTagCompound data = new NBTTagCompound();

			data.setInteger("MiningPoint", point);

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

			point = data.getInteger("MiningPoint");

			if (data.hasKey("MiningCount") && point <= 0)
			{
				point = data.getInteger("MiningCount");
			}
		}

		@Override
		public void init(Entity entity, World world) {}

		public int getMiningPoint()
		{
			return point;
		}

		public void setMiningPoint(int value)
		{
			point = Math.max(value, 0);

			syncMiningData();
		}

		public void addMiningPoint(int value)
		{
			setMiningPoint(point + value);
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