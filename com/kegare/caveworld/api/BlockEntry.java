package com.kegare.caveworld.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class BlockEntry
{
	private Block block;
	private int metadata;

	public BlockEntry(Block block, int metadata)
	{
		this.block = block;
		this.metadata = metadata;
	}

	public BlockEntry(String name, int metadata)
	{
		this(Block.getBlockFromName(name), metadata);
	}

	public Block getBlock()
	{
		return block == null ? Blocks.stone : block;
	}

	public int getMetadata()
	{
		return MathHelper.clamp_int(metadata, 0, 15);
	}
}