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

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import com.google.common.collect.Sets;
import com.kegare.caveworld.block.CaveBlocks;

public class CaveAchievementList
{
	private static final Set<Achievement> CAVE_ACHIEVEMENTS = Sets.newHashSet();

	public static final Achievement caveworld = new CaveAchievement("caveworld", 0, 0, CaveBlocks.caveworld_portal, null).initIndependentStat().registerStat();
	public static final Achievement miner = new CaveAchievement("miner", 2, 1, Items.iron_pickaxe, caveworld).registerStat();

	public static void register()
	{
		AchievementPage.registerAchievementPage(new AchievementPage("Caveworld", CAVE_ACHIEVEMENTS.toArray(new Achievement[CAVE_ACHIEVEMENTS.size()])));
	}

	private static class CaveAchievement extends Achievement
	{
		public CaveAchievement(String name, int column, int row, Block block, Achievement achievement)
		{
			this(name, column, row, new ItemStack(block), achievement);
		}

		public CaveAchievement(String name, int column, int row, Item item, Achievement achievement)
		{
			this(name, column, row, new ItemStack(item), achievement);
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