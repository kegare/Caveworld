package caveworld.client.config;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.api.BlockEntry;
import caveworld.client.config.GuiSelectBlock.SelectListener;
import caveworld.client.gui.GuiListSlot;
import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@SideOnly(Side.CLIENT)
public class GuiMiningPointsEntry extends GuiScreen implements SelectListener
{
	protected final GuiScreen parentScreen;
	protected final ArrayEntry parentElement;

	protected PointList pointList;

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
	protected GuiTextField pointField;

	protected HoverChecker blockHoverChecker;
	protected HoverChecker pointHoverChecker;

	private int maxLabelWidth;

	private final List<String> editLabelList = Lists.newArrayList();
	private final List<GuiTextField> editFieldList = Lists.newArrayList();

	private final Map<Object, List<String>> hoverCache = Maps.newHashMap();

	public GuiMiningPointsEntry(GuiScreen parent, ArrayEntry entry)
	{
		this.parentScreen = parent;
		this.parentElement = entry;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (pointList == null)
		{
			pointList = new PointList();
		}

		pointList.func_148122_a(width, height, 32, height - (editMode ? 80 : 28));

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
		editButton.enabled = pointList.selected != null;
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
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "points.block"));
		editLabelList.add("");
		editLabelList.add(I18n.format(Caveworld.CONFIG_LANG  + "points.point"));

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
		blockField.yPosition = pointList.bottom + 5;
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

		if (pointField == null)
		{
			pointField = new GuiTextField(fontRendererObj, 0, 0, 0, blockField.height);
			pointField.setMaxStringLength(3);
		}

		pointField.xPosition = blockField.xPosition;
		pointField.yPosition = blockField.yPosition + blockField.height + 5;
		pointField.width = fieldWidth;

		editFieldList.clear();

		if (editMode)
		{
			editFieldList.add(blockField);
			editFieldList.add(blockMetaField);
			editFieldList.add(pointField);
		}

		blockHoverChecker = new HoverChecker(blockField.yPosition - 1, blockField.yPosition + blockField.height, blockField.xPosition - maxLabelWidth - 12, blockField.xPosition - 10, 800);
		pointHoverChecker = new HoverChecker(pointField.yPosition - 1, pointField.yPosition + pointField.height, pointField.xPosition - maxLabelWidth - 12, pointField.xPosition - 10, 800);
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
						for (PointEntry entry : pointList.selected)
						{
							if (!Strings.isNullOrEmpty(blockField.getText()))
							{
								BlockEntry block = new BlockEntry(blockField.getText(), NumberUtils.toInt(blockMetaField.getText()));

								if (block.getBlock() != null)
								{
									entry.setBlock(block);
								}
							}

							if (!Strings.isNullOrEmpty(pointField.getText()))
							{
								entry.setPoint(NumberUtils.toInt(pointField.getText(), entry.getPoint()));
							}
						}

						actionPerformed(cancelButton);

						pointList.scrollToTop();
						pointList.scrollToSelected();
					}
					else
					{
						Set<String> values = Sets.newTreeSet();

						for (PointEntry entry : pointList.points)
						{
							values.add(entry.toString());
						}

						parentElement.setListFromChildScreen(values.toArray());

						actionPerformed(cancelButton);

						pointList.selected.clear();
						pointList.scrollToTop();
					}

					break;
				case 1:
					if (editMode)
					{
						actionPerformed(cancelButton);
					}
					else if (!pointList.selected.isEmpty())
					{
						editMode = true;
						initGui();

						pointList.scrollToTop();
						pointList.scrollToSelected();

						if (pointList.selected.size() > 1)
						{
							blockField.setText("");
							blockMetaField.setText("");
							pointField.setText("");
						}
						else for (PointEntry entry : pointList.selected)
						{
							blockField.setText(GameData.getBlockRegistry().getNameForObject(entry.getBlock().getBlock()));
							blockMetaField.setText(Integer.toString(entry.getBlock().getMetadata()));
							pointField.setText(Integer.toString(entry.getPoint()));
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
					Set<BlockEntry> blocks = Sets.newHashSet();

					for (PointEntry entry : pointList.points)
					{
						blocks.add(entry.getBlock());
					}

					mc.displayGuiScreen(new GuiSelectBlock(this).exclude(blocks));
					break;
				case 4:
					for (PointEntry entry : pointList.selected)
					{
						if (pointList.points.remove(entry))
						{
							pointList.contents.remove(entry);
						}
					}

					pointList.selected.clear();
					break;
				case 5:
					pointList.selected.addAll(pointList.points);

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
	public void onBlockSelected(final Set<BlockEntry> result)
	{
		if (editMode)
		{
			return;
		}

		pointList.selected.clear();

		for (BlockEntry block : result)
		{
			PointEntry entry = new PointEntry(block, 1);

			if (pointList.points.addIfAbsent(entry) && pointList.contents.addIfAbsent(entry))
			{
				pointList.selected.add(entry);
			}
		}

		pointList.scrollToTop();
		pointList.scrollToSelected();
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
			editButton.enabled = !pointList.selected.isEmpty();
			removeButton.enabled = editButton.enabled;

			filterTextField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		pointList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "points"), width / 2, 15, 0xFFFFFF);

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
					String key = Caveworld.CONFIG_LANG + "points.block";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(blockHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(blockHoverChecker), mouseX, mouseY);
			}
			else if (pointHoverChecker.checkHover(mouseX, mouseY))
			{
				if (!hoverCache.containsKey(pointHoverChecker))
				{
					List<String> hover = Lists.newArrayList();
					String key = Caveworld.CONFIG_LANG + "points.point";
					hover.add(EnumChatFormatting.GRAY + I18n.format(key));
					hover.addAll(fontRendererObj.listFormattedStringToWidth(I18n.format(key + ".tooltip"), 300));

					hoverCache.put(pointHoverChecker, hover);
				}

				func_146283_a(hoverCache.get(pointHoverChecker), mouseX, mouseY);
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
		else if (pointList.func_148141_e(mouseY) && isCtrlKeyDown())
		{
			PointEntry entry = pointList.contents.get(pointList.func_148124_c(mouseX, mouseY), null);

			if (entry != null)
			{
				if (!hoverCache.containsKey(entry))
				{
					List<String> info = Lists.newArrayList();

					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "points.block") + ": " +
						GameData.getBlockRegistry().getNameForObject(entry.getBlock().getBlock()) + ", " + entry.getBlock().getMetadata());
					info.add(EnumChatFormatting.GRAY + I18n.format(Caveworld.CONFIG_LANG + "points.point") + ": " + entry.getPoint());

					hoverCache.put(entry, info);
				}

				func_146283_a(hoverCache.get(entry), mouseX, mouseY);
			}
		}

		if (pointList.selected.size() > 1 && mouseX <= 100 && mouseY <= 20)
		{
			drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.entry.selected", pointList.selected.size()), 5, 5, 0xEFEFEF);
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
		else if (pointField.isFocused())
		{
			int i = Mouse.getDWheel();

			if (i < 0)
			{
				pointField.setText(Integer.toString(Math.max(NumberUtils.toInt(pointField.getText()) - 1, 1)));
			}
			else if (i > 0)
			{
				pointField.setText(Integer.toString(NumberUtils.toInt(pointField.getText()) + 1));
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
			for (GuiTextField textField : editFieldList)
			{
				textField.mouseClicked(x, y, code);
			}

			if (!isShiftKeyDown())
			{
				if (blockField.isFocused())
				{
					blockField.setFocused(false);

					Set<BlockEntry> blocks = Sets.newHashSet();

					for (PointEntry entry : pointList.points)
					{
						if (!pointList.selected.contains(entry))
						{
							blocks.add(entry.getBlock());
						}
					}

					mc.displayGuiScreen(new GuiSelectBlock(this, blockField, blockMetaField).exclude(blocks));
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
					if (textField != blockField)
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
					pointList.setFilter(null);
				}
				else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
				{
					pointList.setFilter(text);
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
					pointList.selected.clear();
				}
				else if (code == Keyboard.KEY_TAB)
				{
					if (++pointList.nameType > 2)
					{
						pointList.nameType = 0;
					}
				}
				else if (code == Keyboard.KEY_HOME)
				{
					pointList.scrollToTop();
				}
				else if (code == Keyboard.KEY_END)
				{
					pointList.scrollToEnd();
				}
				else if (code == Keyboard.KEY_SPACE)
				{
					pointList.scrollToSelected();
				}
				else if (code == Keyboard.KEY_PRIOR)
				{
					pointList.scrollToPrev();
				}
				else if (code == Keyboard.KEY_NEXT)
				{
					pointList.scrollToNext();
				}
				else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
				{
					filterTextField.setFocused(true);
				}
				else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
				{
					pointList.selected.addAll(pointList.contents);
				}
				else if (code == Keyboard.KEY_DELETE && pointList.selected != null)
				{
					actionPerformed(removeButton);
				}
				else if (code == Keyboard.KEY_X && isCtrlKeyDown())
				{
					keyTyped(Character.MIN_VALUE, Keyboard.KEY_C);

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
		pointList.currentPanoramaPaths = null;
	}

	class PointList extends GuiListSlot implements Comparator<PointEntry>
	{
		protected final ArrayListExtended<PointEntry> points = new ArrayListExtended();
		protected final ArrayListExtended<PointEntry> contents = new ArrayListExtended();
		protected final Set<PointEntry> selected = Sets.newTreeSet(this);
		protected final Map<String, List<PointEntry>> filterCache = Maps.newHashMap();

		protected int nameType;

		protected PointList()
		{
			super(GuiMiningPointsEntry.this.mc, 0, 0, 0, 0, 22);

			for (Object obj : parentElement.getCurrentValues())
			{
				String value = String.valueOf(obj);

				if (!Strings.isNullOrEmpty(value) && value.contains(","))
				{
					value = value.trim();

					int i = value.indexOf(',');
					String str = value.substring(0, i);
					int point = NumberUtils.toInt(value.substring(i + 1));

					if (str.contains(":"))
					{
						i = str.lastIndexOf(':');
						Block block = GameData.getBlockRegistry().getObject(str.substring(0, i));

						if (block != null && block != Blocks.air)
						{
							int meta = NumberUtils.toInt(str.substring(i + 1));
							PointEntry entry = new PointEntry(new BlockEntry(block, meta), point);

							points.add(entry);
							contents.add(entry);
						}
					}
				}
			}
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (PointEntry entry : selected)
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
			PointEntry entry = contents.get(index, null);

			if (entry == null)
			{
				return;
			}

			BlockEntry block = entry.getBlock();
			ItemStack itemstack = new ItemStack(block.getBlock(), 1, block.getMetadata());
			String name = null;

			try
			{
				if (itemstack.getItem() == null)
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
				else
				{
					switch (nameType)
					{
						case 1:
							name = GameData.getBlockRegistry().getNameForObject(block.getBlock()) + ", " + itemstack.getItemDamage();
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
			}
			catch (Throwable e) {}

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 3, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				Item item = Item.getItemFromBlock(block.getBlock());

				if (item != null)
				{
					CaveUtils.renderItemStack(mc, new ItemStack(item, entry.getPoint(), block.getMetadata()), width / 2 - 100, par3 + 1, false, true, Integer.toString(entry.getPoint()));
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

			PointEntry entry = contents.get(index, null);

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
			PointEntry entry = contents.get(index, null);

			return entry != null && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<PointEntry> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = points;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(points, new PointFilter(filter))));
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
		public int compare(PointEntry o1, PointEntry o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(points.indexOf(o1), points.indexOf(o2));
			}

			return i;
		}
	}

	public static class PointEntry
	{
		private BlockEntry block;
		private int point;

		public PointEntry(BlockEntry block, int point)
		{
			this.block = block;
			this.point = point;
		}

		public BlockEntry getBlock()
		{
			return block;
		}

		public void setBlock(BlockEntry entry)
		{
			block = entry;
		}

		public int getPoint()
		{
			return point;
		}

		public void setPoint(int value)
		{
			point = value;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			else if (!(obj instanceof PointEntry))
			{
				return false;
			}

			PointEntry entry = (PointEntry)obj;

			return Objects.equal(block, entry.block);
		}

		@Override
		public int hashCode()
		{
			return block.hashCode();
		}

		@Override
		public String toString()
		{
			return GameData.getBlockRegistry().getNameForObject(block.getBlock()) + ":" + block.getMetadata() + "," + point;
		}
	}

	public static class PointFilter implements Predicate<PointEntry>
	{
		private final String filter;

		public PointFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(PointEntry vein)
		{
			return CaveUtils.blockFilter(vein.getBlock(), filter);
		}
	}
}