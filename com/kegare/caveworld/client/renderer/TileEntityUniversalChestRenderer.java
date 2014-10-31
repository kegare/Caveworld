/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.TileEntityUniversalChest;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TileEntityUniversalChestRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler
{
	private static final TileEntityUniversalChest tileEntityChestDammy = new TileEntityUniversalChest();
	private final ModelChest model = new ModelChest();

	public void renderChest(int direction, float lidAngle, float prevLidAngle, double x, double y, double z, float ticks)
	{
		bindTexture(new ResourceLocation("caveworld", "textures/entity/chest/universal_chest.png"));

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		short rotate = 0;

		switch (direction)
		{
			case 2:
				rotate = 180;
				break;
			case 3:
				rotate = 0;
				break;
			case 4:
				rotate = 90;
				break;
			case 5:
				rotate = -90;
				break;
		}

		GL11.glRotatef(rotate, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		float lidangle = prevLidAngle + (lidAngle - prevLidAngle) * ticks;
		lidangle = 1.0F - lidangle;
		lidangle = 1.0F - lidangle * lidangle * lidangle;

		model.chestLid.rotateAngleX = -(lidangle * 3.141593F / 2.0F);
		model.renderAll();

		GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity raw, double x, double y, double z, float ticks)
	{
		TileEntityUniversalChest entity = (TileEntityUniversalChest)raw;
		int direction = 0;

		if (entity.hasWorldObj())
		{
			direction = entity.getBlockMetadata();
		}

		renderChest(direction, entity.lidAngle, entity.prevLidAngle, x, y, z, ticks);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		TileEntityRendererDispatcher.instance.renderTileEntityAt(tileEntityChestDammy, 0.0D, 0.0D, 0.0D, 0.0F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return Config.RENDER_TYPE_CHEST;
	}
}