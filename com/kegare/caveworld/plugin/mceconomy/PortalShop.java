/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.plugin.mceconomy;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;
import shift.mceconomy2.api.shop.IProductItem;
import shift.mceconomy2.api.shop.IProductList;
import shift.mceconomy2.api.shop.ProductList;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.core.Caveworld;

public class PortalShop
{
	public static final IProductList productList = new ProductList()
	{
		@Override
		public String getProductListName()
		{
			return StatCollector.translateToLocal("caveworld.shop.title");
		}
	};

	public static boolean addProductWithConfig(String name, IProductItem product)
	{
		ItemStack itemstack = product.getProductItem();

		if (itemstack == null)
		{
			return false;
		}

		String item = Item.itemRegistry.getNameForObject(itemstack.getItem());
		int damage = itemstack.getItemDamage();
		int stack = itemstack.stackSize;
		int cost = product.getcost();

		return addProductWithConfig(name, item, damage, stack, cost);
	}

	public static boolean addProductWithConfig(String name, String item, int damage, int stack, int cost)
	{
		String category = "shop";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = MCEconomyPlugin.shopCfg.get(name, "item", "");
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = "Specify the item name (or block name) of this product.";
		if (!Strings.isNullOrEmpty(item) && !item.equals(prop.getString())) prop.set(item);
		propOrder.add(prop.getName());
		item = prop.getString();
		prop = MCEconomyPlugin.shopCfg.get(name, "itemDamage", 0);
		prop.setMinValue(0).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = "Specify the item damage (or block metadata) of this product.";
		if (damage >= 0 && damage != prop.getInt()) prop.set(damage);
		propOrder.add(prop.getName());
		damage = prop.getInt();
		prop = MCEconomyPlugin.shopCfg.get(name, "stackSize", 1);
		prop.setMinValue(0).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = "Specify the stack size of this product.";
		if (stack >= 0 && stack != prop.getInt()) prop.set(stack);
		propOrder.add(prop.getName());
		stack = prop.getInt();
		prop = MCEconomyPlugin.shopCfg.get(name, "productCost", 10);
		prop.setMinValue(0).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = "Specify the product cost of this product.";
		if (cost >= 0 && cost != prop.getInt()) prop.set(cost);
		propOrder.add(prop.getName());
		cost = prop.getInt();

		MCEconomyPlugin.shopCfg.setCategoryPropertyOrder(name, propOrder);

		if (Item.itemRegistry.containsKey(item))
		{
			return productList.getProductList().add(new CaveProduct(item, stack, damage, cost));
		}

		return false;
	}

	public static boolean addProductFromConfig(String name)
	{
		return addProductWithConfig(name, null, -1, -1, -1);
	}

	public static boolean removeProduct(IProductItem product)
	{
		return productList.getProductList().remove(product);
	}

	public static boolean removeProductWithConfig(String name, IProductItem product)
	{
		return removeProductFromConfig(name) && removeProduct(product);
	}

	public static boolean removeProductFromConfig(String name)
	{
		if (MCEconomyPlugin.shopCfg.hasCategory(name))
		{
			MCEconomyPlugin.shopCfg.removeCategory(MCEconomyPlugin.shopCfg.getCategory(name));

			return true;
		}

		return false;
	}

	public static class CaveProduct implements IProductItem
	{
		private ItemStack itemstack;
		private int productCost;

		public CaveProduct(ItemStack itemstack, int cost)
		{
			this.itemstack = itemstack;
			this.productCost = Math.max(cost, 0);
		}

		public CaveProduct(String name, int damage, int stack, int cost)
		{
			this(new ItemStack((Item)Item.itemRegistry.getObject(name), damage, stack), cost);
		}

		@Override
		public ItemStack getProductItem()
		{
			return itemstack == null || itemstack.getItem() == null ? null : itemstack.copy();
		}

		@Override
		public int getcost()
		{
			return productCost;
		}
	}
}