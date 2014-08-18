/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.plugin.ic2;

import ic2.api.item.IC2Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.CaveModPlugin.ModPlugin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

@ModPlugin(modid = IC2Plugin.MODID)
public class IC2Plugin
{
	public static final String MODID = "IC2";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	private void invoke()
	{
		if (Config.veinsAutoRegister)
		{
			ItemStack itemstack;
			BlockEntry block;

			if (CaveworldAPI.getCaveVein("Copper Ore Vein") == null)
			{
				itemstack = IC2Items.getItem("copperOre");
				block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

				CaveworldAPI.addCaveVeinWithConfig("Copper Ore Vein", new CaveVein(block, 16, 20, 0, 255));
			}

			if (CaveworldAPI.getCaveVein("Tin Ore Vein") == null)
			{
				itemstack = IC2Items.getItem("tinOre");
				block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

				CaveworldAPI.addCaveVeinWithConfig("Tin Ore Vein", new CaveVein(block, 14, 24, 0, 255));
			}

			if (CaveworldAPI.getCaveVein("Uranium Ore Vein") == null)
			{
				itemstack = IC2Items.getItem("uraniumOre");
				block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

				CaveworldAPI.addCaveVeinWithConfig("Uranium Ore Vein", new CaveVein(block, 10, 7, 0, 50));
			}

			if (CaveworldAPI.getCaveVein("Lead Ore Vein") == null)
			{
				itemstack = IC2Items.getItem("leadOre");
				block = new BlockEntry(Block.getBlockFromItem(itemstack.getItem()), itemstack.getItemDamage());

				CaveworldAPI.addCaveVeinWithConfig("Lead Ore Vein", new CaveVein(block, 15, 20, 0, 255));
			}
		}
	}
}