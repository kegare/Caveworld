/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRope extends Block implements IRope
{
	public static final Material rope = new MaterialLogic(MapColor.airColor)
	{
		@Override
		public boolean getCanBurn()
		{
			return true;
		}

		@Override
		public boolean isToolNotRequired()
		{
			return true;
		}

		@Override
		public int getMaterialMobility()
		{
			return 2;
		}

		@Override
		public boolean isAdventureModeExempt()
		{
			return false;
		}
	};

	public BlockRope(String name)
	{
		super(rope);
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:rope");
		this.setHardness(0.25F);
		this.setBlockBounds(0.435F, 0.0F, 0.435F, 0.565F, 1.0F, 0.565F);
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
	public int getRenderType()
	{
		return 1;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axis, List list, Entity entity)
	{
		if (entity != null && entity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase)entity;

			if (living.getHeldItem() != null && living.getHeldItem().getItem() == Item.getItemFromBlock(this))
			{
				if (!world.isAirBlock(x + 1, y, z) || !world.isAirBlock(x - 1, y, z) || !world.isAirBlock(x, y, z + 1) || !world.isAirBlock(x, y, z - 1))
				{
					return;
				}
			}

			if (!world.isAirBlock(x, y - 1, z) && world.getBlock(x, y - 1, z) != this || !living.onGround || living.isSneaking() || !living.boundingBox.intersectsWith(axis))
			{
				super.addCollisionBoxesToList(world, x, y, z, axis, list, living);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		AxisAlignedBB result = super.getSelectedBoundingBoxFromPool(world, x, y, z);

		if (world.getBlockMetadata(x, y, z) == 1)
		{
			return result == null ? null : result.expand(0.05D, 0.0D, 0.05D);
		}

		return result;
	}

	@Override
	public int quantityDropped(int metadata, int fortune, Random random)
	{
		return MathHelper.clamp_int(metadata, 0, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
	{
		if (world.getBlockMetadata(x, y, z) == 0 && world.getBlock(x, y + 1, z) != this)
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
		return 1;
	}

	@Override
	public void setUnderRopes(World world, int x, int y, int z)
	{
		if (world.getBlockMetadata(x, y, z) != 0 && world.getBlock(x, y, z) == this && world.isAirBlock(x, y - 1, z) && y - 1 > 0)
		{
			for (int count = 0; count < 5 && world.isAirBlock(x, y - 1, z) && y - 1 > 0; --y)
			{
				if (world.isAirBlock(x, y - 2, z) && world.setBlock(x, y - 1, z, this))
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

	public class DispenceRope extends BehaviorDefaultDispenseItem
	{
		@Override
		public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemstack)
		{
			EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
			World world = blockSource.getWorld();
			int x = blockSource.getXInt() + facing.getFrontOffsetX();
			int y = blockSource.getYInt() + facing.getFrontOffsetY();
			int z = blockSource.getZInt() + facing.getFrontOffsetZ();

			if (world.isAirBlock(x, y, z) && world.setBlock(x, y, z, BlockRope.this, 1, 3))
			{
				setUnderRopes(world, x, y, z);

				--itemstack.stackSize;

				for (int i = 1; itemstack.stackSize > 0 && i < itemstack.stackSize + 1; ++i)
				{
					int next = y - 5 * i;

					if (world.getBlock(x, next, z) == BlockRope.this && world.isAirBlock(x, --next, z) && next > 0)
					{
						if (world.setBlock(x, next, z, BlockRope.this, 1, 3))
						{
							setUnderRopes(world, x, next, z);

							--itemstack.stackSize;
						}
						else break;
					}
				}
			}

			return itemstack;
		}

		@Override
		public void playDispenseSound(IBlockSource blockSource)
		{
			super.playDispenseSound(blockSource);

			blockSource.getWorld().playSoundEffect(blockSource.getXInt(), blockSource.getYInt(), blockSource.getZInt(), CaveBlocks.rope.stepSound.func_150496_b(), 1.0F, 2.0F);
		}
	}
}