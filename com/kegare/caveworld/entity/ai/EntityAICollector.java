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

	private double moveSpeed;
	private float collectDist;
	private EntityItem theDrop;
	private EntityLivingBase theOwner;

	private int tickCounter;

	public EntityAICollector(EntityCaveman entity, double speed, float collectDist)
	{
		this.theCollector = entity;
		this.theWorld = entity.worldObj;
		this.moveSpeed = speed;
		this.collectDist = collectDist;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (theCollector.inventoryFull)
		{
			return false;
		}

		List<EntityItem> list = theWorld.selectEntitiesWithinAABB(EntityItem.class, theCollector.boundingBox.expand(collectDist, 8.0F, collectDist), this);
		EntityItem item = null;

		for (EntityItem entity : list)
		{
			if (item == null)
			{
				item = entity;
			}
			else if (entity.getDistanceSqToEntity(theCollector) < item.getDistanceSqToEntity(theCollector))
			{
				item = entity;
			}
		}

		if (!canMoveToEntity(item))
		{
			return false;
		}

		theDrop = item;
		theOwner = theCollector.getOwner();

		return true;
	}

	@Override
	public boolean continueExecuting()
	{
		if (tickCounter > 60 || theOwner != theCollector.getOwner())
		{
			return false;
		}

		return !theCollector.inventoryFull && canMoveToEntity(theDrop) && theCollector.getDistanceSqToEntity(theDrop) <= collectDist * collectDist;
	}

	@Override
	public void startExecuting()
	{
		theCollector.getNavigator().clearPathEntity();
		tickCounter = 0;

		theCollector.isCollecting = true;
	}

	@Override
	public void resetTask()
	{
		theDrop = null;
		theOwner = null;
		theCollector.getNavigator().clearPathEntity();
		tickCounter = 0;

		theCollector.isCollecting = false;
	}

	@Override
	public void updateTask()
	{
		theCollector.getLookHelper().setLookPositionWithEntity(theDrop, 10.0F, theCollector.getVerticalFaceSpeed());

		if (theCollector.getEntitySenses().canSee(theDrop))
		{
			theCollector.getMoveHelper().setMoveTo(theDrop.posX, theDrop.posY, theDrop.posZ, moveSpeed);
			theCollector.getNavigator().tryMoveToEntityLiving(theDrop, moveSpeed);
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

	@Override
	public boolean isEntityApplicable(Entity entity)
	{
		if (!canMoveToEntity(entity))
		{
			return false;
		}

		EntityItem item = (EntityItem)entity;
		ItemStack itemstack = item.getEntityItem();

		if (itemstack == null || itemstack.getItem() == null || itemstack.stackSize <= 0)
		{
			return false;
		}

		if (theCollector.isTamed())
		{
			EntityPlayer thrower = null;

			if (item.func_145800_j() != null)
			{
				thrower = theWorld.getPlayerEntityByName(item.func_145800_j());
			}

			return !theCollector.func_152114_e(thrower) && theWorld.getEntitiesWithinAABB(EntityPlayer.class, entity.boundingBox.expand(1.0D, 0.0D, 1.0D)).isEmpty();
		}

		return true;
	}
}