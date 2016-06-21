package caveworld.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderCrazyCavenicSkeleton extends RenderSkeleton
{
	private static final ResourceLocation cavenicSkeletonTexture = new ResourceLocation("caveworld", "textures/entity/crazy_cavenic_skeleton.png");

	@Override
	protected void preRenderCallback(EntitySkeleton entity, float ticks)
	{
		GL11.glScalef(1.5F, 1.5F, 1.5F);
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