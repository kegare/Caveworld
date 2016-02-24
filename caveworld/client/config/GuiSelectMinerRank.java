/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import caveworld.client.gui.GuiListSlot;
import caveworld.core.CaverManager.MinerRank;
import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
import caveworld.util.PanoramaPaths;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class GuiSelectMinerRank extends GuiScreen
{
	protected final GuiScreen parent;
	protected GuiTextField parentTextField;

	protected RankList rankList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectMinerRank(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectMinerRank(GuiScreen parent, GuiTextField textField)
	{
		this(parent);
		this.parentTextField = textField;
	}

	@Override
	public void initGui()
	{
		if (rankList == null)
		{
			rankList = new RankList();
		}

		rankList.func_148122_a(width, height, 32, height - 28);

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
					if (rankList.selected != null)
					{
						MinerRank rank = rankList.selected;

						if (parentTextField != null)
						{
							parentTextField.setText(Integer.toString(rank.getRank()));
						}
					}

					mc.displayGuiScreen(parent);

					rankList.selected = null;
					rankList.scrollToTop();
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
		rankList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.minerRank"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			func_146283_a(fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "detail.hover"), 300), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
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
				rankList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				rankList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);
			}
			else if (code == Keyboard.KEY_BACK)
			{
				rankList.selected = null;
			}
			else if (code == Keyboard.KEY_UP)
			{
				rankList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				rankList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				rankList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				rankList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				rankList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				rankList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				rankList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
		}
	}

	class RankList extends GuiListSlot
	{
		private final ArrayListExtended<MinerRank> ranks = new ArrayListExtended();
		private final ArrayListExtended<MinerRank> contents = new ArrayListExtended();
		private final Map<String, List<MinerRank>> filterCache = Maps.newHashMap();

		private MinerRank selected;

		public RankList()
		{
			super(GuiSelectMinerRank.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					ranks.clear();
					contents.clear();
					filterCache.clear();
					selected = null;

					for (MinerRank rank : MinerRank.values())
					{
						ranks.addIfAbsent(rank);
						contents.addIfAbsent(rank);
					}

					if (parentTextField != null)
					{
						String str = parentTextField.getText();

						if (!Strings.isNullOrEmpty(str))
						{
							try
							{
								int i = Integer.parseInt(str);

								if (i >= 0 && i < ranks.size())
								{
									selected = ranks.get(i);
								}
							}
							catch (NumberFormatException e)
							{
								selected = null;
							}
						}
					}
				}
			});
		}

		@Override
		public PanoramaPaths getPanoramaPaths()
		{
			return null;
		}

		@Override
		public void scrollToSelected()
		{
			if (selected != null)
			{
				scrollToTop();
				scrollBy(contents.indexOf(selected) * getSlotHeight());
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
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			MinerRank entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			drawCenteredString(fontRendererObj, I18n.format(entry.getUnlocalizedName()), width / 2, par3 + 1, 0xFFFFFF);

			if (detailInfo.isChecked())
			{
				CaveUtils.renderItemStack(mc, entry.getRenderItemStack(), width / 2 - 100, par3 - 1, true, true, Integer.toString(entry.getRank()));
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			MinerRank entry = contents.get(index, null);

			if (entry != null)
			{
				selected = entry;
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			MinerRank entry = contents.get(index, null);

			return entry != null && selected != null && entry == selected;
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<MinerRank> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = ranks;
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(ranks, new Predicate<MinerRank>()
							{
								@Override
								public boolean apply(MinerRank input)
								{
									String name = input.getUnlocalizedName();

									return StringUtils.containsIgnoreCase(name, filter) || StringUtils.containsIgnoreCase(I18n.format(name), filter);
								}
							})));
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
}