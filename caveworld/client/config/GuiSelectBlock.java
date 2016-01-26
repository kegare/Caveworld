/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.client.config;

import java.util.Collection;
import java.util.Iterator;
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

import caveworld.api.BlockEntry;
import caveworld.client.gui.GuiListSlot;
import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
import caveworld.util.PanoramaPaths;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiSelectBlock extends GuiScreen
{
	protected static final ArrayListExtended<BlockEntry> blocks = new ArrayListExtended();

	static
	{
		List list = Lists.newArrayList();

		for (Block block : GameData.getBlockRegistry().typeSafeIterable())
		{
			try
			{
				list.clear();

				CreativeTabs tab = block.getCreativeTabToDisplayOn();

				if (tab == null)
				{
					tab = CreativeTabs.tabAllSearch;
				}

				block.getSubBlocks(Item.getItemFromBlock(block), tab, list);

				if (list.isEmpty())
				{
					if (!block.hasTileEntity(0))
					{
						blocks.addIfAbsent(new BlockEntry(block, 0));

						if (block instanceof BlockRotatedPillar)
						{
							blocks.addIfAbsent(new BlockEntry(block, 4));
							blocks.addIfAbsent(new BlockEntry(block, 8));
						}
					}
				}
				else for (Object obj : list)
				{
					ItemStack itemstack = (ItemStack)obj;

					if (itemstack != null && itemstack.getItem() != null)
					{
						Block sub = Block.getBlockFromItem(itemstack.getItem());
						int meta = itemstack.getItemDamage();

						if (meta < 0 || meta >= 16 || sub == Blocks.air || sub.hasTileEntity(meta))
						{
							continue;
						}

						blocks.addIfAbsent(new BlockEntry(sub, meta));

						if (sub instanceof BlockRotatedPillar)
						{
							blocks.addIfAbsent(new BlockEntry(sub, meta + 4));
							blocks.addIfAbsent(new BlockEntry(sub, meta + 8));
						}
					}
				}
			}
			catch (Throwable e) {}
		}
	}

	public interface SelectListener
	{
		public void onSelected(Set<BlockEntry> result);
	}

	protected final GuiScreen parentScreen;
	protected GuiTextField parentNameField;
	protected GuiTextField parentMetaField;

	protected final Set<BlockEntry> excluded = Sets.newHashSet();

	protected BlockList blockList;

	protected GuiButton doneButton;
	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBlock(GuiScreen parent)
	{
		this.parentScreen = parent;
	}

	public GuiSelectBlock(GuiScreen parent, GuiTextField nameField, GuiTextField metaField)
	{
		this(parent);
		this.parentNameField = nameField;
		this.parentMetaField = metaField;
	}

	public GuiSelectBlock exclude(Collection<BlockEntry> blocks)
	{
		for (BlockEntry entry : blocks)
		{
			excluded.add(entry);
		}

		return this;
	}

	@Override
	public void initGui()
	{
		if (blockList == null)
		{
			blockList = new BlockList();
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
					if (blockList.selected.isEmpty())
					{
						if (parentNameField != null)
						{
							parentNameField.setText("");
						}

						if (parentMetaField != null)
						{
							parentMetaField.setText("");
						}
					}
					else
					{
						if (parentScreen != null && parentScreen instanceof SelectListener)
						{
							((SelectListener)parentScreen).onSelected(Sets.newHashSet(blockList.selected));
						}

						BlockEntry block = blockList.selected.iterator().next();

						if (parentNameField != null)
						{
							parentNameField.setText(GameData.getBlockRegistry().getNameForObject(block.getBlock()));
						}

						if (parentMetaField != null)
						{
							parentMetaField.setText(Integer.toString(block.getMetadata()));
						}
					}

					if (parentNameField != null)
					{
						parentNameField.setFocused(true);
						parentNameField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parentScreen);

					blockList.selected.clear();
					blockList.scrollToTop();
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

		boolean single = parentNameField != null || parentMetaField != null;
		String name = null;

		if (single)
		{
			name = I18n.format(Caveworld.CONFIG_LANG + "select.block");
		}
		else
		{
			name = I18n.format(Caveworld.CONFIG_LANG + "select.block.multiple");
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

		if (!single && !blockList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.block.selected", blockList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> blocks = Lists.newArrayList();

				for (BlockEntry entry : blockList.selected)
				{
					try
					{
						ItemStack itemstack = new ItemStack(entry.getBlock(), 1, entry.getMetadata());

						if (entry.getBlock() instanceof BlockRotatedPillar)
						{
							int i = entry.getMetadata();

							if (i >= 8)
							{
								itemstack.setItemDamage(i - 8);
							}
							else if (i >= 4)
							{
								itemstack.setItemDamage(i - 4);
							}
						}

						switch (blockList.nameType)
						{
							case 1:
								name = GameData.getBlockRegistry().getNameForObject(entry.getBlock()) + ", " + entry.getMetadata();
								break;
							case 2:
								name = itemstack.getUnlocalizedName();
								name = name.substring(name.indexOf(".") + 1);
								break;
							default:
								name = itemstack.getDisplayName();
								break;
						}

						blocks.add(name);
					}
					catch (Throwable e) {}
				}

				func_146283_a(blocks, mouseX, mouseY);
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

	class BlockList extends GuiListSlot
	{
		protected final ArrayListExtended<BlockEntry> entries = new ArrayListExtended(blocks);
		protected final ArrayListExtended<BlockEntry> contents = new ArrayListExtended(blocks);
		protected final Set<BlockEntry> selected = Sets.newHashSet();

		private final Map<String, List<BlockEntry>> filterCache = Maps.newHashMap();

		protected int nameType;

		private BlockList()
		{
			super(GuiSelectBlock.this.mc, 0, 0, 0, 0, 18);

			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					entries.removeAll(excluded);
					contents.removeAll(excluded);

					if (parentNameField != null)
					{
						int meta = 0;

						if (parentMetaField != null)
						{
							meta = NumberUtils.toInt(parentMetaField.getText());
						}

						for (BlockEntry entry : entries)
						{
							if (GameData.getBlockRegistry().getNameForObject(entry.getBlock()).equals(parentNameField.getText()) && entry.getMetadata() == meta)
							{
								selected.add(entry);
								break;
							}
						}
					}

					scrollToSelected();
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
			drawDefaultBackground();
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
			int meta = entry.getMetadata();
			ItemStack itemstack = new ItemStack(block, 1, meta);
			String name = null;

			try
			{
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
					if (block instanceof BlockRotatedPillar)
					{
						if (meta >= 8)
						{
							itemstack.setItemDamage(meta - 8);
						}
						else if (meta >= 4)
						{
							itemstack.setItemDamage(meta - 4);
						}
					}

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

					itemstack.setItemDamage(meta);
				}
			}
			catch (Throwable e) {}

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked() && itemstack.getItem() != null)
			{
				CaveUtils.renderItemStack(mc, itemstack, width / 2 - 100, par3 - 1, false, true, null);
			}
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			BlockEntry entry = contents.get(index, null);

			if (entry != null && !selected.remove(entry))
			{
				if (parentNameField != null || parentMetaField != null)
				{
					selected.clear();
				}

				selected.add(entry);
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
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<BlockEntry> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = entries;
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(entries, new BlockFilter(filter))));
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

	public static class BlockFilter implements Predicate<BlockEntry>
	{
		private final String filter;

		public BlockFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(BlockEntry entry)
		{
			return CaveUtils.blockFilter(entry, filter);
		}
	}
}