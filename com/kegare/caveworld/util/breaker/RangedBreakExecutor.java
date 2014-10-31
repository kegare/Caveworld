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
import net.minecraft.world.World;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kegare.caveworld.item.ItemMiningPickaxe;

public class RangedBreakExecutor extends MultiBreakExecutor
{
	public static final Table<World, EntityPlayer, RangedBreakExecutor> executors = HashBasedTable.create();

	private RangedBreakExecutor(World world, EntityPlayer player)
	{
		super(world, player);
	}

	public static RangedBreakExecutor getExecutor(World world, EntityPlayer player)
	{
		RangedBreakExecutor executor = executors.get(world, player);

		if (executor == null)
		{
			executor = new RangedBreakExecutor(world, player);

			executors.put(world, player, executor);
		}

		return executor;
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

			if (pickaxe.canBreak(current, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public RangedBreakExecutor setBreakPositions()
	{
		int face = BlockPistonBase.determineOrientation(world, originPos.x, originPos.y, originPos.z, player);

		switch (face)
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