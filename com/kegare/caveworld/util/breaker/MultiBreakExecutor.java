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
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.util.breaker.BreakPos.NearestBreakPosComparator;

public abstract class MultiBreakExecutor
{
	public static final AtomicInteger positionsCount = new AtomicInteger(0);

	protected final EntityPlayer player;
	protected final Set<BreakPos> breakPositions = Sets.newConcurrentHashSet();

	protected BreakPos originPos;
	protected BreakPos currentPos;

	public MultiBreakExecutor(EntityPlayer player)
	{
		this.player = player;
	}

	public MultiBreakExecutor setOriginPos(int x, int y, int z)
	{
		breakPositions.clear();

		originPos = new BreakPos(player.worldObj, x, y, z);
		currentPos = originPos;

		return this;
	}

	public BreakPos getOriginPos()
	{
		return originPos;
	}

	public boolean canBreak(int x, int y, int z)
	{
		if (originPos == null || originPos.world.isAirBlock(x, y, z))
		{
			return false;
		}

		return originPos.getCurrentBlock() == originPos.world.getBlock(x, y, z) && originPos.getCurrentMetadata() == originPos.world.getBlockMetadata(x, y, z) ||
			originPos.getCurrentBlock() instanceof BlockRedstoneOre && originPos.world.getBlock(x, y, z) instanceof BlockRedstoneOre;
	}

	public abstract MultiBreakExecutor setBreakPositions();

	public boolean offer(int x, int y, int z)
	{
		if (canBreak(x, y, z))
		{
			currentPos = new BreakPos(originPos.world, x, y, z);

			return breakPositions.add(currentPos);
		}

		return false;
	}

	public Set<BreakPos> getBreakPositions()
	{
		return breakPositions;
	}

	public void breakAll()
	{
		ItemStack current = player.getCurrentEquippedItem();
		List<BreakPos> list = Lists.newArrayList(breakPositions);

		Collections.sort(list, new NearestBreakPosComparator(originPos));

		for (BreakPos pos : list)
		{
			if (current != null && (!current.isItemStackDamageable() || current.getItemDamage() < current.getMaxDamage()) && !pos.isPlaced())
			{
				pos.doBreak(player);
			}
		}

		breakPositions.clear();
	}

	public void clear()
	{
		breakPositions.clear();
		originPos = null;
		currentPos = null;
	}
}