/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import caveworld.client.gui.MenuType;
import caveworld.item.ICaveniumTool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CommonProxy
{
	public void initConfigEntries() {}

	public void registerKeyBindings() {}

	public void registerRenderers() {}

	public int getUniqueRenderType()
	{
		return -1;
	}

	public void displayMenu(MenuType type) {}

	public void displayPortalMenu(MenuType type, int x, int y, int z) {}

	public int getMultiBreakCount(EntityPlayer player)
	{
		ItemStack current = player.getCurrentEquippedItem();

		if (current == null || !(current.getItem() instanceof ICaveniumTool))
		{
			return 0;
		}

		ICaveniumTool tool = (ICaveniumTool)current.getItem();

		return tool.getMode(current).getExecutor(player).getBreakPositions().size();
	}
}