package caveworld.item;

import caveworld.core.Caveworld;
import net.minecraft.item.ItemHoe;

public class ItemCaveHoe extends ItemHoe
{
	public ItemCaveHoe(String name, String texture, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:" + texture);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}
}