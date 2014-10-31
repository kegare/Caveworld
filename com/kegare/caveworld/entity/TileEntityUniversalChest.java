/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.kegare.caveworld.block.CaveBlocks;

public class TileEntityUniversalChest extends TileEntity
{
	public float lidAngle;
	public float prevLidAngle;

	public int numUsingPlayers;

	private int updateEntityTick;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (++updateEntityTick % 20 * 4 == 0)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, CaveBlocks.universal_chest, 1, numUsingPlayers);
		}

		prevLidAngle = lidAngle;

		float f = 0.1F;
		double d1;

		if (numUsingPlayers > 0 && lidAngle == 0.0F)
		{
			double d0 = xCoord + 0.5D;
			d1 = zCoord + 0.5D;

			worldObj.playSoundEffect(d0, yCoord + 0.5D, d1, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F)
		{
			float f2 = lidAngle;

			if (numUsingPlayers > 0)
			{
				lidAngle += f;
			}
			else
			{
				lidAngle -= f;
			}

			if (lidAngle > 1.0F)
			{
				lidAngle = 1.0F;
			}

			float f1 = 0.5F;

			if (lidAngle < f1 && f2 >= f1)
			{
				d1 = xCoord + 0.5D;
				double d2 = zCoord + 0.5D;

				worldObj.playSoundEffect(d1, yCoord + 0.5D, d2, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F)
			{
				lidAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int id, int param)
	{
		if (id == 1)
		{
			numUsingPlayers = param;

			return true;
		}

		return super.receiveClientEvent(id, param);
	}

	@Override
	public void invalidate()
	{
		updateContainingBlockInfo();

		super.invalidate();
	}

	public void openInventory()
	{
		++numUsingPlayers;

		worldObj.addBlockEvent(xCoord, yCoord, zCoord, CaveBlocks.universal_chest, 1, numUsingPlayers);
	}

	public void closeInventory()
	{
		--numUsingPlayers;

		worldObj.addBlockEvent(xCoord, yCoord, zCoord, CaveBlocks.universal_chest, 1, numUsingPlayers);
	}

	public boolean isUseableByPlayer(EntityPlayer player)
	{
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) == this)
		{
			return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
		}

		return false;
	}
}