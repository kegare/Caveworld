/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Property;
import shift.mceconomy2.api.shop.IProduct;
import shift.mceconomy2.api.shop.IShop;

public class ShopProductManager implements IShop
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
	public String getShopName(World world, EntityPlayer player)
	{
		return "caveworld.shop.title";
	}

	public boolean addShopProduct(ShopProduct product)
	{
		boolean flag = product == null || product.getItem() == null;
		String item = flag ? null : GameData.getItemRegistry().getNameForObject(product.getItem().getItem());
		int stack = flag ? -1 : product.getItem().stackSize;
		int damage = flag ? -1 : product.getItem().getItemDamage();
		int cost = flag ? -1 : product.productCost;

		String name = Integer.toString(SHOP_PRODUCTS.size());

		if (flag && !MCEconomyPlugin.shopCfg.hasCategory(name))
		{
			return false;
		}

		String category = "shop";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = MCEconomyPlugin.shopCfg.get(name, "item", "");
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
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
	public void addProduct(IProduct product)
	{
		addShopProduct((ShopProduct)product);
	}

	public List<ShopProduct> getProducts()
	{
		return SHOP_PRODUCTS;
	}

	@Override
	public ArrayList<IProduct> getProductList(World world, EntityPlayer player)
	{
		return new ArrayList(getProducts());
	}

	public void clearShopProducts()
	{
		SHOP_PRODUCTS.clear();
	}

	public static class ShopProduct implements IProduct
	{
		private ItemStack itemstack;
		private int productCost;

		public ShopProduct(ItemStack itemstack, int cost)
		{
			this.itemstack = itemstack;
			this.productCost = cost;
		}

		public ShopProduct(ShopProduct product)
		{
			this(product.itemstack, product.productCost);
		}

		public ItemStack setItem(ItemStack item)
		{
			return itemstack = item;
		}

		public ItemStack getItem()
		{
			return itemstack;
		}

		@Override
		public ItemStack getItem(IShop shop, World world, EntityPlayer player)
		{
			return itemstack == null || itemstack.getItem() == null ? null : itemstack.copy();
		}

		public int setCost(int cost)
		{
			return productCost = cost;
		}

		public int getCost()
		{
			return productCost;
		}

		@Override
		public int getCost(IShop shop, World world, EntityPlayer player)
		{
			return productCost;
		}

		@Override
		public boolean canBuy(IShop shop, World world, EntityPlayer player)
		{
			return true;
		}
	}
}