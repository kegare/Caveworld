/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityCavenicArrow extends EntityCaveArrow
{
	public EntityCavenicArrow(World world)
	{
		super(world);
	}

	public EntityCavenicArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityCavenicArrow(World world, EntityLivingBase player, EntityLivingBase entity, float par3, float par4)
	{
		super(world, player, entity, par3, par4);
	}

	public EntityCavenicArrow(World world, EntityLivingBase player, float par3)
	{
		super(world, player, par3);
	}

	@Override
	protected void onHit(Block block, int metadata)
	{
		if (block == inTile && metadata == inData && mop != null)
		{
			++ticksInGround;

			if (tryPlaceBlock() || ticksInGround == 100)
			{
				setDead();
			}
		}
		else
		{
			inGround = false;
			motionX *= rand.nextFloat() * 0.2F;
			motionY *= rand.nextFloat() * 0.2F;
			motionZ *= rand.nextFloat() * 0.2F;
			ticksInGround = 0;
			ticksInAir = 0;
		}
	}
}