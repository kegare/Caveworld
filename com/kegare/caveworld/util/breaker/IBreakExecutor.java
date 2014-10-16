/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import java.util.List;

import net.minecraft.block.Block;

import com.kegare.caveworld.api.BlockEntry;

public interface IBreakExecutor
{
	public IBreakExecutor setOriginPos(int x, int y, int z);

	public BreakPos getOriginPos();

	public IBreakExecutor setBreakable(Block block, int metadata);

	public BlockEntry getBreakable();

	public boolean canBreak(int x, int y, int z);

	public IBreakExecutor setBreakPositions();

	public List<BreakPos> getBreakPositions();

	public void breakAll();

	public void clear();
}