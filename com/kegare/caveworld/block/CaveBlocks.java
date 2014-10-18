/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.BlockPortalCaveworld.DispencePortal;
import com.kegare.caveworld.block.BlockRope.DispenceRope;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.item.ItemCaveniumOre;
import com.kegare.caveworld.item.ItemPortalCaveworld;
import com.kegare.caveworld.item.ItemRope;

import cpw.mods.fml.common.registry.GameRegistry;

public class CaveBlocks
{
	public static final BlockPortalCaveworld caveworld_portal = new BlockPortalCaveworld("portalCaveworld");
	public static final BlockRope rope = new BlockRope("rope");
	public static final BlockCaveniumOre cavenium_ore = new BlockCaveniumOre("oreCavenium");

	public static void registerBlocks()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemPortalCaveworld.class, "caveworld_portal");

		OreDictionary.registerOre("portalCaveworld", caveworld_portal);

		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveworld_portal), new DispencePortal());

		if (Config.rope)
		{
			GameRegistry.registerBlock(rope, ItemRope.class, "rope");

			Blocks.fire.setFireInfo(rope, 15, 100);

			OreDictionary.registerOre("rope", new ItemStack(rope));

			Item item = Item.getItemFromBlock(rope);
			ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(item, 0, 2, 5, 10));
			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 0, 3, 6, 10));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(item, 0, 3, 6, 10));

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(rope), new DispenceRope());
		}

		if (Config.oreCavenium)
		{
			GameRegistry.registerBlock(cavenium_ore, ItemCaveniumOre.class, "cavenium_ore");

			ItemStack item = new ItemStack(cavenium_ore, 1, 0);
			OreDictionary.registerOre("oreCavenium", item);
			OreDictionary.registerOre("caveniumOre", item);
			item = new ItemStack(cavenium_ore, 1, 1);
			OreDictionary.registerOre("oreRefinedCavenium", item);
			OreDictionary.registerOre("refinedCaveniumOre", item);

			item = new ItemStack(cavenium_ore, 1, 2);
			OreDictionary.registerOre("blockCavenium", item);
			item = new ItemStack(cavenium_ore, 1, 3);
			OreDictionary.registerOre("blockRefinedCavenium", item);

			CaveworldAPI.setMiningPointAmount(cavenium_ore, 0, 2);
			CaveworldAPI.setMiningPointAmount(cavenium_ore, 1, 3);
		}
	}
}