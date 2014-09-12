/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraft.block.Block;
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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

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
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveBiome;
import com.kegare.caveworld.client.config.GuiSelectBiome.SelectListener;
import com.kegare.caveworld.client.gui.GuiListSlot;
import com.kegare.caveworld.core.CaveBiomeManager;
import com.kegare.caveworld.core.CaveBiomeManager.CaveBiome;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.CaveBiomeComparator;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBiomesEntry extends GuiScreen implements SelectListener
{
	protected final GuiScreen parentScreen;

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
	protected GuiTextField blockField;
	protected GuiTextField metadataField;

	protected HoverChecker weightHoverChecker;
	protected HoverChecker blockHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	private final Map<Object, List<String>> hoverCache = Maps.newHashMap();

	public GuiBiomesEntry(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (biomeList == null)
		{
			biomeList = new BiomeList(this);
		}

		biomeList.func_148122_a(width, height, 32, height - (editMode ? 85 : 28));

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

		if (blockField == null)
		{
			blockField = new GuiTextField(fontRendererObj, 0, 0, 0, weightField.height);
			blockField.setMaxStringLength(100);
		}

		blockField.xPosition = weightField.xPosition;
		blockField.yPosition = weightField.yPosition + weightField.height + 5;
		blockField.width = weightField.width / 4 + weightField.width / 2 - 1;

		if (metadataField == null)
		{
			metadataField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			metadataField.setMaxStringLength(2);
		}

		metadataField.xPosition = blockField.xPosition + blockField.width + 3;
		metadataField.yPosition = blockField.yPosition;
		metadataField.width = weightField.width / 4 - 1;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(weightField);
			editFieldList.add(blockField);
			editFieldList.add(metadataField);
		}

		weightHoverChecker = new HoverChecker(weightField.yPosition - 1, weightField.yPosition + weightField.height, weightField.xPosition - maxLabelWidth - 12, weightField.xPosition - 10, 800);
		blockHoverChecker = new HoverChecker(blockField.yPosition - 1, blockField.yPosition + blockField.height, blockField.xPosition - maxLabelWidth - 12, blockField.xPosition - 10, 800);
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
						if (NumberUtils.toInt(weightField.getText()) <= 0)
						{
							return;
						}

						biomeList.selected.setGenWeight(NumberUtils.toInt(weightField.getText(), biomeList.selected.getGenWeight()));
						biomeList.selected.setTerrainBlock(new BlockEntry(blockField.getText(), NumberUtils.toInt(metadataField.getText())));

						hoverCache.remove(biomeList.selected);

						actionPerformed(cancelButton);

						biomeList.scrollToSelected();
					}
					else
					{
						CaveworldAPI.clearCaveBiomes();
						CaveworldAPI.getCaveBiomes().addAll(biomeList.biomes);

						if (Config.biomesCfg.hasChanged())
						{
							Config.biomesCfg.save();
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

						biomeList.scrollToSelected();

						weightField.setText(Integer.toString(biomeList.selected.getGenWeight()));
						blockField.setText(GameData.getBlockRegistry().getNameForObject(biomeList.selected.getTerrainBlock().getBlock()));
						metadataField.setText(Integer.toString(biomeList.selected.getTerrainBlock().getMetadata()));
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
					BiomeGenBase[] biomes = new BiomeGenBase[biomeList.biomes.size()];

					for (int i = 0; i < biomes.length; ++i)
					{
						biomes[i] = biomeList.biomes.get(i).getBiome();
					}

					mc.displayGuiScreen(new GuiSelectBiome(this).setHiddenBiomes(biomes));
					break;
				case 4:
					if (biomeList.biomes.remove(biomeList.selected))
					{
						int i = biomeList.contents.indexOf(biomeList.selected);

						biomeList.contents.remove(i);
						biomeList.selected = biomeList.contents.get(--i, biomeList.contents.get(++i, null));
					}

					break;
				case 5:
					for (Object entry : biomeList.biomes.toArray())
					{
						biomeList.selected = (ICaveBiome)entry;

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
	public void setResult(final List<Integer> result)
	{
		new ForkJoinPool().execute(new RecursiveAction()
		{
			@Override
			protected void compute()
			{
				BiomeGenBase biome;
				ICaveBiome entry;

				for (Integer id : result)
				{
					biome = BiomeGenBase.getBiome(id);

					if (biome != null)
					{
						for (ICaveBiome cave : biomeList.biomes)
						{
							if (cave.getBiome() == biome)
							{
								biome = null;
								biomeList.selected = cave;
								break;
							}
						}

						if (biome == null)
						{
							continue;
						}

						if (CaveBiomeManager.defaultMapping.containsKey(biome))
						{
							entry = CaveBiomeManager.defaultMapping.get(biome);
						}
						else
						{
							entry = new CaveBiome(biome, 10);
						}

						if (biomeList.biomes.addIfAbsent(entry))
						{
							biomeList.contents.addIfAbsent(entry);
							biomeList.selected = entry;

							entry.setGenWeight(entry.getGenWeight());
							entry.setTerrainBlock(entry.getTerrainBlock());
						}
					}
				}

				Comparator<ICaveBiome> comparator = new CaveBiomeComparator();

				biomeList.contents.sort(comparator);
				biomeList.biomes.sort(comparator);
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
			editButton.enabled = biomeList.selected != null;
			removeButton.enabled = editButton.enabled;

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
			drawCenteredString(fontRendererObj, biomeList.selected.getBiome().biomeName, width / 2, biomeList.bottom + 6, 0xFFFFFF);

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
		else if (weightHoverChecker.checkHover(mouseX, mouseY))
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
		else if (blockHoverChecker.checkHover(mouseX, mouseY))
		{
			if (!hoverCache.containsKey(blockHoverChecker))
			{
				List<String> hover = Lists.newArrayList();
				String key = Caveworld.CONFIG_LANG + "biomes.terrainBlock";
				hover.add(EnumChatFormatting.GRAY + I18n.format(key));
				hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

				hoverCache.put(blockHoverChecker, hover);
			}

			func_146283_a(hoverCache.get(blockHoverChecker), mouseX, mouseY);
		}
		else if (biomeList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			ICaveBiome entry = biomeList.contents.get(biomeList.func_148124_c(mouseX, mouseY), null);

			if (entry != null)
			{
				if (!hoverCache.containsKey(entry))
				{
					BiomeGenBase biome = entry.getBiome();
					BlockEntry block = entry.getTerrainBlock();
					List<String> info = Lists.newArrayList();

					info.add(EnumChatFormatting.DARK_GRAY + Integer.toString(biome.biomeID) + ": " + biome.biomeName);
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.genWeight") + ": " + entry.getGenWeight());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "biomes.terrainBlock") + ": " + GameData.getBlockRegistry().getNameForObject(block.getBlock()) + ", " + block.getMetadata());

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
		else if (metadataField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				metadataField.setText(Integer.toString(Math.max(NumberUtils.toInt(metadataField.getText()) - 1, 0)));
			}
			else if (i > 0)
			{
				metadataField.setText(Integer.toString(Math.min(NumberUtils.toInt(metadataField.getText()) + 1, 15)));
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

			if (!isShiftKeyDown() && blockField.isFocused())
			{
				blockField.setFocused(false);

				mc.displayGuiScreen(new GuiSelectBlock(this, blockField));
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
			if (weightField.isFocused())
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					weightField.setFocused(false);
				}

				if (CharUtils.isAsciiControl(c) || CharUtils.isAsciiNumeric(c))
				{
					weightField.textboxKeyTyped(c, code);
				}
			}
			else if (blockField.isFocused())
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					blockField.setFocused(false);
				}

				blockField.textboxKeyTyped(c, code);
			}
			else if  (metadataField.isFocused())
			{
				if (code == Keyboard.KEY_ESCAPE)
				{
					metadataField.setFocused(false);
				}

				if (CharUtils.isAsciiControl(c) || CharUtils.isAsciiNumeric(c))
				{
					metadataField.textboxKeyTyped(c, code);
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
					biomeList.selected = null;
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
				else if (code == Keyboard.KEY_DELETE && biomeList.selected != null)
				{
					actionPerformed(removeButton);
				}
			}
		}
	}

	protected static class BiomeList extends GuiListSlot
	{
		protected final GuiBiomesEntry parent;

		protected final ArrayListExtended<ICaveBiome> biomes = new ArrayListExtended(CaveworldAPI.getCaveBiomes());
		protected final ArrayListExtended<ICaveBiome> contents = new ArrayListExtended(biomes);

		private final Map<String, List<ICaveBiome>> filterCache = Maps.newHashMap();
		private final Set<Block> ignoredRender = CaveConfigGui.getIgnoredRenderBlocks();

		protected ICaveBiome selected;

		private BiomeList(GuiBiomesEntry parent)
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
			ICaveBiome entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			BiomeGenBase biome = entry.getBiome();

			parent.drawCenteredString(parent.fontRendererObj, biome.biomeName, width / 2, par3 + 3, 0xFFFFFF);

			if (parent.detailInfo.isChecked() || Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				parent.drawString(parent.fontRendererObj, Integer.toString(biome.biomeID), width / 2 - 100, par3 + 3, 0xE0E0E0);

				BlockEntry terrain = entry.getTerrainBlock();
				Block block = terrain.getBlock();

				if (!ignoredRender.contains(block) && Item.getItemFromBlock(block) != null)
				{
					try
					{
						ItemStack itemstack = new ItemStack(block, entry.getGenWeight(), terrain.getMetadata());

						GL11.glEnable(GL12.GL_RESCALE_NORMAL);
						RenderHelper.enableGUIStandardItemLighting();
						RenderItem.getInstance().renderItemAndEffectIntoGUI(parent.fontRendererObj, parent.mc.getTextureManager(), itemstack, width / 2 + 90, par3 + 1);
						RenderItem.getInstance().renderItemOverlayIntoGUI(parent.fontRendererObj, parent.mc.getTextureManager(), itemstack, width / 2 + 90, par3 + 1);
						RenderHelper.disableStandardItemLighting();
						GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					}
					catch (Exception e)
					{
						CaveLog.log(Level.WARN, e, "Failed to trying render item block into gui: %s", GameData.getBlockRegistry().getNameForObject(block));

						ignoredRender.add(block);
					}
				}
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
					List<ICaveBiome> result;

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
			try
			{
				BiomeGenBase biome = entry.getBiome();

				if (biome.biomeID == NumberUtils.toInt(filter, -1) ||
					biome.biomeName.toLowerCase().contains(filter.toLowerCase()) ||
					BiomeDictionary.isBiomeOfType(biome, Type.valueOf(filter.toUpperCase())))
				{
					return true;
				}
			}
			catch (Exception e) {}

			if (entry.getGenWeight() == NumberUtils.toInt(filter, -1))
			{
				return true;
			}

			Block block = entry.getTerrainBlock().getBlock();

			if (GameData.getBlockRegistry().getNameForObject(block).toLowerCase().contains(filter.toLowerCase()) ||
				block.getUnlocalizedName().toLowerCase().contains(filter.toLowerCase()) ||
				block.getLocalizedName().toLowerCase().contains(filter.toLowerCase()))
			{
				return true;
			}

			return false;
		}
	}
}