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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

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
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.BiomeIdFunction;
import com.kegare.caveworld.util.CaveLog;

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
		biomeList.setFilter(null);

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
			biomeList.showBiomeId = Keyboard.getEventKeyState();
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

	private class BiomeList extends GuiSlot
	{
		private final GuiSelectBiome parentScreen;

		private final List<BiomeGenBase>
		biomes = Lists.newArrayList(),
		contents = Collections.synchronizedList(new ArrayList<BiomeGenBase>());

		private final Set<BiomeGenBase> selected = Sets.newHashSet();

		private boolean showBiomeId;

		public BiomeList(GuiSelectBiome parent)
		{
			super(parent.mc, parent.width, parent.height, 32, parent.height - 28, 18);
			this.parentScreen = parent;

			for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray())
			{
				if (biome != null && !biomes.contains(biome))
				{
					biomes.add(biome);
				}
			}

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
			parentScreen.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int id, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			if (contents.size() > id)
			{
				BiomeGenBase biome = contents.get(id);

				parentScreen.drawCenteredString(mc.fontRenderer, biome.biomeName, width / 2, par3 + 1, 0xFFFFFF);

				if (showBiomeId)
				{
					parentScreen.drawString(mc.fontRenderer, Integer.toString(biome.biomeID), width / 2 - 100, par3 + 1, 0xE0E0E0);
				}
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

		protected void setFilter(final String filter)
		{
			ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

			Futures.addCallback(pool.submit(
				new Callable<List<BiomeGenBase>>()
				{
					@Override
					public List<BiomeGenBase> call() throws Exception
					{
						if (Strings.isNullOrEmpty(filter))
						{
							return biomes;
						}

						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(biomes, new BiomeFilter(filter))));
						}

						return filterCache.get(filter);
					}
				}),
				new FutureCallback<List<BiomeGenBase>>()
				{
					@Override
					public void onSuccess(List<BiomeGenBase> result)
					{
						contents.clear();
						contents.addAll(result);
					}

					@Override
					public void onFailure(Throwable throwable)
					{
						CaveLog.log(Level.WARN, throwable, "Failed to trying biomes filtering");

						contents.clear();
					}
				});

			pool.shutdown();
		}
	}

	private class BiomeFilter implements Predicate<BiomeGenBase>
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