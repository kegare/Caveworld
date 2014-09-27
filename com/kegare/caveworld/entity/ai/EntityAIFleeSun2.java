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

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.kegare.caveworld.api.CaveworldAPI;

public class EntityAIFleeSun2 extends EntityAIBase
{
	private final EntityCreature theCreature;
	private final World theWorld;
	private final double moveSpeed;

	private double shelterX;
	private double shelterY;
	private double shelterZ;

	public EntityAIFleeSun2(EntityCreature entity, double speed)
	{
		this.theCreature = entity;
		this.moveSpeed = speed;
		this.theWorld = entity.worldObj;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (!theWorld.isDaytime() || CaveworldAPI.isEntityInCaveworld(theCreature))
		{
			return false;
		}
		else if (!theWorld.canBlockSeeTheSky(MathHelper.floor_double(theCreature.posX), (int)theCreature.boundingBox.minY, MathHelper.floor_double(theCreature.posZ)))
		{
			return false;
		}
		else
		{
			Vec3 vec3 = findPossibleShelter();

			if (vec3 == null)
			{
				return false;
			}

			shelterX = vec3.xCoord;
			shelterY = vec3.yCoord;
			shelterZ = vec3.zCoord;

			return true;
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return !theCreature.getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		theCreature.getNavigator().tryMoveToXYZ(shelterX, shelterY, shelterZ, moveSpeed);
	}

	private Vec3 findPossibleShelter()
	{
		Random random = theCreature.getRNG();

		for (int i = 0; i < 10; ++i)
		{
			int x = MathHelper.floor_double(theCreature.posX + random.nextInt(20) - 10.0D);
			int y = MathHelper.floor_double(theCreature.boundingBox.minY + random.nextInt(6) - 3.0D);
			int z = MathHelper.floor_double(theCreature.posZ + random.nextInt(20) - 10.0D);

			if (!theWorld.canBlockSeeTheSky(x, y, z) && theCreature.getBlockPathWeight(x, y, z) < 0.0F)
			{
				return Vec3.createVectorHelper(x, y, z);
			}
		}

		return null;
	}
}