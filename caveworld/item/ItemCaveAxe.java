package caveworld.item;

import caveworld.core.Caveworld;
import net.minecraft.item.ItemAxe;

public class ItemCaveAxe extends ItemAxe
{
	public ItemCaveAxe(String name, String texture, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:" + texture);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}
}