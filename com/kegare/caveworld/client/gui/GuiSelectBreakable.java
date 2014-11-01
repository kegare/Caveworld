/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.gui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.client.config.CaveConfigGui;
import com.kegare.caveworld.client.config.GuiSelectBlock;
import com.kegare.caveworld.client.config.GuiSelectBlock.BlockFilter;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.item.ItemMiningPickaxe;
import com.kegare.caveworld.network.SelectBreakableMessage;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.PanoramaPaths;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;

public class GuiSelectBreakable extends GuiScreen
{
	protected final ItemStack parentTool;

	protected BlockList blockList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBreakable(ItemStack itemstack)
	{
		this.parentTool = itemstack;
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
					List<String> selected = Lists.newArrayList();

					for (BlockEntry entry : blockList.selected)
					{
						selected.add(CaveUtils.toStringHelper(entry.getBlock(), entry.getMetadata()));
					}

					String value = Joiner.on("|").join(selected);

					parentTool.getTagCompound().setString("Blocks", value);
					Caveworld.network.sendToServer(new SelectBreakableMessage(value));

					mc.displayGuiScreen(null);
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

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.block.multiple"), width / 2, 15, 0xFFFFFF);

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
				mc.displayGuiScreen(null);
			}
			else if (code == Keyboard.KEY_BACK)
			{
				blockList.selected.clear();
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
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				blockList.selected.addAll(blockList.contents);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected static class BlockList extends GuiListSlot
	{
		protected static final ArrayListExtended<BlockEntry> blocks = new ArrayListExtended();

		private static final Map<String, List<BlockEntry>> filterCache = Maps.newHashMap();

		static
		{
			List list = Lists.newArrayList();

			for (Block block : GuiSelectBlock.raws)
			{
				try
				{
					Item item = Item.getItemFromBlock(block);

					if (item == null)
					{
						continue;
					}

					list.clear();

					CreativeTabs tab = block.getCreativeTabToDisplayOn();

					if (tab == null)
					{
						tab = CreativeTabs.tabAllSearch;
					}

					block.getSubBlocks(item, tab, list);

					if (list.isEmpty())
					{
						if (Strings.nullToEmpty(block.getHarvestTool(0)).equalsIgnoreCase("pickaxe") || CaveItems.mining_pickaxe.func_150897_b(block) ||
							block instanceof BlockOre || block instanceof BlockRedstoneOre)
						{
							blocks.addIfAbsent(new BlockEntry(block, 0));
						}
					}
					else for (Object obj : list)
					{
						ItemStack itemstack = (ItemStack)obj;

						if (itemstack != null && itemstack.getItem() != null)
						{
							Block sub = Block.getBlockFromItem(itemstack.getItem());
							int meta = itemstack.getItemDamage();

							if (meta < 0 || meta >= 16 || sub == Blocks.air)
							{
								continue;
							}

							if (Strings.nullToEmpty(sub.getHarvestTool(meta)).equalsIgnoreCase("pickaxe") ||
								CaveItems.mining_pickaxe.func_150897_b(sub) || block instanceof BlockOre || block instanceof BlockRedstoneOre)
							{
								blocks.addIfAbsent(new BlockEntry(sub, meta));
							}
						}
					}
				}
				catch (Throwable e) {}
			}
		}

		protected final GuiSelectBreakable parent;
		protected final ArrayListExtended<BlockEntry> contents = new ArrayListExtended(blocks);

		protected int nameType;
		protected final Set<BlockEntry> selected = Sets.newHashSet();

		private BlockList(final GuiSelectBreakable parent)
		{
			super(parent.mc, 0, 0, 0, 0, 18);
			this.parent = parent;

			new ForkJoinPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					if (parent.parentTool.getItem() instanceof ItemMiningPickaxe)
					{
						for (BlockEntry entry : blocks)
						{
							if (((ItemMiningPickaxe)parent.parentTool.getItem()).canBreak(parent.parentTool, entry.getBlock(), entry.getMetadata()))
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

				for (Iterator<BlockEntry> iterator = selected.iterator(); iterator.hasNext();)
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
			BlockEntry entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			Block block = entry.getBlock();
			ItemStack itemstack = new ItemStack(block, 1, entry.getMetadata());
			String name = null;

			if (itemstack.getItem() == null)
			{
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
			}
			else
			{
				switch (nameType)
				{
					case 1:
						name = GameData.getBlockRegistry().getNameForObject(block) + ", " + itemstack.getItemDamage();
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

			parent.drawCenteredString(parent.fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);

			if (parent.detailInfo.isChecked() && itemstack.getItem() != null)
			{
				CaveUtils.renderItemStack(mc, itemstack, width / 2 - 100, par3 - 1, false, false, null);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			BlockEntry entry = contents.get(index, null);

			if (entry != null && !selected.add(entry))
			{
				selected.remove(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			BlockEntry entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			new ForkJoinPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<BlockEntry> result;

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
}