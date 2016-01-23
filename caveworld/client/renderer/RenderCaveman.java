/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCaveman extends RenderBiped
{
	private static final ResourceLocation cavemanTextures = new ResourceLocation("caveworld", "textures/entity/caveman.png");

	private final ModelCaveman cavemanModel;

	public RenderCaveman()
	{
		super(new ModelCaveman(), 0.45F, 1.0F);
		this.cavemanModel = (ModelCaveman)super.mainModel;
		this.setRenderPassModel(cavemanModel);
	}

	@Override
	protected void func_82421_b()
	{
		field_82423_g = new ModelCaveman();
		field_82425_h = new ModelCaveman();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return cavemanTextures;
	}
}