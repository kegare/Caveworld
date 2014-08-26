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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectItem extends GuiScreen
{
	protected final GuiScreen parentScreen;
	protected final GuiTextField parentTextField;

	private GuiButtonExt doneButton;
	private GuiCheckBox instantFilter;
	private GuiTextField filterTextField;
	private ItemList itemList;

	private HoverChecker instantHoverChecker;

	private final Map<String, List<Item>> filterCache = Maps.newHashMap();

	public GuiSelectItem(GuiScreen parent, GuiTextField textField)
	{
		this.parentScreen = parent;
		this.parentTextField = textField;
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

		itemList = new ItemList(this);
		itemList.registerScrollButtons(2, 3);

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
					if (itemList.selected == null)
					{
						parentTextField.setText("");
					}
					else
					{
						parentTextField.setText(GameData.getItemRegistry().getNameForObject(itemList.selected));
					}

					parentTextField.setFocused(true);
					parentTextField.setCursorPositionEnd();

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
		itemList.drawScreen(mouseX, mouseY, ticks);
		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.item"), width / 2, 15, 0xFFFFFF);

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
				itemList.selected = null;
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++itemList.nameType > 2)
				{
					itemList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				itemList.scrollBy(-5);
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				itemList.scrollBy(5);
			}
			else if (code == Keyboard.KEY_F)
			{
				filterTextField.setFocused(true);
			}
		}
	}

	private void setFilter(String filter)
	{
		itemList.contents.clear();

		if (Strings.isNullOrEmpty(filter))
		{
			itemList.contents.addAll(itemList.base);

			return;
		}

		if (filterCache.containsKey(filter))
		{
			itemList.contents.addAll(filterCache.get(filter));
		}
		else for (Item item : itemList.base)
		{
			if (GameData.getItemRegistry().getNameForObject(item).toLowerCase().contains(filter.toLowerCase()) ||
				item.getUnlocalizedName().toLowerCase().contains(filter.toLowerCase()) ||
				item.getItemStackDisplayName(new ItemStack(item)).toLowerCase().contains(filter.toLowerCase()) ||
				item.getToolClasses(new ItemStack(item)).contains(filter))
			{
				itemList.contents.add(item);
			}
		}

		if (!itemList.contents.isEmpty())
		{
			filterCache.put(filter, Lists.newArrayList(itemList.contents));
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		CaveConfigGui.instantFilter = instantFilter.isChecked();
	}

	private class ItemList extends GuiSlot
	{
		private final GuiSelectItem parentScreen;

		private final List<Item>
		base = Lists.newArrayList(),
		contents = Lists.newArrayList();

		private int nameType;
		private Item selected = null;

		public ItemList(GuiSelectItem parent)
		{
			super(parent.mc, parent.width, parent.height, 32, parent.height - 28, 18);
			this.parentScreen = parent;

			for (Object obj : GameData.getItemRegistry())
			{
				base.add((Item)obj);
			}

			Collections.sort(base, new Comparator<Item>()
			{
				@Override
				public int compare(Item o1, Item o2)
				{
					String item1 = GameData.getItemRegistry().getNameForObject(o1);
					String item2 = GameData.getItemRegistry().getNameForObject(o2);

					return item1.substring(0, item1.indexOf(":")).compareTo(item2.substring(0, item2.indexOf(":")));
				}
			});

			contents.addAll(base);

			selected = GameData.getItemRegistry().getObject(parent.parentTextField.getText());
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
			Item item = contents.get(id);

			try
			{
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
				RenderHelper.enableGUIStandardItemLighting();
				RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(item), width / 2 - 100, par3 - 1);
			}
			catch (Exception e) {}
			finally
			{
				RenderHelper.disableStandardItemLighting();
				GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			}

			String name = null;

			switch (nameType)
			{
				case 1:
					name = GameData.getItemRegistry().getNameForObject(item);
					break;
				case 2:
					name = item.getUnlocalizedName();
					name = name.substring(name.indexOf(".") + 1);
					break;
				default:
					name = item.getItemStackDisplayName(new ItemStack(item));
					break;
			}

			parentScreen.drawCenteredString(mc.fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int id, boolean flag, int mouseX, int mouseY)
		{
			selected = selected == contents.get(id) ? null : contents.get(id);
		}

		@Override
		protected boolean isSelected(int id)
		{
			return selected != null && selected == contents.get(id);
		}
	}
}