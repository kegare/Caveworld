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

import com.kegare.caveworld.item.ItemMiningPickaxe;

public class RangedBreakExecutor extends MultiBreakExecutor
{
	public RangedBreakExecutor(EntityPlayer player)
	{
		super(player);
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
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
	public RangedBreakExecutor setBreakPositions()
	{
		switch (BlockPistonBase.determineOrientation(originPos.world, originPos.x, originPos.y, originPos.z, player))
		{
			case 0:
			case 1:
				setBreakPositionsY(originPos.x, originPos.y, originPos.z);
				break;
			case 2:
			case 3:
				setBreakPositionsZ(originPos.x, originPos.y, originPos.z);
				break;
			case 4:
			case 5:
				setBreakPositionsX(originPos.x, originPos.y, originPos.z);
				break;
			default:
				return this;
		}

		return this;
	}

	private void setBreakPositionsX(int x, int y, int z)
	{
		for (int i = -1; i <= 1; ++i)
		{
			for (int j = -1; j <= 1; ++j)
			{
				offer(x, y + i, z + j);
			}
		}
	}

	private void setBreakPositionsY(int x, int y, int z)
	{
		for (int i = -1; i <= 1; ++i)
		{
			for (int j = -1; j <= 1; ++j)
			{
				offer(x + i, y, z + j);
			}
		}
	}

	private void setBreakPositionsZ(int x, int y, int z)
	{
		for (int i = -1; i <= 1; ++i)
		{
			for (int j = -1; j <= 1; ++j)
			{
				offer(x + i, y + j, z);
			}
		}
	}
}