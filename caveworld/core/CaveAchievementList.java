/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import caveworld.block.CaveBlocks;
import caveworld.item.CaveItems;
import caveworld.util.ArrayListExtended;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class CaveAchievementList
{
	private static final ArrayListExtended<Achievement> achievementList = new ArrayListExtended();

	public static final Achievement portal = CaveAchievement.of("portal", 0, -2, Blocks.mossy_cobblestone, null, true).initIndependentStat();
	public static final Achievement caveworld = CaveAchievement.of("caveworld", 0, 0, CaveBlocks.caveworld_portal, null, true).initIndependentStat();
	public static final Achievement cavern = CaveAchievement.of("cavern", 2, 0, CaveBlocks.cavern_portal, null, true).initIndependentStat();
	public static final Achievement cavenium = CaveAchievement.of("cavenium", -1, 3, CaveItems.cavenium, caveworld, true);
	public static final Achievement oreFinder = CaveAchievement.of("oreFinder", -3, 5, CaveItems.ore_compass, cavenium, true);
	public static final Achievement theMiner = CaveAchievement.of("theMiner", 3, 2, Items.iron_pickaxe, caveworld, true);
	public static final Achievement theRoper = CaveAchievement.of("theRoper", -2, 1, CaveBlocks.rope, caveworld, true);
	public static final Achievement caveman = CaveAchievement.of("caveman", -4, -1, CaveBlocks.cavenium_ore, caveworld, true);
	public static final Achievement cavenicSkeletonSlayer = CaveAchievement.of("cavenicSkeletonSlayer", 3, -3, new ItemStack(CaveItems.cavenium, 1, 0), caveworld, true);
	public static final Achievement masterCavenicSkeletonSlayer = CaveAchievement.of("masterCavenicSkeletonSlayer", 5, -3, new ItemStack(CaveItems.cavenium, 1, 1), cavenicSkeletonSlayer, true).setSpecial();
	public static final Achievement aquamarine = CaveAchievement.of("aquamarine", 2, 6, new ItemStack(CaveItems.gem, 1, 0), caveworld, true);
	public static final Achievement randomite = CaveAchievement.of("randomite", 2, 4, new ItemStack(CaveBlocks.gem_ore, 1, 2), caveworld, true);

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