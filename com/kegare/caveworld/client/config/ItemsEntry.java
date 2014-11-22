/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.google.common.collect.Lists;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemsEntry extends CaveCategoryEntry
{
	public ItemsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.itemsCfg;
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		for (String category : getConfig().getCategoryNames())
		{
			list.addAll(new ItemElement(getConfig().getCategory(category)).getChildElements());
		}

		return list;
	}

	private class ItemElement<T> extends ConfigElement<T>
	{
		private ConfigCategory category;
		private Property prop;
		private boolean isProperty;
		private boolean categoriesFirst = true;

		public ItemElement(ConfigCategory category)
		{
			super(category);
			this.category = category;
			this.isProperty = false;
		}

		public ItemElement(Property prop)
		{
			super(prop);
			this.prop = prop;
			this.isProperty = true;
		}

		@Override
		public ConfigElement<T> listCategoriesFirst(boolean first)
		{
			categoriesFirst = first;

			return super.listCategoriesFirst(first);
		}

		@Override
		public List<IConfigElement> getChildElements()
		{
			if (!isProperty)
			{
				List<IConfigElement> elements = Lists.newArrayList();
				Iterator<ConfigCategory> ccI = category.getChildren().iterator();
				Iterator<Property> pI = category.getOrderedValues().iterator();

				if (categoriesFirst)
				{
					while (ccI.hasNext())
					{
						ConfigElement temp = new ItemElement(ccI.next());

						if (temp.showInGui())
						{
							elements.add(temp);
						}
					}
				}

				while (pI.hasNext())
				{
					ConfigElement<?> temp = getPropertyElement(pI.next());

					if (temp.showInGui())
					{
						elements.add(temp);
					}
				}

				if (!categoriesFirst)
				{
					while (ccI.hasNext())
					{
						ConfigElement temp = new ItemElement(ccI.next());

						if (temp.showInGui())
						{
							elements.add(temp);
						}
					}
				}

				return elements;
			}

			return null;
		}

		@Override
		public String getName()
		{
			return isProperty ? I18n.format("item." + prop.getName() + ".name") : super.getName();
		}

		@Override
		public String getComment()
		{
			return isProperty ? I18n.format(Caveworld.CONFIG_LANG + "whether", getName()) : super.getComment();
		}

		public ConfigElement<?> getPropertyElement(Property prop)
		{
			switch (getType(prop))
			{
				case BOOLEAN:
					return new ItemElement<Boolean>(prop);
				case DOUBLE:
					return new ItemElement<Double>(prop);
				case INTEGER:
					return new ItemElement<Integer>(prop);
				default:
					return new ItemElement<String>(prop);
			}
		}
	}
}