/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.world.World;

public class EntityAquaSquid extends EntitySquid
{
	public EntityAquaSquid(World world)
	{
		super(world);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return worldObj.checkNoEntityCollision(boundingBox);
	}
}