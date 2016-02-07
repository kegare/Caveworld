/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.handler;

import caveworld.block.BlockPervertedLeaves;
import caveworld.block.BlockPervertedLog;
import caveworld.block.BlockPervertedSapling;
import caveworld.item.ItemRope;
import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CaveFuelHandler implements IFuelHandler
{
	@Override
	public int getBurnTime(ItemStack fuel)
	{
		if (fuel.getItem() instanceof ItemRope)
		{
			return 50;
		}

		Block block = Block.getBlockFromItem(fuel.getItem());

		if (block instanceof BlockPervertedLog)
		{
			return 100;
		}
		else if (block instanceof BlockPervertedLeaves || block instanceof BlockPervertedSapling)
		{
			return 35;
		}

		return 0;
	}
}