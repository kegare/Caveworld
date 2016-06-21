package caveworld.plugin.nei;

import caveworld.item.CaveItems;
import caveworld.item.ICaveniumTool;
import net.minecraft.item.Item;

public class DiggingShovelRecipeHandler extends CaveniumToolRecipeHandler
{
	@Override
	public ICaveniumTool getTool()
	{
		return CaveItems.digging_shovel;
	}

	@Override
	public Item getToolItem()
	{
		return CaveItems.digging_shovel;
	}
}