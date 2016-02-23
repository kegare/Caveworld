/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.util.farmer;

import caveworld.core.Config;
import net.minecraft.entity.player.EntityPlayer;

public class QuickFarmExecutor extends MultiFarmExecutor
{
	public QuickFarmExecutor(EntityPlayer player)
	{
		super(player);
	}

	@Override
	public boolean canFarm(int x, int y, int z)
	{
		if (Config.quickBreakLimit > 0 && farmPositions.size() >= Config.quickBreakLimit)
		{
			return false;
		}

		return super.canFarm(x, y, z);
	}

	@Override
	public QuickFarmExecutor setFarmPositions()
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

			if (offer(x, y, z - 1))
			{
				setChainedPositions();

				if (!flag) flag = true;
			}
		}
		while (flag);
	}
}