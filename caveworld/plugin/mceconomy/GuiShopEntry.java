/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import caveworld.client.config.CaveConfigGui;
import caveworld.client.config.GuiSelectItem;
import caveworld.client.config.GuiSelectItem.SelectListener;
import caveworld.client.config.GuiSelectMinerRank;
import caveworld.client.gui.GuiListSlot;
import caveworld.core.CaverManager;
import caveworld.core.CaverManager.MinerRank;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.common.OpRemoteCheckMessage;
import caveworld.plugin.mceconomy.ShopProductManager.ShopProduct;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
import caveworld.util.ItemEntry;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@SideOnly(Side.CLIENT)
public class GuiShopEntry extends GuiScreen implements SelectListener
{
	protected final GuiScreen parentScreen;
	protected final IShopProductManager productManager;

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
	protected GuiTextField minerRankField;

	protected HoverChecker itemHoverChecker;
	protected HoverChecker stackHoverChecker;
	protected HoverChecker costHoverChecker;
	protected HoverChecker minerRankHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	private final Map<Object, List<String>> hoverCache = Maps.newHashMap();

	public GuiShopEntry(GuiScreen parent, IShopProductManager manager)
	{
		this.parentScreen = parent;
		this.productManager = manager;
	}

	public boolean isReadOnly()
	{
		if (mc.thePlayer != null && !mc.isIntegratedServerRunning())
		{
			return !OpRemoteCheckMessage.operator;
		}

		return productManager.isReadOnly();
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (productList == null)
		{
			productList = new ProductList();

			if (mc.thePlayer != null && !mc.isIntegratedServerRunning())
			{
				CaveNetworkRegistry.sendToServer(new OpRemoteCheckMessage());
			}
		}

		productList.func_148122_a(width, height, 32, height - (editMode ? 110 : 28));

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
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "shop.minerRank"));

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

		if (minerRankField == null)
		{
			minerRankField = new GuiTextField(fontRendererObj, 0, 0, 0, itemField.height);
		}

		minerRankField.xPosition = costField.xPosition;
		minerRankField.yPosition = costField.yPosition + costField.height + 5;
		minerRankField.width = fieldWidth;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(itemField);
			editFieldList.add(damageField);
			editFieldList.add(stackField);
			editFieldList.add(costField);
			editFieldList.add(minerRankField);
		}

		itemHoverChecker = new HoverChecker(itemField.yPosition - 1, itemField.yPosition + itemField.height, itemField.xPosition - maxLabelWidth - 12, itemField.xPosition - 10, 800);
		stackHoverChecker = new HoverChecker(stackField.yPosition - 1, stackField.yPosition + stackField.height, stackField.xPosition - maxLabelWidth - 12, stackField.xPosition - 10, 800);
		costHoverChecker = new HoverChecker(costField.yPosition - 1, costField.yPosition + costField.height, costField.xPosition - maxLabelWidth - 12, costField.xPosition - 10, 800);
		minerRankHoverChecker = new HoverChecker(minerRankField.yPosition - 1, minerRankField.yPosition + minerRankField.height, minerRankField.xPosition - maxLabelWidth - 12, minerRankField.xPosition - 10, 800);
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
						if (!isReadOnly())
						{
							for (IShopProduct entry : productList.selected)
							{
								if (!Strings.isNullOrEmpty(itemField.getText()))
								{
									ItemStack item = new ItemStack(GameData.getItemRegistry().getObject(itemField.getText()), NumberUtils.toInt(stackField.getText(), 1), NumberUtils.toInt(damageField.getText()));

									if (item.getItem() != null)
									{
										entry.setItem(item);
									}
								}

								if (!Strings.isNullOrEmpty(costField.getText()))
								{
									entry.setCost(NumberUtils.toInt(costField.getText()));
								}

								if (!Strings.isNullOrEmpty(minerRankField.getText()))
								{
									entry.setMinerRank(NumberUtils.toInt(minerRankField.getText()));
								}

								hoverCache.remove(entry);
							}
						}

						actionPerformed(cancelButton);

						productList.scrollToTop();
						productList.scrollToSelected();
					}
					else
					{
						if (!isReadOnly())
						{
							if (mc.thePlayer == null || mc.isIntegratedServerRunning())
							{
								boolean flag = productManager.getProducts().size() != productList.products.size();

								productManager.getProducts().clear();

								if (flag)
								{
									try
									{
										FileUtils.forceDelete(new File(productManager.getConfig().toString()));

										productManager.getConfig().load();
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}
								}

								for (IShopProduct product : productList.products)
								{
									productManager.addShopProduct(product);
								}

								Config.saveConfig(productManager.getConfig());
							}
							else
							{
								productManager.getProducts().clear();

								for (IShopProduct product : productList.products)
								{
									productManager.getProducts().add(product);
								}

								CaveNetworkRegistry.sendToServer(new ProductAdjustMessage(productManager));
							}
						}

						actionPerformed(cancelButton);

						productList.selected.clear();
						productList.scrollToTop();
					}

					break;
				case 1:
					if (editMode)
					{
						actionPerformed(cancelButton);
					}
					else if (!productList.selected.isEmpty())
					{
						editMode = true;
						initGui();

						productList.scrollToTop();
						productList.scrollToSelected();

						if (productList.selected.size() > 1)
						{
							itemField.setText("");
							damageField.setText("");
							stackField.setText("");
							costField.setText("");
							minerRankField.setText("");
						}
						else for (IShopProduct entry : productList.selected)
						{
							itemField.setText(GameData.getItemRegistry().getNameForObject(entry.getItem().getItem()));
							damageField.setText(Integer.toString(entry.getItem().getItemDamage()));
							stackField.setText(Integer.toString(entry.getItem().stackSize));
							costField.setText(Integer.toString(entry.getCost()));
							minerRankField.setText(Integer.toString(entry.getMinerRank()));
						}
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
					if (!isReadOnly())
					{
						mc.displayGuiScreen(new GuiSelectItem(this));
					}

					break;
				case 4:
					if (!isReadOnly())
					{
						for (IShopProduct entry : productList.selected)
						{
							if (productList.products.remove(entry))
							{
								productList.contents.remove(entry);
							}
						}

						productList.selected.clear();
					}

					break;
				case 5:
					if (!isReadOnly())
					{
						productList.selected.addAll(productList.products);

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
	public void onItemSelected(final Set<ItemEntry> result)
	{
		if (editMode || isReadOnly())
		{
			return;
		}

		productList.selected.clear();

		for (ItemEntry item : result)
		{
			ShopProduct product = new ShopProduct(item.getItemStack(), 0);

			if (productList.products.addIfAbsent(product))
			{
				productList.contents.addIfAbsent(product);
				productList.selected.add(product);
			}
		}

		productList.scrollToTop();
		productList.scrollToSelected();
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
			editButton.enabled = !productList.selected.isEmpty();
			addButton.enabled = !isReadOnly();
			removeButton.enabled = !isReadOnly() && editButton.enabled;
			clearButton.enabled = !isReadOnly();

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

			if (itemHoverChecker.checkHover(mouseX, mouseY))
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
			else if (minerRankHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(minerRankHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "shop.minerRank";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(minerRankHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(minerRankHoverChecker), mouseX, mouseY);
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
		else if (productList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			IShopProduct entry = productList.contents.get(productList.func_148124_c(mouseX, mouseY), null);

			if (entry != null)
			{
				if (!hoverCache.containsKey(entry))
				{
					List<String> info = Lists.newArrayList();

					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.item") + ": " +
						GameData.getItemRegistry().getNameForObject(entry.getItem().getItem()) + ", " + entry.getItem().getItemDamage());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.stackSize") + ": " + entry.getItem().stackSize);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.productCost") + ": " + entry.getCost());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "shop.minerRank") + ": " + entry.getMinerRank());

					hoverCache.put(entry, info);
				}

				func_146283_a(hoverCache.get(entry), mouseX, mouseY);
			}
		}

		if (productList.selected.size() > 1 && mouseX <= 100 && mouseY <= 20)
		{
			drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.entry.selected", productList.selected.size()), 5, 5, 0xEFEFEF);
		}
	}

	@Override
	public void handleMouseInput()
	{
		super.handleMouseInput();

		if (isReadOnly())
		{
			return;
		}

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

		if (code == 1)
		{
			actionPerformed(editButton);
		}
		else if (editMode)
		{
			if (isReadOnly())
			{
				return;
			}

			for (GuiTextField textField : editFieldList)
			{
				textField.mouseClicked(x, y, code);
			}

			if (!isShiftKeyDown())
			{
				if (itemField.isFocused())
				{
					itemField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectItem(this, itemField, damageField));
				}
				else if (minerRankField.isFocused())
				{
					minerRankField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectMinerRank(this, minerRankField));
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
			if (isReadOnly())
			{
				return;
			}

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
					productList.selected.clear();
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
					if (isCtrlKeyDown())
					{
						Collections.sort(productList.selected, productList);

						for (IShopProduct product : productList.selected)
						{
							productList.contents.swapTo(productList.contents.indexOf(product), -1);
							productList.products.swapTo(productList.products.indexOf(product), -1);
						}

						productList.scrollToTop();
						productList.scrollToSelected();
					}
					else
					{
						productList.scrollUp();
					}
				}
				else if (code == Keyboard.KEY_DOWN)
				{
					if (isCtrlKeyDown())
					{
						Collections.sort(productList.selected, productList);
						Collections.reverse(productList.selected);

						for (IShopProduct product : productList.selected)
						{
							productList.contents.swapTo(productList.contents.indexOf(product), 1);
							productList.products.swapTo(productList.products.indexOf(product), 1);
						}

						productList.scrollToTop();
						productList.scrollToSelected();
					}
					else
					{
						productList.scrollDown();
					}
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
				else if (code == Keyboard.KEY_C && isCtrlKeyDown())
				{
					Collections.sort(productList.selected, productList);

					productList.copied.clear();

					for (IShopProduct entry : productList.selected)
					{
						productList.copied.add(new ShopProduct(entry));
					}
				}
				else if (code == Keyboard.KEY_X && isCtrlKeyDown())
				{
					keyTyped(Character.MIN_VALUE, Keyboard.KEY_C);

					actionPerformed(removeButton);
				}
				else if (code == Keyboard.KEY_V && isCtrlKeyDown() && !productList.copied.isEmpty())
				{
					int index1 = -1;
					int index2 = -1;
					int i = 0;

					for (IShopProduct product : productList.copied)
					{
						IShopProduct entry = new ShopProduct(product);

						if (productList.products.add(entry) && productList.contents.add(entry) && !productList.selected.isEmpty())
						{
							if (index1 < 0)
							{
								index1 = productList.contents.indexOf(productList.selected.get(productList.selected.size() - 1)) + 1;
							}

							Collections.swap(productList.contents, index1 + i, productList.contents.indexOf(entry));

							if (index2 < 0)
							{
								index2 = productList.products.indexOf(productList.selected.get(productList.selected.size() - 1)) + 1;
							}

							Collections.swap(productList.products, index2 + i, productList.products.indexOf(entry));

							++i;
						}
					}

					productList.scrollToTop();
					productList.scrollToSelected();
				}
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed()
	{
		productList.currentPanoramaPaths = null;
	}

	class ProductList extends GuiListSlot implements Comparator<IShopProduct>
	{
		protected final ArrayListExtended<IShopProduct> products = new ArrayListExtended();
		protected final ArrayListExtended<IShopProduct> contents = new ArrayListExtended();
		protected final List<IShopProduct> selected = Lists.newArrayList();
		protected final List<IShopProduct> copied = Lists.newArrayList();
		protected final Map<String, List<IShopProduct>> filterCache = Maps.newHashMap();

		protected int nameType;

		protected ProductList()
		{
			super(GuiShopEntry.this.mc, 0, 0, 0, 0, 22);

			for (IShopProduct product : productManager.getProducts())
			{
				products.addIfAbsent(product);
				contents.addIfAbsent(product);
			}
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (IShopProduct entry : selected)
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
			IShopProduct entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			ItemStack itemstack = entry.getItem();
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
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 3, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				CaveUtils.renderItemStack(mc, itemstack, width / 2 - 100, par3 + 1, false, true, null);

				MinerRank rank = CaverManager.getRank(entry.getMinerRank());

				if (rank.getRank() > 0)
				{
					CaveUtils.renderItemStack(mc, rank.getRenderItemStack(), width / 2 + 90, par3 - 1, true, true, null);
				}

				name = Integer.toString(entry.getCost());

				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				drawString(fontRendererObj, name, width / 2 + 107 - fontRendererObj.getStringWidth(name), par3 + 8, 0xD0D0D0);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			if (editMode)
			{
				return;
			}

			IShopProduct entry = contents.get(index, null);

			if (entry != null && !selected.remove(entry))
			{
				if (!isCtrlKeyDown())
				{
					selected.clear();
				}

				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			IShopProduct entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<IShopProduct> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = products;
					}
					else if (filter.equals("selected"))
					{
						result = selected;
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

		@Override
		public int compare(IShopProduct o1, IShopProduct o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(products.indexOf(o1), products.indexOf(o2));
			}

			return i;
		}
	}

	public static class ProductFilter implements Predicate<IShopProduct>
	{
		private final String filter;

		public ProductFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(IShopProduct product)
		{
			return CaveUtils.itemFilter(product.getItem(), filter) || product.getCost() == NumberUtils.toInt(filter, -1);
		}
	}
}