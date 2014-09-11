/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

public abstract class GuiListSlot extends GuiSlot
{
	public GuiListSlot(Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
	{
		super(mc, width, height, top, bottom, slotHeight);
	}

	public void scrollUp()
	{
		int i = getAmountScrolled() % getSlotHeight();

		if (i == 0)
		{
			scrollBy(-getSlotHeight());
		}
		else
		{
			scrollBy(-i);
		}
	}

	public void scrollDown()
	{
		scrollBy(getSlotHeight() - (getAmountScrolled() % getSlotHeight()));
	}

	public void scrollToTop()
	{
		scrollBy(-getAmountScrolled());
	}

	public void scrollToEnd()
	{
		scrollBy(getSlotHeight() * getSize());
	}

	public abstract void scrollToSelected();

	public void scrollToPrev()
	{
		scrollBy(-((getAmountScrolled() % getSlotHeight()) + ((bottom - top) / getSlotHeight()) * getSlotHeight()));
	}

	public void scrollToNext()
	{
		scrollBy((getAmountScrolled() % getSlotHeight()) + ((bottom - top) / getSlotHeight()) * getSlotHeight());
	}
}