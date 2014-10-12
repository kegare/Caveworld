/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.config;

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

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kegare.caveworld.client.gui.GuiListSlot;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.BlockComparator;
import com.kegare.caveworld.util.CaveLog;
import com.kegare.caveworld.util.PanoramaPaths;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectBlock extends GuiScreen
{
	public interface SelectListener
	{
		public void onSelected(Block block);
	}

	protected final GuiScreen parentScreen;
	protected GuiTextField parentTextField;

	protected BlockList blockList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBlock(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	public GuiSelectBlock(GuiScreen parent, GuiTextField textField)
	{
		this(parent);
		this.parentTextField = textField;
	}

	@Override
	public void initGui()
	{
		if (blockList == null)
		{
			blockList = new BlockList(this);
		}

		blockList.func_148122_a(width, height, 32, height - 28);

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
					if (blockList.selected != null && parentScreen instanceof SelectListener)
					{
						((SelectListener)parentScreen).onSelected(blockList.selected);
					}

					if (parentTextField != null)
					{
						if (blockList.selected == null)
						{
							parentTextField.setText("");
						}
						else
						{
							parentTextField.setText(GameData.getBlockRegistry().getNameForObject(blockList.selected));
						}

						parentTextField.setFocused(true);
						parentTextField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parentScreen);
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
		blockList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.block"), width / 2, 15, 0xFFFFFF);

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
				blockList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				blockList.setFilter(text);
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
				blockList.selected = null;
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++blockList.nameType > 2)
				{
					blockList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				blockList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				blockList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				blockList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				blockList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				blockList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				blockList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				blockList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
		}
	}

	protected static class BlockList extends GuiListSlot
	{
		protected static final ArrayListExtended<Block> blocks = new ArrayListExtended<Block>().addAllObject(GameData.getBlockRegistry()).sort(new BlockComparator());

		private static final Map<String, List<Block>> filterCache = Maps.newHashMap();

		protected final GuiSelectBlock parent;

		protected final ArrayListExtended<Block> contents = new ArrayListExtended(blocks);

		private final Set<Block> ignoredRender = CaveConfigGui.getIgnoredRenderBlocks();

		protected int nameType;
		protected Block selected = null;

		private BlockList(GuiSelectBlock parent)
		{
			super(parent.mc, 0, 0, 0, 0, 18);
			this.parent = parent;

			if (parent.parentTextField != null)
			{
				this.selected = Block.getBlockFromName(parent.parentTextField.getText());
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
			Block block = contents.get(index, null);

			if (block == null)
			{
				return;
			}

			String name = null;

			switch (nameType)
			{
				case 1:
					name = GameData.getBlockRegistry().getNameForObject(block);
					break;
				case 2:
					name = block.getUnlocalizedName();
					name = name.substring(name.indexOf(".") + 1);
					break;
				default:
					name = block.getLocalizedName();
					break;
			}

			parent.drawCenteredString(parent.fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);

			if (parent.detailInfo.isChecked() && !ignoredRender.contains(block) && Item.getItemFromBlock(block) != null)
			{
				try
				{
					GL11.glEnable(GL12.GL_RESCALE_NORMAL);
					RenderHelper.enableGUIStandardItemLighting();
					RenderItem.getInstance().renderItemAndEffectIntoGUI(parent.fontRendererObj, parent.mc.getTextureManager(), new ItemStack(block), width / 2 - 100, par3 - 1);
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

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			selected = isSelected(index) ? null : contents.get(index, null);
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
					List<Block> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = blocks;
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(blocks, new BlockFilter(filter))));
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

	public static class BlockFilter implements Predicate<Block>
	{
		private final String filter;

		public BlockFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(Block block)
		{
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