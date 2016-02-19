/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import net.minecraft.item.ItemStack;

public interface IModeItem
{
	public long getHighlightStart();

	public void setHighlightStart(long time);

	public String getModeName(ItemStack itemstack);

	public String getModeDisplayName(ItemStack itemstack);

	public String getModeInfomation(ItemStack itemstack);
}