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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemRope extends ItemBlock
{
	public ItemRope(Block block)
	{
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (side == 0 && world.getBlock(x, y, z).getMaterial().isSolid() || world.getBlock(x, y, z) == field_150939_a)
		{
			--y;

			if (itemstack.stackSize == 0)
			{
				return false;
			}
			else if (!player.canPlayerEdit(x, y, z, side, itemstack))
			{
				return false;
			}
			else if (!world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, player, itemstack))
			{
				return false;
			}
			else if (world.isAirBlock(x, y, z) && placeBlockAt(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ, 1))
			{
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, field_150939_a.stepSound.func_150496_b(), (field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, field_150939_a.stepSound.getPitch() * 0.8F);

				--itemstack.stackSize;

				if (player.isSneaking() && !player.isOnLadder())
				{
					for (int i = 1; itemstack.stackSize > 0 && i < itemstack.stackSize + 1; ++i)
					{
						int next = y - 5 * i;

						if (world.getBlock(x, next, z) == field_150939_a && world.isAirBlock(x, --next, z) && next > 0)
						{
							if (placeBlockAt(itemstack, player, world, x, next, z, side, hitX, hitY, hitZ, 1))
							{
								--itemstack.stackSize;
							}
							else break;
						}
					}
				}
			}
		}

		return false;
	}
}
