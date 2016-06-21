package caveworld.plugin.nei;

import caveworld.item.CaveItems;
import caveworld.item.ICaveniumTool;
import net.minecraft.item.Item;

public class LumberingAxeRecipeHandler extends CaveniumToolRecipeHandler
{
	@Override
	public ICaveniumTool getTool()
	{
		return CaveItems.lumbering_axe;
	}

	@Override
	public Item getToolItem()
	{
		return CaveItems.lumbering_axe;
	}
}