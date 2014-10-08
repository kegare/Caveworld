/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity.ai;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.entity.EntityCaveman;

public class EntityAICollector extends EntityAIBase implements IEntitySelector
{
	private final EntityCaveman theCollector;
	private final World theWorld;
	private final boolean prevTamed;
	private final double moveSpeed;
	private final float followDist;
	private final float collectDist;

	private EntityItem theDrop;
	private EntityLivingBase theOwner;
	private boolean moveSuccess;
	private int failedCount;

	public EntityAICollector(EntityCaveman entity, double speed, float followDist, float collectDist)
	{
		this.theCollector = entity;
		this.theWorld = entity.worldObj;
		this.prevTamed = entity.isTamed();
		this.moveSpeed = speed;
		this.followDist = followDist;
		this.collectDist = collectDist;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (theCollector.inventoryFull)
		{
			theOwner = theCollector.getOwner();

			return continueExecuting();
		}

		List<EntityItem> list = theWorld.selectEntitiesWithinAABB(EntityItem.class, theCollector.boundingBox.expand(collectDist, 8.0F, collectDist), this);

		for (EntityItem target : list)
		{
			if (theDrop == null)
			{
				theDrop = target;
			}
			else if (target.getDistanceSqToEntity(theCollector) < theDrop.getDistanceSqToEntity(theCollector))
			{
				theDrop = target;
			}
		}

		if (theDrop == null)
		{
			theOwner = theCollector.getOwner();
		}

		return continueExecuting();
	}

	@Override
	public boolean continueExecuting()
	{
		boolean result = prevTamed == theCollector.isTamed() && failedCount <= 20;

		if (canMoveToEntity(theDrop))
		{
			return result && !theCollector.inventoryFull && theCollector.getDistanceSqToEntity(theDrop) <= collectDist * collectDist;
		}

		if (canMoveToEntity(theOwner))
		{
			return result && theCollector.getDistanceSqToEntity(theOwner) > followDist * followDist;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		if (theDrop != null)
		{
			theOwner = null;
		}
		else if (theOwner != null)
		{
			theDrop = null;
		}

		moveSuccess = true;
		failedCount = 0;
	}

	@Override
	public void resetTask()
	{
		theDrop = null;
		theOwner = null;

		failedCount = 0;
	}

	@Override
	public void updateTask()
	{
		if (canMoveToEntity(theDrop))
		{
			theCollector.getLookHelper().setLookPositionWithEntity(theDrop, 10.0F, theCollector.getVerticalFaceSpeed());

			if (theCollector.getEntitySenses().canSee(theDrop))
			{
				theCollector.getMoveHelper().setMoveTo(theDrop.posX, theDrop.posY, theDrop.posZ, moveSpeed);
			}
		}
		else if (canMoveToEntity(theOwner))
		{
			theCollector.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, theCollector.getVerticalFaceSpeed());

			if (!theCollector.isSitting() && theCollector.getEntitySenses().canSee(theOwner))
			{
				theCollector.getMoveHelper().setMoveTo(theOwner.posX, theOwner.posY, theOwner.posZ, moveSpeed);
			}

			if (!theCollector.getLeashed() && !theCollector.isSitting() && theCollector.getDistanceSqToEntity(theOwner) >= 144.0D)
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
							theCollector.setLocationAndAngles(x + i + 0.5F, y, z + j + 0.5F, theCollector.rotationYaw, theCollector.rotationPitch);

							flag = true;
						}
					}
				}
			}
		}

		moveSuccess = theCollector.getStoppedTime() == 0L;

		if (moveSuccess)
		{
			failedCount = 0;
		}
		else
		{
			++failedCount;

			if (canMoveToEntity(theDrop))
			{
				theCollector.getNavigator().tryMoveToXYZ(theDrop.posX, theDrop.posY, theDrop.posZ, moveSpeed);
			}
			else if (!theCollector.getLeashed() && !theCollector.isSitting() && canMoveToEntity(theOwner))
			{
				theCollector.getNavigator().tryMoveToXYZ(theOwner.posX, theOwner.posY, theOwner.posZ, moveSpeed);
			}
		}
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

	@Override
	public boolean isEntityApplicable(Entity entity)
	{
		if (canMoveToEntity(entity))
		{
			ItemStack itemstack = ((EntityItem)entity).getEntityItem();

			return itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0 && (!theCollector.isTamed() ||
				!entity.getEntityData().getBoolean(EntityList.getEntityString(theCollector) + ":NoCollect") &&  theWorld.getEntitiesWithinAABB(EntityPlayer.class, entity.boundingBox.expand(1.0D, 0.0D, 1.0D)).isEmpty());
		}

		return false;
	}
}