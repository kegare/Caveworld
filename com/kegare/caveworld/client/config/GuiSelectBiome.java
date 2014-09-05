/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
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

import com.google.common.base.Function;
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
	public interface SelectListener
	{
		public void setResult(List<Integer> result);
	}

	protected final GuiScreen parentScreen;
	protected ArrayEntry parentElement;

	protected BiomeList biomeList;

	protected GuiButton doneButton;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	private HoverChecker selectedHoverChecker;
	private HoverChecker instantHoverChecker;

	private static final Map<String, List<BiomeGenBase>> filterCache = Maps.newHashMap();
	private final Map<BiomeGenBase, List<String>> infoCache = Maps.newHashMap();

	public GuiSelectBiome(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	public GuiSelectBiome(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.parentElement = entry;
	}

	@Override
	public void initGui()
	{
		if (biomeList == null)
		{
			biomeList = new BiomeList(this);
		}

		biomeList.func_148122_a(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.xPosition = width / 2 + 10;
		doneButton.yPosition = height - doneButton.height - 4;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(1, 0, 8, I18n.format(Caveworld.CONFIG_LANG + "instant"), CaveConfigGui.instantFilter);
		}

		instantFilter.xPosition = width / 2 + 95;

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;

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
						if (parentElement != null)
						{
							parentElement.setListFromChildScreen(new Object[0]);
						}
					}
					else
					{
						List<Integer> result = Lists.newArrayList(Collections2.transform(biomeList.selected, new Function<BiomeGenBase, Integer>()
						{
							@Override
							public Integer apply(BiomeGenBase biome)
							{
								return biome == null ? 0 : biome.biomeID;
							}
						}));

						Collections.sort(result);

						if (parentScreen instanceof SelectListener)
						{
							((SelectListener)parentScreen).setResult(result);
						}

						if (parentElement != null)
						{
							parentElement.setListFromChildScreen(result.toArray());
						}
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
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "instant.hover"), 300), mouseX, mouseY);
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
			else if (code == Keyboard.KEY_HOME)
			{
				biomeList.scrollBy(-biomeList.getAmountScrolled());
			}
			else if (code == Keyboard.KEY_END)
			{
				biomeList.scrollBy(biomeList.getSlotHeight() * biomeList.getSize());
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

	protected static class BiomeList extends GuiSlot implements Comparator<BiomeGenBase>
	{
		protected static final ArrayListExtended<BiomeGenBase> biomes = new ArrayListExtended<BiomeGenBase>().addAllObject(BiomeGenBase.getBiomeGenArray());

		protected final GuiSelectBiome parent;

		protected final ArrayListExtended<BiomeGenBase> contents = new ArrayListExtended(biomes);
		protected final Set<BiomeGenBase> selected = Sets.newTreeSet(this);

		private BiomeList(GuiSelectBiome parent)
		{
			super(parent.mc, 0, 0, 0, 0, 18);
			this.parent = parent;

			if (parent.parentElement != null)
			{
				for (Object obj : parent.parentElement.getCurrentValues())
				{
					selected.add(BiomeGenBase.getBiome(Integer.parseInt(String.valueOf(obj))));
				}
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
			new ForkJoinPool().execute(new RecursiveAction()
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