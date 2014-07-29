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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import shift.mceconomy2.api.MCEconomyAPI;
import shift.mceconomy2.api.shop.IProductItem;
import shift.mceconomy2.api.shop.ProductItem;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.config.Config;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public final class MCEconomyPlugin
{
	public static final String MODID = "mceconomy2";

	public static Configuration shopCfg;
	public static int SHOP = -1;

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void syncShopCfg()
	{
		if (shopCfg == null)
		{
			shopCfg = Config.loadConfig("shop");
		}

		List<IProductItem> products = Lists.newArrayList();

		if (shopCfg.getCategoryNames().isEmpty())
		{
			if (Config.hardcore)
			{
				products.add(new ProductItem(new ItemStack(Items.bread, 6), 30));
				products.add(new ProductItem(new ItemStack(Blocks.sapling, 1, 0), 15));
				products.add(new ProductItem(new ItemStack(Blocks.sapling, 1, 1), 15));
				products.add(new ProductItem(new ItemStack(Blocks.sapling, 1, 2), 15));
				products.add(new ProductItem(new ItemStack(Blocks.sapling, 1, 3), 15));
				products.add(new ProductItem(new ItemStack(Items.wheat_seeds, 10), 10));
				products.add(new ProductItem(new ItemStack(Items.bed), 100));
			}

			products.add(new ProductItem(new ItemStack(Blocks.torch, 64), 50));
			products.add(new ProductItem(new ItemStack(Items.iron_sword), 100));
			products.add(new ProductItem(new ItemStack(Items.iron_pickaxe), 150));
			products.add(new ProductItem(new ItemStack(Items.iron_axe), 150));
			products.add(new ProductItem(new ItemStack(Items.iron_shovel), 50));
			products.add(new ProductItem(new ItemStack(Items.iron_hoe), 100));

			if (CaveBlocks.rope != null)
			{
				products.add(new ProductItem(new ItemStack(CaveBlocks.rope, 5), 10));
			}

			for (IProductItem product : products)
			{
				PortalShop.addProductWithConfig(Integer.toString(PortalShop.productList.getItemProductSize()), product);
			}
		}
		else
		{
			ConfigCategory category;

			for (String name : shopCfg.getCategoryNames())
			{
				category = shopCfg.getCategory(name);

				if (Strings.isNullOrEmpty(category.get("item").getString()))
				{
					shopCfg.removeCategory(category);
				}
				else
				{
					PortalShop.addProductFromConfig(name);
				}
			}
		}

		if (shopCfg.hasChanged())
		{
			shopCfg.save();
		}
	}

	@Method(modid = MODID)
	protected void invoke()
	{
		syncShopCfg();

		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.caveworld_portal), 2000);

		if (CaveBlocks.rope != null)
		{
			MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.rope), 2);
		}

		if (!PortalShop.productList.getProductList().isEmpty())
		{
			SHOP = MCEconomyAPI.registerProductList(PortalShop.productList);
		}
	}
}