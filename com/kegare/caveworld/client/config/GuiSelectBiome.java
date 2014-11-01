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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.client.gui.GuiListSlot;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.PanoramaPaths;

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
	protected static final ArrayListExtended<BiomeGenBase> biomes = new ArrayListExtended<BiomeGenBase>().addAllObject(BiomeGenBase.getBiomeGenArray());

	public interface SelectListener
	{
		public void setResult(Set<BiomeGenBase> result);
	}

	protected final GuiScreen parentScreen;
	protected GuiTextField parentTextField;
	protected ArrayEntry parentElement;

	protected BiomeList biomeList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	private final Set<BiomeGenBase> hiddenBiomes = Sets.newHashSet();
	private final Map<BiomeGenBase, List<String>> hoverCache = Maps.newHashMap();

	public GuiSelectBiome(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	public GuiSelectBiome(GuiScreen parent, GuiTextField textField)
	{
		this(parent);
		this.parentTextField = textField;
	}

	public GuiSelectBiome(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.parentElement = entry;
	}

	public GuiSelectBiome setHiddenBiomes(BiomeGenBase... hidden)
	{
		hiddenBiomes.clear();

		for (BiomeGenBase biome : hidden)
		{
			if (biome != null)
			{
				hiddenBiomes.add(biome);
			}
		}

		biomes.removeAll(hiddenBiomes);

		return this;
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

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(1, 0, 5, I18n.format(Caveworld.CONFIG_LANG + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.xPosition = width / 2 + 95;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(2, 0, detailInfo.yPosition + detailInfo.height + 2, I18n.format(Caveworld.CONFIG_LANG + "instant"), true);
		}

		instantFilter.setIsChecked(CaveConfigGui.instantFilter);
		instantFilter.xPosition = detailInfo.xPosition;

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(detailInfo);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;

		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
		detailHoverChecker = new HoverChecker(detailInfo, 800);
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
						if (parentTextField != null)
						{
							parentTextField.setText("");
						}

						if (parentElement != null)
						{
							parentElement.setListFromChildScreen(new Object[0]);
						}
					}
					else
					{
						if (parentScreen != null && parentScreen instanceof SelectListener)
						{
							((SelectListener)parentScreen).setResult(Sets.newHashSet(biomeList.selected));
						}

						List<Integer> result = Lists.newArrayList(Collections2.transform(biomeList.selected, new Function<BiomeGenBase, Integer>()
						{
							@Override
							public Integer apply(BiomeGenBase biome)
							{
								return biome == null ? 0 : biome.biomeID;
							}
						}));

						Collections.sort(result);

						if (parentTextField != null)
						{
							parentTextField.setText(Ints.join(", ", Ints.toArray(result)));
						}

						if (parentElement != null)
						{
							parentElement.setListFromChildScreen(result.toArray());
						}
					}

					if (parentTextField != null)
					{
						parentTextField.setFocused(true);
						parentTextField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parentScreen);
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
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

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "detail.hover"), 300), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "instant.hover"), 300), mouseX, mouseY);
		}
		else if (biomeList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			BiomeGenBase biome = biomeList.contents.get(biomeList.func_148124_c(mouseX, mouseY), null);

			if (biome != null)
			{
				List<String> info;

				if (!hoverCache.containsKey(biome))
				{
					ICaveBiome entry = CaveworldAPI.getCaveBiome(biome);

					info = Lists.newArrayList();
					info.add(EnumChatFormatting.DARK_GRAY + Integer.toString(biome.biomeID) + ": " + biome.biomeName);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.genWeight") + ": " + entry.getGenWeight());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.terrainBlock") + ": " + GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock()) + ", " + entry.getTerrainBlock().getMetadata());

					Block block = biome.topBlock;

					if (block != null)
					{
						info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "select.biome.info.topBlock") + ": " + GameData.getBlockRegistry().getNameForObject(block));
					}

					block = biome.fillerBlock;

					if (block != null)
					{
						info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "select.biome.info.fillerBlock") + ": " + GameData.getBlockRegistry().getNameForObject(block));
					}

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

					hoverCache.put(biome, info);
				}

				info = hoverCache.get(biome);

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
				biomeList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				biomeList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				biomeList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				biomeList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				biomeList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				biomeList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				biomeList.scrollToNext();
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

		if (biomes.addAll(hiddenBiomes))
		{
			Collections.sort(biomes, CaveUtils.biomeComparator);
		}
	}

	protected static class BiomeList extends GuiListSlot
	{
		private static final Map<String, List<BiomeGenBase>> filterCache = Maps.newHashMap();

		protected final GuiSelectBiome parent;

		protected final ArrayListExtended<BiomeGenBase> contents = new ArrayListExtended(biomes);
		protected final Set<BiomeGenBase> selected = Sets.newTreeSet(CaveUtils.biomeComparator);

		private BiomeList(GuiSelectBiome parent)
		{
			super(parent.mc, 0, 0, 0, 0, 18);
			this.parent = parent;

			if (parent.parentTextField != null)
			{
				Set<Integer> ids = Sets.newHashSet();

				for (String str : Splitter.on(',').trimResults().omitEmptyStrings().split(parent.parentTextField.getText()))
				{
					if (NumberUtils.isNumber(str))
					{
						ids.add(Integer.parseInt(str));
					}
				}

				for (Integer id : ids)
				{
					selected.add(BiomeGenBase.getBiome(id));
				}
			}

			if (parent.parentElement != null)
			{
				for (Object obj : parent.parentElement.getCurrentValues())
				{
					selected.add(BiomeGenBase.getBiome(Integer.parseInt(String.valueOf(obj))));
				}
			}
		}

		@Override
		public PanoramaPaths getPanoramaPaths()
		{
			return null;
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (Iterator<BiomeGenBase> iterator = selected.iterator(); iterator.hasNext();)
				{
					amount = contents.indexOf(iterator.next()) * getSlotHeight();

					if (getAmountScrolled() != amount)
					{
						break;
					}
				}

				scrollToTop();
				scrollBy(amount);
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

			if (parent.detailInfo.isChecked() || Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				parent.drawString(parent.fontRendererObj, Integer.toString(biome.biomeID), width / 2 - 100, par3 + 1, 0xE0E0E0);

				if (Keyboard.isKeyDown(Keyboard.KEY_TAB))
				{
					Block block = biome.topBlock;
					int meta = biome.field_150604_aj;

					if (block != null && Item.getItemFromBlock(block) != null)
					{
						CaveUtils.renderItemStack(mc, new ItemStack(block, 1, meta), width / 2 + 70, par3 - 1, false, false, null);
					}

					block = biome.fillerBlock;
					meta = biome.field_76754_C;

					if (block != null && Item.getItemFromBlock(block) != null)
					{
						CaveUtils.renderItemStack(mc, new ItemStack(block, 1, meta), width / 2 + 90, par3 - 1, false, false, null);
					}
				}
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
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(biomes, new BiomeFilter(filter))));
						}

						result = filterCache.get(filter);
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

	public static class BiomeFilter implements Predicate<BiomeGenBase>
	{
		private final String filter;

		public BiomeFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(BiomeGenBase biome)
		{
			return CaveUtils.biomeFilter(biome, filter);
		}
	}
}