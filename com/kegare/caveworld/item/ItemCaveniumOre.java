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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemCaveniumOre extends ItemBlockWithMetadata
{
	public ItemCaveniumOre(Block block)
	{
		super(block, block);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		switch (itemstack.getItemDamage())
		{
			case 1:
				return getUnlocalizedName() + ".refined";
			case 2:
				return "tile.blockCavenium";
			case 3:
				return "tile.blockCavenium.refined";
			default:
				return getUnlocalizedName();
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack)
	{
		int damage = itemstack.getItemDamage();

		return damage == 1 || damage == 3 ? EnumRarity.rare : super.getRarity(itemstack);
	}
}