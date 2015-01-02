/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRopeLadder extends Block implements IRope
{
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

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
		return getTextureName();
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

		AxisAlignedBB result = super.getSelectedBoundingBoxFromPool(world, x, y, z);

		if (world.getBlockMetadata(x, y, z) > 5)
		{
			return result == null ? null : result.expand(0.05D, 0.0D, 0.05D);
		}

		return result;
	}

	public void updateBlockBounds(int meta)
	{
		float f = 0.3F;

		if (meta > 5)
		{
			meta -= 4;
		}

		if (meta == 2)
		{
			setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 0.65F);
		}

		if (meta == 3)
		{
			setBlockBounds(0.0F, 0.0F, 0.35F, 1.0F, 1.0F, f);
		}

		if (meta == 4)
		{
			setBlockBounds(1.0F - f, 0.0F, 0.0F, 0.65F, 1.0F, 1.0F);
		}

		if (meta == 5)
		{
			setBlockBounds(0.35F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
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

		if ((meta == 0 || side == 2) && world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH))
		{
			i = 2;
		}

		if ((i == 0 || side == 3) && world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH))
		{
			i = 3;
		}

		if ((i == 0 || side == 4) && world.isSideSolid(x + 1, y, z, ForgeDirection.WEST))
		{
			i = 4;
		}

		if ((i == 0 || side == 5) && world.isSideSolid(x - 1, y, z, ForgeDirection.EAST))
		{
			i = 5;
		}

		return i;
	}

	@Override
	public int quantityDropped(int metadata, int fortune, Random random)
	{
		return metadata > 5 ? 1 : 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		if (world.getBlockMetadata(x, y, z) <= 5 && world.getBlock(x, y + 1, z) != this)
		{
			world.setBlockToAir(x, y, z);
		}
		else if (world.isAirBlock(x, y + 1, z) && world.setBlockToAir(x, y, z))
		{
			dropBlockAsItem(world, x, y, z, new ItemStack(this));
		}
	}

	@Override
	public int getKnotMetadata(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (world.getBlock(x, y + 1, z) == this)
		{
			int meta = world.getBlockMetadata(x, y + 1, z);

			if (meta > 5)
			{
				return meta;
			}

			return meta + 4;
		}

		if (side < 2)
		{
			int meta;

			switch (MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3)
			{
				case 1:
					meta = 5;
					break;
				case 2:
					meta = 3;
					break;
				case 3:
					meta = 4;
					break;
				default:
					meta = 2;
					break;
			}

			return meta + 4;
		}

		return onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, 1) + 4;
	}

	@Override
	public void setUnderRopes(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (meta > 5 && world.getBlock(x, y, z) == this && world.isAirBlock(x, y - 1, z) && y - 1 > 0)
		{
			for (int count = 0; count < 5 && world.isAirBlock(x, y - 1, z) && y - 1 > 0; --y)
			{
				if (world.isAirBlock(x, y - 2, z) && world.setBlock(x, y - 1, z, this, meta - 4, 3))
				{
					if (!world.isRemote)
					{
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendToAllNear(x, y - 1, z, 64.0D, world.provider.dimensionId, new S23PacketBlockChange(x, y - 1, z, world));
					}

					++count;
				}
				else return;
			}
		}
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

		if (meta > 5)
		{
			meta -= 4;
		}

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