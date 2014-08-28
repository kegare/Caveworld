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

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.client.config.CaveCategoryEntry;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.plugin.mceconomy.PortalShop.CaveProduct;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.StringEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShopEntry extends CaveCategoryEntry
{
	public ShopEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return MCEconomyPlugin.shopCfg;
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.add(new DummyCategoryElement("addProductEntry", Caveworld.CONFIG_LANG + "shop.add", AddProductEntry.class));

		for (String category : getConfig().getCategoryNames())
		{
			list.add(new ShopElement(getConfig().getCategory(category)));
		}

		return list;
	}

	public static class ShopElement extends ConfigElement
	{
		private final String item;
		private final int itemDamage;
		private final int stackSize;
		private final int productCost;

		public ShopElement(ConfigCategory category)
		{
			super(category);
			this.item = category.get("item").getString();
			this.itemDamage = category.get("itemDamage").getInt(0);
			this.stackSize = category.get("stackSize").getInt(1);
			this.productCost = category.get("productCost").getInt(10);
		}

		@Override
		public String getComment()
		{
			List<String> list = Lists.newArrayList();
			list.add(item);
			list.add(Integer.toString(itemDamage));
			list.add(Integer.toString(stackSize));
			list.add(Integer.toString(productCost));

			return Joiner.on(", ").skipNulls().join(list);
		}
	}

	public static class AddProductEntry extends ShopEntry
	{
		public AddProductEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected List<IConfigElement> getConfigElements()
		{
			List<IConfigElement> list = Lists.newArrayList();

			list.add(new ConfigElement<String>(new Property("productName", "New Product", Property.Type.STRING, Caveworld.CONFIG_LANG + "shop.productName").setConfigEntryClass(ShopProductEntry.class)));
			list.add(new ConfigElement<String>(new Property("item", "", Property.Type.STRING, Caveworld.CONFIG_LANG + "shop.item")));
			list.add(new ConfigElement<Integer>(new Property("itemDamage", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "shop.itemDamage").setMinValue(0)));
			list.add(new ConfigElement<Integer>(new Property("stackSize", "1", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "shop.stackSize").setMinValue(0).setMaxValue(64)));
			list.add(new ConfigElement<Integer>(new Property("productCost", "10", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "shop.productCost").setMinValue(0).setMaxValue(MCEconomyPlugin.Player_MP_MAX)));

			return list;
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, owningScreen.configID + ".add",
					configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
					GuiConfig.getAbridgedConfigPath(getConfig().toString()));
		}
	}

	public static class ShopProductEntry extends StringEntry
	{
		public ShopProductEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
		{
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		public void keyTyped(char eventChar, int eventKey)
		{
			super.keyTyped(eventChar, eventKey);

			if (enabled() && eventKey == Keyboard.KEY_DELETE && (owningScreen.configID == null || !owningScreen.configID.endsWith(".add")))
			{
				for (IConfigElement element : owningScreen.configElements)
				{
					switch (element.getName())
					{
						case "item":
							element.set("");
							break;
						case "stackSize":
							element.set(0);
							break;
					}
				}

				mc.displayGuiScreen(owningScreen.parentScreen);
			}
		}

		@Override
		public void onGuiClosed()
		{
			String name = null;
			String item = null;
			int damage = 0;
			int stack = 1;
			int cost = 10;

			owningEntryList.saveConfigElements();

			for (IConfigElement element : owningScreen.configElements)
			{
				switch (element.getName())
				{
					case "productName":
						name = element.get().toString();
						break;
					case "item":
						item = element.get().toString();
						break;
					case "itemDamage":
						damage = Integer.parseInt(element.get().toString());
						break;
					case "stackSize":
						stack = Integer.parseInt(element.get().toString());
						break;
					case "productCost":
						cost = Integer.parseInt(element.get().toString());
						break;
				}
			}

			if (!Strings.isNullOrEmpty(owningScreen.configID) && owningScreen.configID.endsWith(".add"))
			{
				if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(item) || stack <= 0)
				{
					return;
				}

				CaveProduct product = new CaveProduct(item, damage, stack, cost);

				if (PortalShop.addProductWithConfig(name, product) && owningScreen.parentScreen instanceof GuiConfig)
				{
					GuiConfig parent = (GuiConfig)owningScreen.parentScreen;
					boolean found = false;

					for (IConfigElement entry : parent.configElements)
					{
						if (entry.getName().equals(name))
						{
							found = true;
						}
					}

					if (!found)
					{
						parent.configElements.add(new ShopElement(MCEconomyPlugin.shopCfg.getCategory(name)));
					}

					parent.needsRefresh = true;
					parent.initGui();
				}
			}
			else
			{
				name = owningScreen.titleLine2;

				if (!Strings.isNullOrEmpty(name) && (Strings.isNullOrEmpty(item) || stack <= 0))
				{
					if (PortalShop.removeProductFromConfig(name) && owningScreen.parentScreen instanceof GuiConfig)
					{
						GuiConfig parent = (GuiConfig)owningScreen.parentScreen;

						for (Iterator<IConfigElement> elements = parent.configElements.iterator(); elements.hasNext();)
						{
							if (elements.next().getName().equals(name))
							{
								elements.remove();
							}
						}

						parent.needsRefresh = true;
						parent.initGui();
					}
				}
			}
		}
	}
}