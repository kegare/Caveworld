/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.breaker.BreakPos;
import com.kegare.caveworld.util.comparator.BreakPosComparator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemOreCompass extends Item
{
	@SideOnly(Side.CLIENT)
	protected IIcon[] compassIcons;

	@SideOnly(Side.CLIENT)
	private long prevFindTime;
	@SideOnly(Side.CLIENT)
	private BreakPos nearestOrePos;

	public ItemOreCompass(String name)
	{
		super();
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:ore_compass");
		this.setMaxStackSize(1);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		compassIcons = new IIcon[32];

		for (int i = 0; i < compassIcons.length; ++i)
		{
			compassIcons[i] = iconRegister.registerIcon(getIconString() + "_" + i);
		}

		itemIcon = compassIcons[0];
	}

	@SideOnly(Side.CLIENT)
	protected int getCompassIconIndex(ItemStack itemstack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.theWorld == null || mc.thePlayer == null)
		{
			return -1;
		}

		if (nearestOrePos != null && !nearestOrePos.isPlaced())
		{
			NBTTagCompound nbt = itemstack.getTagCompound();

			if (nbt == null)
			{
				nbt = new NBTTagCompound();

				itemstack.setTagCompound(nbt);
			}

			int max = compassIcons.length;
			double angle = nbt.getDouble("angle");
			double delta = nbt.getDouble("delta");

			if (nbt.getLong("time") != mc.theWorld.getWorldTime())
			{
				nbt.setLong("time", mc.theWorld.getWorldTime());

				double dir;
				double vec;

				if (mc.theWorld.provider.dimensionId != nearestOrePos.world.provider.dimensionId)
				{
					dir = Math.random() * 360.0D;
				}
				else
				{
					if (itemstack.isOnItemFrame())
					{
						dir = Math.atan2(nearestOrePos.z + 0.5D - itemstack.getItemFrame().posZ, nearestOrePos.x + 0.5D - itemstack.getItemFrame().posX) * 180.0D / Math.PI + itemstack.getItemFrame().rotationYaw + 90.0D;
					}
					else
					{
						dir = Math.atan2(nearestOrePos.z + 0.5D - mc.thePlayer.posZ, nearestOrePos.x + 0.5D - mc.thePlayer.posX) * 180.0D / Math.PI - mc.thePlayer.rotationYaw + 90.0D;
					}
				}

				vec = dir - angle;

				while (vec < -180.0D)
				{
					vec += 360.0D;
				}

				while (vec >= 180.0)
				{
					vec -= 360.0;
				}

				if (vec >  6.0)
				{
					vec =  6.0;
				}

				if (vec < -6.0)
				{
					vec = -6.0;
				}

				delta = (delta + vec) * 0.8;
				angle += delta;

				while (angle < 0.0)
				{
					angle += 360.0;
				}

				while (angle >= 360.0)
				{
					angle -= 360.0;
				}

				nbt.setDouble("angle", angle);
				nbt.setDouble("delta", delta);
			}

			return MathHelper.floor_double(angle * max / 360.0) % max;
		}

		return -1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int pass)
	{
		if (nearestOrePos == null || nearestOrePos.isPlaced() || System.currentTimeMillis() - prevFindTime >= 1000L)
		{
			findNearestOre();
		}

		if (pass == 0)
		{
			int i = getCompassIconIndex(itemstack);

			if (i >= 0 && i < compassIcons.length)
			{
				return compassIcons[i];
			}
		}

		return super.getIcon(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		findNearestOre();

		return super.onItemRightClick(itemstack, world, player);
	}

	@SideOnly(Side.CLIENT)
	public void findNearestOre()
	{
		prevFindTime = System.currentTimeMillis();

		nearestOrePos = new ForkJoinPool().invoke(new RecursiveTask<BreakPos>()
		{
			@Override
			protected BreakPos compute()
			{
				Minecraft mc = FMLClientHandler.instance().getClient();

				if (mc.theWorld == null || mc.thePlayer == null)
				{
					return null;
				}

				int findDistance = 64;
				int originX = MathHelper.floor_double(mc.thePlayer.posX);
				int originY = MathHelper.floor_double(mc.thePlayer.posY);
				int originZ = MathHelper.floor_double(mc.thePlayer.posZ);
				List<BreakPos> result = Lists.newArrayList();

				for (int x = originX - findDistance - 1; x <= originX + findDistance; ++x)
				{
					for (int z = originZ - findDistance - 1; z <= originZ + findDistance; ++z)
					{
						for (int y = originY - 3; y <= originY + 3; ++y)
						{
							Block block = mc.theWorld.getBlock(x, y, z);
							int meta = mc.theWorld.getBlockMetadata(x, y, z);

							if (block instanceof BlockOre || block instanceof BlockRedstoneOre || CaveworldAPI.getMiningPointAmount(block, meta) > 0)
							{
								result.add(new BreakPos(mc.theWorld, x, y, z));
							}
						}
					}
				}

				if (!result.isEmpty())
				{
					Collections.sort(result, new BreakPosComparator(new BreakPos(mc.theWorld, originX, originY, originZ)));

					return result.get(0);
				}

				return null;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@Override
	public int getRenderPasses(int metadata)
	{
		return 1;
	}
}