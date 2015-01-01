/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.entity.player.EntityPlayer;

public class LumberBreakExecutor extends QuickBreakExecutor
{
	public LumberBreakExecutor(EntityPlayer player)
	{
		super(player);
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
		if (y < originPos.y)
		{
			return false;
		}

		if (super.canBreak(x, y, z))
		{
			return true;
		}

		Block block = originPos.world.getBlock(x, y, z);
		int i = originPos.getCurrentMetadata();

		if (originPos.getCurrentBlock() == block && block instanceof BlockRotatedPillar)
		{
			int meta = originPos.world.getBlockMetadata(x, y, z);

			if (i < 4)
			{
				if (meta == i + 4 || meta == i + 8)
				{
					return true;
				}
			}
			else if (i >= 8)
			{
				if (meta == i - 4 || meta == i - 8)
				{
					return true;
				}
			}
			else if (meta == i + 4 || meta == i - 4)
			{
				return true;
			}
		}

		return false;
	}
}