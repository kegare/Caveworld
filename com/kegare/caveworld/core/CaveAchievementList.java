/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.util.ArrayListExtended;

import cpw.mods.fml.common.registry.GameData;

public class CaveAchievementList
{
	private static final ArrayListExtended<Achievement> achievementList = new ArrayListExtended();

	public static final Achievement portal = CaveAchievement.of("portal", 0, -2, Blocks.mossy_cobblestone, null, true).initIndependentStat();
	public static final Achievement caveworld = CaveAchievement.of("caveworld", 0, 0, CaveBlocks.caveworld_portal, null, true).initIndependentStat();
	public static final Achievement cavenium = CaveAchievement.of("cavenium", -1, 3, CaveItems.cavenium, caveworld, Config.cavenium);
	public static final Achievement oreFinder = CaveAchievement.of("oreFinder", -3, 5, CaveItems.ore_compass, cavenium, Config.oreCompass);
	public static final Achievement theMiner = CaveAchievement.of("theMiner", 3, 2, Items.iron_pickaxe, caveworld, true);
	public static final Achievement caveman = CaveAchievement.of("caveman", -4, -1, Blocks.stone, caveworld, true);
	public static final Achievement compCaving = CaveAchievement.of("compCaving", -5, -3, Items.egg, caveman, true);
	public static final Achievement cavenicSkeletonSlayer = CaveAchievement.of("cavenicSkeletonSlayer", 3, -3, new ItemStack(CaveItems.cavenium, 1, 1), caveworld, true);
	public static final Achievement masterCavenicSkeletonSlayer = CaveAchievement.of("masterCavenicSkeletonSlayer", 5, -3, new ItemStack(CaveItems.cavenium, 1, 1), cavenicSkeletonSlayer, true).setSpecial();
	public static final Achievement deepCaves = CaveAchievement.of("deepCaves", 0, 7, new ItemStack(Blocks.stonebrick, 1, 1), caveworld, CaveworldAPI.isDeepExist());
	public static final Achievement underCaves = CaveAchievement.of("underCaves", 2, 8, Blocks.water, caveworld, true);
	public static final Achievement aquaCaves = CaveAchievement.of("aquaCaves", 2, 11, Blocks.water, underCaves, CaveworldAPI.isAquaExist());
	public static final Achievement aquamarine = CaveAchievement.of("aquamarine", 5, 11, new ItemStack(CaveItems.gem, 1, 0), caveworld, Config.gem);

	public static void registerAchievements()
	{
		AchievementPage page = new AchievementPage("Caveworld");
		page.getAchievements().addAll(achievementList);

		AchievementPage.registerAchievementPage(page);
	}

	public static int getAchievementIndex(Achievement achievement)
	{
		for (int i = 0; i < achievementList.size(); ++i)
		{
			Achievement entry = achievementList.get(i);

			if (entry.statId.equals(achievement.statId))
			{
				return i;
			}
		}

		return -1;
	}

	public static Achievement getAchievement(int index)
	{
		return achievementList.get(index, null);
	}

	public static class CaveAchievement extends Achievement
	{
		private CaveAchievement(String name, int column, int row, ItemStack itemstack, Achievement parent)
		{
			super("achievement.caveworld." + name, "caveworld." + name, column, row, itemstack, parent);
		}

		private static CaveAchievement of(String name, int column, int row, Block block, Achievement parent, boolean register)
		{
			return of(name, column, row, new ItemStack(block), parent, register);
		}

		private static CaveAchievement of(String name, int column, int row, Item item, Achievement parent, boolean register)
		{
			return of(name, column, row, new ItemStack(item), parent, register);
		}

		private static CaveAchievement of(String name, int column, int row, ItemStack itemstack, Achievement parent, boolean register)
		{
			if (itemstack.getItem() == null || GameData.getItemRegistry().getNameForObject(itemstack.getItem()) == null)
			{
				itemstack = new ItemStack(Blocks.stone);
			}

			CaveAchievement achievement = new CaveAchievement(name, column, row, itemstack, parent);

			if (register)
			{
				achievement.registerStat();
			}

			return achievement;
		}

		@Override
		public Achievement registerStat()
		{
			achievementList.addIfAbsent(this);

			return super.registerStat();
		}
	}
}