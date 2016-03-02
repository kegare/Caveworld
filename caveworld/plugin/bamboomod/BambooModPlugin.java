/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.bamboomod;

import caveworld.block.CaveBlocks;
import caveworld.item.CaveItems;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.item.ItemStack;
import ruby.bamboo.api.crafting.grind.GrindRegistory;

public class BambooModPlugin implements ICavePlugin
{
	public static final String MODID = "BambooMod";

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

	@Method(modid = MODID)
	@Override
	public void invoke()
	{
		GrindRegistory.addRecipe(new ItemStack(CaveItems.gem, 2, 2), new ItemStack(CaveBlocks.gem_ore, 1, 3));
	}
}