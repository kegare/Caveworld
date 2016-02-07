/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import caveworld.block.BlockPervertedLog;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemPervertedLeaves extends ItemBlockWithMetadata
{
	public ItemPervertedLeaves(Block block)
	{
		super(block, block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		int i = itemstack.getItemDamage();

		if (i < 0 || i >= BlockPervertedLog.types.length)
		{
			i = 0;
		}

		String name = ("" + StatCollector.translateToLocal(Blocks.leaves.getUnlocalizedName() + "." + BlockPervertedLog.types[i] + ".name")).trim();

		return ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".type.name")).trim() + " " + name;
	}
}