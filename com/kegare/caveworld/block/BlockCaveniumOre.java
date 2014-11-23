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

import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.item.CaveItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCaveniumOre extends BlockOre
{
	private final Random random = new Random();

	@SideOnly(Side.CLIENT)
	protected IIcon refinedIcon;
	@SideOnly(Side.CLIENT)
	protected IIcon clustIcon;
	@SideOnly(Side.CLIENT)
	protected IIcon clustRefinedIcon;

	public BlockCaveniumOre(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:cavenium_ore");
		this.setHardness(3.5F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypePiston);
		this.setHarvestLevel("pickaxe", 2);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(getTextureName());
		refinedIcon = iconRegister.registerIcon(getTextureName() + ".refined");
		clustIcon = iconRegister.registerIcon("caveworld:cavenium_block");
		clustRefinedIcon = iconRegister.registerIcon("caveworld:cavenium_block.refined");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		switch (metadata)
		{
			case 1:
				return refinedIcon;
			case 2:
				return clustIcon;
			case 3:
				return clustRefinedIcon;
			default:
				return blockIcon;
		}
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune)
	{
		return metadata < 2 ? CaveItems.cavenium : Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata & 3;
	}

	@Override
	public int quantityDropped(int metadata, int fortune, Random random)
	{
		switch (metadata)
		{
			case 0:
				return random.nextInt(10) == 0 ? random.nextInt(10) == 0 ? 3 : 2 : 1;
			case 1:
				return random.nextInt(12) == 0 ? random.nextInt(15) == 0 ? 3 : 2 : 1;
			default:
				return 1;
		}
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int bonus)
	{
		switch (metadata)
		{
			case 0:
				return MathHelper.getRandomIntegerInRange(random, 2, 5);
			case 1:
				return MathHelper.getRandomIntegerInRange(random, 4, 6);
			default:
				return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i <= 3; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}