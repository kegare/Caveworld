/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.renderer;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCaveman extends RenderBiped
{
	private static final ResourceLocation cavemanTextures = new ResourceLocation("caveworld", "textures/entity/caveman.png");
	private static final ResourceLocation keimanTextures = new ResourceLocation("caveworld", "textures/entity/keiman.png");

	private final ModelCaveman cavemanModel;

	public RenderCaveman()
	{
		super(new ModelCaveman(), 0.45F);
		this.cavemanModel = (ModelCaveman)super.mainModel;
		this.setRenderPassModel(cavemanModel);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		if (entity instanceof EntityLiving && ((EntityLiving)entity).hasCustomNameTag() && ((EntityLiving)entity).getCustomNameTag().equals("kei"))
		{
			return keimanTextures;
		}

		return cavemanTextures;
	}
}