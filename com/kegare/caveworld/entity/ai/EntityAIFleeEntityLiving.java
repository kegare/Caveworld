/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity.ai;

import java.util.Random;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeEntityLiving extends EntityAIBase
{
	private final EntityTameable theCreature;
	private final World theWorld;
	private final IEntitySelector entitySelector;
	private final double moveSpeed;

	private double fledX;
	private double fledY;
	private double fledZ;

	public EntityAIFleeEntityLiving(EntityTameable entity, IEntitySelector selector, double speed)
	{
		this.theCreature = entity;
		this.theWorld = entity.worldObj;
		this.entitySelector = selector;
		this.moveSpeed = speed;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (!theCreature.isTamed() && !isNoNearbyTarget(theCreature.boundingBox))
		{
			Vec3 vec3 = findPossibleCoord();

			if (vec3 == null)
			{
				return false;
			}

			fledX = vec3.xCoord;
			fledY = vec3.yCoord;
			fledZ = vec3.zCoord;

			return true;
		}

		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		return !theCreature.getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		theCreature.getLookHelper().setLookPosition(fledX, fledY, fledZ, 10.0F, theCreature.getVerticalFaceSpeed());
		theCreature.getNavigator().tryMoveToXYZ(fledX, fledY, fledZ, moveSpeed);
	}

	public boolean isNoNearbyTarget(AxisAlignedBB boundingBox)
	{
		return boundingBox != null && theWorld.selectEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(5.0D, 2.0D, 5.0D), entitySelector).isEmpty();
	}

	private Vec3 findPossibleCoord()
	{
		Random random = theCreature.getRNG();

		for (int i = 0; i < 10; ++i)
		{
			int x = MathHelper.floor_double(theCreature.posX + random.nextInt(20) - 10.0D);
			int y = MathHelper.floor_double(theCreature.boundingBox.minY + random.nextInt(6) - 3.0D);
			int z = MathHelper.floor_double(theCreature.posZ + random.nextInt(20) - 10.0D);

			if (isNoNearbyTarget(AxisAlignedBB.getBoundingBox(x, y, z, x, y, z)) && theCreature.getBlockPathWeight(x, y, z) < 0.0F)
			{
				return Vec3.createVectorHelper(x, y, z);
			}
		}

		return null;
	}
}