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
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCavenicSpider extends RenderSpider
{
	private static final ResourceLocation cavenicSpiderTexture = new ResourceLocation("caveworld", "textures/entity/cavenic_spider.png");

	@Override
	protected ResourceLocation getEntityTexture(EntitySpider entity)
	{
		return cavenicSpiderTexture;
	}

	@Override
	protected int shouldRenderPass(EntitySpider entity, int pass, float par3)
	{
		return -1;
	}
}