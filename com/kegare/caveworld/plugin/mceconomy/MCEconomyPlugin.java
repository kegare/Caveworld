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
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import shift.mceconomy2.api.MCEconomyAPI;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.CaveModPlugin.ModPlugin;
import com.kegare.caveworld.plugin.mceconomy.PortalShop.CaveProduct;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

@ModPlugin(modid = MCEconomyPlugin.MODID)
public final class MCEconomyPlugin
{
	public static final String MODID = "mceconomy2";

	public static Configuration shopCfg;
	public static int SHOP = -1;

	public static int Player_MP_MAX = 1000000;

	public static Class productEntryClass;

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

		Map<String, CaveProduct> products = Maps.newHashMap();

		if (shopCfg.getCategoryNames().isEmpty())
		{
			products.put("Bread", new CaveProduct(new ItemStack(Items.bread, 6), 30));
			products.put("Oak Sapling", new CaveProduct(new ItemStack(Blocks.sapling, 1, 0), 15));
			products.put("Spruce Sapling", new CaveProduct(new ItemStack(Blocks.sapling, 1, 1), 15));
			products.put("Birch Sapling", new CaveProduct(new ItemStack(Blocks.sapling, 1, 2), 15));
			products.put("Jungle Sapling", new CaveProduct(new ItemStack(Blocks.sapling, 1, 3), 15));
			products.put("Acacia Sapling", new CaveProduct(new ItemStack(Blocks.sapling, 1, 4), 18));
			products.put("Dark Oak Sapling", new CaveProduct(new ItemStack(Blocks.sapling, 1, 5), 18));
			products.put("Seeds", new CaveProduct(new ItemStack(Items.wheat_seeds, 10), 10));
			products.put("Bed", new CaveProduct(new ItemStack(Items.bed), 100));
			products.put("Torch", new CaveProduct(new ItemStack(Blocks.torch, 64), 50));
			products.put("Iron Sword", new CaveProduct(new ItemStack(Items.iron_sword), 100));
			products.put("Iron Pickaxe", new CaveProduct(new ItemStack(Items.iron_pickaxe), 150));
			products.put("Iron Axe", new CaveProduct(new ItemStack(Items.iron_axe), 150));
			products.put("Iron Shovel", new CaveProduct(new ItemStack(Items.iron_shovel), 50));
			products.put("Iron Hoe", new CaveProduct(new ItemStack(Items.iron_hoe), 100));

			if (Config.rope)
			{
				products.put("Rope", new CaveProduct(new ItemStack(CaveBlocks.rope, 5), 10));
			}

			for (Entry<String, CaveProduct> entry : products.entrySet())
			{
				PortalShop.addProductWithConfig(entry.getKey(), entry.getValue());
			}
		}
		else
		{
			ConfigCategory category;

			for (String name : shopCfg.getCategoryNames())
			{
				try
				{
					category = shopCfg.getCategory(name);

					if (Strings.isNullOrEmpty(category.get("item").getString()) || category.get("stackSize").getInt() <= 0)
					{
						shopCfg.removeCategory(category);
					}
					else
					{
						PortalShop.addProductFromConfig(name);
					}
				}
				catch (Exception e)
				{
					continue;
				}
			}
		}

		if (shopCfg.hasChanged())
		{
			shopCfg.save();
		}
	}

	@Method(modid = MODID)
	private void invoke()
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

		if (!PortalShop.productList.getProductList().isEmpty())
		{
			SHOP = MCEconomyAPI.registerProductList(PortalShop.productList);
		}
	}
}