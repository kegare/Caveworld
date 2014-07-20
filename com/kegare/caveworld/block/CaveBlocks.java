/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.block;

import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.kegare.caveworld.block.BlockPortalCaveworld.DispencePortal;
import com.kegare.caveworld.block.BlockRope.DispenceRope;
import com.kegare.caveworld.config.Config;
import com.kegare.caveworld.item.ItemPortalCaveworld;
import com.kegare.caveworld.item.ItemRope;

import cpw.mods.fml.common.registry.GameRegistry;

public class CaveBlocks
{
	public static BlockPortalCaveworld caveworld_portal;
	public static BlockRope rope;

	public static void initialize()
	{
		caveworld_portal = new BlockPortalCaveworld("portalCaveworld");

		if (Config.rope)
		{
			rope = new BlockRope("rope");
		}
	}

	public static void register()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemPortalCaveworld.class, "caveworld_portal");

		OreDictionary.registerOre("portalCaveworld", caveworld_portal);

		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveworld_portal), new DispencePortal());

		if (rope != null)
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
	}
}