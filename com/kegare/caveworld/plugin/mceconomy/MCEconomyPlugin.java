/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.mceconomy;

import java.io.File;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.math.NumberUtils;

import shift.mceconomy2.api.MCEconomyAPI;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.mceconomy.ShopProductManager.ShopProduct;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public final class MCEconomyPlugin
{
	public static final String MODID = "mceconomy2";

	public static Configuration shopCfg;
	public static int SHOP = -1;
	public static int Player_MP_MAX = 1000000;

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
		else
		{
			ShopProductManager.instance().clearShopProducts();
		}

		if (shopCfg.getCategoryNames().isEmpty())
		{
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.bread, 6), 30));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 0), 15));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 1), 15));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 2), 15));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 3), 15));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 4), 18));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 5), 18));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.wheat_seeds, 10), 10));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.bed), 100));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Blocks.torch, 64), 50));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.iron_sword), 100));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.iron_pickaxe), 150));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.iron_axe), 150));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.iron_shovel), 50));
			ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(Items.iron_hoe), 100));

			if (Config.rope)
			{
				ShopProductManager.instance().addShopProduct(new ShopProduct(new ItemStack(CaveBlocks.rope, 5), 10));
			}
		}
		else for (String name : shopCfg.getCategoryNames())
		{
			if (NumberUtils.isNumber(name))
			{
				ShopProductManager.instance().addShopProduct(null);
			}
		}

		if (shopCfg.hasChanged())
		{
			shopCfg.save();
		}
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		File file = new File(Loader.instance().getConfigDir(), "mceconomy2.cfg");

		if (file.isFile() && file.exists())
		{
			Configuration config = new Configuration(file);
			ConfigCategory category;

			for (String name : config.getCategoryNames())
			{
				category = config.getCategory(name);

				if (category.containsKey("Player_MP_MAX"))
				{
					Player_MP_MAX = category.get("Player_MP_MAX").getInt(Player_MP_MAX);

					break;
				}
			}
		}

		syncShopCfg();

		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.caveworld_portal), 2000);

		if (Config.rope)
		{
			MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.rope), 2);
		}

		SHOP = MCEconomyAPI.registerProductList(ShopProductManager.instance());
	}
}