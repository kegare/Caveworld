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

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.packet.MiningCountPacket;
import com.kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.Set;

public class CaveMiningManager
{
	private static final Map<String, Integer> MINING_COUNTS = Maps.newHashMap();

	public static void loadMiningData()
	{
		NBTTagCompound data = WorldProviderCaveworld.getDimData();

		if (data.hasKey("Caveworld:MiningCount"))
		{
			for (String str : data.getString("Caveworld:MiningCount").split(";"))
			{
				String[] entry = str.split(":");

				MINING_COUNTS.put(entry[0], NumberUtils.toInt(entry[1]));
			}
		}
	}

	public static void saveMiningData()
	{
		NBTTagCompound data = WorldProviderCaveworld.getDimData();

		if (!MINING_COUNTS.isEmpty())
		{
			Set<String> entries = Sets.newHashSet();

			for (String key : MINING_COUNTS.keySet())
			{
				entries.add(key + ":" + MINING_COUNTS.get(key));
			}

			if (!entries.isEmpty())
			{
				data.setString("Caveworld:MiningCount", Joiner.on(';').skipNulls().join(entries));
			}
		}
	}

	public static void clearMiningData()
	{
		MINING_COUNTS.clear();
	}

	public static void setMiningCount(EntityPlayer player, int count)
	{
		MINING_COUNTS.put(player.getCommandSenderName(), Math.max(count, 0));
	}

	public static void setMiningLevel(EntityPlayer player, int level)
	{
		setMiningCount(player, level * 500);
	}

	public static int addMiningCount(EntityPlayer player, int count)
	{
		String key = player.getCommandSenderName();

		if (MINING_COUNTS.containsKey(key))
		{
			int dest = getMiningCount(player);

			MINING_COUNTS.put(key, Math.max(dest += count, 0));
		}
		else
		{
			setMiningCount(player, Math.max(count, 0));
		}

		return syncMiningCount(player);
	}

	public static int addMiningLevel(EntityPlayer player, int level)
	{
		return addMiningCount(player, level * 500);
	}

	public static int getMiningCount(EntityPlayer player)
	{
		String key = player.getCommandSenderName();

		if (MINING_COUNTS.containsKey(key))
		{
			return Math.max(MINING_COUNTS.get(key), 0);
		}

		return 0;
	}

	public static int getMiningLevel(EntityPlayer player)
	{
		return getMiningCount(player) / 500;
	}

	public static int syncMiningCount(EntityPlayer player)
	{
		int count = getMiningCount(player);

		if (player instanceof EntityPlayerMP)
		{
			Caveworld.packetPipeline.sendPacketToPlayer(new MiningCountPacket(count), (EntityPlayerMP)player);
		}

		return count;
	}
}