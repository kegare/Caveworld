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

import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Sets;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.breaker.BreakPos.NearestBreakPosComparator;

public abstract class MultiBreakExecutor
{
	protected final EntityPlayer player;
	protected final ArrayListExtended<BreakPos> breakPositions = new ArrayListExtended();

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
		originPos = null;
		currentPos = null;
	}
}