package caveworld.client.gui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.client.config.CaveConfigGui;
import caveworld.client.config.GuiSelectBlock.BlockFilter;
import caveworld.core.Caveworld;
import caveworld.item.ICaveniumTool;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.common.HeldItemNBTAdjustMessage;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@SideOnly(Side.CLIENT)
public class GuiSelectBreakable extends GuiScreen
{
	protected final ItemStack toolItem;

	protected BlockList blockList;

	protected GuiButton doneButton;
	protected GuiButton modeButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;
	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBreakable(ItemStack itemstack)
	{
		this.toolItem = itemstack;
	}

	public ICaveniumTool getTool()
	{
		return toolItem == null ? null : (ICaveniumTool)toolItem.getItem();
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

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

		modeButton = new GuiButtonExt(3, 5, 5, 50, 20, getTool().getModeDisplayName(toolItem));

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
		buttonList.add(modeButton);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;

		if (detailHoverChecker == null)
		{
			detailHoverChecker = new HoverChecker(detailInfo, 800);
		}

		if (instantHoverChecker == null)
		{
			instantHoverChecker = new HoverChecker(instantFilter, 800);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					saveToNBT();

					CaveNetworkRegistry.sendToServer(new HeldItemNBTAdjustMessage(toolItem));

					getTool().setHighlightStart(System.currentTimeMillis());

					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				case 3:
					saveToNBT();

					while (((Enum)getTool().toggleMode(toolItem)).name().equalsIgnoreCase("NORMAL"));

					initGui();
					blockList.initEntries();
					break;
			}
		}
	}

	protected void saveToNBT()
	{
		Set<String> values = Sets.newTreeSet();

		for (BlockEntry block : blockList.selected)
		{
			values.add(CaveUtils.toStringHelper(block.getBlock(), block.getMetadata()));
		}

		NBTTagCompound nbt = toolItem.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setString(getTool().getModeName(toolItem) + ":Blocks", Joiner.on('|').skipNulls().join(values));
		toolItem.setTagCompound(nbt);
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

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.block.multiple.breakable", getTool().getModeDisplayName(toolItem)), width / 2, 15, 0xFFFFFF);

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

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();

		Keyboard.enableRepeatEvents(false);
	}

	class BlockList extends GuiListSlot implements Comparator<BlockEntry>
	{
		protected final ArrayListExtended<BlockEntry> blocks = new ArrayListExtended();
		protected final ArrayListExtended<BlockEntry> contents = new ArrayListExtended();
		protected final Set<BlockEntry> selected = Sets.newTreeSet(this);
		protected final Map<String, List<BlockEntry>> filterCache = Maps.newHashMap();

		protected int nameType;

		protected BlockList()
		{
			super(GuiSelectBreakable.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			blocks.clear();
			contents.clear();
			selected.clear();
			filterCache.clear();

			for (BlockEntry block : getTool().getBreakableBlocks())
			{
				blocks.addIfAbsent(block);
				contents.addIfAbsent(block);

				if (getTool().canBreak(toolItem, block.getBlock(), block.getMetadata()))
				{
					selected.add(block);
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

				for (BlockEntry entry : selected)
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
			BlockEntry entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			Block block = entry.getBlock();
			int meta = entry.getMetadata();
			ItemStack itemstack = new ItemStack(block, 1, meta);
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

			drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);

			if (detailInfo.isChecked() && itemstack.getItem() != null)
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

		@Override
		public int compare(BlockEntry o1, BlockEntry o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(blocks.indexOf(o1), blocks.indexOf(o2));
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
					List<BlockEntry> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = getTool().getBreakableBlocks();
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(getTool().getBreakableBlocks(), new BlockFilter(filter))));
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