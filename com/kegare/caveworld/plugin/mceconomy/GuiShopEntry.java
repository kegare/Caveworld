/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.mceconomy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kegare.caveworld.client.config.CaveConfigGui;
import com.kegare.caveworld.client.config.GuiSelectItem;
import com.kegare.caveworld.client.config.GuiSelectItem.SelectListener;
import com.kegare.caveworld.client.gui.GuiListSlot;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.plugin.mceconomy.ShopProductManager.ShopProduct;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiShopEntry extends GuiScreen implements SelectListener
{
	protected final GuiScreen parentScreen;

	protected ProductList productList;

	protected GuiButton doneButton;
	protected GuiButton editButton;
	protected GuiButton cancelButton;
	protected GuiButton addButton;
	protected GuiButton removeButton;
	protected GuiButton clearButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	protected boolean editMode;
	protected GuiTextField itemField;
	protected GuiTextField damageField;
	protected GuiTextField stackField;
	protected GuiTextField costField;

	protected HoverChecker itemHoverChecker;
	protected HoverChecker stackHoverChecker;
	protected HoverChecker costHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	private final Map<Object, List<String>> hoverCache = Maps.newHashMap();

	public GuiShopEntry(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (productList == null)
		{
			productList = new ProductList(this);
		}

		productList.func_148122_a(width, height, 32, height - (editMode ? 90 : 28));

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 65, 20, I18n.format("gui.done"));
		}

		doneButton.xPosition = width / 2 + 135;
		doneButton.yPosition = height - doneButton.height - 4;

		if (editButton == null)
		{
			editButton = new GuiButtonExt(1, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.edit"));
			editButton.enabled = false;
		}

		editButton.xPosition = doneButton.xPosition - doneButton.width - 3;
		editButton.yPosition = doneButton.yPosition;
		editButton.enabled = productList.selected != null;
		editButton.visible = !editMode;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(2, 0, 0, editButton.width, editButton.height, I18n.format("gui.cancel"));
		}

		cancelButton.xPosition = editButton.xPosition;
		cancelButton.yPosition = editButton.yPosition;
		cancelButton.visible = editMode;

		if (removeButton == null)
		{
			removeButton = new GuiButtonExt(4, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.remove"));
		}

		removeButton.xPosition = editButton.xPosition - editButton.width - 3;
		removeButton.yPosition = doneButton.yPosition;
		removeButton.visible =  !editMode;

		if (addButton == null)
		{
			addButton = new GuiButtonExt(3, 0, 0, doneButton.width, doneButton.height, I18n.format("gui.add"));
		}

		addButton.xPosition = removeButton.xPosition - removeButton.width - 3;
		addButton.yPosition = doneButton.yPosition;
		addButton.visible = !editMode;

		if (clearButton == null)
		{
			clearButton = new GuiButtonExt(5, 0, 0, removeButton.width, removeButton.height, I18n.format("gui.clear"));
		}

		clearButton.xPosition = removeButton.xPosition;
		clearButton.yPosition = removeButton.yPosition;
		clearButton.visible = false;

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(6, 0, 5, I18n.format(Caveworld.CONFIG_LANG + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.xPosition = width / 2 + 95;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(7, 0, detailInfo.yPosition + detailInfo.height + 2, I18n.format(Caveworld.CONFIG_LANG + "instant"), true);
		}

		instantFilter.setIsChecked(CaveConfigGui.instantFilter);
		instantFilter.xPosition = detailInfo.xPosition;

		buttonList.clear();
		buttonList.add(doneButton);

		if (editMode)
		{
			buttonList.add(cancelButton);
		}
		else
		{
			buttonList.add(editButton);
			buttonList.add(addButton);
			buttonList.add(removeButton);
			buttonList.add(clearButton);
		}

		buttonList.add(detailInfo);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 122, 16);
			filterTextField.setMaxStringLength(500);
		}

		filterTextField.xPosition = width / 2 - 200;
		filterTextField.yPosition = height - filterTextField.height - 6;

		detailHoverChecker = new HoverChecker(detailInfo, 800);
		instantHoverChecker = new HoverChecker(instantFilter, 800);

		editLabelList.clear();
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "shop.item"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "shop.stackSize"));
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "shop.productCost"));

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRendererObj.getStringWidth(key));
		}

		if (itemField == null)
		{
			itemField = new GuiTextField(fontRendererObj, 0, 0, 0, 15);
			itemField.setMaxStringLength(100);
		}

		int i = maxLabelWidth + 8 + width / 2;
		itemField.xPosition = width / 2 - i / 2 + maxLabelWidth + 10;
		itemField.yPosition = productList.bottom + 5;
		int fieldWidth = width / 2 + i / 2 - 45 - itemField.xPosition + 40;
		itemField.width = fieldWidth / 4 + fieldWidth / 2 - 1;

		if (damageField == null)
		{
			damageField = new GuiTextField(fontRendererObj, 0, 0, 0, itemField.height);
			damageField.setMaxStringLength(5);
		}

		damageField.xPosition = itemField.xPosition + itemField.width + 3;
		damageField.yPosition = itemField.yPosition;
		damageField.width = fieldWidth / 4 - 1;

		if (stackField == null)
		{
			stackField = new GuiTextField(fontRendererObj, 0, 0, 0, itemField.height);
			stackField.setMaxStringLength(2);
		}

		stackField.xPosition = itemField.xPosition;
		stackField.yPosition = itemField.yPosition + itemField.height + 5;
		stackField.width = fieldWidth;

		if (costField == null)
		{
			costField = new GuiTextField(fontRendererObj, 0, 0, 0, itemField.height);
		}

		costField.xPosition = stackField.xPosition;
		costField.yPosition = stackField.yPosition + stackField.height + 5;
		costField.width = fieldWidth;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(itemField);
			editFieldList.add(damageField);
			editFieldList.add(stackField);
			editFieldList.add(costField);
		}

		itemHoverChecker = new HoverChecker(itemField.yPosition - 1, itemField.yPosition + itemField.height, itemField.xPosition - maxLabelWidth - 12, itemField.xPosition - 10, 800);
		stackHoverChecker = new HoverChecker(stackField.yPosition - 1, stackField.yPosition + stackField.height, stackField.xPosition - maxLabelWidth - 12, stackField.xPosition - 10, 800);
		costHoverChecker = new HoverChecker(costField.yPosition - 1, costField.yPosition + costField.height, costField.xPosition - maxLabelWidth - 12, costField.xPosition - 10, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (editMode)
					{
						if (Strings.isNullOrEmpty(itemField.getText()) || !GameData.getItemRegistry().containsKey(itemField.getText()) || NumberUtils.toInt(stackField.getText()) <= 0)
						{
							return;
						}

						productList.selected.setProductItem(new ItemStack(GameData.getItemRegistry().getObject(itemField.getText()),
							NumberUtils.toInt(stackField.getText()), NumberUtils.toInt(damageField.getText())));
						productList.selected.setCost(NumberUtils.toInt(costField.getText()));

						hoverCache.remove(productList.selected);

						actionPerformed(cancelButton);

						productList.scrollToSelected();
					}
					else
					{
						boolean flag = ShopProductManager.instance().getShopProducts().size() != productList.products.size();

						ShopProductManager.instance().getShopProducts().clear();

						if (flag)
						{
							try
							{
								FileUtils.forceDelete(new File(MCEconomyPlugin.shopCfg.toString()));

								MCEconomyPlugin.shopCfg.load();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}

						for (ShopProduct product : productList.products)
						{
							ShopProductManager.instance().addShopProduct(product);
						}

						if (MCEconomyPlugin.shopCfg.hasChanged())
						{
							MCEconomyPlugin.shopCfg.save();
						}

						actionPerformed(cancelButton);
					}

					break;
				case 1:
					if (editMode)
					{
						actionPerformed(cancelButton);
					}
					else
					{
						editMode = true;
						initGui();

						productList.scrollToSelected();

						itemField.setText(GameData.getItemRegistry().getNameForObject(productList.selected.getProductItem().getItem()));
						damageField.setText(Integer.toString(productList.selected.getProductItem().getItemDamage()));
						stackField.setText(Integer.toString(productList.selected.getProductItem().stackSize));
						costField.setText(Integer.toString(productList.selected.getcost()));
					}

					break;
				case 2:
					if (editMode)
					{
						editMode = false;
						initGui();
					}
					else
					{
						mc.displayGuiScreen(parentScreen);
					}

					break;
				case 3:
					mc.displayGuiScreen(new GuiSelectItem(this));
					break;
				case 4:
					if (productList.products.remove(productList.selected))
					{
						int i = productList.contents.indexOf(productList.selected);

						productList.contents.remove(i);
						productList.selected = productList.contents.get(i, productList.contents.get(--i, null));
					}

					break;
				case 5:
					for (Object obj : productList.products.toArray())
					{
						productList.selected = (ShopProduct)obj;

						actionPerformed(removeButton);
					}

					break;
				case 6:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 7:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
			}
		}
	}

	@Override
	public void onSelected(Item item)
	{
		if (!editMode)
		{
			ShopProduct product = new ShopProduct(new ItemStack(item), 0);

			if (productList.products.addIfAbsent(product))
			{
				productList.contents.addIfAbsent(product);
				productList.selected = product;

				editButton.enabled = true;
				actionPerformed(editButton);
			}
		}
	}

	@Override
	public void updateScreen()
	{
		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				textField.updateCursorCounter();
			}
		}
		else
		{
			editButton.enabled = productList.selected != null;
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		productList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "shop"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		if (editMode)
		{
			GuiTextField textField;

			for (int i = 0; i < editFieldList.size(); ++i)
			{
				textField = editFieldList.get(i);
				textField.drawTextBox();
				drawString(fontRendererObj, editLabelList.get(i), textField.xPosition - maxLabelWidth - 10, textField.yPosition + 3, 0xBBBBBB);
			}
		}
		else
		{
			filterTextField.drawTextBox();
		}

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			if (!hoverCache.containsKey(detailHoverChecker))
			{
				hoverCache.put(detailHoverChecker, fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "detail.hover"), 300));
			}

			func_146283_a(hoverCache.get(detailHoverChecker), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			if (!hoverCache.containsKey(instantHoverChecker))
			{
				hoverCache.put(instantHoverChecker, fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "instant.hover"), 300));
			}

			func_146283_a(hoverCache.get(instantHoverChecker), mouseX, mouseY);
		}
		else if (itemHoverChecker.checkHover(mouseX, mouseY))
		{
			if (!hoverCache.containsKey(itemHoverChecker))
			{
				List<String> hover = Lists.newArrayList();
				String key = Caveworld.CONFIG_LANG + "shop.item";
				hover.add(EnumChatFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				hoverCache.put(itemHoverChecker, hover);
			}

			func_146283_a(hoverCache.get(itemHoverChecker), mouseX, mouseY);
		}
		else if (stackHoverChecker.checkHover(mouseX, mouseY))
		{
			if (!hoverCache.containsKey(stackHoverChecker))
			{
				List<String> hover = Lists.newArrayList();
				String key = Caveworld.CONFIG_LANG + "shop.stackSize";
				hover.add(EnumChatFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				hoverCache.put(stackHoverChecker, hover);
			}

			func_146283_a(hoverCache.get(stackHoverChecker), mouseX, mouseY);
		}
		else if (costHoverChecker.checkHover(mouseX, mouseY))
		{
			if (!hoverCache.containsKey(costHoverChecker))
			{
				List<String> hover = Lists.newArrayList();
				String key = Caveworld.CONFIG_LANG + "shop.productCost";
				hover.add(EnumChatFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				hoverCache.put(costHoverChecker, hover);
			}

			func_146283_a(hoverCache.get(costHoverChecker), mouseX, mouseY);
		}
		else if (productList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			ShopProduct entry = productList.contents.get(productList.func_148124_c(mouseX, mouseY), null);

			if (entry != null)
			{
				if (!hoverCache.containsKey(entry))
				{
					List<String> info = Lists.newArrayList();

					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.item") + ": " +
						GameData.getItemRegistry().getNameForObject(entry.getProductItem().getItem()) + ", " + entry.getProductItem().getItemDamage());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.stackSize") + ": " + entry.getProductItem().stackSize);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.productCost") + ": " + entry.getcost());

					hoverCache.put(entry, info);
				}

				func_146283_a(hoverCache.get(entry), mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();

		if (damageField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				damageField.setText(Integer.toString(Math.max(NumberUtils.toInt(damageField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				damageField.setText(Integer.toString(Math.min(NumberUtils.toInt(damageField.getText()) + 1, Short.MAX_VALUE)));
			}
		}
		else if (stackField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				stackField.setText(Integer.toString(Math.max(NumberUtils.toInt(stackField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				stackField.setText(Integer.toString(Math.min(NumberUtils.toInt(stackField.getText()) + 1, 64)));
			}
		}
		else if (costField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				costField.setText(Integer.toString(Math.max(NumberUtils.toInt(costField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				costField.setText(Integer.toString(Math.min(NumberUtils.toInt(costField.getText()) + 1, MCEconomyPlugin.Player_MP_MAX)));
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int code)
	{
		super.mouseClicked(x, y, code);

		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				textField.mouseClicked(x, y, code);
			}

			if (!isShiftKeyDown())
			{
				if (itemField.isFocused())
				{
					itemField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectItem(this, itemField));
				}
			}
		}
		else
		{
			filterTextField.mouseClicked(x, y, code);
		}
	}

	@Override
	public void handleKeyboardInput()
	{
		super.handleKeyboardInput();

		if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT)
		{
			clearButton.visible = !editMode && Keyboard.getEventKeyState();
		}
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (editMode)
		{
			for (GuiTextField textField : editFieldList)
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					textField.setFocused(false);
				}
				else if (textField.isFocused())
				{
					if (textField != itemField)
					{
						if (!CharUtils.isAsciiControl(c) && !CharUtils.isAsciiNumeric(c))
						{
							continue;
						}
					}

					textField.textboxKeyTyped(c, code);
				}
			}
		}
		else
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
					productList.setFilter(null);
				}
				else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
				{
					productList.setFilter(text);
				}
			}
			else
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					actionPerformed(doneButton);
				}
				else if (code == Keyboard.KEY_BACK)
				{
					productList.selected = null;
				}
				else if (code == Keyboard.KEY_TAB)
				{
					if (++productList.nameType > 2)
					{
						productList.nameType = 0;
					}
				}
				else if (code == Keyboard.KEY_UP)
				{
					productList.scrollUp();
				}
				else if (code == Keyboard.KEY_DOWN)
				{
					productList.scrollDown();
				}
				else if (code == Keyboard.KEY_HOME)
				{
					productList.scrollToTop();
				}
				else if (code == Keyboard.KEY_END)
				{
					productList.scrollToEnd();
				}
				else if (code == Keyboard.KEY_SPACE)
				{
					productList.scrollToSelected();
				}
				else if (code == Keyboard.KEY_PRIOR)
				{
					productList.scrollToPrev();
				}
				else if (code == Keyboard.KEY_NEXT)
				{
					productList.scrollToNext();
				}
				else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
				{
					filterTextField.setFocused(true);
				}
				else if (code == Keyboard.KEY_DELETE && productList.selected != null)
				{
					actionPerformed(removeButton);
				}
			}
		}
	}

	protected static class ProductList extends GuiListSlot
	{
		protected final GuiShopEntry parent;

		protected final ArrayListExtended<ShopProduct> products = new ArrayListExtended(ShopProductManager.instance().getShopProducts());
		protected final ArrayListExtended<ShopProduct> contents = new ArrayListExtended(products);

		private final Map<String, List<ShopProduct>> filterCache = Maps.newHashMap();
		private final Set<Item> ignoredRender = CaveConfigGui.getIgnoredRenderItems();

		protected int nameType;
		protected ShopProduct selected;

		private ProductList(GuiShopEntry parent)
		{
			super(parent.mc, 0, 0, 0, 0, 22);
			this.parent = parent;
		}

		@Override
		public void scrollToSelected()
		{
			scrollToTop();

			if (selected != null)
			{
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
			parent.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, Tessellator tessellator, int mouseX, int mouseY)
		{
			ShopProduct entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			ItemStack itemstack = entry.getProductItem();
			String name = null;

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

			parent.drawCenteredString(parent.fontRendererObj, name, width / 2, par3 + 3, 0xFFFFFF);

			if (parent.detailInfo.isChecked())
			{
				if (!ignoredRender.contains(itemstack.getItem()))
				{
					try
					{
						GL11.glEnable(GL12.GL_RESCALE_NORMAL);
						RenderHelper.enableGUIStandardItemLighting();
						RenderItem.getInstance().renderItemAndEffectIntoGUI(parent.fontRendererObj, parent.mc.getTextureManager(), itemstack, width / 2 - 100, par3 + 1);
						RenderItem.getInstance().renderItemOverlayIntoGUI(parent.fontRendererObj, parent.mc.getTextureManager(), itemstack, width / 2 - 100, par3 + 1);
						RenderHelper.disableStandardItemLighting();
						GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					}
					catch (Exception e)
					{
						CaveLog.log(Level.WARN, e, "Failed to trying render item into gui: %s", GameData.getBlockRegistry().getNameForObject(itemstack.getItem()));

						ignoredRender.add(itemstack.getItem());
					}
				}

				name = Integer.toString(entry.getcost());
				parent.drawString(parent.fontRendererObj, name, width / 2 + 107 - parent.fontRendererObj.getStringWidth(name), par3 + 8, 0xD0D0D0);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			if (!parent.editMode)
			{
				selected = isSelected(index) ? null : contents.get(index, null);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			return selected == contents.get(index, null);
		}

		protected void setFilter(final String filter)
		{
			new ForkJoinPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<ShopProduct> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = products;
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(products, new ProductFilter(filter))));
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

	public static class ProductFilter implements Predicate<ShopProduct>
	{
		private final String filter;

		public ProductFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(ShopProduct product)
		{
			ItemStack itemstack = product.getProductItem();

			if (GameData.getItemRegistry().getNameForObject(itemstack.getItem()).toLowerCase().contains(filter.toLowerCase()) ||
				itemstack.getUnlocalizedName().toLowerCase().contains(filter.toLowerCase()) ||
				itemstack.getDisplayName().toLowerCase().contains(filter.toLowerCase()) ||
				itemstack.getItem().getToolClasses(itemstack).contains(filter) ||
				product.getcost() == NumberUtils.toInt(filter, -1))
			{
				return true;
			}

			return false;
		}
	}
}