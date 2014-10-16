/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.util.breaker;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.google.common.base.Objects;

public class BreakPos
{
	public final World world;
	public int x;
	public int y;
	public int z;
	public final Block prevBlock;
	public final int prevMeta;

	public BreakPos(World world, int x, int y, int z)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.prevBlock = getCurrentBlock();
		this.prevMeta = world.getBlockMetadata(x, y, z);
	}

	public boolean isPlaced()
	{
		return getCurrentBlock() != prevBlock || getCurrentMetadata() != prevMeta;
	}

	public boolean doBreak(EntityPlayer player)
	{
		Block block = getCurrentBlock();
		int meta = getCurrentMetadata();

		if (world.setBlockToAir(x, y, z))
		{
			if (player.capabilities.isCreativeMode)
			{
				return true;
			}

			block.harvestBlock(world, player, x, y, z, meta);

			BreakEvent event = new BreakEvent(x, y, z, world, block, meta, player);

			if (MinecraftForge.EVENT_BUS.post(event))
			{
				block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
			}

			player.getCurrentEquippedItem().damageItem(1, player);

			return true;
		}

		return false;
	}

	public Block getCurrentBlock()
	{
		return world.getBlock(x, y, z);
	}

	public int getCurrentMetadata()
	{
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof BreakPos)
		{
			BreakPos pos = (BreakPos)obj;

			return world.provider.dimensionId == pos.world.provider.dimensionId && x == pos.x && y == pos.y && z == pos.z;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(world.provider.dimensionId, x, y, z);
	}

	public double getDistance(int x, int y, int z)
	{
		return Math.sqrt(getDistanceSq(x, y, z));
	}

	public int getDistanceSq(int x, int y, int z)
	{
		int distX = this.x - x;
		int distY = this.y - y;
		int distZ = this.z - z;

		return distX * distX + distY * distY + distZ * distZ;
	}

	public double getDistance(BreakPos pos)
	{
		return Math.sqrt(getDistanceSq(pos));
	}

	public int getDistanceSq(BreakPos pos)
	{
		return getDistanceSq(pos.x, pos.y, pos.z);
	}
}