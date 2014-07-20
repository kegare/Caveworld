/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.util;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;

public class BlockEntry
{
	private Block block;
	private int blockMetadata;
	private final Block blockDefault;

	public BlockEntry(Block block, int metadata)
	{
		this.block = block;
		this.blockMetadata = metadata;
		this.blockDefault = null;
	}

	public BlockEntry(String name, int metadata, Block block)
	{
		this.block = Block.getBlockFromName(name);
		this.blockMetadata = metadata;
		this.blockDefault = block;
	}

	@Override
	public boolean equals(Object obj)
	{
		BlockEntry entry = null;

		if (obj instanceof BlockEntry)
		{
			entry = (BlockEntry)obj;
		}

		return entry != null && block == entry.block && blockMetadata == entry.blockMetadata;
	}

	public Block getBlock()
	{
		return block == null ? blockDefault : block;
	}

	public int getMetadata()
	{
		return MathHelper.clamp_int(blockMetadata, 0, 15);
	}

	public Block getDefault()
	{
		return blockDefault;
	}
}