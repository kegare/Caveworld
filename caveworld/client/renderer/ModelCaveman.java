/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.renderer;

import caveworld.entity.EntityCaveman;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelCaveman extends ModelBiped
{
	public ModelRenderer bipedBack;

	private final float
	headPtY = -1.0F,
	bodyPtY = 2.0F,
	backPtY = 3.0F,
	armPtY = 4.0F,
	legPtY = 14.0F,
	sit = 8.5F;

	public ModelCaveman()
	{
		super();
		this.bipedHead = new ModelRenderer(this, 0, 0);
		this.bipedHead.addBox(-4.0F, -5.0F, -4.0F, 8, 8, 8);
		this.bipedHead.setRotationPoint(0.0F, headPtY, 0.0F);
		this.bipedHeadwear = bipedHead;
		this.bipedBody = new ModelRenderer(this, 0, 16);
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
		this.bipedBody.setRotationPoint(0.0F, bodyPtY, 0.0F);
		this.bipedBack = new ModelRenderer(this, 32, 0);
		this.bipedBack.addBox(0.0F, 0.0F, 0.0F, 6, 11, 3);
		this.bipedBack.setRotationPoint(-3.0F, backPtY, 2.0F);
		this.bipedRightArm = new ModelRenderer(this, 24, 18);
		this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2);
		this.bipedRightArm.setRotationPoint(-5.0F, armPtY, 0.0F);
		this.bipedRightArm.rotateAngleZ = 1.5F;
		this.bipedLeftArm = new ModelRenderer(this, 24, 18);
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2);
		this.bipedLeftArm.setRotationPoint(5.0F, armPtY, 0.0F);
		this.bipedLeftArm.rotateAngleZ = -1.5F;
		this.bipedLeftArm.mirror = true;
		this.bipedRightLeg = new ModelRenderer(this, 24, 18);
		this.bipedRightLeg.addBox(-1.0F, 0F, -1.0F, 2, 12, 2);
		this.bipedRightLeg.setRotationPoint(-2.0F, legPtY, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(this, 24, 18);
		this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2);
		this.bipedLeftLeg.setRotationPoint(2.0F, legPtY, 0.0F);
		this.bipedLeftLeg.mirror = true;
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity)
	{
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);

		if (((EntityCaveman)entity).isStopped())
		{
			bipedRightArm.rotateAngleX += -((float)Math.PI / 5.0F);
			bipedLeftArm.rotateAngleX += -((float)Math.PI / 5.0F);
			bipedRightLeg.rotateAngleX = -((float)Math.PI * 2.35F / 5.0F);
			bipedLeftLeg.rotateAngleX = -((float)Math.PI * 2.35F / 5.0F);
			bipedRightLeg.rotateAngleY = (float)Math.PI / 10.0F;
			bipedLeftLeg.rotateAngleY = -((float)Math.PI / 10.0F);
			bipedHead.rotationPointY = headPtY + sit;
			bipedHeadwear.rotationPointY = bipedHead.rotationPointY;
			bipedBody.rotationPointY = bodyPtY + sit;
			bipedBack.rotationPointY = backPtY + sit;
			bipedRightArm.rotationPointY = armPtY + sit;
			bipedLeftArm.rotationPointY = bipedRightArm.rotationPointY;
			bipedRightLeg.rotationPointY = legPtY + sit;
			bipedLeftLeg.rotationPointY = bipedRightLeg.rotationPointY;
		}
		else
		{
			bipedHead.rotationPointY = headPtY;
			bipedHeadwear.rotationPointY = bipedHead.rotationPointY;
			bipedBody.rotationPointY = bodyPtY;
			bipedBack.rotationPointY = backPtY;
			bipedRightArm.rotationPointY = armPtY;
			bipedLeftArm.rotationPointY = bipedRightArm.rotationPointY;
			bipedRightLeg.rotationPointY = legPtY;
			bipedLeftLeg.rotationPointY = bipedRightLeg.rotationPointY;
		}
	}

	@Override
	public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		setRotationAngles(par2, par3, par4, par5, par6, par7, entity);

		if (bipedHead != null)
		{
			bipedHead.render(par7);
		}

		if (bipedBody != null)
		{
			bipedBody.render(par7);
		}

		if (bipedRightArm != null)
		{
			bipedRightArm.render(par7);
		}

		if (bipedLeftArm != null)
		{
			bipedLeftArm.render(par7);
		}

		if (bipedRightLeg != null)
		{
			bipedRightLeg.render(par7);
		}

		if (bipedLeftLeg != null)
		{
			bipedLeftLeg.render(par7);
		}

		if (bipedBack != null)
		{
			bipedBack.render(par7);
		}
	}

	@Override
	public void renderEars(float par1) {}

	@Override
	public void renderCloak(float par1) {}
}