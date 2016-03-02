/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import caveworld.core.Caveworld;
import net.minecraft.item.ItemSword;

public class ItemCaveSword extends ItemSword
{
	protected final ToolMaterial toolMaterial;

	public ItemCaveSword(String name, String texture, ToolMaterial material)
	{
		super(material);
		this.toolMaterial = material;
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:" + texture);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	public ToolMaterial getToolMaterial()
	{
		return toolMaterial;
	}
}