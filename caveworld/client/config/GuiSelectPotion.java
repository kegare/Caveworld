/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.client.gui.GuiListSlot;
import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
import caveworld.util.PanoramaPaths;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;

@SideOnly(Side.CLIENT)
public class GuiSelectPotion extends GuiScreen
{
	private final GuiScreen parent;
	private ArrayEntry configElement;

	private PotionList potionList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;
	protected HoverChecker selectedHoverChecker;

	public GuiSelectPotion(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectPotion(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.configElement = entry;
	}

	@Override
	public void initGui()
	{
		if (potionList == null)
		{
			potionList = new PotionList();
		}

		potionList.func_148122_a(width, height, 32, height - 28);

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
		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (!potionList.selected.isEmpty())
					{
						if (configElement != null)
						{
							Set<Integer> entries = Sets.newTreeSet();

							for (Potion potion : potionList.selected)
							{
								entries.add(potion.getId());
							}

							configElement.setListFromChildScreen(entries.toArray());
						}
					}

					mc.displayGuiScreen(parent);

					potionList.selected.clear();
					potionList.scrollToTop();
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
		potionList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.potion"), width / 2, 15, 0xFFFFFF);

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

		if (!potionList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.potion.selected", potionList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (detailInfo.isChecked() && selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> list = Lists.newArrayList();

				for (Potion potion : potionList.selected)
				{
					list.add(I18n.format(potion.getName()));
				}

				func_146283_a(list, mouseX, mouseY);
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
				potionList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				potionList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);

				if (parent == null)
				{
					mc.setIngameFocus();
				}
			}
			else if (code == Keyboard.KEY_BACK)
			{
				potionList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++potionList.nameType > 1)
				{
					potionList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				potionList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				potionList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				potionList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				potionList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				potionList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				potionList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				potionList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				potionList.selected.clear();
				potionList.selected.addAll(potionList.contents);
			}
		}
	}

	class PotionList extends GuiListSlot
	{
		private final ArrayListExtended<Potion> potions = new ArrayListExtended();
		private final ArrayListExtended<Potion> contents = new ArrayListExtended();
		private final Set<Potion> selected = Sets.newHashSet();
		private final Map<String, List<Potion>> filterCache = Maps.newHashMap();

		protected int nameType;

		public PotionList()
		{
			super(GuiSelectPotion.this.mc, 0, 0, 0, 0, 18);
			this.potions.addAll(CaveUtils.getPotions());
			this.contents.addAll(potions);

			if (configElement != null)
			{
				CaveUtils.getPool().execute(new RecursiveAction()
				{
					@Override
					protected void compute()
					{
						Set<Integer> current = Sets.newHashSet();

						for (Object obj : configElement.getCurrentValues())
						{
							current.add((Integer)obj);
						}

						for (Potion potion : potions)
						{
							if (current.contains(potion.getId()))
							{
								selected.add(potion);
							}
						}
					}
				});
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

				for (Iterator<Potion> iterator = selected.iterator(); iterator.hasNext();)
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
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			Potion entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			String name;

			switch (nameType)
			{
				case 1:
					name = entry.getName();
					break;
				default:
					name = I18n.format(entry.getName());
					break;
			}

			drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			Potion entry = contents.get(index, null);

			if (entry != null && !selected.remove(entry))
			{
				if (configElement != null)
				{
					int i = configElement.getConfigElement().getMaxListLength();

					if (i > 0 && selected.size() >= i)
					{
						return;
					}
				}

				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			Potion entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<Potion> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = potions;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(potions, new Predicate<Potion>()
							{
								@Override
								public boolean apply(Potion input)
								{
									return StringUtils.containsIgnoreCase(input.getName(), filter) || StringUtils.containsIgnoreCase(I18n.format(input.getName()), filter);
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