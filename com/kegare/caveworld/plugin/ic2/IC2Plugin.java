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
		ItemStack itemstack = IC2Items.getItem("copperOre");
		BlockEntry block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

		if (Config.veinsAutoRegister)
		{
			CaveworldAPI.addCaveVein(new CaveVein(block, 16, 20, 100, 0, 255));
		}

		CaveworldAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 1);

		itemstack = IC2Items.getItem("tinOre");
		block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

		if (Config.veinsAutoRegister)
		{
			CaveworldAPI.addCaveVein(new CaveVein(block, 14, 24, 100, 0, 255));
		}

		CaveworldAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 1);

		itemstack = IC2Items.getItem("uraniumOre");
		block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

		if (Config.veinsAutoRegister)
		{
			CaveworldAPI.addCaveVein(new CaveVein(block, 10, 7, 100, 0, 50));
		}

		CaveworldAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 2);

		itemstack = IC2Items.getItem("leadOre");
		block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

		if (Config.veinsAutoRegister)
		{
			CaveworldAPI.addCaveVein(new CaveVein(block, 15, 20, 100, 0, 255));
		}

		CaveworldAPI.setMiningPointAmount(block.getBlock(), block.getMetadata(), 1);
	}
}