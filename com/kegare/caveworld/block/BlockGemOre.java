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

import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.item.CaveItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGemOre extends BlockOre
{
	private final Random random = new Random();

	@SideOnly(Side.CLIENT)
	private IIcon[] oreIcons;

	public BlockGemOre(String name)
	{
		super();
		this.setBlockName(name);
		this.setResistance(5.0F);
		this.setHarvestLevel("pickaxe", 1);
		this.setHarvestLevel("pickaxe", 2, 0);
		this.setHarvestLevel("pickaxe", 2, 1);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune)
	{
		switch (metadata)
		{
			case 0:
				return CaveItems.gem;
			default:
				return Item.getItemFromBlock(this);
		}
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int bonus)
	{
		switch (metadata)
		{
			case 0:
				return MathHelper.getRandomIntegerInRange(random, 2, 6);
			default:
				return 0;
		}
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		switch (meta)
		{
			case 0:
				return 3.0F;
			case 1:
				return 4.5F;
			default:
				return super.getBlockHardness(world, x, y, z);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		switch (meta)
		{
			case 0:
			case 1:
				return 5;
			default:
				return super.getLightValue(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		oreIcons = new IIcon[2];
		oreIcons[0] = iconRegister.registerIcon("caveworld:aquamarine_ore");
		oreIcons[1] = iconRegister.registerIcon("caveworld:aquamarine_block");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		if (metadata < 0 || metadata >= oreIcons.length)
		{
			return super.getIcon(side, metadata);
		}

		return oreIcons[metadata];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < oreIcons.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}