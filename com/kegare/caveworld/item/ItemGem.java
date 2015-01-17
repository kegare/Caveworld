/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGem extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] gemIcons;

	public ItemGem(String name)
	{
		this.setUnlocalizedName(name);
		this.setCreativeTab(Caveworld.tabCaveworld);
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		switch (itemstack.getItemDamage())
		{
			case 0:
				return getUnlocalizedName() + ".aquamarine";
			default:
				return super.getUnlocalizedName(itemstack);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		gemIcons = new IIcon[1];
		gemIcons[0] = iconRegister.registerIcon("caveworld:aquamarine");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		if (damage < 0 || damage >= gemIcons.length)
		{
			return super.getIconFromDamage(damage);
		}

		return gemIcons[damage];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < gemIcons.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}