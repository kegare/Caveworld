/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.renderer;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.Config;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderPortalCaveworld implements ISimpleBlockRenderingHandler
{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if (getRenderId() == modelId)
		{
			Tessellator tessellator = Tessellator.instance;
			IIcon icon = renderer.getBlockIcon(block);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			renderer.setRenderBounds(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
			tessellator.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (getRenderId() == modelId)
		{
			renderer.setOverrideBlockTexture(CaveBlocks.caveworld_portal.portalIcon);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.clearOverrideBlockTexture();

			return true;
		}

		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return getRenderId() == modelId;
	}

	@Override
	public int getRenderId()
	{
		return Config.RENDER_TYPE_PORTAL;
	}
}