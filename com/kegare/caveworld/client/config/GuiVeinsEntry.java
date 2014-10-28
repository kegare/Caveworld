/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.client.config.GuiSelectBlock.SelectListener;
import com.kegare.caveworld.client.gui.GuiListSlot;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.CaveUtils;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVeinsEntry extends GuiScreen implements SelectListener
{
	protected final GuiScreen parentScreen;

	protected VeinList veinList;

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
	protected GuiTextField blockField;
	protected GuiTextField blockMetaField;
	protected GuiTextField countField;
	protected GuiTextField weightField;
	protected GuiTextField rateField;
	protected GuiTextField minHeightField;
	protected GuiTextField maxHeightField;
	protected GuiTextField targetField;
	protected GuiTextField targetMetaField;
	protected GuiTextField biomesField;

	protected HoverChecker blockHoverChecker;
	protected HoverChecker countHoverChecker;
	protected HoverChecker weightHoverChecker;
	protected HoverChecker rateHoverChecker;
	protected HoverChecker heightHoverChecker;
	protected HoverChecker targetHoverChecker;
	protected HoverChecker biomesHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	private final Map<Object, List<String>> hoverCache = Maps.newHashMap();

	public GuiVeinsEntry(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (veinList == null)
		{
			veinList = new VeinList(this);
		}

		veinList.func_148122_a(width, height, 32, height - (editMode ? 170 : 28));

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
		editButton.enabled = veinList.selected != null;
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
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.block"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.genBlockCount"));
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.genWeight"));
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.genRate"));
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.genHeight"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.genTargetBlock"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "veins.genBiomes"));

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRendererObj.getStringWidth(key));
		}

		if (blockField == null)
		{
			blockField = new GuiTextField(fontRendererObj, 0, 0, 0, 15);
			blockField.setMaxStringLength(100);
		}

		int i = maxLabelWidth + 8 + width / 2;
		blockField.xPosition = width / 2 - i / 2 + maxLabelWidth + 10;
		blockField.yPosition = veinList.bottom + 5;
		int fieldWidth = width / 2 + i / 2 - 45 - blockField.xPosition + 40;
		blockField.width = fieldWidth / 4 + fieldWidth / 2 - 1;

		if (blockMetaField == null)
		{
			blockMetaField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			blockMetaField.setMaxStringLength(2);
		}

		blockMetaField.xPosition = blockField.xPosition + blockField.width + 3;
		blockMetaField.yPosition = blockField.yPosition;
		blockMetaField.width = fieldWidth / 4 - 1;

		if (countField == null)
		{
			countField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			countField.setMaxStringLength(3);
		}

		countField.xPosition = blockField.xPosition;
		countField.yPosition = blockField.yPosition + blockField.height + 5;
		countField.width = fieldWidth;

		if (weightField == null)
		{
			weightField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			weightField.setMaxStringLength(3);
		}

		weightField.xPosition = countField.xPosition;
		weightField.yPosition = countField.yPosition + countField.height + 5;
		weightField.width = fieldWidth;

		if (rateField == null)
		{
			rateField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			rateField.setMaxStringLength(3);
		}

		rateField.xPosition = weightField.xPosition;
		rateField.yPosition = weightField.yPosition + weightField.height + 5;
		rateField.width = weightField.width;

		if (minHeightField == null)
		{
			minHeightField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			minHeightField.setMaxStringLength(3);
		}

		minHeightField.xPosition = rateField.xPosition;
		minHeightField.yPosition = rateField.yPosition + rateField.height + 5;
		minHeightField.width = fieldWidth / 2 - 1;

		if (maxHeightField == null)
		{
			maxHeightField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			maxHeightField.setMaxStringLength(3);
		}

		maxHeightField.xPosition = minHeightField.xPosition + minHeightField.width + 3;
		maxHeightField.yPosition = minHeightField.yPosition;
		maxHeightField.width = minHeightField.width;

		if (targetField == null)
		{
			targetField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			targetField.setMaxStringLength(100);
		}

		targetField.xPosition = minHeightField.xPosition;
		targetField.yPosition = maxHeightField.yPosition + maxHeightField.height + 5;
		targetField.width = fieldWidth / 4 + fieldWidth / 2 - 1;

		if (targetMetaField == null)
		{
			targetMetaField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			targetMetaField.setMaxStringLength(2);
		}

		targetMetaField.xPosition = targetField.xPosition + targetField.width + 3;
		targetMetaField.yPosition = targetField.yPosition;
		targetMetaField.width = fieldWidth / 4 - 1;

		if (biomesField == null)
		{
			biomesField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			biomesField.setMaxStringLength(800);
		}

		biomesField.xPosition = targetField.xPosition;
		biomesField.yPosition = targetField.yPosition + targetField.height + 5;
		biomesField.width = fieldWidth;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(blockField);
			editFieldList.add(blockMetaField);
			editFieldList.add(countField);
			editFieldList.add(weightField);
			editFieldList.add(rateField);
			editFieldList.add(minHeightField);
			editFieldList.add(maxHeightField);
			editFieldList.add(targetField);
			editFieldList.add(targetMetaField);
			editFieldList.add(biomesField);
		}

		blockHoverChecker = new HoverChecker(blockField.yPosition - 1, blockField.yPosition + blockField.height, blockField.xPosition - maxLabelWidth - 12, blockField.xPosition - 10, 800);
		countHoverChecker = new HoverChecker(countField.yPosition - 1, countField.yPosition + countField.height, countField.xPosition - maxLabelWidth - 12, countField.xPosition - 10, 800);
		weightHoverChecker = new HoverChecker(weightField.yPosition - 1, weightField.yPosition + weightField.height, weightField.xPosition - maxLabelWidth - 12, weightField.xPosition - 10, 800);
		rateHoverChecker = new HoverChecker(rateField.yPosition - 1, rateField.yPosition + rateField.height, rateField.xPosition - maxLabelWidth - 12, rateField.xPosition - 10, 800);
		heightHoverChecker = new HoverChecker(minHeightField.yPosition - 1, minHeightField.yPosition + minHeightField.height, minHeightField.xPosition - maxLabelWidth - 12, minHeightField.xPosition - 10, 800);
		targetHoverChecker = new HoverChecker(targetField.yPosition - 1, targetField.yPosition + targetField.height, targetField.xPosition - maxLabelWidth - 12, targetField.xPosition - 10, 800);
		biomesHoverChecker = new HoverChecker(biomesField.yPosition - 1, biomesField.yPosition + biomesField.height, biomesField.xPosition - maxLabelWidth - 12, biomesField.xPosition - 10, 800);
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
						for (ICaveVein entry : veinList.selected)
						{
							if (!Strings.isNullOrEmpty(blockField.getText()))
							{
								entry.setBlock(new BlockEntry(blockField.getText(), NumberUtils.toInt(blockMetaField.getText())));
							}

							if (!Strings.isNullOrEmpty(countField.getText()))
							{
								entry.setGenBlockCount(NumberUtils.toInt(countField.getText(), entry.getGenBlockCount()));
							}

							if (!Strings.isNullOrEmpty(weightField.getText()))
							{
								entry.setGenWeight(NumberUtils.toInt(weightField.getText(), entry.getGenWeight()));
							}

							if (!Strings.isNullOrEmpty(rateField.getText()))
							{
								entry.setGenRate(NumberUtils.toInt(rateField.getText(), entry.getGenRate()));
							}

							if (!Strings.isNullOrEmpty(minHeightField.getText()))
							{
								entry.setGenMinHeight(NumberUtils.toInt(minHeightField.getText(), entry.getGenMinHeight()));
							}

							if (!Strings.isNullOrEmpty(maxHeightField.getText()))
							{
								entry.setGenMaxHeight(NumberUtils.toInt(maxHeightField.getText(), entry.getGenMaxHeight()));
							}

							if (!Strings.isNullOrEmpty(targetField.getText()))
							{
								entry.setGenTargetBlock(new BlockEntry(targetField.getText(), NumberUtils.toInt(targetMetaField.getText())));
							}

							if (!Strings.isNullOrEmpty(biomesField.getText()))
							{
								List<Integer> ids = Lists.newArrayList();

								for (String str : Splitter.on(',').trimResults().omitEmptyStrings().split(biomesField.getText()))
								{
									if (NumberUtils.isNumber(str))
									{
										ids.add(Integer.parseInt(str));
									}
								}

								Collections.sort(ids);

								entry.setGenBiomes(Ints.toArray(ids));
							}

							hoverCache.remove(entry);
						}

						actionPerformed(cancelButton);

						veinList.scrollToTop();
						veinList.scrollToSelected();
					}
					else
					{
						boolean flag = CaveworldAPI.getCaveVeins().size() != veinList.veins.size();

						CaveworldAPI.clearCaveVeins();

						if (flag)
						{
							try
							{
								FileUtils.forceDelete(new File(Config.veinsCfg.toString()));

								Config.veinsCfg.load();
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}

						for (ICaveVein vein : veinList.veins)
						{
							CaveworldAPI.addCaveVein(vein);
						}

						if (Config.veinsCfg.hasChanged())
						{
							Config.veinsCfg.save();
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

						veinList.scrollToTop();
						veinList.scrollToSelected();

						if (veinList.selected.size() == 1)
						{
							ICaveVein entry = veinList.selected.iterator().next();

							blockField.setText(GameData.getBlockRegistry().getNameForObject(entry.getBlock().getBlock()));
							blockMetaField.setText(Integer.toString(entry.getBlock().getMetadata()));
							countField.setText(Integer.toString(entry.getGenBlockCount()));
							weightField.setText(Integer.toString(entry.getGenWeight()));
							rateField.setText(Integer.toString(entry.getGenRate()));
							minHeightField.setText(Integer.toString(entry.getGenMinHeight()));
							maxHeightField.setText(Integer.toString(entry.getGenMaxHeight()));
							targetField.setText(GameData.getBlockRegistry().getNameForObject(entry.getGenTargetBlock().getBlock()));
							targetMetaField.setText(Integer.toString(entry.getGenTargetBlock().getMetadata()));
							biomesField.setText(Ints.join(", ", entry.getGenBiomes()));
						}
						else
						{
							blockField.setText("");
							blockMetaField.setText("");
							countField.setText("");
							weightField.setText("");
							rateField.setText("");
							minHeightField.setText("");
							maxHeightField.setText("");
							targetField.setText("");
							targetMetaField.setText("");
							biomesField.setText("");
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
					mc.displayGuiScreen(new GuiSelectBlock(this));
					break;
				case 4:
					for (ICaveVein entry : veinList.selected)
					{
						if (veinList.veins.remove(entry))
						{
							veinList.contents.remove(entry);
						}
					}

					veinList.selected.clear();
					break;
				case 5:
					veinList.selected.addAll(veinList.veins);

					actionPerformed(removeButton);
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
	public void onSelected(final Set<BlockEntry> result)
	{
		if (editMode)
		{
			return;
		}

		new ForkJoinPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				veinList.selected.clear();

				for (BlockEntry block : result)
				{
					ICaveVein entry = new CaveVein(block, 1, 1, 100, 0, 255);

					if (veinList.veins.addIfAbsent(entry))
					{
						veinList.contents.addIfAbsent(entry);
						veinList.selected.add(entry);
					}
				}

				veinList.scrollToTop();
				veinList.scrollToSelected();
			}
		});
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
			editButton.enabled = !veinList.selected.isEmpty();
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		veinList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "veins"), width / 2, 15, 0xFFFFFF);

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

			if (blockHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(blockHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.block";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(blockHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(blockHoverChecker), mouseX, mouseY);
			}
			else if (countHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(countHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.genBlockCount";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(countHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(countHoverChecker), mouseX, mouseY);
			}
			else if (weightHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(weightHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.genWeight";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(weightHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(weightHoverChecker), mouseX, mouseY);
			}
			else if (rateHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(rateHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.genRate";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(rateHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(rateHoverChecker), mouseX, mouseY);
			}
			else if (heightHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(heightHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.genHeight";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(heightHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(heightHoverChecker), mouseX, mouseY);
			}
			else if (targetHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(targetHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.genTargetBlock";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(targetHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(targetHoverChecker), mouseX, mouseY);
			}
			else if (biomesHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(biomesHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "veins.genBiomes";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(biomesHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(biomesHoverChecker), mouseX, mouseY);
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
		else if (veinList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			ICaveVein entry = veinList.contents.get(veinList.func_148124_c(mouseX, mouseY), null);

			if (entry != null)
			{
				if (!hoverCache.containsKey(entry))
				{
					List<String> info = Lists.newArrayList();

					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "veins.block") + ": " +
						GameData.getBlockRegistry().getNameForObject(entry.getBlock().getBlock()) + ", " + entry.getBlock().getMetadata());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "veins.genBlockCount") + ": " + entry.getGenBlockCount());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "veins.genWeight") + ": " + entry.getGenWeight());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "veins.genRate") + ": " + entry.getGenRate());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "veins.genHeight") + ": " + entry.getGenMinHeight() + ", " + entry.getGenMaxHeight());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "veins.genTargetBlock") + ": " +
						GameData.getBlockRegistry().getNameForObject(entry.getGenTargetBlock().getBlock()) + ", " + entry.getGenTargetBlock().getMetadata());

					if (entry.getGenBiomes().length > 0)
					{
						List<String> biomes = fontRendererObj.listFormattedStringToWidth(I18n.format(Caveworld.CONFIG_LANG + "veins.genBiomes") + ": " + Ints.join(", ", entry.getGenBiomes()), 300);

						for (String str : biomes)
						{
							info.add(EnumChatFormatting.GRAY + str);
						}
					}

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

		if (blockMetaField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				blockMetaField.setText(Integer.toString(Math.max(NumberUtils.toInt(blockMetaField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				blockMetaField.setText(Integer.toString(Math.min(NumberUtils.toInt(blockMetaField.getText()) + 1, 15)));
			}
		}
		else if (countField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				countField.setText(Integer.toString(Math.max(NumberUtils.toInt(countField.getText()) - 1, 1)));
			}
			else if (i > 0)
			{
				countField.setText(Integer.toString(Math.min(NumberUtils.toInt(countField.getText()) + 1, 500)));
			}
		}
		else if (weightField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				weightField.setText(Integer.toString(Math.max(NumberUtils.toInt(weightField.getText()) - 1, 1)));
			}
			else if (i > 0)
			{
				weightField.setText(Integer.toString(Math.min(NumberUtils.toInt(weightField.getText()) + 1, 100)));
			}
		}
		else if (rateField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				rateField.setText(Integer.toString(Math.max(NumberUtils.toInt(rateField.getText()) - 1, 1)));
			}
			else if (i > 0)
			{
				rateField.setText(Integer.toString(Math.min(NumberUtils.toInt(rateField.getText()) + 1, 100)));
			}
		}
		else if (minHeightField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				minHeightField.setText(Integer.toString(Math.max(NumberUtils.toInt(minHeightField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				minHeightField.setText(Integer.toString(Math.min(NumberUtils.toInt(minHeightField.getText()) + 1, NumberUtils.toInt(maxHeightField.getText()) - 1)));
			}
		}
		else if (maxHeightField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				maxHeightField.setText(Integer.toString(Math.max(NumberUtils.toInt(maxHeightField.getText()) - 1, NumberUtils.toInt(minHeightField.getText()) + 1)));
			}
			else if (i > 0)
			{
				maxHeightField.setText(Integer.toString(Math.min(NumberUtils.toInt(maxHeightField.getText()) + 1, 255)));
			}
		}
		else if (targetMetaField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				targetMetaField.setText(Integer.toString(Math.max(NumberUtils.toInt(targetMetaField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				targetMetaField.setText(Integer.toString(Math.min(NumberUtils.toInt(targetMetaField.getText()) + 1, 15)));
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
				if (blockField.isFocused())
				{
					blockField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, blockField, blockMetaField));
				}
				else if (targetField.isFocused())
				{
					targetField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, targetField, targetMetaField));
				}
				else if (biomesField.isFocused())
				{
					biomesField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBiome(this, biomesField));
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
					if (textField != blockField && textField != targetField && textField != biomesField)
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
					veinList.setFilter(null);
				}
				else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
				{
					veinList.setFilter(text);
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
					veinList.selected.clear();
				}
				else if (code == Keyboard.KEY_TAB)
				{
					if (++veinList.nameType > 2)
					{
						veinList.nameType = 0;
					}
				}
				else if (code == Keyboard.KEY_UP)
				{
					if (isCtrlKeyDown())
					{
						veinList.contents.swapTo(veinList.contents.indexOf(veinList.selected), -1);
						veinList.veins.swapTo(veinList.veins.indexOf(veinList.selected), -1);

						veinList.scrollToSelected();
					}
					else
					{
						veinList.scrollUp();
					}
				}
				else if (code == Keyboard.KEY_DOWN)
				{
					if (isCtrlKeyDown())
					{
						veinList.contents.swapTo(veinList.contents.indexOf(veinList.selected), 1);
						veinList.veins.swapTo(veinList.veins.indexOf(veinList.selected), 1);

						veinList.scrollToSelected();
					}
					else
					{
						veinList.scrollDown();
					}
				}
				else if (code == Keyboard.KEY_HOME)
				{
					veinList.scrollToTop();
				}
				else if (code == Keyboard.KEY_END)
				{
					veinList.scrollToEnd();
				}
				else if (code == Keyboard.KEY_SPACE)
				{
					veinList.scrollToSelected();
				}
				else if (code == Keyboard.KEY_PRIOR)
				{
					veinList.scrollToPrev();
				}
				else if (code == Keyboard.KEY_NEXT)
				{
					veinList.scrollToNext();
				}
				else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
				{
					filterTextField.setFocused(true);
				}
				else if (code == Keyboard.KEY_DELETE && veinList.selected != null)
				{
					actionPerformed(removeButton);
				}
				else if (code == Keyboard.KEY_C && isCtrlKeyDown())
				{
					veinList.copied.clear();

					for (ICaveVein entry : veinList.veins)
					{
						veinList.copied.add(new CaveVein(entry));
					}
				}
				else if (code == Keyboard.KEY_X && isCtrlKeyDown())
				{
					keyTyped(Character.MIN_VALUE, Keyboard.KEY_C);

					actionPerformed(removeButton);
				}
				else if (code == Keyboard.KEY_V && isCtrlKeyDown() && veinList.copied != null)
				{
					for (ICaveVein vein : veinList.copied)
					{
						ICaveVein entry = new CaveVein(vein);

						if (veinList.veins.addIfAbsent(entry))
						{
							veinList.contents.addIfAbsent(entry);
						}
					}

					veinList.scrollToTop();
					veinList.scrollToSelected();
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
		veinList.currentPanoramaPaths = null;
	}

	protected static class VeinList extends GuiListSlot implements Comparator<ICaveVein>
	{
		protected final GuiVeinsEntry parent;

		protected final ArrayListExtended<ICaveVein> veins = new ArrayListExtended(CaveworldAPI.getCaveVeins());
		protected final ArrayListExtended<ICaveVein> contents = new ArrayListExtended(veins);
		protected final Set<ICaveVein> selected = Sets.newTreeSet(this);
		protected final Set<ICaveVein> copied = Sets.newTreeSet(this);

		private final Map<String, List<ICaveVein>> filterCache = Maps.newHashMap();

		protected int nameType;

		private VeinList(GuiVeinsEntry parent)
		{
			super(parent.mc, 0, 0, 0, 0, 22);
			this.parent = parent;
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (Iterator<ICaveVein> iterator = selected.iterator(); iterator.hasNext();)
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
			ICaveVein entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			BlockEntry block = entry.getBlock();
			String name = null;

			try
			{
				switch (nameType)
				{
					case 1:
						name = GameData.getBlockRegistry().getNameForObject(block.getBlock());
						break;
					case 2:
						name = block.getBlock().getUnlocalizedName();
						name = name.substring(name.indexOf(".") + 1);
						break;
					default:
						name = block.getBlock().getLocalizedName();
						break;
				}
			}
			catch (Throwable e) {}

			if (!Strings.isNullOrEmpty(name))
			{
				parent.drawCenteredString(parent.fontRendererObj, name, width / 2, par3 + 3, 0xFFFFFF);
			}

			if (parent.detailInfo.isChecked())
			{
				Item item = Item.getItemFromBlock(block.getBlock());

				if (item != null)
				{
					CaveUtils.renderItemStack(mc, new ItemStack(block.getBlock(), entry.getGenBlockCount(), block.getMetadata()), width / 2 - 100, par3 + 1, true, null);
				}

				block = entry.getGenTargetBlock();
				item = Item.getItemFromBlock(block.getBlock());

				if (item != null)
				{
					CaveUtils.renderItemStack(mc, new ItemStack(block.getBlock(), entry.getGenWeight(), block.getMetadata()), width / 2 + 90, par3 + 1, true, null);
				}
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			if (parent.editMode)
			{
				return;
			}

			ICaveVein entry = contents.get(index, null);

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
			ICaveVein entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			new ForkJoinPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<ICaveVein> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = veins;
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(veins, new VeinFilter(filter))));
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
		public int compare(ICaveVein o1, ICaveVein o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(veins.indexOf(o1), veins.indexOf(o2));
			}

			return i;
		}
	}

	public static class VeinFilter implements Predicate<ICaveVein>
	{
		private final String filter;

		public VeinFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(ICaveVein vein)
		{
			if (CaveUtils.blockFilter(vein.getBlock(), filter) || CaveUtils.blockFilter(vein.getGenTargetBlock(), filter))
			{
				return true;
			}

			for (int id : vein.getGenBiomes())
			{
				if (CaveUtils.biomeFilter(BiomeGenBase.getBiome(id), filter))
				{
					return true;
				}
			}

			return false;
		}
	}
}