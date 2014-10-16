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
import com.kegare.caveworld.recipe.RecipeMiningPickaxe;

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

			OreDictionary.registerOre("cavenium", cavenium);
			OreDictionary.registerOre("gemCavenium", cavenium);

			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(cavenium, 0, 3, 5, 10));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(cavenium, 0, 3, 5, 10));
		}

		if (Config.pickaxeMining)
		{
			GameRegistry.registerItem(mining_pickaxe, "mining_pickaxe");

			ItemStack itemstack = new ItemStack(mining_pickaxe);
			mining_pickaxe.initializeItemStackNBT(itemstack);
			GameRegistry.registerCustomItemStack("mining_pickaxe", itemstack);

			GameRegistry.addRecipe(new RecipeMiningPickaxe());
		}
	}
}