package caveworld.item;

import caveworld.core.Caveworld;
import net.minecraft.item.ItemSpade;

public class ItemCaveShovel extends ItemSpade
{
	public ItemCaveShovel(String name, String texture, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:" + texture);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}
}