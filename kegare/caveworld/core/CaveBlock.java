package kegare.caveworld.core;

import kegare.caveworld.block.BlockPortalCaveworld;
import kegare.caveworld.item.ItemPortalCaveworld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class CaveBlock
{
	public static final BlockPortalCaveworld portalCaveworld = new BlockPortalCaveworld(Config.portalCaveworld, "portalCaveworld");

	public static void load()
	{
		LanguageRegistry.addName(portalCaveworld, "Caveworld Portal");
		GameRegistry.registerBlock(portalCaveworld, ItemPortalCaveworld.class, "portalCaveworld");
		GameRegistry.addRecipe(new ItemStack(portalCaveworld), "EDE", "DED", "EDE", 'E', Item.emerald, 'D', Item.diamond);
	}
}