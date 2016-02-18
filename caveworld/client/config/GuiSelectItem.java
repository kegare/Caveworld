/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Function;
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
import caveworld.util.ItemEntry;
import caveworld.util.PanoramaPaths;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiSelectItem extends GuiScreen
{
	protected static final ArrayListExtended<ItemEntry> items = new ArrayListExtended();

	private static final Map<String, List<ItemEntry>> filterCache = Maps.newHashMap();

	static
	{
		refreshItems();
	}

	private static void refreshItems()
	{
		items.clear();

		List list = Lists.newArrayList();

		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			list.clear();
			item.getSubItems(item, item.getCreativeTab(), list);

			if (list.isEmpty())
			{
				items.addIfAbsent(new ItemEntry(item, 0));
			}
			else for (Object obj : list)
			{
				ItemStack itemstack = (ItemStack)obj;

				if (itemstack != null && itemstack.getItem() != null)
				{
					items.addIfAbsent(new ItemEntry(itemstack.getItem(), itemstack.getItemDamage()));
				}
			}
		}
	}

	public interface SelectListener
	{
		public void onSelected(Set<ItemEntry> result);
	}

	protected final GuiScreen parentScreen;
	protected GuiTextField parentNameField;
	protected GuiTextField parentDamageField;
	protected ArrayEntry parentElement;

	protected boolean hideBlocks;

	protected ItemList itemList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectItem(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	public GuiSelectItem(GuiScreen parent, GuiTextField nameField, GuiTextField damageField)
	{
		this(parent);
		this.parentNameField = nameField;
		this.parentDamageField = damageField;
	}

	public GuiSelectItem(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.parentElement = entry;
	}

	public GuiSelectItem setHideBlocks(boolean hide)
	{
		hideBlocks = hide;

		return this;
	}

	@Override
	public void initGui()
	{
		if (itemList == null)
		{
			itemList = new ItemList();
		}

		itemList.func_148122_a(width, height, 32, height - 28);

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
					if (itemList.selected.isEmpty())
					{
						if (parentNameField != null)
						{
							parentNameField.setText("");
						}

						if (parentDamageField != null)
						{
							parentDamageField.setText("");
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
							((SelectListener)parentScreen).onSelected(Sets.newHashSet(itemList.selected));
						}

						ItemEntry item = itemList.selected.iterator().next();

						if (parentNameField != null)
						{
							parentNameField.setText(GameData.getItemRegistry().getNameForObject(item.item));
						}

						if (parentDamageField != null)
						{
							parentDamageField.setText(Integer.toString(item.damage));
						}

						if (parentElement != null)
						{
							List<String> result = Lists.newArrayList(Collections2.transform(itemList.selected, new Function<ItemEntry, String>()
							{
								@Override
								public String apply(ItemEntry entry)
								{
									String str = entry.toString();

									if (str.endsWith(":0"))
									{
										str = str.substring(0, str.lastIndexOf(":"));
									}

									return str;
								}
							}));

							Collections.sort(result);

							parentElement.setListFromChildScreen(result.toArray());
						}
					}

					if (parentNameField != null)
					{
						parentNameField.setFocused(true);
						parentNameField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parentScreen);

					itemList.selected.clear();
					itemList.scrollToTop();
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
		itemList.drawScreen(mouseX, mouseY, ticks);

		boolean single = parentNameField != null || parentDamageField != null;
		String name = null;

		if (single)
		{
			name = I18n.format(Caveworld.CONFIG_LANG + "select.item");
		}
		else
		{
			name = I18n.format(Caveworld.CONFIG_LANG + "select.item.multiple");
		}

		if (!Strings.isNullOrEmpty(name))
		{
			drawCenteredString(fontRendererObj, name, width / 2, 15, 0xFFFFFF);
		}

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

		if (!single && !itemList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.item.selected", itemList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> items = Lists.newArrayList();

				for (ItemEntry entry : itemList.selected)
				{
					try
					{
						ItemStack itemstack = entry.getItemStack();

						switch (itemList.nameType)
						{
							case 1:
								name = GameData.getItemRegistry().getNameForObject(entry.item) + ", " + entry.damage;
								break;
							case 2:
								name = itemstack.getUnlocalizedName();
								name = name.substring(name.indexOf(".") + 1);
								break;
							default:
								name = itemstack.getDisplayName();
								break;
						}

						items.add(name);
					}
					catch (Throwable e) {}
				}

				func_146283_a(items, mouseX, mouseY);
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
				itemList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				itemList.setFilter(text);
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
				itemList.selected.clear();
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
				itemList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				itemList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				itemList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				itemList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				itemList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				itemList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				itemList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				itemList.selected.addAll(itemList.contents);
			}
		}
	}

	@Override
	public void onGuiClosed()
	{
		if (hideBlocks)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					refreshItems();
				}
			});
		}
	}

	class ItemList extends GuiListSlot
	{
		protected final ArrayListExtended<ItemEntry> contents = new ArrayListExtended(items);
		protected final Set<ItemEntry> selected = Sets.newHashSet();

		protected int nameType;

		private ItemList()
		{
			super(GuiSelectItem.this.mc, 0, 0, 0, 0, 18);

			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					if (hideBlocks)
					{
						Set<ItemEntry> hidden = Sets.newHashSet();

						for (ItemEntry item : items)
						{
							if (item.item instanceof ItemBlock ||Block.getBlockFromItem(item.item) != Blocks.air)
							{
								hidden.add(item);
							}
						}

						for (ItemEntry item : hidden)
						{
							items.remove(item);
							contents.remove(item);
						}
					}

					if (parentNameField != null)
					{
						int damage = 0;

						if (parentDamageField != null)
						{
							damage = NumberUtils.toInt(parentDamageField.getText());
						}

						for (ItemEntry entry : items)
						{
							if (GameData.getItemRegistry().getNameForObject(entry.item).equals(parentNameField.getText()) && entry.damage == damage)
							{
								selected.add(entry);
							}
						}
					}

					if (parentElement != null)
					{
						for (Object obj : parentElement.getCurrentValues())
						{
							String str = String.valueOf(obj);

							if (!str.contains("@"))
							{
								str += "@0";
							}

							String[] args = str.split("@");
							ItemEntry entry = new ItemEntry(args[0], NumberUtils.toInt(args[1]));

							if (entry.item != null)
							{
								selected.add(entry);
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
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (Iterator<ItemEntry> iterator = selected.iterator(); iterator.hasNext();)
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
			ItemEntry entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			ItemStack itemstack = entry.getItemStack();
			String name = null;

			try
			{
				switch (nameType)
				{
					case 1:
						name = GameData.getItemRegistry().getNameForObject(itemstack.getItem());
						break;
					case 2:
						name = itemstack.getUnlocalizedName();
						name = name.substring(name.indexOf(".") + 1);
						break;
					default:
						name = itemstack.getDisplayName();
						break;
				}
			}
			catch (Throwable e) {}

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				CaveUtils.renderItemStack(mc, itemstack, width / 2 - 100, par3 - 1, false, false, null);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			ItemEntry entry = contents.get(index, null);

			if (entry != null && !selected.remove(entry))
			{
				if (parentNameField != null || parentDamageField != null)
				{
					selected.clear();
				}

				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			ItemEntry entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<ItemEntry> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = items;
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(items, new ItemFilter(filter))));
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

	public static class ItemFilter implements Predicate<ItemEntry>
	{
		private final String filter;

		public ItemFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(ItemEntry entry)
		{
			return CaveUtils.itemFilter(entry, filter);
		}
	}
}