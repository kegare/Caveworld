package caveworld.util.farmer;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;

public class RangedFarmExecutor extends MultiFarmExecutor
{
	public RangedFarmExecutor(EntityPlayer player)
	{
		super(player);
	}

	@Override
	public RangedFarmExecutor setFarmPositions()
	{
		switch (BlockPistonBase.determineOrientation(originPos.world, originPos.x, originPos.y, originPos.z, player))
		{
			case 0:
			case 1:
				setBreakPositionsY(originPos.x, originPos.y, originPos.z);
				break;
			default:
				return this;
		}

		return this;
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
}