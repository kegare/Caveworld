/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemGemOre extends ItemBlockWithMetadata
{
	public ItemGemOre(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		String name = field_150939_a.getUnlocalizedName();

		switch (itemstack.getItemDamage())
		{
			case 0:
				return name + ".aquamarine";
			case 1:
				return name + ".aquamarine.block";
			default:
				return name;
		}
	}
}