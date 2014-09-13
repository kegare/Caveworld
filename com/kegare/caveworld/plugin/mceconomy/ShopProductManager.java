/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.mceconomy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;
import shift.mceconomy2.api.shop.IProductItem;
import shift.mceconomy2.api.shop.IProductList;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.ArrayListExtended;

import cpw.mods.fml.common.registry.GameData;

public class ShopProductManager implements IProductList
{
	private static ShopProductManager instance;

	public static ShopProductManager instance()
	{
		if (instance == null)
		{
			instance = new ShopProductManager();
		}

		return instance;
	}

	private final ArrayListExtended<ShopProduct> SHOP_PRODUCTS = new ArrayListExtended();

	@Override
	public String getProductListName()
	{
		return StatCollector.translateToLocal("caveworld.shop.title");
	}

	public boolean addShopProduct(ShopProduct product)
	{
		boolean flag = product == null || product.getProductItem() == null;
		String item = flag ? null : GameData.getItemRegistry().getNameForObject(product.getProductItem().getItem());
		int stack = flag ? -1 : product.getProductItem().stackSize;
		int damage = flag ? -1 : product.getProductItem().getItemDamage();
		int cost = flag ? -1 : product.getcost();

		String name = Integer.toString(getItemProductSize());

		if (flag && !MCEconomyPlugin.shopCfg.hasCategory(name))
		{
			return false;
		}

		String category = "shop";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = MCEconomyPlugin.shopCfg.get(name, "item", "");
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(Config.selectItemEntryClass);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(item)) prop.set(item);
		propOrder.add(prop.getName());
		item = prop.getString();
		if (!GameData.getItemRegistry().containsKey(Strings.nullToEmpty(item))) return false;
		prop = MCEconomyPlugin.shopCfg.get(name, "itemDamage", 0);
		prop.setMinValue(0).setMaxValue(Short.MAX_VALUE).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (damage >= 0) prop.set(MathHelper.clamp_int(damage, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		damage = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = MCEconomyPlugin.shopCfg.get(name, "stackSize", 1);
		prop.setMinValue(0).setMaxValue(64).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (stack >= 0) prop.set(MathHelper.clamp_int(stack, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		stack = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = MCEconomyPlugin.shopCfg.get(name, "productCost", 10);
		prop.setMinValue(0).setMaxValue(MCEconomyPlugin.Player_MP_MAX).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (cost >= 0) prop.set(MathHelper.clamp_int(cost, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		cost = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		MCEconomyPlugin.shopCfg.setCategoryPropertyOrder(name, propOrder);

		if (flag)
		{
			product = new ShopProduct(new ItemStack(GameData.getItemRegistry().getObject(item), stack, damage), cost);
		}

		return SHOP_PRODUCTS.addIfAbsent(product);
	}

	@Override
	public void addItemProduct(IProductItem product)
	{
		addShopProduct((ShopProduct)product);
	}


	@Override
	public int getItemProductSize()
	{
		return SHOP_PRODUCTS.size();
	}

	public List<ShopProduct> getShopProducts()
	{
		return SHOP_PRODUCTS;
	}

	@Override
	public ArrayList<IProductItem> getProductList()
	{
		return new ArrayList(getShopProducts());
	}

	public void clearShopProducts()
	{
		SHOP_PRODUCTS.clear();
	}

	public static class ShopProduct implements IProductItem
	{
		private ItemStack itemstack;
		private int productCost;

		public ShopProduct(ItemStack itemstack, int cost)
		{
			this.itemstack = itemstack;
			this.productCost = cost;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ShopProduct)
			{
				ShopProduct entry = (ShopProduct)obj;

				return ItemStack.areItemStacksEqual(getProductItem(), entry.getProductItem()) && getcost() == entry.getcost();
			}

			return false;
		}

		@Override
		public int hashCode()
		{
			int hash = getcost();
			ItemStack item = getProductItem();

			if (item != null)
			{
				hash += Objects.hashCode(item.getItem(), item.stackSize, item.getItemDamage());
			}

			return hash;
		}

		public ItemStack setProductItem(ItemStack item)
		{
			return itemstack = item;
		}

		@Override
		public ItemStack getProductItem()
		{
			return itemstack == null || itemstack.getItem() == null ? null : itemstack.copy();
		}

		public int setCost(int cost)
		{
			return productCost = cost;
		}

		@Override
		public int getcost()
		{
			return productCost;
		}
	}
}