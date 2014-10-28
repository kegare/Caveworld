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
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.item.ItemMiningPickaxe;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.comparator.BreakPosComparator;

public class AditBreakExecutor implements IBreakExecutor
{
	public static final Table<World, EntityPlayer, AditBreakExecutor> executors = HashBasedTable.create();

	private final World world;
	private final EntityPlayer player;

	protected final ArrayListExtended<BreakPos> breakPositions = new ArrayListExtended();
	protected BlockEntry breakableBlock;
	protected BreakPos originPos;
	protected BreakPos currentPos;

	private AditBreakExecutor(World world, EntityPlayer player)
	{
		this.world = world;
		this.player = player;
	}

	public static AditBreakExecutor getExecutor(World world, EntityPlayer player)
	{
		AditBreakExecutor executor = executors.get(world, player);

		if (executor == null)
		{
			executor = new AditBreakExecutor(world, player);

			executors.put(world, player, executor);
		}

		return executor;
	}

	@Override
	public AditBreakExecutor setOriginPos(int x, int y, int z)
	{
		breakPositions.clear();

		originPos = new BreakPos(world, x, y, z);
		currentPos = originPos;

		return this;
	}

	@Override
	public BreakPos getOriginPos()
	{
		return originPos;
	}

	@Override
	public AditBreakExecutor setBreakable(Block block, int metadata)
	{
		breakableBlock = new BlockEntry(block, metadata);

		return this;
	}

	@Override
	public BlockEntry getBreakable()
	{
		if (breakableBlock == null)
		{
			breakableBlock = new BlockEntry(originPos.prevBlock, originPos.prevMeta);
		}

		return breakableBlock;
	}

	@Override
	public boolean canBreak(int x, int y, int z)
	{
		if (originPos == null || world.isAirBlock(x, y, z))
		{
			return false;
		}

		ItemStack current = player.getCurrentEquippedItem();

		if (current != null && current.getItem() != null && current.getItem() instanceof ItemMiningPickaxe)
		{
			ItemMiningPickaxe pickaxe = (ItemMiningPickaxe)current.getItem();

			if (pickaxe.canBreak(current, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z)))
			{
				return true;
			}
		}

		return getBreakable().getBlock() == world.getBlock(x, y, z) && getBreakable().getMetadata() == world.getBlockMetadata(x, y, z) ||
			getBreakable().getBlock() instanceof BlockRedstoneOre && world.getBlock(x, y, z) instanceof BlockRedstoneOre;
	}

	@Override
	public AditBreakExecutor setBreakPositions()
	{
		int face = BlockPistonBase.determineOrientation(world, originPos.x, originPos.y, originPos.z, player);

		if (face == 0 || face == 1)
		{
			return this;
		}

		if (originPos.y == MathHelper.floor_double(player.posY))
		{
			offer(originPos.x, originPos.y + 1, originPos.z);
		}
		else
		{
			offer(originPos.x, originPos.y - 1, originPos.z);
		}

		return this;
	}

	private boolean offer(int x, int y, int z)
	{
		if (canBreak(x, y, z) && y >= MathHelper.floor_double(player.posY))
		{
			currentPos = new BreakPos(world, x, y, z);

			return breakPositions.addIfAbsent(currentPos);
		}

		return false;
	}

	@Override
	public List<BreakPos> getBreakPositions()
	{
		return breakPositions;
	}

	@Override
	public void breakAll()
	{
		Collections.sort(breakPositions, new BreakPosComparator(originPos));

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

	@Override
	public void clear()
	{
		breakPositions.clear();
		breakableBlock = null;
		originPos = null;
		currentPos = null;
	}
}
