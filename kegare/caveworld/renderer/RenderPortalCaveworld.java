package kegare.caveworld.renderer;

import kegare.caveworld.core.Caveworld;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderPortalCaveworld implements ISimpleBlockRenderingHandler
{
	public static int renderIdPortal = -1;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		Tessellator tessellator = Tessellator.instance;
		Icon icon = renderer.getBlockIcon(block);
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

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		renderer.setOverrideBlockTexture(Caveworld.portalCaveworld.portalIcon);

		if (world.getBlockId(x - 1, y, z) != block.blockID && world.getBlockId(x + 1, y, z) != block.blockID)
		{
			renderer.setRenderBounds(0.35D, 0.0D, 0.0D, 0.65D, 1.0D, 1.0D);
		}
		else
		{
			renderer.setRenderBounds(0.0D, 0.0D, 0.35D, 1.0D, 1.0D, 0.65D);
		}

		renderer.renderStandardBlock(block, x, y, z);
		renderer.clearOverrideBlockTexture();

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return renderIdPortal;
	}
}