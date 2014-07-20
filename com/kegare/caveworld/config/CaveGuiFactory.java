/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.config;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CaveGuiFactory implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft mc)
	{
		FMLCommonHandler.instance().bus().register(this);
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass()
	{
		return CaveConfigGui.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
	{
		return null;
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (Caveworld.MODID.equals(event.modID) && !event.isWorldRunning)
		{
			if (Configuration.CATEGORY_GENERAL.equals(event.configID))
			{
				Config.syncGeneralCfg();
			}
			else if ("blocks".equals(event.configID))
			{
				Config.syncBlocksCfg();
			}
			else if ("dimension".equals(event.configID))
			{
				Config.syncDimensionCfg();
			}
			else if ("biomes".equals(event.configID))
			{
				Config.syncBiomesCfg();
			}
			else if ("veins".equals(event.configID))
			{
				Config.syncVeinsCfg();
			}
		}
	}

	public static class CaveConfigGui extends GuiConfig
	{
		public CaveConfigGui(GuiScreen parent)
		{
			super(parent, getConfigElements(), Caveworld.MODID, false, false, I18n.format(Config.LANG_KEY + "title"));
		}

		private static List<IConfigElement> getConfigElements()
		{
			List<IConfigElement> list = Lists.newArrayList();
			list.add(new DummyCategoryElement("generalCfg", Config.LANG_KEY + Configuration.CATEGORY_GENERAL, GeneralEntry.class));
			list.add(new DummyCategoryElement("blocksCfg", Config.LANG_KEY + "blocks", BlocksEntry.class));
			list.add(new DummyCategoryElement("dimensionCfg", Config.LANG_KEY + "dimension", DimensionEntry.class));
			list.add(new DummyCategoryElement("biomesCfg", Config.LANG_KEY + "biomes", BiomesEntry.class));
			list.add(new DummyCategoryElement("veinsCfg", Config.LANG_KEY + "veins", VeinsEntry.class));

			return list;
		}

		public static class GeneralEntry extends CategoryEntry
		{
			public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen()
			{
				List<IConfigElement> list = Lists.newArrayList();

				for (String category : Config.generalCfg.getCategoryNames())
				{
					list.addAll(new ConfigElement(Config.generalCfg.getCategory(category)).getChildElements());
				}

				return new GuiConfig(owningScreen, list, owningScreen.modID, Configuration.CATEGORY_GENERAL,
					configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
					GuiConfig.getAbridgedConfigPath(Config.generalCfg.toString()));
			}
		}

		public static class BlocksEntry extends CategoryEntry
		{
			public BlocksEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen()
			{
				List<IConfigElement> list = Lists.newArrayList();

				for (String category : Config.blocksCfg.getCategoryNames())
				{
					list.addAll(new ConfigElement(Config.blocksCfg.getCategory(category)).getChildElements());
				}

				return new GuiConfig(owningScreen, list, owningScreen.modID, "blocks",
					configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
					GuiConfig.getAbridgedConfigPath(Config.blocksCfg.toString()));
			}
		}

		public static class DimensionEntry extends CategoryEntry
		{
			public DimensionEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen()
			{
				List<IConfigElement> list = Lists.newArrayList();

				for (String category : Config.dimensionCfg.getCategoryNames())
				{
					list.addAll(new ConfigElement(Config.dimensionCfg.getCategory(category)).getChildElements());
				}

				return new GuiConfig(owningScreen, list, owningScreen.modID, "dimension",
						configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
						GuiConfig.getAbridgedConfigPath(Config.dimensionCfg.toString()));
			}
		}

		public static class BiomesEntry extends CategoryEntry
		{
			public BiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen()
			{
				List<IConfigElement> list = Lists.newArrayList();

				for (ConfigCategory category : getBiomeCategories())
				{
					list.add(new BiomeElement(category));
				}

				return new GuiConfig(owningScreen, list, owningScreen.modID, "biomes",
						configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
						GuiConfig.getAbridgedConfigPath(Config.biomesCfg.toString()));
			}

			public static List<ConfigCategory> getBiomeCategories()
			{
				List<ConfigCategory> list = Lists.newArrayList();
				SortedSet<String> entries = Sets.newTreeSet(new Comparator<String>()
				{
					@Override
					public int compare(String o1, String o2)
					{
						return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
					}
				});

				entries.addAll(Config.biomesCfg.getCategoryNames());

				for (String name : entries)
				{
					list.add(Config.biomesCfg.getCategory(name));
				}

				return list;
			}

			private class BiomeElement extends ConfigElement
			{
				private BiomeGenBase biome;
				private final int genWeight;
				private final String terrainBlock;
				private final int terrainBlockMetadata;

				public BiomeElement(ConfigCategory category)
				{
					super(category);
					this.biome = BiomeGenBase.getBiome(NumberUtils.toInt(category.getName(), BiomeGenBase.plains.biomeID));
					this.genWeight = category.get("genWeight").getInt(0);
					this.terrainBlock = category.get("terrainBlock").getString();
					this.terrainBlockMetadata = category.get("terrainBlockMetadata").getInt(0);
				}

				public BiomeGenBase getBiome()
				{
					return biome == null ? BiomeGenBase.plains : biome;
				}

				@Override
				public String getName()
				{
					return BiomeGenBase.getBiome(Integer.valueOf(super.getName())).biomeName;
				}

				@Override
				public String getComment()
				{
					List<String> list = Lists.newArrayList();
					list.add(Integer.toString(genWeight));
					list.add(terrainBlock);
					list.add(Integer.toString(terrainBlockMetadata));
					String data = Joiner.on(", ").skipNulls().join(list);

					if (BiomeDictionary.isBiomeRegistered(getBiome()))
					{
						Set<String> types = Sets.newHashSet();

						for (Type type : BiomeDictionary.getTypesForBiome(getBiome()))
						{
							types.add(type.name());
						}

						return super.getName() + ": " + Joiner.on(", ").skipNulls().join(types) + " [" + data + "]";
					}

					return super.getName() + ": [" + data + "]";
				}
			}
		}

		public static class VeinsEntry extends CategoryEntry
		{
			public VeinsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
			{
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen()
			{
				List<IConfigElement> list = Lists.newArrayList();

				list.add(new DummyCategoryElement("addVeinEntry", Config.LANG_KEY + "veins.add", AddVeinEntry.class));

				for (String category : Config.veinsCfg.getCategoryNames())
				{
					list.add(new VeinElement(Config.veinsCfg.getCategory(category)));
				}

				return new GuiConfig(owningScreen, list, owningScreen.modID, "veins",
						configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
						GuiConfig.getAbridgedConfigPath(Config.veinsCfg.toString()));
			}

			@Override
			public boolean isChanged()
			{
				if (childScreen instanceof GuiConfig)
				{
					GuiConfig child = (GuiConfig)childScreen;

					return child.entryList.listEntries.size() != child.initEntries.size() || child.entryList.hasChangedEntry(true);
				}

				return false;
			}

			public static class VeinCategoryEntry extends CategoryEntry
			{
				private static final Set<String> removedEntries = Sets.newHashSet();

				public VeinCategoryEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
				{
					super(owningScreen, owningEntryList, configElement);
				}

				@Override
				public boolean enabled()
				{
					return !removedEntries.contains(name);
				}
			}

			public static class VeinConfigEntry extends NumberSliderEntry
			{
				public VeinConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
				{
					super(owningScreen, owningEntryList, configElement);
				}

				@Override
				public void onGuiClosed()
				{
					String name = owningScreen.titleLine2;

					owningEntryList.saveConfigElements();

					for (IConfigElement element : owningScreen.configElements)
					{
						if ("block".equals(element.getName()) && Strings.isNullOrEmpty(element.get().toString()))
						{
							if (Config.veinsCfg.hasCategory(name))
							{
								Config.veinsCfg.removeCategory(Config.veinsCfg.getCategory(name));

								VeinCategoryEntry.removedEntries.add(name);

								if (owningScreen.parentScreen instanceof GuiConfig)
								{
									GuiConfig parent = (GuiConfig)owningScreen.parentScreen;

									parent.needsRefresh = true;
									parent.initGui();
								}
							}

							return;
						}
					}
				}
			}

			public static class AddVeinEntry extends CategoryEntry
			{
				public AddVeinEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
				{
					super(owningScreen, owningEntryList, prop);
				}

				@Override
				protected GuiScreen buildChildScreen()
				{
					List<IConfigElement> list = Lists.newArrayList();

					list.add(new ConfigElement<String>(new Property("veinName", "New Vein", Property.Type.STRING, Config.LANG_KEY + "veins.veinName")));
					list.add(new ConfigElement<String>(new Property("block", Block.blockRegistry.getNameForObject(Blocks.stone), Property.Type.STRING, Config.LANG_KEY + "veins.block")));
					list.add(new ConfigElement<Integer>(new Property("blockMetadata", "0", Property.Type.INTEGER, Config.LANG_KEY + "veins.blockMetadata")));
					list.add(new ConfigElement<Integer>(new Property("genBlockCount", "1", Property.Type.INTEGER, Config.LANG_KEY + "veins.genBlockCount")));
					list.add(new ConfigElement<Integer>(new Property("genWeight", "1", Property.Type.INTEGER, Config.LANG_KEY + "veins.genWeight")));
					list.add(new ConfigElement<Integer>(new Property("genMinHeight", "0", Property.Type.INTEGER, Config.LANG_KEY + "veins.genMinHeight")));
					list.add(new ConfigElement<Integer>(new Property("genMaxHeight", "255", Property.Type.INTEGER, Config.LANG_KEY + "veins.genMaxHeight")));
					list.add(new ConfigElement<String>(new Property("genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone), Property.Type.STRING, Config.LANG_KEY + "veins.genTargetBlock")));
					list.add(new ConfigElement<Integer>(new Property("genTargetBlockMetadata", "0", Property.Type.INTEGER, Config.LANG_KEY + "veins.genTargetBlockMetadata")));
					list.add(new ConfigElement<Integer>(new Property("genBiomes", new String[] {}, Property.Type.INTEGER, Config.LANG_KEY + "veins.genBiomes").setConfigEntryClass(GenBiomesEntry.class)));

					return new GuiConfig(owningScreen, list, owningScreen.modID,
							configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
							GuiConfig.getAbridgedConfigPath(Config.veinsCfg.toString()));
				}

				@Override
				public boolean isChanged()
				{
					return true;
				}

				public static class GenBiomesEntry extends ArrayEntry
				{
					public GenBiomesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
					{
						super(owningScreen, owningEntryList, configElement);
					}

					@Override
					public void onGuiClosed()
					{
						String name = null;
						String block = Block.blockRegistry.getNameForObject(Blocks.stone);
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
							if ("veinName".equals(element.getName()))
							{
								name = element.get().toString();
							}
							else if ("block".equals(element.getName()))
							{
								block = element.get().toString();
							}
							else if ("blockMetadata".equals(element.getName()))
							{
								blockMetadata = Integer.valueOf(element.get().toString());
							}
							else if ("genBlockCount".equals(element.getName()))
							{
								count = Integer.valueOf(element.get().toString());
							}
							else if ("genWeight".equals(element.getName()))
							{
								weight = Integer.valueOf(element.get().toString());
							}
							else if ("genMinHeight".equals(element.getName()))
							{
								min = Integer.valueOf(element.get().toString());
							}
							else if ("genMaxHeight".equals(element.getName()))
							{
								max = Integer.valueOf(element.get().toString());
							}
							else if ("genTargetBlock".equals(element.getName()))
							{
								target = element.get().toString();
							}
							else if ("genTargetBlockMetadata".equals(element.getName()))
							{
								targetMetadata = Integer.valueOf(element.get().toString());
							}
							else if ("genBiomes".equals(element.getName()) && element.isList())
							{
								for (Object obj : element.getList())
								{
									biomes = ArrayUtils.add(new int[] {}, Integer.valueOf(obj.toString()));
								}
							}
						}

						Config.addVeinEntry(name, block, blockMetadata, count, weight, min, max, target, targetMetadata, biomes);

						if (owningScreen.parentScreen instanceof GuiConfig)
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
				}
			}

			public static class VeinElement extends ConfigElement
			{
				private final String block;
				private final int blockMetadata;
				private final int genBlockCount;
				private final int genWeight;
				private final int genMinHeight;
				private final int genMaxHeight;
				private final String genTargetBlock;
				private final int genTargetBlockMetadata;
				private final int[] genBiomes;

				public VeinElement(ConfigCategory category)
				{
					super(category);
					this.block = category.get("block").getString();
					this.blockMetadata = category.get("blockMetadata").getInt(0);
					this.genBlockCount = category.get("genBlockCount").getInt(1);
					this.genWeight = category.get("genWeight").getInt(1);
					this.genMinHeight = category.get("genMinHeight").getInt(0);
					this.genMaxHeight = category.get("genMaxHeight").getInt(255);
					this.genTargetBlock = category.get("genTargetBlock").getString();
					this.genTargetBlockMetadata = category.get("genTargetBlockMetadata").getInt(0);
					this.genBiomes = category.get("genBiomes").getIntList();
				}

				@Override
				public Class getConfigEntryClass()
				{
					return VeinCategoryEntry.class;
				}

				@Override
				public String getComment()
				{
					List<String> list = Lists.newArrayList();
					list.add(block);
					list.add(Integer.toString(blockMetadata));
					list.add(Integer.toString(genBlockCount));
					list.add(Integer.toString(genWeight));
					list.add(Integer.toString(genMinHeight));
					list.add(Integer.toString(genMaxHeight));
					list.add(genTargetBlock);
					list.add(Integer.toString(genTargetBlockMetadata));

					if (genBiomes != null && genBiomes.length > 0)
					{
						List<String> biomes = Lists.newArrayList();

						for (int biome : genBiomes)
						{
							biomes.add(Integer.toString(biome));
						}

						list.add("[" + Joiner.on(", ").join(biomes) + "]");
					}

					return Joiner.on(", ").skipNulls().join(list);
				}
			}
		}
	}
}