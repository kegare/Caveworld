package com.kegare.caveworld.api;

public interface ICaveVein
{
	public BlockEntry getBlock();

	public int getGenBlockCount();

	public int getGenWeight();

	public int getGenMinHeight();

	public int getGenMaxHeight();

	public BlockEntry getGenTargetBlock();

	public int[] getGenBiomes();
}