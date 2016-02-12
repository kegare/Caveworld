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
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCavenicCreeper extends RenderCreeper
{
	private static final ResourceLocation cavenicCreeperTexture = new ResourceLocation("caveworld", "textures/entity/cavenic_creeper.png");

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity)
	{
		return cavenicCreeperTexture;
	}
}