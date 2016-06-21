package caveworld.util.breaker;

import caveworld.config.Config;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.player.EntityPlayer;

public class QuickBreakExecutor extends MultiBreakExecutor
{
	public QuickBreakExecutor(EntityPlayer player)
	{
		super(player);
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
		if (Config.quickBreakLimit > 0 && breakPositions.size() >= Config.quickBreakLimit)
		{
			return false;
		}

		if (originPos.getCurrentBlock() instanceof BlockRedstoneOre && originPos.world.getBlock(x, y, z) instanceof BlockRedstoneOre)
		{
			return true;
		}

		return super.canBreak(x, y, z);
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