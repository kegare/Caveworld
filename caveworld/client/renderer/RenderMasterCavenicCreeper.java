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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderMasterCavenicCreeper extends RenderCreeper
{
	private static final ResourceLocation cavenicCreeperTexture = new ResourceLocation("caveworld", "textures/entity/master_cavenic_creeper.png");

	@Override
	public void doRender(EntityLiving entity, double posX, double posY, double posZ, float f1, float f2)
	{
		BossStatus.setBossStatus((IBossDisplayData)entity, false);

		super.doRender(entity, posX, posY, posZ, f1, f2);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCreeper entity)
	{
		return cavenicCreeperTexture;
	}
}