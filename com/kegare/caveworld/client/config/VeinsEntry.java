/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.client.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.CaveVeinManager;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.ConfigCategoryFunction;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.StringEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VeinsEntry extends CaveCategoryEntry
{
	public VeinsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return Config.veinsCfg;
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();
		Collection<ConfigCategory> categories = Collections2.transform(getConfig().getCategoryNames(), new ConfigCategoryFunction(getConfig()));

		list.add(new DummyCategoryElement("addVeinEntry", Caveworld.CONFIG_LANG + "veins.add", AddVeinEntry.class));

		for (ConfigCategory category : categories)
		{
			list.add(new VeinElement(category));
		}

		return list;
	}

	public static class VeinElement extends ConfigElement
	{
		private final ConfigCategory category;

		public VeinElement(ConfigCategory category)
		{
			super(category);
			this.category = category;
		}

		@Override
		public String getComment()
		{
			List<String> list = Lists.newArrayList();
			list.add(category.get("block").getString());
			list.add(category.get("blockMetadata").getString());
			list.add(category.get("genBlockCount").getString());
			list.add(category.get("genWeight").getString());
			list.add(category.get("genMinHeight").getString());
			list.add(category.get("genMaxHeight").getString());
			list.add(category.get("genTargetBlock").getString());
			list.add(category.get("genTargetBlockMetadata").getString());

			String[] biomes = category.get("genBiomes").getStringList();

			if (biomes != null && biomes.length > 0)
			{
				list.add("[" + Joiner.on(", ").join(biomes) + "]");
			}

			return Joiner.on(", ").skipNulls().join(list);
		}
	}

	public static class AddVeinEntry extends VeinsEntry
	{
		public AddVeinEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected List<IConfigElement> getConfigElements()
		{
			List<IConfigElement> list = Lists.newArrayList();

			list.add(new ConfigElement<String>(new Property("veinName", "", Property.Type.STRING, Caveworld.CONFIG_LANG + "veins.veinName").setConfigEntryClass(CaveVeinManager.veinEntryClass)));
			list.add(new ConfigElement<String>(new Property("block", Block.blockRegistry.getNameForObject(Blocks.stone), Property.Type.STRING, Caveworld.CONFIG_LANG + "veins.block").setConfigEntryClass(Config.selectBlockEntryClass)));
			list.add(new ConfigElement<Integer>(new Property("blockMetadata", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.blockMetadata").setMinValue(0).setMaxValue(15)));
			list.add(new ConfigElement<Integer>(new Property("genBlockCount", "1", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genBlockCount").setMinValue(1).setMaxValue(100)));
			list.add(new ConfigElement<Integer>(new Property("genWeight", "1", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genWeight").setMinValue(0).setMaxValue(100)));
			list.add(new ConfigElement<Integer>(new Property("genMinHeight", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genMinHeight").setMinValue(0).setMaxValue(254)));
			list.add(new ConfigElement<Integer>(new Property("genMaxHeight", "255", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genMaxHeight").setMinValue(1).setMaxValue(255)));
			list.add(new ConfigElement<String>(new Property("genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone), Property.Type.STRING, Caveworld.CONFIG_LANG + "veins.genTargetBlock").setConfigEntryClass(Config.selectBlockEntryClass)));
			list.add(new ConfigElement<Integer>(new Property("genTargetBlockMetadata", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genTargetBlockMetadata").setMinValue(0).setMaxValue(15)));
			list.add(new ConfigElement<Integer>(new Property("genBiomes", new String[0], Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genBiomes").setConfigEntryClass(Config.selectBiomeEntryClass)));

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

	public static class VeinConfigEntry extends StringEntry
	{
		public VeinConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
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
						case "block":
							element.set("");
							break;
						case "genWeight":
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
			String block = null;
			int blockMetadata = 0;
			int count = 1;
			int weight = 1;
			int min = 0;
			int max = 255;
			String target = Block.blockRegistry.getNameForObject(Blocks.stone);
			int targetMetadata = 0;
			int[] biomes = new int[] {};

			owningEntryList.saveConfigElements();

			for (IConfigElement element : owningScreen.configElements)
			{
				switch (element.getName())
				{
					case "veinName":
						name = element.get().toString();
						break;
					case "block":
						block = element.get().toString();
						break;
					case "blockMetadata":
						blockMetadata = Integer.parseInt(element.get().toString());
						break;
					case "genBlockCount":
						count = Integer.parseInt(element.get().toString());
						break;
					case "genWeight":
						weight = Integer.parseInt(element.get().toString());
						break;
					case "genMinHeight":
						min = Integer.parseInt(element.get().toString());
						break;
					case "genMaxHeight":
						max = Integer.parseInt(element.get().toString());
						break;
					case "genTargetBlock":
						target = element.get().toString();
						break;
					case "genTargetBlockMetadata":
						targetMetadata = Integer.parseInt(element.get().toString());
						break;
					case "genBiomes":
						Object[] temp = element.getList();
						int[] ids = new int[temp.length];

						for (int i = 0; i < temp.length; ++i)
						{
							ids[i] = Integer.parseInt(temp[i].toString());
						}

						biomes = ids;
						break;
				}
			}

			if (min >= max)
			{
				min = 0;
			}

			if (!Strings.isNullOrEmpty(owningScreen.configID) && owningScreen.configID.endsWith(".add"))
			{
				if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(block) || weight <= 0)
				{
					return;
				}

				CaveVein vein = new CaveVein(new BlockEntry(block, blockMetadata), count, weight, min, max, new BlockEntry(target, targetMetadata), biomes);

				if (CaveworldAPI.addCaveVeinWithConfig(name, vein) && owningScreen.parentScreen instanceof GuiConfig)
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
						parent.configElements.add(new VeinElement(Config.veinsCfg.getCategory(name)));
					}

					parent.needsRefresh = true;
					parent.initGui();
				}
			}
			else
			{
				name = owningScreen.titleLine2;

				if (!Strings.isNullOrEmpty(name) && (Strings.isNullOrEmpty(block) || weight <= 0))
				{
					if (CaveworldAPI.removeCaveVeinFromConfig(name) && owningScreen.parentScreen instanceof GuiConfig)
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