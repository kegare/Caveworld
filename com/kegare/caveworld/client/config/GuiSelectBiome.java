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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.BiomeIdFunction;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectBiome extends GuiScreen
{
	protected final GuiScreen parentScreen;
	protected final ArrayEntry parentElement;

	private GuiButtonExt doneButton;
	private GuiCheckBox instantFilter;
	private GuiTextField filterTextField;
	private BiomeList biomeList;

	private HoverChecker selectedHoverChecker;
	private HoverChecker instantHoverChecker;

	private static final Map<String, List<BiomeGenBase>> filterCache = Maps.newHashMap();
	private final Map<BiomeGenBase, List<String>> infoCache = Maps.newHashMap();

	public GuiSelectBiome(GuiScreen parent, ArrayEntry entry)
	{
		this.parentScreen = parent;
		this.parentElement = entry;
	}

	@Override
	public void initGui()
	{
		doneButton = new GuiButtonExt(0, width / 2 - 155 + 165, height - 24, 145, 20, I18n.format("gui.done"));
		instantFilter = new GuiCheckBox(1, width / 2 - 155 + 250, 8, I18n.format(Caveworld.CONFIG_LANG + "select.instant"), CaveConfigGui.instantFilter);

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(instantFilter);

		filterTextField = new GuiTextField(fontRendererObj, width / 2 - 155, height - 23, 150, 16);
		filterTextField.setMaxStringLength(100);

		biomeList = new BiomeList(this);
		biomeList.registerScrollButtons(2, 3);

		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
		instantHoverChecker = new HoverChecker(instantFilter, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (biomeList.selected.isEmpty())
					{
						parentElement.setListFromChildScreen(new Object[0]);
					}
					else
					{
						List<Integer> list = Lists.transform(Lists.newArrayList(biomeList.selected), new BiomeIdFunction());
						List<Integer> result = Lists.newArrayList(list);

						Collections.sort(result);

						parentElement.setListFromChildScreen(result.toArray());
					}

					mc.displayGuiScreen(parentScreen);
					break;
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		filterTextField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		biomeList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.biome"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		GL11.glDisable(GL11.GL_LIGHTING);
		filterTextField.drawTextBox();
		GL11.glEnable(GL11.GL_LIGHTING);

		if (biomeList.func_148141_e(mouseY))
		{
			BiomeGenBase biome = biomeList.contents.get(biomeList.func_148124_c(mouseX, mouseY), null);

			if (biome != null && Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				List<String> info;

				if (!infoCache.containsKey(biome))
				{
					info = Lists.newArrayList();

					info.add(EnumChatFormatting.DARK_GRAY + Integer.toString(biome.biomeID) + ": " + biome.biomeName);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.genWeight") + ": " + CaveworldAPI.getBiomeGenWeight(biome));

					BlockEntry block = CaveworldAPI.getBiomeTerrainBlock(biome);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.terrainBlock") + ": " + GameData.getBlockRegistry().getNameForObject(block.getBlock()) + ", " + block.getMetadata());

					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "select.biome.info.temperature") + ": " + biome.temperature);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "select.biome.info.rainfall") + ": " + biome.rainfall);

					if (BiomeDictionary.isBiomeRegistered(biome))
					{
						Set<String> types = Sets.newTreeSet();

						for (Type type : BiomeDictionary.getTypesForBiome(biome))
						{
							types.add(type.name());
						}

						info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "select.biome.info.type") + ": " + Joiner.on(", ").skipNulls().join(types));
					}

					infoCache.put(biome, info);
				}

				info = infoCache.get(biome);

				if (!info.isEmpty())
				{
					func_146283_a(info, mouseX, mouseY);
				}
			}
		}

		if (!biomeList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.biome.selected", biomeList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> biomes = Lists.newArrayList();

				for (BiomeGenBase selected : biomeList.selected)
				{
					biomes.add(String.format("%d: %s", selected.biomeID, selected.biomeName));
				}

				func_146283_a(biomes, mouseX, mouseY);
			}
		}

		if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "select.instant.hover"), 300), mouseX, mouseY);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int code)
	{
		super.mouseClicked(x, y, code);

		filterTextField.mouseClicked(x, y, code);
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (filterTextField.isFocused())
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				filterTextField.setFocused(false);
			}

			String prev = filterTextField.getText();

			filterTextField.textboxKeyTyped(c, code);

			String text = filterTextField.getText();
			boolean changed = text != prev;

			if (Strings.isNullOrEmpty(text) && changed)
			{
				biomeList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				biomeList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parentScreen);
			}
			else if (code == Keyboard.KEY_BACK)
			{
				biomeList.selected.clear();
			}
			else if (code == Keyboard.KEY_UP)
			{
				int i = biomeList.getAmountScrolled() % biomeList.getSlotHeight();

				if (i == 0)
				{
					biomeList.scrollBy(-biomeList.getSlotHeight());
				}
				else
				{
					biomeList.scrollBy(-i);
				}
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				biomeList.scrollBy(biomeList.getSlotHeight() - (biomeList.getAmountScrolled() % biomeList.getSlotHeight()));
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				biomeList.scrollBy(-((biomeList.getAmountScrolled() % biomeList.getSlotHeight()) + ((biomeList.bottom - biomeList.top) / biomeList.getSlotHeight()) * biomeList.getSlotHeight()));
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				biomeList.scrollBy((biomeList.getAmountScrolled() % biomeList.getSlotHeight()) + ((biomeList.bottom - biomeList.top) / biomeList.getSlotHeight()) * biomeList.getSlotHeight());
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				biomeList.scrollBy(-biomeList.getAmountScrolled());

				if (!biomeList.selected.isEmpty())
				{
					biomeList.scrollBy(biomeList.contents.indexOf(biomeList.selected.iterator().next()) * biomeList.getSlotHeight());
				}
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				biomeList.selected.addAll(biomeList.contents);
			}
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		CaveConfigGui.instantFilter = instantFilter.isChecked();
	}

	private static class BiomeList extends GuiSlot implements Comparator<BiomeGenBase>
	{
		private static final ArrayListExtended<BiomeGenBase> biomes = new ArrayListExtended<BiomeGenBase>().addAllObject(BiomeGenBase.getBiomeGenArray());

		private final GuiSelectBiome parent;

		private final ArrayListExtended<BiomeGenBase> contents = new ArrayListExtended(biomes);

		private final Set<BiomeGenBase> selected = Sets.newTreeSet(this);

		public BiomeList(GuiSelectBiome parent)
		{
			super(parent.mc, parent.width, parent.height, 32, parent.height - 28, 18);
			this.parent = parent;

			for (Object obj : parent.parentElement.getCurrentValues())
			{
				selected.add(BiomeGenBase.getBiome(Integer.parseInt(String.valueOf(obj))));
			}
		}

		@Override
		protected int getSize()
		{
			return contents.size();
		}

		@Override
		protected void drawBackground()
		{
			parent.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			BiomeGenBase biome = contents.get(index, null);

			if (biome == null)
			{
				return;
			}

			parent.drawCenteredString(parent.fontRendererObj, biome.biomeName, width / 2, par3 + 1, 0xFFFFFF);

			if (Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				parent.drawString(parent.fontRendererObj, Integer.toString(biome.biomeID), width / 2 - 100, par3 + 1, 0xE0E0E0);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			BiomeGenBase biome = contents.get(index, null);

			if (biome != null && !selected.add(biome))
			{
				selected.remove(biome);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			BiomeGenBase biome = contents.get(index, null);

			return biome != null && selected.contains(biome);
		}

		@Override
		public int compare(BiomeGenBase o1, BiomeGenBase o2)
		{
			return Integer.compare(o1.biomeID, o2.biomeID);
		}

		protected void setFilter(final String filter)
		{
			ForkJoinPool pool = new ForkJoinPool();

			pool.execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<BiomeGenBase> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = biomes;
					}
					else
					{
						if (!GuiSelectBiome.filterCache.containsKey(filter))
						{
							GuiSelectBiome.filterCache.put(filter, Lists.newArrayList(Collections2.filter(biomes, new BiomeFilter(filter))));
						}

						result = GuiSelectBiome.filterCache.get(filter);
					}

					if (!contents.equals(result))
					{
						contents.clear();
						contents.addAll(result);
					}
				}
			});

			pool.shutdown();
		}
	}

	private static class BiomeFilter implements Predicate<BiomeGenBase>
	{
		private final String filter;

		private BiomeFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(BiomeGenBase biome)
		{
			try
			{
				if (biome.biomeID == NumberUtils.toInt(filter, -1) ||
					biome.biomeName.toLowerCase().contains(filter.toLowerCase()) ||
					BiomeDictionary.isBiomeOfType(biome, Type.valueOf(filter.toUpperCase())))
				{
					return true;
				}
			}
			catch (Exception e) {}

			return false;
		}
	}
}