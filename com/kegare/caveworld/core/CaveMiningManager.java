/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy2.api.MCEconomyAPI;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.kegare.caveworld.api.ICaveMiningManager;
import com.kegare.caveworld.network.MiningSyncMessage;
import com.kegare.caveworld.plugin.mceconomy.MCEconomyPlugin;

public class CaveMiningManager implements ICaveMiningManager
{
	public static final String MINING_TAG = "Caveworld:CaveMining";

	private static final Table<Block, Integer, Integer> pointAmounts = HashBasedTable.create();
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
		return pointAmounts.contains(block, metadata) ? pointAmounts.get(block, metadata) : 0;
	}

	@Override
	public void setMiningPointAmount(Block block, int metadata, int amount)
	{
		pointAmounts.put(block, metadata, amount);
	}

	@Override
	public void setMiningPointAmount(String oredict, int amount)
	{
		ArrayList<ItemStack> ores = OreDictionary.getOres(oredict);

		if (!ores.isEmpty())
		{
			for (ItemStack entry : ores)
			{
				Block block = Block.getBlockFromItem(entry.getItem());
				int meta = entry.getItemDamage();

				if (block != Blocks.air && !pointAmounts.contains(block, meta))
				{
					setMiningPointAmount(block, meta, amount);
				}
			}
		}
	}

	@Override
	public void saveMiningData(EntityPlayer player, NBTTagCompound compound)
	{
		MiningPlayer mining = getMiningPlayer(player);

		if (compound == null)
		{
			NBTTagCompound data = new NBTTagCompound();

			mining.saveNBTData(data);

			miningData.put(player.getGameProfile().getId().toString(), data);
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
			mining.loadNBTData(miningData.remove(player.getGameProfile().getId().toString()));
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

			if (point > 0 && point % 100 == 0)
			{
				player.addExperience(player.xpBarCap() / 2);

				if (MCEconomyPlugin.enabled())
				{
					MCEconomyAPI.addPlayerMP(player, 10 + point / 100 - 1, false);
				}
			}

			if (point >= 1000)
			{
				player.triggerAchievement(CaveAchievementList.theMiner);
			}
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