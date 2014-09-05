/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import com.google.common.collect.Lists;
import com.kegare.caveworld.block.CaveBlocks;

public class CaveAchievementList
{
	private static final List<Achievement> achievementList = Lists.newArrayList();

	public static final Achievement portal = new CaveAchievement("portal", 0, 0, Blocks.mossy_cobblestone, null).initIndependentStat().registerStat();
	public static final Achievement caveworld = new CaveAchievement("caveworld", 2, 1, CaveBlocks.caveworld_portal, null).registerStat();

	public static void registerAchievements()
	{
		AchievementPage page = new AchievementPage("Caveworld");
		page.getAchievements().addAll(achievementList);

		AchievementPage.registerAchievementPage(page);
	}

	private static class CaveAchievement extends Achievement
	{
		public CaveAchievement(String name, int column, int row, Block block, Achievement parent)
		{
			this(name, column, row, new ItemStack(block), parent);
		}

		public CaveAchievement(String name, int column, int row, Item item, Achievement parent)
		{
			this(name, column, row, new ItemStack(item), parent);
		}

		public CaveAchievement(String name, int column, int row, ItemStack itemstack, Achievement parent)
		{
			super("achievement.caveworld." + name, "caveworld." + name, column, row, itemstack, parent);
		}

		@Override
		public Achievement registerStat()
		{
			achievementList.add(this);

			return super.registerStat();
		}
	}
}