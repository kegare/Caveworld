/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mekanism;

import java.lang.reflect.Method;

import caveworld.block.CaveBlocks;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import net.minecraft.item.ItemStack;

public class MekanismPlugin implements ICavePlugin
{
	public static final String MODID = "Mekanism";

	public static boolean pluginState = true;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Override
	public void invoke()
	{
		addCrusherRecipe(new ItemStack(CaveBlocks.gem_ore, 1, 3), new ItemStack(CaveItems.gem, 2, 2));
	}

	public static void addCrusherRecipe(ItemStack input, ItemStack output)
	{
		try
		{
			Class recipeClass = Class.forName("mekanism.common.recipe.RecipeHandler");
			Method m = recipeClass.getMethod("addCrusherRecipe", ItemStack.class, ItemStack.class);

			m.invoke(null, input, output);
		}
		catch(Exception e)
		{
			System.err.println("Error while adding recipe: " + e.getMessage());
		}
	}
}