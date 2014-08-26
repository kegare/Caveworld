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
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.BiomeIdFunction;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.HoverChecker;
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

	private HoverChecker instantHoverChecker;

	private final Map<String, List<BiomeGenBase>> filterCache = Maps.newHashMap();

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

		if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "select.instant.hover"), 300), mouseX, mouseY);
		}

		GL11.glDisable(GL11.GL_LIGHTING);

		filterTextField.drawTextBox();
	}

	@Override
	protected void mouseClicked(int x, int y, int code)
	{
		super.mouseClicked(x, y, code);

		filterTextField.mouseClicked(x, y, code);
	}

	@Override
	public void handleKeyboardInput()
	{
		super.handleKeyboardInput();

		if (Keyboard.getEventKey() == Keyboard.KEY_TAB)
		{
			if (Keyboard.getEventKeyState())
			{
				biomeList.advanced = true;
			}
			else
			{
				biomeList.advanced = false;
			}
		}
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (filterTextField.isFocused() && code != 1)
		{
			String prev = filterTextField.getText();

			filterTextField.textboxKeyTyped(c, code);

			String text = filterTextField.getText();
			boolean changed = text != prev;

			if (Strings.isNullOrEmpty(text) && changed)
			{
				setFilter("");
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				setFilter(text);
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
				biomeList.scrollBy(-5);
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				biomeList.scrollBy(5);
			}
			else if (code == Keyboard.KEY_F)
			{
				filterTextField.setFocused(true);
			}
		}
	}

	private void setFilter(String filter)
	{
		biomeList.contents.clear();

		if (Strings.isNullOrEmpty(filter))
		{
			biomeList.contents.addAll(biomeList.base);

			return;
		}

		if (filterCache.containsKey(filter))
		{
			biomeList.contents.addAll(filterCache.get(filter));
		}
		else for (BiomeGenBase biome : biomeList.base)
		{
			try
			{
				if (biome.biomeID == NumberUtils.toInt(filter, -1) ||
					biome.biomeName.toLowerCase().contains(filter.toLowerCase()) ||
					BiomeDictionary.isBiomeOfType(biome, Type.valueOf(filter.toUpperCase())))
				{
					biomeList.contents.add(biome);
				}
			}
			catch (Exception e) {}
		}

		if (!biomeList.contents.isEmpty())
		{
			filterCache.put(filter, Lists.newArrayList(biomeList.contents));
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		CaveConfigGui.instantFilter = instantFilter.isChecked();
	}

	private class BiomeList extends GuiSlot
	{
		private final GuiSelectBiome parentScreen;

		private final List<BiomeGenBase>
		base = Lists.newArrayList(),
		contents = Lists.newArrayList();

		private boolean advanced;
		private final Set<BiomeGenBase> selected = Sets.newHashSet();

		public BiomeList(GuiSelectBiome parent)
		{
			super(parent.mc, parent.width, parent.height, 32, parent.height - 28, 18);
			this.parentScreen = parent;

			for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
			{
				if (biome != null && !base.contains(biome))
				{
					base.add(biome);
				}
			}

			contents.addAll(base);

			for (Object obj : parent.parentElement.getCurrentValues())
			{
				if (obj instanceof Integer)
				{
					selected.add(BiomeGenBase.getBiome((Integer)obj));
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
			parentScreen.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int id, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			BiomeGenBase biome = contents.get(id);
			String name;

			try
			{
				name = biome.biomeName;
			}
			catch (Exception e)
			{
				name = null;
			}

			parentScreen.drawCenteredString(mc.fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);

			if (advanced)
			{
				parentScreen.drawString(mc.fontRenderer, Integer.toString(biome.biomeID), width / 2 - 100, par3 + 1, 0xE0E0E0);
			}
		}

		@Override
		protected void elementClicked(int id, boolean flag, int mouseX, int mouseY)
		{
			if (!selected.add(contents.get(id)))
			{
				selected.remove(contents.get(id));
			}
		}

		@Override
		protected boolean isSelected(int id)
		{
			return selected.contains(contents.get(id));
		}
	}
}