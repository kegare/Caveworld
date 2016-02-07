/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import caveworld.block.BlockPervertedLog;
import caveworld.block.CaveBlocks;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.entity.EntityArcherZombie;
import caveworld.entity.EntityCaveman;
import caveworld.entity.EntityCavenicSkeleton;
import caveworld.entity.EntityMasterCavenicSkeleton;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import caveworld.plugin.mceconomy.ShopProductManager.ShopProduct;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import shift.mceconomy2.api.MCEconomyAPI;

public final class MCEconomyPlugin implements ICavePlugin
{
	public static final String MODID = "mceconomy2";

	public static Configuration shopCfg;
	public static int SHOP = -1;
	public static int Player_MP_MAX = 1000000;

	public static ShopProductManager productManager;
	@SideOnly(Side.CLIENT)
	public static ShopProductManager prevProductManager;

	public static boolean pluginState = true;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Method(modid = MODID)
	@Override
	public void invoke()
	{
		productManager = new ShopProductManager();

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
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.rope), 2);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.rope_ladder), 5);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.cavenium_ore, 1, 0), 300);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.cavenium_ore, 1, 1), 800);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.cavenium_ore, 1, 2), 1800);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.cavenium_ore, 1, 3), 4500);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.universal_chest), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.gem_ore, 1, 0), 60);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.gem_ore, 1, 1), 315);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.gem_ore, 1, 2), 50);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.cavern_portal), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.cavenium, 1, 0), 200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.cavenium, 1, 1), 500);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.mining_pickaxe), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.lumbering_axe), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.digging_shovel), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.cavenic_bow), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ore_compass), 2200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.gem, 1, 0), 35);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.aquamarine_pickaxe), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.aquamarine_axe), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.aquamarine_shovel), 40);

		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityCaveman.class, 20);
		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityArcherZombie.class, 4);
		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityCavenicSkeleton.class, 100);
		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityMasterCavenicSkeleton.class, 1500);

		SHOP = MCEconomyAPI.registerShop(productManager);

		Caveworld.network.registerMessage(ProductAdjustMessage.class, ProductAdjustMessage.class, Caveworld.messageNext++, Side.CLIENT);
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
			productManager.clearProducts();
		}

		if (shopCfg.getCategoryNames().isEmpty())
		{
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.bread, 6), 30));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 0), 15));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 1), 15));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 2), 15));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 3), 15));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 4), 18));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.sapling, 1, 5), 18));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.wheat_seeds, 10), 10));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.bed), 100));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Blocks.torch, 64), 50));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.iron_sword), 100));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.iron_pickaxe), 150));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.iron_axe), 150));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.iron_shovel), 50));
			productManager.addShopProduct(new ShopProduct(new ItemStack(Items.iron_hoe), 100));
			productManager.addShopProduct(new ShopProduct(new ItemStack(CaveBlocks.rope, 5), 10));

			for (int i = 0; i < BlockPervertedLog.types.length; ++i)
			{
				productManager.addShopProduct(new ShopProduct(new ItemStack(CaveBlocks.perverted_sapling, 1, i), 10));
			}
		}
		else
		{
			int i = 0;

			for (String name : shopCfg.getCategoryNames())
			{
				if (NumberUtils.isNumber(name))
				{
					productManager.addShopProduct(null);
				}
				else ++i;
			}

			if (i > 0)
			{
				try
				{
					FileUtils.forceDelete(new File(shopCfg.toString()));

					productManager.clearProducts();

					shopCfg = null;
					syncShopCfg();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		if (shopCfg.hasChanged())
		{
			shopCfg.save();
		}
	}
}