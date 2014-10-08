/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public abstract class GuiListSlot extends GuiSlot
{
	public static final ResourceLocation[] panoramaPaths = new ResourceLocation[] {
		new ResourceLocation("caveworld", "textures/gui/panorama/panorama_0.png"),
		new ResourceLocation("caveworld", "textures/gui/panorama/panorama_1.png"),
		new ResourceLocation("caveworld", "textures/gui/panorama/panorama_2.png"),
		new ResourceLocation("caveworld", "textures/gui/panorama/panorama_3.png"),
		new ResourceLocation("caveworld", "textures/gui/panorama/panorama_4.png"),
		new ResourceLocation("caveworld", "textures/gui/panorama/panorama_5.png")
	};

	protected final Minecraft mc;

	private final DynamicTexture viewportTexture;
	private final ResourceLocation panoramaBackground;
	private float panoramaTicks;

	private static int panoramaTimer;

	public GuiListSlot(Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
	{
		super(mc, width, height, top, bottom, slotHeight);
		this.mc = mc;
		this.viewportTexture = new DynamicTexture(256, 256);
		this.panoramaBackground = mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
	}

	public abstract ResourceLocation[] getPanoramaPaths();

	private void drawPanorama(float ticks)
	{
		Tessellator tessellator = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		byte b0 = 8;

		for (int k = 0; k < b0 * b0; ++k)
		{
			GL11.glPushMatrix();
			float f1 = ((float)(k % b0) / (float)b0 - 0.5F) / 64.0F;
			float f2 = ((float)(k / b0) / (float)b0 - 0.5F) / 64.0F;
			float f3 = 0.0F;
			GL11.glTranslatef(f1, f2, f3);
			GL11.glRotatef(MathHelper.sin((panoramaTimer + ticks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-(panoramaTimer + ticks) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int l = 0; l < 6; ++l)
			{
				GL11.glPushMatrix();

				if (l == 1)
				{
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 2)
				{
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 3)
				{
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (l == 4)
				{
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (l == 5)
				{
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				mc.getTextureManager().bindTexture(getPanoramaPaths()[l]);
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA_I(16777215, 255 / (k + 1));
				float f4 = 0.0F;
				tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, 0.0F + f4, 0.0F + f4);
				tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, 1.0F - f4, 0.0F + f4);
				tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F - f4, 1.0F - f4);
				tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, 0.0F + f4, 1.0F - f4);
				tessellator.draw();
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glColorMask(true, true, true, false);
		}

		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glColorMask(true, true, true, true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private void rotateAndBlurSkybox(float ticks)
	{
		mc.getTextureManager().bindTexture(panoramaBackground);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		byte b0 = 3;

		for (int i = 0; i < b0; ++i)
		{
			tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (i + 1));
			int j = width;
			int k = height;
			float f1 = (i - b0 / 2) / 256.0F;
			tessellator.addVertexWithUV(j, k, 0.0F, 0.0F + f1, 1.0D);
			tessellator.addVertexWithUV(j, 0.0D, 0.0F, 1.0F + f1, 1.0D);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0F, 1.0F + f1, 0.0D);
			tessellator.addVertexWithUV(0.0D, k, 0.0F, 0.0F + f1, 0.0D);
		}

		tessellator.draw();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColorMask(true, true, true, true);
	}

	private void renderSkybox(float ticks)
	{
		mc.getFramebuffer().unbindFramebuffer();
		GL11.glViewport(0, 0, 256, 256);
		drawPanorama(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		rotateAndBlurSkybox(ticks);
		mc.getFramebuffer().bindFramebuffer(true);
		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float f1 = width > height ? 120.0F / width : 120.0F / height;
		float f2 = height * f1 / 256.0F;
		float f3 = width * f1 / 256.0F;
		tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int k = width;
		int l = height;
		tessellator.addVertexWithUV(0.0D, l, 0.0F, 0.5F - f2, 0.5F + f3);
		tessellator.addVertexWithUV(k, l, 0.0F, 0.5F - f2, 0.5F - f3);
		tessellator.addVertexWithUV(k, 0.0D, 0.0F, 0.5F + f2, 0.5F - f3);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0F, 0.5F + f2, 0.5F + f3);
		tessellator.draw();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		panoramaTicks = partialTicks;

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator)
	{
		if (getPanoramaPaths() != null)
		{
			++panoramaTimer;

			GL11.glDisable(GL11.GL_ALPHA_TEST);
			renderSkybox(panoramaTicks);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
		else super.drawContainerBackground(tessellator);
	}

	public void scrollUp()
	{
		int i = getAmountScrolled() % getSlotHeight();

		if (i == 0)
		{
			scrollBy(-getSlotHeight());
		}
		else
		{
			scrollBy(-i);
		}
	}

	public void scrollDown()
	{
		scrollBy(getSlotHeight() - getAmountScrolled() % getSlotHeight());
	}

	public void scrollToTop()
	{
		scrollBy(-getAmountScrolled());
	}

	public void scrollToEnd()
	{
		scrollBy(getSlotHeight() * getSize());
	}

	public abstract void scrollToSelected();

	public void scrollToPrev()
	{
		scrollBy(-(getAmountScrolled() % getSlotHeight() + (bottom - top) / getSlotHeight() * getSlotHeight()));
	}

	public void scrollToNext()
	{
		scrollBy(getAmountScrolled() % getSlotHeight() + (bottom - top) / getSlotHeight() * getSlotHeight());
	}
}