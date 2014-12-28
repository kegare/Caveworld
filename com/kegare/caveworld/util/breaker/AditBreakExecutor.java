/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import com.kegare.caveworld.item.ItemMiningPickaxe;

public class AditBreakExecutor extends MultiBreakExecutor
{
	public AditBreakExecutor(EntityPlayer player)
	{
		super(player);
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
		if (y < MathHelper.floor_double(player.posY))
		{
			return false;
		}

		if (super.canBreak(x, y, z))
		{
			return true;
		}

		ItemStack current = player.getCurrentEquippedItem();

		if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe)
		{
			ItemMiningPickaxe pickaxe = (ItemMiningPickaxe)current.getItem();

			if (pickaxe.canBreak(current, originPos.world.getBlock(x, y, z), originPos.world.getBlockMetadata(x, y, z)))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public AditBreakExecutor setBreakPositions()
	{
		int face = BlockPistonBase.determineOrientation(originPos.world, originPos.x, originPos.y, originPos.z, player);

		if (face == 0 || face == 1)
		{
			return this;
		}

		offer(originPos.x, originPos.y, originPos.z);

		if (originPos.y == MathHelper.floor_double(player.posY))
		{
			offer(originPos.x, originPos.y + 1, originPos.z);
		}
		else
		{
			offer(originPos.x, originPos.y - 1, originPos.z);
		}

		return this;
	}
}