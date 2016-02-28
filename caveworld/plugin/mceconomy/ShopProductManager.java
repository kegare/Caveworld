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

import caveworld.api.CaverAPI;
import caveworld.core.CaverManager.MinerRank;
import caveworld.core.Caveworld;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import shift.mceconomy2.api.shop.IProduct;
import shift.mceconomy2.api.shop.IShop;

public class ShopProductManager implements IShopProductManager, IShop
{
	private final List<IShopProduct> PRODUCTS = Lists.newArrayList();

	private boolean readOnly;

	@Override
	public Configuration getConfig()
	{
		return MCEconomyPlugin.shopCfg;
	}

	@Override
	public int getType()
	{
		return 0;
	}

	@Override
	public String getShopName(World world, EntityPlayer player)
	{
		return "caveworld.shop.title";
	}

	@Override
	public boolean isReadOnly()
	{
		return readOnly;
	}

	@Override
	public IShopProductManager setReadOnly(boolean flag)
	{
		readOnly = flag;

		return this;
	}

	@Override
	public boolean addShopProduct(IShopProduct product)
	{
		boolean flag = product == null || product.getItem() == null;
		String item = flag ? null : GameData.getItemRegistry().getNameForObject(product.getItem().getItem());
		int stack = flag ? -1 : product.getItem().stackSize;
		int damage = flag ? -1 : product.getItem().getItemDamage();
		int cost = flag ? -1 : product.getCost();
		int minerRank = flag ? -1 : product.getMinerRank();

		String name = Integer.toString(PRODUCTS.size());

		if (flag && !getConfig().hasCategory(name))
		{
			return false;
		}

		String category = "shop";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = getConfig().get(name, "item", "");
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(item)) prop.set(item);
		propOrder.add(prop.getName());
		item = prop.getString();
		if (!GameData.getItemRegistry().containsKey(Strings.nullToEmpty(item))) return false;
		prop = getConfig().get(name, "itemDamage", 0);
		prop.setMinValue(0).setMaxValue(Short.MAX_VALUE).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (damage >= 0) prop.set(MathHelper.clamp_int(damage, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		damage = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = getConfig().get(name, "stackSize", 1);
		prop.setMinValue(0).setMaxValue(64).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (stack >= 0) prop.set(MathHelper.clamp_int(stack, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		stack = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = getConfig().get(name, "productCost", 10);
		prop.setMinValue(0).setMaxValue(MCEconomyPlugin.Player_MP_MAX).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (cost >= 0) prop.set(MathHelper.clamp_int(cost, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		cost = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));
		prop = getConfig().get(name, "minerRank", 0);
		prop.setMinValue(0).setMaxValue(MinerRank.values().length - 1).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (minerRank >= 0) prop.set(MathHelper.clamp_int(minerRank, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		minerRank = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		getConfig().setCategoryPropertyOrder(name, propOrder);

		if (flag)
		{
			product = new ShopProduct(new ItemStack(GameData.getItemRegistry().getObject(item), stack, damage), cost, minerRank);
		}

		return getProducts().add(product);
	}

	@Override
	public void addProduct(IProduct product)
	{
		addShopProduct((ShopProduct)product);
	}

	@Override
	public List<IShopProduct> getProducts()
	{
		return PRODUCTS;
	}

	@Override
	public ArrayList<IProduct> getProductList(World world, EntityPlayer player)
	{
		return new ArrayList(getProducts());
	}

	@Override
	public void clearProducts()
	{
		getProducts().clear();
	}

	@Override
	public void loadFromNBT(NBTTagList list)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			IShopProduct product = new ShopProduct();
			product.loadFromNBT(list.getCompoundTagAt(i));

			getProducts().add(product);
		}
	}

	@Override
	public NBTTagList saveToNBT()
	{
		NBTTagList list = new NBTTagList();

		for (IShopProduct product : getProducts())
		{
			list.appendTag(product.saveToNBT());
		}

		return list;
	}

	public static class ShopProduct implements IShopProduct, IProduct
	{
		private ItemStack itemstack;
		private int productCost;
		private int minerRank;

		public ShopProduct() {}

		public ShopProduct(ItemStack itemstack, int cost)
		{
			this.itemstack = itemstack;
			this.productCost = cost;
		}

		public ShopProduct(ItemStack itemstack, int cost, int rank)
		{
			this(itemstack, cost);
			this.minerRank = rank;
		}

		public ShopProduct(IShopProduct product)
		{
			this(product.getItem(), product.getCost());
		}

		@Override
		public ItemStack setItem(ItemStack item)
		{
			return itemstack = item;
		}

		@Override
		public ItemStack getItem()
		{
			return itemstack;
		}

		@Override
		public ItemStack getItem(IShop shop, World world, EntityPlayer player)
		{
			return itemstack == null || itemstack.getItem() == null ? null : itemstack.copy();
		}

		@Override
		public int setCost(int cost)
		{
			return productCost = cost;
		}

		@Override
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
		public int setMinerRank(int rank)
		{
			return minerRank = rank;
		}

		@Override
		public int getMinerRank()
		{
			return minerRank;
		}

		@Override
		public boolean canBuy(IShop shop, World world, EntityPlayer player)
		{
			return getMinerRank() <= 0 || CaverAPI.getMinerRank(player) >= getMinerRank();
		}

		@Override
		public void loadFromNBT(NBTTagCompound nbt)
		{
			setItem(ItemStack.loadItemStackFromNBT(nbt));
			setCost(nbt.getInteger("Cost"));
			setMinerRank(nbt.getInteger("MinerRank"));
		}

		@Override
		public NBTTagCompound saveToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();

			getItem().writeToNBT(nbt);
			nbt.setInteger("Cost", getCost());
			nbt.setInteger("MinerRank", getMinerRank());

			return nbt;
		}
	}
}