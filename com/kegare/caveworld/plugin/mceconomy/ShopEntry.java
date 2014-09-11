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
import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShopEntry extends CategoryEntry
{
	public ShopEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiShopEntry(owningScreen);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		try
		{
			FileUtils.forceDelete(new File(MCEconomyPlugin.shopCfg.toString()));

			ShopProductManager.instance().clearShopProducts();

			MCEconomyPlugin.shopCfg = null;
			MCEconomyPlugin.syncShopCfg();
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		if (childScreen instanceof GuiShopEntry)
		{
			GuiShopEntry gui = (GuiShopEntry)childScreen;

			if (gui.productList != null)
			{
				gui.productList.products.clear();
				gui.productList.products.addAll(ShopProductManager.instance().getShopProducts());
				gui.productList.contents.clear();
				gui.productList.contents.addAll(gui.productList.products);
			}
		}
	}
}