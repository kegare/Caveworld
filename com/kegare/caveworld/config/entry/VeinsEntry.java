package com.kegare.caveworld.config.entry;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.config.Config;
import com.kegare.caveworld.core.CaveVeinManager;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.BlockEntry;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry;
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

		list.add(new DummyCategoryElement("addVeinEntry", Caveworld.CONFIG_LANG + "veins.add", AddVeinEntry.class));

		for (String category : getConfig().getCategoryNames())
		{
			list.add(new VeinElement(getConfig().getCategory(category)));
		}

		return list;
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

			list.add(new ConfigElement<String>(new Property("veinName", "New Vein", Property.Type.STRING, Caveworld.CONFIG_LANG + "veins.veinName")));
			list.add(new ConfigElement<String>(new Property("block", Block.blockRegistry.getNameForObject(Blocks.stone), Property.Type.STRING, Caveworld.CONFIG_LANG + "veins.block")));
			list.add(new ConfigElement<Integer>(new Property("blockMetadata", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.blockMetadata").setMinValue(0).setMaxValue(15)));
			list.add(new ConfigElement<Integer>(new Property("genBlockCount", "1", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genBlockCount").setMinValue(1).setMaxValue(100)));
			list.add(new ConfigElement<Integer>(new Property("genWeight", "1", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genWeight").setMinValue(0).setMaxValue(100).setConfigEntryClass(VeinConfigEntry.class)));
			list.add(new ConfigElement<Integer>(new Property("genMinHeight", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genMinHeight").setMinValue(0).setMaxValue(254)));
			list.add(new ConfigElement<Integer>(new Property("genMaxHeight", "255", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genMaxHeight").setMinValue(1).setMaxValue(255)));
			list.add(new ConfigElement<String>(new Property("genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone), Property.Type.STRING, Caveworld.CONFIG_LANG + "veins.genTargetBlock")));
			list.add(new ConfigElement<Integer>(new Property("genTargetBlockMetadata", "0", Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genTargetBlockMetadata").setMinValue(0).setMaxValue(15)));
			list.add(new ConfigElement<Integer>(new Property("genBiomes", new String[] {}, Property.Type.INTEGER, Caveworld.CONFIG_LANG + "veins.genBiomes")));

			return list;
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			return new GuiConfig(owningScreen, getConfigElements(), owningScreen.modID, owningScreen.configID + ".add",
					configElement.requiresWorldRestart() || owningScreen.allRequireWorldRestart, configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
					GuiConfig.getAbridgedConfigPath(getConfig().toString()));
		}

		@Override
		public boolean isChanged()
		{
			return true;
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

					if (Strings.isNullOrEmpty(target))
					{
						target = element.getDefault().toString();
					}
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

			if (min >= max)
			{
				min = 0;
			}

			CaveVein vein = new CaveVein(new BlockEntry(block, blockMetadata, Blocks.stone), count, weight, min, max, new BlockEntry(target, targetMetadata, Blocks.stone), biomes);

			if (!Strings.isNullOrEmpty(owningScreen.configID) && owningScreen.configID.endsWith(".add"))
			{
				if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(block) || weight <= 0)
				{
					return;
				}

				if (CaveVeinManager.addCaveVeinWithConfig(name, vein) && owningScreen.parentScreen instanceof GuiConfig)
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

				if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(block) || weight <= 0)
				{
					if (CaveVeinManager.removeCaveVeinFromConfig(name))
					{
						CaveVeinManager.removeCaveVein(vein);

						if (owningScreen.parentScreen instanceof GuiConfig)
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
}