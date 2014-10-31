/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class QuickBreakExecutor extends MultiBreakExecutor
{
	public static final Table<World, EntityPlayer, QuickBreakExecutor> executors = HashBasedTable.create();

	private QuickBreakExecutor(World world, EntityPlayer player)
	{
		super(world, player);
	}

	public static QuickBreakExecutor getExecutor(World world, EntityPlayer player)
	{
		QuickBreakExecutor executor = executors.get(world, player);

		if (executor == null)
		{
			executor = new QuickBreakExecutor(world, player);

			executors.put(world, player, executor);
		}

		return executor;
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
		return originPos.getDistance(x, y, z) <= 64.0D && breakPositions.size() < 1000 && super.canBreak(x, y, z);
	}

	@Override
	public QuickBreakExecutor setBreakPositions()
	{
		boolean flag;

		do
		{
			int x = currentPos.x;
			int y = currentPos.y;
			int z = currentPos.z;

			flag = false;

			if (offer(x + 1, y, z))
			{
				setBreakPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y + 1, z))
			{
				setBreakPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y, z + 1))
			{
				setBreakPositions();

				if (!flag) flag = true;
			}

			if (offer(x - 1, y, z))
			{
				setBreakPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y - 1, z))
			{
				setBreakPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y, z - 1))
			{
				setBreakPositions();

				if (!flag) flag = true;
			}
		}
		while (flag);

		return this;
	}
}