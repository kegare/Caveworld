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

	public EntityPlayer getClientPlayer()
	{
		return null;
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

	public void setDebugBoundingBox(boolean flag) {}
}