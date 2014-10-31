/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.breaker.BreakPos.NearestBreakPosComparator;

public abstract class MultiBreakExecutor
{
	protected final World world;
	protected final EntityPlayer player;

	protected final ArrayListExtended<BreakPos> breakPositions = new ArrayListExtended();
	protected BlockEntry breakableBlock;
	protected BreakPos originPos;
	protected BreakPos currentPos;

	public MultiBreakExecutor(World world, EntityPlayer player)
	{
		this.world = world;
		this.player = player;
	}

	public MultiBreakExecutor setOriginPos(int x, int y, int z)
	{
		breakPositions.clear();

		originPos = new BreakPos(world, x, y, z);
		currentPos = originPos;

		return this;
	}

	public BreakPos getOriginPos()
	{
		return originPos;
	}

	public MultiBreakExecutor setBreakable(Block block, int metadata)
	{
		breakableBlock = new BlockEntry(block, metadata);

		return this;
	}

	public BlockEntry getBreakable()
	{
		if (breakableBlock == null)
		{
			breakableBlock = new BlockEntry(originPos.prevBlock, originPos.prevMeta);
		}

		return breakableBlock;
	}

	public boolean canBreak(int x, int y, int z)
	{
		if (originPos == null || world.isAirBlock(x, y, z))
		{
			return false;
		}

		return getBreakable().getBlock() == world.getBlock(x, y, z) && getBreakable().getMetadata() == world.getBlockMetadata(x, y, z) ||
			getBreakable().getBlock() instanceof BlockRedstoneOre && world.getBlock(x, y, z) instanceof BlockRedstoneOre;
	}

	public abstract MultiBreakExecutor setBreakPositions();

	public boolean offer(int x, int y, int z)
	{
		if (canBreak(x, y, z))
		{
			currentPos = new BreakPos(world, x, y, z);

			return breakPositions.addIfAbsent(currentPos);
		}

		return false;
	}

	public List<BreakPos> getBreakPositions()
	{
		return breakPositions;
	}

	public void breakAll()
	{
		Collections.sort(breakPositions, new NearestBreakPosComparator(originPos));

		Set<BreakPos> remove = Sets.newHashSet();

		for (BreakPos pos : breakPositions)
		{
			ItemStack current = player.getCurrentEquippedItem();

			if (current == null || current.isItemStackDamageable() && current.getItemDamage() >= current.getMaxDamage() || pos.isPlaced())
			{
				remove.add(pos);
			}
			else
			{
				pos.doBreak(player);

				remove.add(pos);
			}
		}

		for (BreakPos pos : remove)
		{
			breakPositions.remove(pos);
		}
	}

	public void clear()
	{
		breakPositions.clear();
		breakableBlock = null;
		originPos = null;
		currentPos = null;
	}
}