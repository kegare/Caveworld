package com.kegare.caveworld.api;

public interface ICaveVein
{
	public BlockEntry setBlock(BlockEntry entry);

	public BlockEntry getBlock();

	public int setGenBlockCount(int count);

	public int getGenBlockCount();

	public int setGenWeight(int weight);

	public int getGenWeight();

	public int setGenMinHeight(int height);

	public int getGenMinHeight();

	public int setGenMaxHeight(int height);

	public int getGenMaxHeight();

	public BlockEntry setGenTargetBlock(BlockEntry entry);

	public BlockEntry getGenTargetBlock();

	public int[] setGenBiomes(int[] biomes);

	public int[] getGenBiomes();
}