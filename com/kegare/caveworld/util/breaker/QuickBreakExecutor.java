/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Maps;
import com.kegare.caveworld.core.Config;

public class QuickBreakExecutor extends MultiBreakExecutor
{
	public static final Map<EntityPlayer, QuickBreakExecutor> executors = Maps.newHashMap();

	private QuickBreakExecutor(EntityPlayer player)
	{
		super(player);
	}

	public static QuickBreakExecutor getExecutor(EntityPlayer player)
	{
		QuickBreakExecutor executor = executors.get(player);

		if (executor == null)
		{
			executor = new QuickBreakExecutor(player);

			executors.put(player, executor);
		}

		return executor;
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
		return originPos.getDistance(x, y, z) <= 64.0D && (Config.quickBreakLimit <= 0 || breakPositions.size() < Config.quickBreakLimit) && super.canBreak(x, y, z);
	}

	@Override
	public QuickBreakExecutor setBreakPositions()
	{
		setChainedPositions();

		return this;
	}

	private void setChainedPositions()
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
				setChainedPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y + 1, z))
			{
				setChainedPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y, z + 1))
			{
				setChainedPositions();

				if (!flag) flag = true;
			}

			if (offer(x - 1, y, z))
			{
				setChainedPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y - 1, z))
			{
				setChainedPositions();

				if (!flag) flag = true;
			}

			if (offer(x, y, z - 1))
			{
				setChainedPositions();

				if (!flag) flag = true;
			}
		}
		while (flag);
	}
}