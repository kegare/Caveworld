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

import com.kegare.caveworld.block.BlockRope;

public class ItemRope extends ItemBlock
{
	public ItemRope(Block block)
	{
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (side == 1)
		{
			return false;
		}

		if (world.getBlock(x, y, z) == field_150939_a)
		{
			--y;
		}
		else switch (side)
		{
			case 0:
				--y;
				break;
			case 2:
				--z;
				break;
			case 3:
				++z;
				break;
			case 4:
				--x;
				break;
			case 5:
				++x;
				break;
		}

		if (itemstack.stackSize > 0 && player.canPlayerEdit(x, y, z, side, itemstack) &&
			world.isAirBlock(x, y, z) && (world.getBlock(x, y + 1, z).getMaterial().isSolid() || world.getBlock(x, y + 1, z) == field_150939_a) &&
			world.canPlaceEntityOnSide(field_150939_a, x, y, z, false, side, player, itemstack) &&
			placeBlockAt(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ, 1))
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

			return true;
		}

		return false;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (world.setBlock(x, y, z, field_150939_a, metadata, 3))
		{
			if (!world.isRemote && field_150939_a instanceof BlockRope)
			{
				((BlockRope)field_150939_a).setUnderRopes(world, x, y, z);
			}

			return true;
		}

		return false;
	}
}
