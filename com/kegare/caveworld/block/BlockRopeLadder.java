/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import static net.minecraftforge.common.util.ForgeDirection.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRopeLadder extends Block
{
	public BlockRopeLadder(String name)
	{
		super(BlockRope.rope);
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:rope_ladder");
		this.setHardness(0.25F);
		this.setStepSound(soundTypeCloth);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemIconName()
	{
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);

		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		updateBlockBounds(blockAccess.getBlockMetadata(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);

		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		if (side <= 1)
		{
			return false;
		}

		return true;
	}

	public void updateBlockBounds(int meta)
	{
		float f = 0.125F;

		if (meta == 2)
		{
			setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		}

		if (meta == 3)
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}

		if (meta == 4)
		{
			setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}

		if (meta == 5)
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		int i = meta;

		if ((meta == 0 || side == 2) && world.isSideSolid(x, y, z + 1, NORTH))
		{
			i = 2;
		}

		if ((i == 0 || side == 3) && world.isSideSolid(x, y, z - 1, SOUTH))
		{
			i = 3;
		}

		if ((i == 0 || side == 4) && world.isSideSolid(x + 1, y, z, WEST))
		{
			i = 4;
		}

		if ((i == 0 || side == 5) && world.isSideSolid(x - 1, y, z, EAST))
		{
			i = 5;
		}

		return i;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return !world.isAirBlock(x, y + 1, z);
	}

	@Override
	public boolean func_149698_L()
	{
		return false;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		super.registerBlockIcons(iconRegister);

		sideIcon = iconRegister.registerIcon(getTextureName() + "_side");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 2 || meta == 3)
		{
			if (side == 2 || side == 3)
			{
				return super.getIcon(world, x, y, z, side);
			}

			return sideIcon;
		}

		if (meta == 4 || meta == 5)
		{
			if (side == 4 || side == 5)
			{
				return super.getIcon(world, x, y, z, side);
			}

			return sideIcon;
		}

		return super.getIcon(world, x, y, z, side);
	}
}