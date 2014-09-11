package com.kegare.caveworld.api;

import net.minecraft.init.Blocks;

public class EmptyCaveVein implements ICaveVein
{
	@Override
	public BlockEntry setBlock(BlockEntry entry)
	{
		return getBlock();
	}

	@Override
	public BlockEntry getBlock()
	{
		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public int setGenBlockCount(int count)
	{
		return getGenBlockCount();
	}

	@Override
	public int getGenBlockCount()
	{
		return 0;
	}

	@Override
	public int setGenWeight(int weight)
	{
		return getGenWeight();
	}

	@Override
	public int getGenWeight()
	{
		return 0;
	}

	@Override
	public int setGenMinHeight(int height)
	{
		return getGenMinHeight();
	}

	@Override
	public int getGenMinHeight()
	{
		return 0;
	}

	@Override
	public int setGenMaxHeight(int height)
	{
		return getGenMaxHeight();
	}

	@Override
	public int getGenMaxHeight()
	{
		return 0;
	}

	@Override
	public BlockEntry setGenTargetBlock(BlockEntry entry)
	{
		return getGenTargetBlock();
	}

	@Override
	public BlockEntry getGenTargetBlock()
	{
		return new BlockEntry(Blocks.stone, 0);
	}

	@Override
	public int[] setGenBiomes(int[] biomes)
	{
		return getGenBiomes();
	}

	@Override
	public int[] getGenBiomes()
	{
		return new int[0];
	}
}