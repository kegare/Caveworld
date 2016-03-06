/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

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
		switch (itemstack.getItemDamage())
		{
			case 0:
				return "tile.oreAquamarine";
			case 1:
				return "tile.blockAquamarine";
			case 2:
				return "tile.oreRandomite";
			case 3:
				return "tile.oreMagnite";
			case 4:
				return "tile.blockMagnite";
			case 5:
				return "tile.oreHexcite";
			case 6:
				return "tile.blockHexcite";
			case 7:
				return "tile.oreInfitite";
			case 8:
				return "tile.blockInfitite";
		}

		return super.getUnlocalizedName(itemstack);
	}
}