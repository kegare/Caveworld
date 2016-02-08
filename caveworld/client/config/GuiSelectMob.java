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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import caveworld.client.gui.GuiListSlot;
import caveworld.core.Caveworld;
import caveworld.util.ArrayListExtended;
import caveworld.util.CaveUtils;
import caveworld.util.PanoramaPaths;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiConfigEntries.ArrayEntry;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;

@SideOnly(Side.CLIENT)
public class GuiSelectMob extends GuiScreen
{
	private final GuiScreen parent;
	private ArrayEntry configElement;

	private MobList mobList;
	private GuiButton doneButton;
	private GuiTextField filterTextField;
	private HoverChecker selectedHoverChecker;

	private Collection<String> presetMobs;

	public GuiSelectMob(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectMob(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.configElement = entry;
	}

	public GuiSelectMob setPresetMobs(Collection<String> mobs)
	{
		presetMobs = mobs;

		return this;
	}

	public Collection<String> getPresetMobs()
	{
		return presetMobs;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (mobList == null)
		{
			mobList = new MobList();
		}

		mobList.func_148122_a(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.xPosition = width / 2 + 10;
		doneButton.yPosition = height - doneButton.height - 4;

		buttonList.clear();
		buttonList.add(doneButton);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;

		if (selectedHoverChecker == null)
		{
			selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
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
					if (configElement != null)
					{
						configElement.setListFromChildScreen(mobList.selected.toArray(new String[mobList.selected.size()]));
					}

					mc.displayGuiScreen(parent);

					if (parent == null)
					{
						mc.setIngameFocus();
					}

					mobList.selected.clear();
					mobList.scrollToTop();
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
		mobList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.mob"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (!mobList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Caveworld.CONFIG_LANG + "select.mob.selected", mobList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				func_146283_a(mobList.getSelectedMobs(), mouseX, mouseY);
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
	public void onGuiClosed()
	{
		super.onGuiClosed();

		Keyboard.enableRepeatEvents(false);
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
				mobList.setFilter(null);
			}
			else if (changed || code == Keyboard.KEY_RETURN)
			{
				mobList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);

				if (parent == null)
				{
					mc.setIngameFocus();
				}
			}
			else if (code == Keyboard.KEY_BACK)
			{
				mobList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++mobList.nameType > 1)
				{
					mobList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				mobList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				mobList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				mobList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				mobList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				mobList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				mobList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				mobList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				mobList.selected.clear();
				mobList.selected.addAll(mobList.contents);
			}
		}
	}

	class MobList extends GuiListSlot
	{
		private final ArrayListExtended<String> mobs = new ArrayListExtended();
		private final ArrayListExtended<String> contents = new ArrayListExtended();
		private final Set<String> selected = Sets.newTreeSet();
		private final Map<String, List<String>> filterCache = Maps.newHashMap();

		protected int nameType;

		public MobList()
		{
			super(GuiSelectMob.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					mobs.clear();
					contents.clear();
					selected.clear();
					filterCache.clear();

					for (Iterator iterator = EntityList.stringToClassMapping.entrySet().iterator(); iterator.hasNext();)
					{
						Entry entry = (Entry)iterator.next();
						String name = (String)entry.getKey();
						Class clazz = (Class)entry.getValue();

						if (!Strings.isNullOrEmpty(name) && EntityMob.class != clazz && EntityLiving.class != clazz && EntityLiving.class.isAssignableFrom(clazz))
						{
							mobs.addIfAbsent(name);
						}
					}

					Collections.sort(mobs);

					contents.addAll(mobs);

					if (presetMobs != null && !presetMobs.isEmpty())
					{
						selected.addAll(presetMobs);
					}
					else if (configElement != null)
					{
						for (Object obj : configElement.getCurrentValues())
						{
							selected.add(String.valueOf(obj));
						}
					}
				}
			});
		}

		protected List<String> getMobs()
		{
			return Lists.transform(contents, new Function<String, String>()
			{
				@Override
				public String apply(String input)
				{
					return CaveUtils.getEntityLocalizedName(input);
				}
			});
		}

		protected List<String> getSelectedMobs()
		{
			return Lists.transform(Lists.newArrayList(selected), new Function<String, String>()
			{
				@Override
				public String apply(String input)
				{
					return CaveUtils.getEntityLocalizedName(input);
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

				for (Iterator<String> iterator = selected.iterator(); iterator.hasNext();)
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
			String entry = contents.get(index, null);

			if (entry == null || entry.isEmpty())
			{
				return;
			}

			String name;

			switch (nameType)
			{
				case 1:
					name = entry;
					break;
				default:
					name = CaveUtils.getEntityLocalizedName(entry);
					break;
			}

			drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			String entry = contents.get(index, null);

			if (entry != null && !entry.isEmpty() && !selected.remove(entry))
			{
				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			String entry = contents.get(index, null);

			return entry != null && !entry.isEmpty() && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<String> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = mobs;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(mobs, new Predicate<String>()
							{
								@Override
								public boolean apply(String input)
								{
									return StringUtils.containsIgnoreCase(input, filter) || StringUtils.containsIgnoreCase(CaveUtils.getEntityLocalizedName(input), filter);
								}
							})));
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