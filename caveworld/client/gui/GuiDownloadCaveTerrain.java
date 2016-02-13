/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import caveworld.util.PanoramaPaths;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiDownloadCaveTerrain extends GuiDownloadTerrain
{
	private static final Random random = new Random();

	private final DynamicTexture viewportTexture;
	private final ResourceLocation panoramaBackground;

	private long prevTime;

	public PanoramaPaths currentPanoramaPaths;

	protected static int panoramaTimer;

	public GuiDownloadCaveTerrain(NetHandlerPlayClient handler)
	{
		super(handler);
		this.mc = FMLClientHandler.instance().getClient();
		this.viewportTexture = new DynamicTexture(256, 256);
		this.panoramaBackground = mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
		this.prevTime = Minecraft.getSystemTime();
	}

	public PanoramaPaths getPanoramaPaths()
	{
		if (GuiListSlot.panoramaPaths.isEmpty())
		{
			currentPanoramaPaths = null;
		}
		else if (currentPanoramaPaths == null)
		{
			currentPanoramaPaths = GuiListSlot.panoramaPaths.get(random.nextInt(GuiListSlot.panoramaPaths.size()), null);
		}

		return currentPanoramaPaths;
	}

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
			GL11.glRotatef(MathHelper.sin((panoramaTimer + ticks) / 400.0F) * 15.0F + 10.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-(panoramaTimer + ticks) * 0.05F, 0.0F, 1.0F, 0.0F);

			for (int l = 0; l < 6; ++l)
			{
				GL11.glPushMatrix();

				switch (l)
				{
					case 1:
						GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 2:
						GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 3:
						GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 4:
						GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
						break;
					case 5:
						GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
						break;
				}

				mc.getTextureManager().bindTexture(getPanoramaPaths().getPath(l));
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
		long time = Minecraft.getSystemTime() - prevTime;

		if (getPanoramaPaths() != null)
		{
			if (time > 200L)
			{
				++panoramaTimer;
			}

			GL11.glDisable(GL11.GL_ALPHA_TEST);
			renderSkybox(partialTicks);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
		else
		{
			drawBackground(0);
		}

		if (time > 500L)
		{
			drawCenteredString(fontRendererObj, getInfoText(), width / 2, height / 2 + 40, 0xFFFFFF);
		}

		if (time > 2000L)
		{
			drawCenteredString(fontRendererObj, getSubText(), width / 2, height / 2 + 65, 0xCCCCCC);
		}
	}

	public String getInfoText()
	{
		return I18n.format("multiplayer.downloadingTerrain");
	}

	public String getSubText()
	{
		return I18n.format("caveworld.terrain.wait");
	}
}