package caveworld.item;

import caveworld.core.Caveworld;
import net.minecraft.item.ItemPickaxe;

public class ItemCavePickaxe extends ItemPickaxe
{
	public ItemCavePickaxe(String name, String texture, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:" + texture);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}
}