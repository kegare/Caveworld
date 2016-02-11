/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.renderer;

import org.lwjgl.opengl.GL11;

import caveworld.block.IBlockRenderOverlay;
import caveworld.core.Config;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderBlockOverlay implements ISimpleBlockRenderingHandler
{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		IIcon icon;
		IIcon overlay = null;

		if (Config.oreRenderOverlay && block instanceof IBlockRenderOverlay)
		{
			overlay = ((IBlockRenderOverlay)block).getOverlayIcon(metadata);
		}

		if (overlay == null)
		{
			icon = renderer.getBlockIconFromSideAndMetadata(block, 0, metadata);
		}
		else
		{
			icon = ((IBlockRenderOverlay)block).getBaseIcon(metadata);

			if (icon == null)
			{
				icon = renderer.getBlockIcon(Blocks.stone);
			}
			else
			{
				icon = renderer.getIconSafe(icon);
			}

			overlay = renderer.getIconSafe(overlay);
		}

		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
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

		if (overlay != null)
		{
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, overlay);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, overlay);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, overlay);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, overlay);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, overlay);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, overlay);
			tessellator.draw();
		}

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		IIcon icon;
		IIcon overlay = null;
		int meta = world.getBlockMetadata(x, y, z);

		if (Config.oreRenderOverlay && block instanceof IBlockRenderOverlay)
		{
			overlay = ((IBlockRenderOverlay)block).getOverlayIcon(meta);
		}

		if (overlay == null)
		{
			icon = renderer.getBlockIcon(block, world, x, y, z, 0);
		}
		else
		{
			icon = ((IBlockRenderOverlay)block).getBaseIcon(meta);

			if (icon == null)
			{
				icon = renderer.getBlockIcon(Blocks.stone);
			}
			else
			{
				icon = renderer.getIconSafe(icon);
			}

			overlay = renderer.getIconSafe(overlay);
		}

		renderer.setOverrideBlockTexture(icon);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.clearOverrideBlockTexture();

		if (overlay != null)
		{
			renderer.setOverrideBlockTexture(overlay);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.clearOverrideBlockTexture();
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return Config.RENDER_TYPE_OVERLAY;
	}
}