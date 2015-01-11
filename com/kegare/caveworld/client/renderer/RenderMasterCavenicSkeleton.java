/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.renderer;

import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMasterCavenicSkeleton extends RenderSkeleton
{
	private static final ResourceLocation cavenicSkeletonTexture = new ResourceLocation("caveworld", "textures/entity/master_cavenic_skeleton.png");

	@Override
	protected void preRenderCallback(EntitySkeleton entity, float ticks)
	{
		GL11.glScalef(1.3F, 1.3F, 1.3F);
	}

	@Override
	public void doRender(EntityLiving entity, double posX, double posY, double posZ, float f1, float f2)
	{
		BossStatus.setBossStatus((IBossDisplayData)entity, true);

		super.doRender(entity, posX, posY, posZ, f1, f2);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySkeleton entity)
	{
		return cavenicSkeletonTexture;
	}
}