/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kegare.caveworld.item.ItemMiningPickaxe;

public class AditBreakExecutor extends MultiBreakExecutor
{
	public static final Table<World, EntityPlayer, AditBreakExecutor> executors = HashBasedTable.create();

	private AditBreakExecutor(World world, EntityPlayer player)
	{
		super(world, player);
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
	public boolean canBreak(int x, int y, int z)
	{
		if (y < MathHelper.floor_double(player.posY))
		{
			return false;
		}

		if (super.canBreak(x, y, z))
		{
			return true;
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

		return false;
	}

	@Override
	public AditBreakExecutor setBreakPositions()
	{
		int face = BlockPistonBase.determineOrientation(world, originPos.x, originPos.y, originPos.z, player);

		if (face == 0 || face == 1)
		{
			return this;
		}

		offer(originPos.x, originPos.y, originPos.z);

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
}