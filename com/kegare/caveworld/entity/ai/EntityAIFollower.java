/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.entity.EntityCaveman;

public class EntityAIFollower extends EntityAIBase
{
	private final EntityCaveman theFollower;
	private final World theWorld;

	private double followSpeed;
	private EntityLivingBase theOwner;

	private int tickCounter;

	public EntityAIFollower(EntityCaveman entity, double speed)
	{
		this.theFollower = entity;
		this.theWorld = entity.worldObj;
		this.followSpeed = speed;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (theFollower.isCollecting)
		{
			return false;
		}

		EntityLivingBase owner = theFollower.getOwner();

		if (!canMoveToEntity(owner) || theFollower.isSitting() || theFollower.getDistanceSqToEntity(owner) < 2.0D * 2.0D)
		{
			return false;
		}

		theOwner = owner;

		return true;
	}

	@Override
	public boolean continueExecuting()
	{
		if (tickCounter > 60 || theOwner != theFollower.getOwner())
		{
			return false;
		}

		return !theFollower.isSitting() && canMoveToEntity(theOwner) && theFollower.getDistanceSqToEntity(theOwner) > 3.0D * 3.0D;
	}

	@Override
	public void startExecuting()
	{
		theFollower.getNavigator().clearPathEntity();
		tickCounter = 0;
	}

	@Override
	public void resetTask()
	{
		theOwner = null;
		theFollower.getNavigator().clearPathEntity();
		tickCounter = 0;
	}

	@Override
	public void updateTask()
	{
		theFollower.getMoveHelper().setMoveTo(theOwner.posX, theOwner.posY, theOwner.posZ, followSpeed);
		theFollower.getNavigator().tryMoveToEntityLiving(theOwner, followSpeed);
		theFollower.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, theFollower.getVerticalFaceSpeed());

		if (!theFollower.getLeashed() && theFollower.getDistanceSqToEntity(theOwner) >= 144.0D && theOwner.onGround)
		{
			int x = MathHelper.floor_double(theOwner.posX) - 2;
			int y = MathHelper.floor_double(theOwner.boundingBox.minY);
			int z = MathHelper.floor_double(theOwner.posZ) - 2;
			boolean flag = false;

			for (int i = 0; !flag && i <= 4; ++i)
			{
				for (int j = 0; !flag && j <= 4; ++j)
				{
					if ((i < 1 || j < 1 || i > 3 || j > 3) && World.doesBlockHaveSolidTopSurface(theWorld, x + i, y - 1, z + j) &&
						!theWorld.getBlock(x + i, y, z + j).isNormalCube() && !theWorld.getBlock(x + i, y + 1, z + j).isNormalCube())
					{
						theFollower.setLocationAndAngles(x + i + 0.5D, y + 0.5D, z + j + 0.5D, theFollower.rotationYaw, theFollower.rotationPitch);

						flag = true;
					}
				}
			}
		}

		++tickCounter;
	}

	public boolean canMoveToEntity(Entity entity)
	{
		if (entity == null || entity.isDead)
		{
			return false;
		}

		if (!theWorld.isDaytime() || CaveworldAPI.isEntityInCaveworld(entity))
		{
			return true;
		}

		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.boundingBox.minY);
		int z = MathHelper.floor_double(entity.posZ);

		return !theWorld.canBlockSeeTheSky(x, y, z);
	}
}