/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.particle;

import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityUniversalChestFX extends EntityPortalFX
{
	public EntityUniversalChestFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
	{
		super(world, x, y, z, motionX, motionY, motionZ);
		float f = rand.nextFloat() * 0.6F + 0.4F;
		this.particleRed = this.particleGreen = this.particleBlue = 0.65F * f * 0.8F;
	}
}