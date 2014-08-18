package com.kegare.caveworld.api;

import net.minecraft.init.Blocks;

public class EmptyCaveVein implements ICaveVein
{
	@Override
	public BlockEntry getBlock()
	{
		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public int getGenBlockCount()
	{
		return 0;
	}

	@Override
	public int getGenWeight()
	{
		return 0;
	}

	@Override
	public int getGenMinHeight()
	{
		return 0;
	}

	@Override
	public int getGenMaxHeight()
	{
		return 0;
	}

	@Override
	public BlockEntry getGenTargetBlock()
	{
		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public int[] getGenBiomes()
	{
		return new int[0];
	}
}