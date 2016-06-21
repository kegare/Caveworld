package caveworld.client.config;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.math.NumberUtils;
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiSelectItem extends GuiScreen
{
	private static final ArrayListExtended<ItemEntry> items = new ArrayListExtended();

	static
	{
		List list = Lists.newArrayList();

		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			list.clear();

			CreativeTabs[] tabs = item.getCreativeTabs();

			if (tabs == null)
			{
				item.getSubItems(item, item.getCreativeTab(), list);
			}
			else for (CreativeTabs tab : tabs)
			{
				item.getSubItems(item, tab, list);
			}

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
		public void onItemSelected(Set<ItemEntry> result);
	}

	protected final GuiScreen parentScreen;

	protected GuiTextField nameField;
	protected GuiTextField damageField;

	protected ArrayEntry configElement;

	protected final Set<ItemEntry> excluded = Sets.newHashSet();

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
		this.nameField = nameField;
		this.damageField = damageField;
	}

	public GuiSelectItem(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.configElement = entry;
	}

	public GuiSelectItem exclude(Collection<ItemEntry> items)
	{
		for (ItemEntry entry : items)
		{
			excluded.add(entry);
		}

		return this;
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
						if (nameField != null)
						{
							nameField.setText("");
						}

						if (damageField != null)
						{
							damageField.setText("");
						}

						if (configElement != null)
						{
							configElement.setListFromChildScreen(new Object[0]);
						}
					}
					else
					{
						if (parentScreen != null && parentScreen instanceof SelectListener)
						{
							((SelectListener)parentScreen).onItemSelected(itemList.selected);
						}

						ItemEntry item = itemList.selected.iterator().next();

						if (nameField != null)
						{
							nameField.setText(GameData.getItemRegistry().getNameForObject(item.item));
						}

						if (damageField != null)
						{
							damageField.setText(Integer.toString(item.damage));
						}

						if (configElement != null)
						{
							Set<String> values = Sets.newLinkedHashSet();

							for (ItemEntry entry : itemList.selected)
							{
								String value = entry.toString();

								if (value.endsWith(":0"))
								{
									value = value.substring(0, value.lastIndexOf(":"));
								}

								values.add(value);
							}

							configElement.setListFromChildScreen(values.toArray());
						}
					}

					if (nameField != null)
					{
						nameField.setFocused(true);
						nameField.setCursorPositionEnd();
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

		boolean single = nameField != null || damageField != null;
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
				if (configElement == null || configElement.getConfigElement().getMaxListLength() <= 0)
				{
					itemList.selected.addAll(itemList.contents);
				}
			}
		}
	}

	class ItemList extends GuiListSlot implements Comparator<ItemEntry>
	{
		protected final ArrayListExtended<ItemEntry> entries = new ArrayListExtended();
		protected final ArrayListExtended<ItemEntry> contents = new ArrayListExtended();
		protected final Set<ItemEntry> selected = Sets.newLinkedHashSet();
		protected final Map<String, List<ItemEntry>> filterCache = Maps.newHashMap();

		protected int nameType;

		protected ItemList()
		{
			super(GuiSelectItem.this.mc, 0, 0, 0, 0, 18);

			for (ItemEntry item : items)
			{
				if (excluded.isEmpty() || !excluded.contains(item))
				{
					if (hideBlocks)
					{
						if (item.item instanceof ItemBlock || Block.getBlockFromItem(item.item) != Blocks.air || item.item.getUnlocalizedName().startsWith("tile."))
						{
							continue;
						}
					}

					entries.addIfAbsent(item);
					contents.addIfAbsent(item);
				}
			}

			if (nameField != null)
			{
				int damage = -1;

				if (damageField != null)
				{
					damage = NumberUtils.toInt(damageField.getText());
				}

				for (ItemEntry item : entries)
				{
					String text = nameField.getText();

					if (!Strings.isNullOrEmpty(text) && text.equals(GameData.getItemRegistry().getNameForObject(item.item)))
					{
						if (damage < 0 || damage == item.damage)
						{
							selected.add(item);
							break;
						}
					}
				}
			}

			if (configElement != null)
			{
				for (Object obj : configElement.getCurrentValues())
				{
					String value = String.valueOf(obj);

					if (!Strings.isNullOrEmpty(value))
					{
						value = value.trim();

						if (!value.contains(":"))
						{
							value = "minecraft:" + value;
						}

						if (value.indexOf(':') != value.lastIndexOf(':'))
						{
							int i = value.lastIndexOf(':');
							Item item = GameData.getItemRegistry().getObject(value.substring(0, i));

							if (item != null)
							{
								selected.add(new ItemEntry(item, Integer.parseInt(value.substring(i + 1))));
							}
						}
						else
						{
							Item item = GameData.getItemRegistry().getObject(value);

							if (item != null)
							{
								selected.add(new ItemEntry(item, 0));
							}
						}
					}
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

				for (ItemEntry entry : selected)
				{
					amount = contents.indexOf(entry) * getSlotHeight();

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
						name = entry.getString();
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
				if (nameField != null || damageField != null)
				{
					selected.clear();
				}

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
			ItemEntry entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		@Override
		public int compare(ItemEntry o1, ItemEntry o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(entries.indexOf(o1), entries.indexOf(o2));
			}

			return i;
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
						result = entries;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(entries, new ItemFilter(filter))));
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