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
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.EntityCaveman;

public class EntityAISoldier extends EntityAIBase implements IEntitySelector
{
	public enum CombatType
	{
		SWORD,
		NONE
	}

	private final EntityCaveman theSoldier;
	private final World theWorld;

	private EntityLivingBase theTarget;
	private ItemStack theWeapon;
	private boolean moveSuccess;
	private int failedCount;

	private CombatType currentType = CombatType.NONE;

	public EntityAISoldier(EntityCaveman entity)
	{
		this.theSoldier = entity;
		this.theWorld = entity.worldObj;
		this.setMutexBits(1);
	}

	public CombatType getCurrentType()
	{
		return currentType == null ? CombatType.NONE : currentType;
	}

	@Override
	public boolean shouldExecute()
	{
		ItemStack itemstack = theSoldier.getHeldItem();

		if (itemstack == null || itemstack.getItem() == null || itemstack.stackSize <= 0)
		{
			currentType = CombatType.NONE;

			return false;
		}

		if (itemstack.getItem() instanceof ItemSword)
		{
			currentType = CombatType.SWORD;
		}
		else
		{
			currentType = CombatType.NONE;
		}

		switch (getCurrentType())
		{
			case SWORD:
				List<EntityLivingBase> list = theWorld.selectEntitiesWithinAABB(EntityLivingBase.class, theSoldier.boundingBox.expand(10.0D, 4.0D, 10.0D), this);

				for (EntityLivingBase target : list)
				{
					if (theTarget == null)
					{
						theTarget = target;
					}
					else if (target.getDistanceSqToEntity(theSoldier) < theTarget.getDistanceSqToEntity(theSoldier))
					{
						theTarget = target;
					}
				}

				theWeapon = itemstack.copy();

				return canMoveToEntity(theTarget);
			default:
				return false;
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return failedCount <= 20 && getCurrentType() != CombatType.NONE && canMoveToEntity(theTarget) && ItemStack.areItemStacksEqual(theWeapon, theSoldier.getHeldItem());
	}

	@Override
	public void startExecuting()
	{
		moveSuccess = true;
		failedCount = 0;
	}

	@Override
	public void resetTask()
	{
		theTarget = null;
		theWeapon = null;
		failedCount = 0;
	}

	@Override
	public void updateTask()
	{
		theSoldier.getLookHelper().setLookPositionWithEntity(theTarget, 10.0F, theSoldier.getVerticalFaceSpeed());

		switch (getCurrentType())
		{
			case SWORD:
				if (theSoldier.getDistanceSqToEntity(theTarget) > 3.0D)
				{
					boolean flag = !theSoldier.isSitting() && theSoldier.getEntitySenses().canSee(theTarget);

					if (flag)
					{
						theSoldier.getMoveHelper().setMoveTo(theTarget.posX, theTarget.posY, theTarget.posZ, 0.85D);
					}

					moveSuccess = theSoldier.getStoppedTime() == 0L;

					if (moveSuccess)
					{
						failedCount = 0;
					}
					else
					{
						++failedCount;

						if (flag)
						{
							theSoldier.getNavigator().tryMoveToXYZ(theTarget.posX, theTarget.posY, theTarget.posZ, 1.0D);
						}
					}
				}
				else
				{
					theSoldier.getMoveHelper().setMoveTo(theSoldier.posX, theSoldier.posY, theSoldier.posZ, 1.0D);

					if (theSoldier.ticksExisted % 10 == 0)
					{
						ItemSword item = (ItemSword)theWeapon.getItem();

						theSoldier.swingItem();

						if (theTarget.attackEntityFrom(DamageSource.causeMobDamage(theSoldier), item.func_150931_i()))
						{
							item.hitEntity(theWeapon, theTarget, theSoldier);
						}
					}
				}

				break;
			default:
				break;
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
		if (!canMoveToEntity(entity))
		{
			return false;
		}

		switch (Config.cavemanCreatureType)
		{
			case 0:
				if (theSoldier.isTamed())
				{
					return entity instanceof IMob;
				}

				return false;
			case 1:
				if (theSoldier.isTamed())
				{
					return entity instanceof IMob;
				}

				return entity instanceof EntityPlayer;
			default:
				return false;
		}
	}
}