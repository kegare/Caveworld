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

import com.google.common.collect.Sets;
import com.kegare.caveworld.block.CaveBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import java.util.Set;

public class CaveAchievementList
{
	private static final Set<Achievement> CAVE_ACHIEVEMENTS = Sets.newHashSet();

	public static final Achievement caveworld = new CaveAchievement("caveworld", 0, 0, CaveBlocks.caveworld_portal, null).initIndependentStat().registerStat();
	public static final Achievement miner = new CaveAchievement("miner", 2, 1, Items.iron_pickaxe, caveworld).registerStat();

	static Achievement[] toArray()
	{
		Object[] array = CAVE_ACHIEVEMENTS.toArray();
		Achievement[] achievements = new Achievement[array.length];

		for (int i = 0; i < array.length; ++i)
		{
			Object obj = array[i];

			if (obj != null && obj instanceof Achievement)
			{
				achievements[i] = (Achievement)obj;
			}
		}

		return achievements;
	}

	static class CaveAchievement extends Achievement
	{
		public CaveAchievement(String name, int column, int row, Item item, Achievement achievement)
		{
			super("achievement.caveworld." + name, "caveworld." + name, column, row, item, achievement);
		}

		public CaveAchievement(String name, int column, int row, Block block, Achievement achievement)
		{
			super("achievement.caveworld." + name, "caveworld." + name, column, row, block, achievement);
		}

		public CaveAchievement(String name, int column, int row, ItemStack itemstack, Achievement achievement)
		{
			super("achievement.caveworld." + name, "caveworld." + name, column, row, itemstack, achievement);
		}

		@Override
		public Achievement registerStat()
		{
			super.registerStat();
			CAVE_ACHIEVEMENTS.add(this);

			return this;
		}
	}
}