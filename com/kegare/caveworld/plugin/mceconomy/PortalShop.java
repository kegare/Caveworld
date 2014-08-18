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
import net.minecraft.util.MathHelper;
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

	public static boolean addProduct(IProductItem product)
	{
		return productList.getProductList().add(product);
	}

	public static boolean addProductWithConfig(String name, IProductItem product)
	{
		ItemStack itemstack = product == null ? null : product.getProductItem();
		String item = product == null ? null : Item.itemRegistry.getNameForObject(itemstack.getItem());
		int damage = product == null ? -1 : itemstack.getItemDamage();
		int stack = product == null ? -1 : itemstack.stackSize;
		int cost = product == null ? -1 : product.getcost();

		String category = "shop";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = MCEconomyPlugin.shopCfg.get(name, "item", "");
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(MCEconomyPlugin.PRODUCT_ENTRY.orNull());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(item)) prop.set(item);
		propOrder.add(prop.getName());
		item = prop.getString();
		prop = MCEconomyPlugin.shopCfg.get(name, "itemDamage", 0);
		prop.setMinValue(0).setMaxValue(Short.MAX_VALUE).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (damage >= 0) prop.set(MathHelper.clamp_int(damage, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		damage = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = MCEconomyPlugin.shopCfg.get(name, "stackSize", 1);
		prop.setMinValue(0).setMaxValue(64).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (stack >= 0) prop.set(MathHelper.clamp_int(stack, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		stack = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = MCEconomyPlugin.shopCfg.get(name, "productCost", 10);
		prop.setMinValue(0).setMaxValue(MCEconomyPlugin.Player_MP_MAX).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (cost >= 0) prop.set(MathHelper.clamp_int(cost, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		cost = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));

		MCEconomyPlugin.shopCfg.setCategoryPropertyOrder(name, propOrder);

		if (MCEconomyPlugin.shopCfg.hasChanged())
		{
			MCEconomyPlugin.shopCfg.save();
		}

		if (Item.itemRegistry.containsKey(item))
		{
			return addProduct(new CaveProduct(item, stack, damage, cost));
		}

		return false;
	}

	public static boolean addProductFromConfig(String name)
	{
		return addProductWithConfig(name, null);
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

			if (MCEconomyPlugin.shopCfg.hasChanged())
			{
				MCEconomyPlugin.shopCfg.save();
			}

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