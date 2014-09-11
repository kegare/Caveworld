/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.ic2;

import ic2.api.item.IC2Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Config;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public class IC2Plugin
{
	public static final String MODID = "IC2";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		if (Config.veinsAutoRegister)
		{
			ItemStack itemstack = IC2Items.getItem("copperOre");
			BlockEntry block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			CaveworldAPI.addCaveVein(new CaveVein(block, 16, 20, 0, 255));

			itemstack = IC2Items.getItem("tinOre");
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			CaveworldAPI.addCaveVein(new CaveVein(block, 14, 24, 0, 255));

			itemstack = IC2Items.getItem("uraniumOre");
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			CaveworldAPI.addCaveVein(new CaveVein(block, 10, 7, 0, 50));

			itemstack = IC2Items.getItem("leadOre");
			block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

			CaveworldAPI.addCaveVein(new CaveVein(block, 15, 20, 0, 255));
		}
	}
}