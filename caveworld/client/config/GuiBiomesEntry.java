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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveBiomeManager;
import caveworld.client.config.GuiSelectBiome.SelectListener;
import caveworld.client.gui.GuiListSlot;
import caveworld.core.CaveBiomeManager;
import caveworld.core.CaveBiomeManager.CaveBiome;
import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.ConfigCategory;

@SideOnly(Side.CLIENT)
public class GuiBiomesEntry extends GuiScreen implements SelectListener
{
	protected final GuiScreen parentScreen;
	protected final ICaveBiomeManager biomeManager;

	protected BiomeList biomeList;

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
	protected GuiTextField weightField;
	protected GuiTextField terrainBlockField;
	protected GuiTextField terrainMetaField;
	protected GuiTextField topBlockField;
	protected GuiTextField topMetaField;

	protected HoverChecker weightHoverChecker;
	protected HoverChecker terrainHoverChecker;
	protected HoverChecker topHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	private final Map<Object, List<String>> hoverCache = Maps.newHashMap();

	public GuiBiomesEntry(GuiScreen parent, ICaveBiomeManager manager)
	{
		this.parentScreen = parent;
		this.biomeManager = manager;
	}

	public boolean isReadOnly()
	{
		return biomeManager.isReadOnly();
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (biomeList == null)
		{
			biomeList = new BiomeList();
		}

		biomeList.func_148122_a(width, height, 32, height - (editMode ? 105 : 28));

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
		editButton.enabled = biomeList.selected != null;
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
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "biomes.genWeight"));
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "biomes.terrainBlock"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG + "biomes.topBlock"));
		editLabelList.add("");

		for (String key : editLabelList)
		{
			maxLabelWidth = Math.max(maxLabelWidth, fontRendererObj.getStringWidth(key));
		}

		if (weightField == null)
		{
			weightField = new GuiTextField(fontRendererObj, 0, 0, 0, 15);
			weightField.setMaxStringLength(3);
		}

		int i = maxLabelWidth + 8 + width / 2;
		weightField.xPosition = width / 2 - i / 2 + maxLabelWidth + 10;
		weightField.yPosition = biomeList.bottom + 1 + 20;
		weightField.width = width / 2 + i / 2 - 45 - weightField.xPosition + 40;

		if (terrainBlockField == null)
		{
			terrainBlockField = new GuiTextField(fontRendererObj, 0, 0, 0, weightField.height);
			terrainBlockField.setMaxStringLength(100);
		}

		terrainBlockField.xPosition = weightField.xPosition;
		terrainBlockField.yPosition = weightField.yPosition + weightField.height + 5;
		terrainBlockField.width = weightField.width / 4 + weightField.width / 2 - 1;

		if (terrainMetaField == null)
		{
			terrainMetaField = new GuiTextField(fontRendererObj, 0, 0, 0, terrainBlockField.height);
			terrainMetaField.setMaxStringLength(2);
		}

		terrainMetaField.xPosition = terrainBlockField.xPosition + terrainBlockField.width + 3;
		terrainMetaField.yPosition = terrainBlockField.yPosition;
		terrainMetaField.width = weightField.width / 4 - 1;

		if (topBlockField == null)
		{
			topBlockField = new GuiTextField(fontRendererObj, 0, 0, 0, terrainBlockField.height);
			topBlockField.setMaxStringLength(100);
		}

		topBlockField.xPosition = terrainBlockField.xPosition;
		topBlockField.yPosition = terrainMetaField.yPosition + terrainMetaField.height + 5;
		topBlockField.width = terrainBlockField.width;

		if (topMetaField == null)
		{
			topMetaField = new GuiTextField(fontRendererObj, 0, 0, 0, topBlockField.height);
		}

		topMetaField.xPosition = terrainMetaField.xPosition;
		topMetaField.yPosition = topBlockField.yPosition;
		topMetaField.width = terrainMetaField.width;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(weightField);
			editFieldList.add(terrainBlockField);
			editFieldList.add(terrainMetaField);
			editFieldList.add(topBlockField);
			editFieldList.add(topMetaField);
		}

		weightHoverChecker = new HoverChecker(weightField.yPosition - 1, weightField.yPosition + weightField.height, weightField.xPosition - maxLabelWidth - 12, weightField.xPosition - 10, 800);
		terrainHoverChecker = new HoverChecker(terrainBlockField.yPosition - 1, terrainBlockField.yPosition + terrainBlockField.height, terrainBlockField.xPosition - maxLabelWidth - 12, terrainBlockField.xPosition - 10, 800);
		topHoverChecker = new HoverChecker(topBlockField.yPosition - 1, topBlockField.yPosition + topBlockField.height, topBlockField.xPosition - maxLabelWidth - 12, topBlockField.xPosition - 10, 800);
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
							for (final ICaveBiome entry : biomeList.selected)
							{
								CaveUtils.getPool().execute(new RecursiveAction()
								{
									@Override
									protected void compute()
									{
										if (!Strings.isNullOrEmpty(weightField.getText()))
										{
											entry.setGenWeight(NumberUtils.toInt(weightField.getText(), entry.getGenWeight()));
										}

										if (!Strings.isNullOrEmpty(terrainBlockField.getText()))
										{
											entry.setTerrainBlock(new BlockEntry(terrainBlockField.getText(), NumberUtils.toInt(terrainMetaField.getText())));
										}

										if (!Strings.isNullOrEmpty(topBlockField.getText()))
										{
											entry.setTopBlock(new BlockEntry(topBlockField.getText(), NumberUtils.toInt(topMetaField.getText())));
										}

										hoverCache.remove(entry);
									}
								});
							}
						}

						actionPerformed(cancelButton);

						biomeList.scrollToTop();
						biomeList.scrollToSelected();
					}
					else
					{
						if (!isReadOnly())
						{
							CaveUtils.getPool().execute(new RecursiveAction()
							{
								@Override
								protected void compute()
								{
									biomeManager.clearCaveBiomes();

									ConfigCategory category;

									for (ICaveBiome entry : biomeList.biomes)
									{
										category = biomeManager.getConfig().getCategory(Integer.toString(entry.getBiome().biomeID));
										category.get("genWeight").set(entry.getGenWeight());
										category.get("terrainBlock").set(GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock()));
										category.get("terrainBlockMetadata").set(entry.getTerrainBlock().getMetadata());
										category.get("topBlock").set(GameData.getBlockRegistry().getNameForObject(entry.getTopBlock().getBlock()));
										category.get("topBlockMetadata").set(entry.getTopBlock().getMetadata());

										biomeManager.addCaveBiome(entry);
									}

									if (biomeManager.getConfig().hasChanged())
									{
										biomeManager.getConfig().save();
									}
								}
							});
						}

						actionPerformed(cancelButton);

						biomeList.selected.clear();
						biomeList.scrollToTop();
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

						biomeList.scrollToTop();
						biomeList.scrollToSelected();

						if (biomeList.selected.size() == 1)
						{
							ICaveBiome entry = biomeList.selected.iterator().next();

							weightField.setText(Integer.toString(entry.getGenWeight()));
							terrainBlockField.setText(GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock()));
							terrainMetaField.setText(Integer.toString(entry.getTerrainBlock().getMetadata()));
							topBlockField.setText(GameData.getBlockRegistry().getNameForObject(entry.getTopBlock().getBlock()));
							topMetaField.setText(Integer.toString(entry.getTopBlock().getMetadata()));
						}
						else
						{
							weightField.setText("");
							terrainBlockField.setText("");
							terrainMetaField.setText("");
							topBlockField.setText("");
							topMetaField.setText("");
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
						BiomeGenBase[] biomes = new BiomeGenBase[biomeList.biomes.size()];

						for (int i = 0; i < biomes.length; ++i)
						{
							biomes[i] = biomeList.biomes.get(i).getBiome();
						}

						mc.displayGuiScreen(new GuiSelectBiome(this).setHiddenBiomes(biomes));
					}

					break;
				case 4:
					if (!isReadOnly())
					{
						CaveUtils.getPool().execute(new RecursiveAction()
						{
							@Override
							protected void compute()
							{
								for (ICaveBiome entry : biomeList.selected)
								{
									if (biomeList.biomes.remove(entry))
									{
										biomeManager.getConfig().getCategory(Integer.toString(entry.getBiome().biomeID)).get("genWeight").set(0);

										biomeList.contents.remove(entry);
									}
								}

								biomeList.selected.clear();
							}
						});
					}

					break;
				case 5:
					if (!isReadOnly())
					{
						biomeList.selected.addAll(biomeList.biomes);

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
	public void setResult(final Set<BiomeGenBase> result)
	{
		if (editMode || isReadOnly())
		{
			return;
		}

		CaveUtils.getPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				ICaveBiome entry = null;

				biomeList.selected.clear();

				for (BiomeGenBase biome : result)
				{
					for (ICaveBiome cave : biomeList.biomes)
					{
						if (cave.getBiome() == biome)
						{
							entry = cave;
							biomeList.selected.add(cave);
							break;
						}
					}

					if (entry != null)
					{
						break;
					}

					if (CaveBiomeManager.presets.containsKey(biome))
					{
						entry = CaveBiomeManager.presets.get(biome);
					}
					else
					{
						entry = new CaveBiome(biome, 10);
					}

					if (biomeList.biomes.addIfAbsent(entry))
					{
						biomeList.contents.addIfAbsent(entry);
						biomeList.selected.add(entry);
					}

					entry = null;
				}

				Collections.sort(biomeList.contents, CaveBiome.caveBiomeComparator);
				Collections.sort(biomeList.biomes, CaveBiome.caveBiomeComparator);

				biomeList.scrollToTop();
				biomeList.scrollToSelected();
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
			editButton.enabled = !biomeList.selected.isEmpty();
			addButton.enabled = !isReadOnly();
			removeButton.enabled = !isReadOnly() && editButton.enabled;
			clearButton.enabled = !isReadOnly();

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		biomeList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "biomes"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		if (editMode)
		{
			List<String> names = Lists.newArrayList();

			for (ICaveBiome entry : biomeList.selected)
			{
				names.add(entry.getBiome().biomeName);
			}

			drawCenteredString(fontRendererObj, Joiner.on(", ").skipNulls().join(names), width / 2, biomeList.bottom + 6, 0xFFFFFF);

			GuiTextField textField;

			for (int i = 0; i < editFieldList.size(); ++i)
			{
				textField = editFieldList.get(i);
				textField.drawTextBox();
				drawString(fontRendererObj, editLabelList.get(i), textField.xPosition - maxLabelWidth - 10, textField.yPosition + 3, 0xBBBBBB);
			}

			if (weightHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(weightHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "biomes.genWeight";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(weightHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(weightHoverChecker), mouseX, mouseY);
			}
			else if (terrainHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(terrainHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "biomes.terrainBlock";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(terrainHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(terrainHoverChecker), mouseX, mouseY);
			}
			else if (topHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(topHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "biomes.topBlock";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(topHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(topHoverChecker), mouseX, mouseY);
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
		else if (biomeList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			ICaveBiome entry = biomeList.contents.get(biomeList.func_148124_c(mouseX, mouseY), null);

			if (entry != null)
			{
				if (!hoverCache.containsKey(entry))
				{
					BiomeGenBase biome = entry.getBiome();
					List<String> info = Lists.newArrayList();

					info.add(EnumChatFormatting.DARK_GRAY + Integer.toString(biome.biomeID) + ": " + biome.biomeName);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.genWeight") + ": " + entry.getGenWeight());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.terrainBlock") + ": " +
						GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock()) + ", " + entry.getTerrainBlock().getMetadata());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.topBlock") + ": " +
						GameData.getBlockRegistry().getNameForObject(entry.getTopBlock().getBlock()) + ", " + entry.getTopBlock().getMetadata());

					hoverCache.put(entry, info);
				}

				func_146283_a(hoverCache.get(entry), mouseX, mouseY);
			}
		}

		if (biomeList.selected.size() > 1 && mouseX <= 100 && mouseY <= 20)
		{
			drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.entry.selected", biomeList.selected.size()), 5, 5, 0xEFEFEF);
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

		if (weightField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				weightField.setText(Integer.toString(Math.max(NumberUtils.toInt(weightField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				weightField.setText(Integer.toString(Math.min(NumberUtils.toInt(weightField.getText()) + 1, 100)));
			}
		}
		else if (terrainMetaField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				terrainMetaField.setText(Integer.toString(Math.max(NumberUtils.toInt(terrainMetaField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				terrainMetaField.setText(Integer.toString(Math.min(NumberUtils.toInt(terrainMetaField.getText()) + 1, 15)));
			}
		}
		else if (topMetaField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				topMetaField.setText(Integer.toString(Math.max(NumberUtils.toInt(topMetaField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				topMetaField.setText(Integer.toString(Math.min(NumberUtils.toInt(topMetaField.getText()) + 1, 15)));
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int code)
	{
		super.mouseClicked(x, y, code);

		if (editMode)
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
				if (terrainBlockField.isFocused())
				{
					terrainBlockField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, terrainBlockField, terrainMetaField));
				}
				else if (topBlockField.isFocused())
				{
					topBlockField.setFocused(false);

					mc.displayGuiScreen(new GuiSelectBlock(this, topBlockField, topMetaField));
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
				if (textField.isFocused())
				{
					if (code == Keyboard.KEY_ESCAPE)
					{
						textField.setFocused(false);
					}

					if (textField == weightField || textField == terrainMetaField || textField == topMetaField)
					{
						if (CharUtils.isAsciiControl(c) || CharUtils.isAsciiNumeric(c))
						{
							textField.textboxKeyTyped(c, code);
						}
					}
					else
					{
						textField.textboxKeyTyped(c, code);
					}
				}
			}

			if (code == Keyboard.KEY_TAB)
			{
				String terrainBlock = terrainBlockField.getText();
				String terrainMeta = terrainMetaField.getText();

				terrainBlockField.setText(topBlockField.getText());
				terrainMetaField.setText(topMetaField.getText());
				topBlockField.setText(terrainBlock);
				topMetaField.setText(terrainMeta);
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
					actionPerformed(doneButton);
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
				else if (code == Keyboard.KEY_DELETE && biomeList.selected != null)
				{
					actionPerformed(removeButton);
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
		biomeList.currentPanoramaPaths = null;
	}

	class BiomeList extends GuiListSlot implements Comparator<ICaveBiome>
	{
		protected final ArrayListExtended<ICaveBiome> biomes = new ArrayListExtended();
		protected final ArrayListExtended<ICaveBiome> contents = new ArrayListExtended();
		protected final Set<ICaveBiome> selected = Sets.newTreeSet(this);

		private final Map<String, List<ICaveBiome>> filterCache = Maps.newHashMap();

		private BiomeList()
		{
			super(GuiBiomesEntry.this.mc, 0, 0, 0, 0, 22);
			this.biomes.addAll(biomeManager.getCaveBiomes());
			this.contents.addAll(biomes);
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (Iterator<ICaveBiome> iterator = selected.iterator(); iterator.hasNext();)
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
			ICaveBiome entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			drawCenteredString(fontRendererObj, entry.getBiome().biomeName, width / 2, par3 + 3, 0xFFFFFF);

			if (detailInfo.isChecked() || Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				BlockEntry block = entry.getTerrainBlock();

				if (Item.getItemFromBlock(block.getBlock()) != null)
				{
					CaveUtils.renderItemStack(mc, new ItemStack(block.getBlock(), 1, block.getMetadata()), width / 2 - 100, par3 + 1, false, true, Integer.toString(entry.getBiome().biomeID));
				}

				block = entry.getTopBlock();

				if (Item.getItemFromBlock(block.getBlock()) != null)
				{
					CaveUtils.renderItemStack(mc, new ItemStack(block.getBlock(), entry.getGenWeight(), block.getMetadata()), width / 2 + 90, par3 + 1, false, true, null);
				}
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			if (editMode)
			{
				return;
			}

			ICaveBiome entry = contents.get(index, null);

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
			ICaveBiome entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<ICaveBiome> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = biomes;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
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

		@Override
		public int compare(ICaveBiome o1, ICaveBiome o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(biomes.indexOf(o1), biomes.indexOf(o2));
			}

			return i;
		}
	}

	public static class BiomeFilter implements Predicate<ICaveBiome>
	{
		private final String filter;

		public BiomeFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(ICaveBiome entry)
		{
			return CaveUtils.biomeFilter(entry.getBiome(), filter) || entry.getGenWeight() == NumberUtils.toInt(filter, -1) ||
				CaveUtils.blockFilter(entry.getTerrainBlock(), filter) || CaveUtils.blockFilter(entry.getTopBlock(), filter);
		}
	}
}