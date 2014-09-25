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
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAICollector extends EntityAIBase implements IEntitySelector
{
	private final EntityTameable theCollector;
	private final World theWorld;
	private final double moveSpeed;
	private final float followDist;
	private final float collectDist;

	private EntityItem theDrop;
	private EntityLivingBase theOwner;

	public EntityAICollector(EntityTameable entity, double speed, float followDist, float collectDist)
	{
		this.theCollector = entity;
		this.theWorld = entity.worldObj;
		this.moveSpeed = speed;
		this.followDist = followDist;
		this.collectDist = collectDist;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		List<EntityItem> list = theWorld.selectEntitiesWithinAABB(EntityItem.class, theCollector.boundingBox.expand(collectDist, collectDist / 2, collectDist), this);

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
		if (canMoveToEntity(theDrop))
		{
			return theCollector.getDistanceSqToEntity(theDrop) <= collectDist * collectDist;
		}

		if (canMoveToEntity(theOwner))
		{
			return !theCollector.isSitting() && theCollector.getDistanceSqToEntity(theOwner) > followDist * followDist;
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
	}

	@Override
	public void resetTask()
	{
		theDrop = null;
		theOwner = null;
	}

	@Override
	public void updateTask()
	{
		if (canMoveToEntity(theDrop))
		{
			theCollector.getLookHelper().setLookPositionWithEntity(theDrop, 10.0F, theCollector.getVerticalFaceSpeed());

			int x = MathHelper.floor_double(theDrop.posX);
			int y = MathHelper.floor_double(theDrop.boundingBox.minY);
			int z = MathHelper.floor_double(theDrop.posZ);

			if (!theWorld.canBlockSeeTheSky(x, y, z))
			{
				theCollector.getMoveHelper().setMoveTo(theDrop.posX, theDrop.posY, theDrop.posZ, moveSpeed);
			}
		}
		else if (canMoveToEntity(theOwner))
		{
			theCollector.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, theCollector.getVerticalFaceSpeed());

			int x = MathHelper.floor_double(theOwner.posX);
			int y = MathHelper.floor_double(theOwner.boundingBox.minY);
			int z = MathHelper.floor_double(theOwner.posZ);

			theCollector.getMoveHelper().setMoveTo(theOwner.posX, theOwner.posY, theOwner.posZ, moveSpeed);

			if (!theCollector.getLeashed() && theCollector.getDistanceSqToEntity(theOwner) >= 144.0D)
			{
				x = MathHelper.floor_double(theOwner.posX) - 2;
				y = MathHelper.floor_double(theOwner.boundingBox.minY);
				z = MathHelper.floor_double(theOwner.posZ) - 2;

				for (int i = 0; i <= 4; ++i)
				{
					for (int j = 0; j <= 4; ++j)
					{
						if ((i < 1 || j < 1 || i > 3 || j > 3) && World.doesBlockHaveSolidTopSurface(theWorld, x + i, y - 1, z + j) && !theWorld.getBlock(x + i, y, z + j).isNormalCube() && !theWorld.getBlock(x + i, y + 1, z + j).isNormalCube())
						{
							theCollector.setLocationAndAngles(x + i + 0.5F, y, z + j + 0.5F, theCollector.rotationYaw, theCollector.rotationPitch);

							return;
						}
					}
				}
			}
		}
	}

	public boolean canMoveToEntity(Entity entity)
	{
		if (entity == null || entity.isDead)
		{
			return false;
		}

		if (!theWorld.isDaytime())
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
		if (entity.isEntityAlive() && theCollector.getEntitySenses().canSee(entity) && canMoveToEntity(entity))
		{
			ItemStack itemstack = ((EntityItem)entity).getEntityItem();

			return itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0 &&
				(!theCollector.isTamed() || !entity.getEntityData().getBoolean(EntityList.getEntityString(theCollector) + ":NoCollect")) &&
				(!theCollector.isTamed() || theWorld.getEntitiesWithinAABB(EntityPlayer.class, entity.boundingBox.expand(1.0D, 0.0D, 1.0D)).isEmpty());
		}

		return false;
	}
}