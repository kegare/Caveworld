/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.kegare.caveworld.core.Config;

import cpw.mods.fml.common.registry.GameRegistry;

public class CaveItems
{
	public static final ItemCavenium cavenium = new ItemCavenium("cavenium");
	public static final ItemMiningPickaxe mining_pickaxe = new ItemMiningPickaxe("pickaxeMining");

	public static void registerItems()
	{
		if (Config.cavenium)
		{
			GameRegistry.registerItem(cavenium, "cavenium");

			ItemStack item = new ItemStack(cavenium, 1, 0);
			OreDictionary.registerOre("cavenium", item);
			OreDictionary.registerOre("gemCavenium", item);

			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 3, 5, 10));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(item, 3, 5, 10));

			item = new ItemStack(cavenium, 1, 1);
			OreDictionary.registerOre("refinedCavenium", item);
			OreDictionary.registerOre("gemRefinedCavenium", item);

			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 1, 3, 3));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(item, 1, 3, 3));
		}

		if (Config.pickaxeMining)
		{
			GameRegistry.registerItem(mining_pickaxe, "mining_pickaxe");

			ItemStack item = new ItemStack(mining_pickaxe);
			mining_pickaxe.initializeItemStackNBT(item);
			GameRegistry.registerCustomItemStack("mining_pickaxe", item);
		}
	}
}