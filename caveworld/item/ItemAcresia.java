/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import java.util.List;

import caveworld.block.CaveBlocks;
import caveworld.core.Caveworld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemAcresia extends ItemFood implements IPlantable
{
	@SideOnly(Side.CLIENT)
	private IIcon[] itemIcons;

	public ItemAcresia(String name)
	{
		super(1, 0.0F, false);
		this.setUnlocalizedName(name);
		this.setCreativeTab(Caveworld.tabCaveworld);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (itemstack != null && itemstack.getItemDamage() == 0)
		{
			if (side == 1 && player.canPlayerEdit(x, y, z, side, itemstack) && player.canPlayerEdit(x, y + 1, z, side, itemstack))
			{
				Block block = world.getBlock(x, y, z);

				if (block != Blocks.bedrock && block.canSustainPlant(world, x, y, z, ForgeDirection.UP, this) && world.isAirBlock(x, y + 1, z))
				{
					world.setBlock(x, y + 1, z, getPlant(world, x, y, z));

					--itemstack.stackSize;

					return true;
				}
			}

			return false;
		}

		return super.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (itemstack != null && itemstack.getItemDamage() == 1)
		{
			return super.onItemRightClick(itemstack, world, player);
		}

		return itemstack;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (itemstack != null && itemstack.getItemDamage() == 1)
		{
			return super.onEaten(itemstack, world, player);
		}

		return itemstack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItemDamage() == 1)
		{
			return EnumAction.eat;
		}

		return EnumAction.none;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
	{
		return world.getBlock(x, y - 1, z).isSideSolid(world, x, y - 1, z, ForgeDirection.UP) ? EnumPlantType.Cave : EnumPlantType.Plains;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z)
	{
		return CaveBlocks.acresia_crops;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z)
	{
		return 0;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		switch (itemstack.getItemDamage())
		{
			case 0:
				return "item.seedsAcresia";
			case 1:
				return "item.fruitsAcresia";
		}

		return super.getUnlocalizedName(itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		itemIcons = new IIcon[2];
		itemIcons[0] = iconRegister.registerIcon("caveworld:acresia_seeds");
		itemIcons[1] = iconRegister.registerIcon("caveworld:acresia_fruits");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage)
	{
		if (damage < 0 || damage >= itemIcons.length)
		{
			return super.getIconFromDamage(damage);
		}

		return itemIcons[damage];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < itemIcons.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}