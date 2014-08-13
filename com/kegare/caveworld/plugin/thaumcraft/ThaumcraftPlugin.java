/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.plugin.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.plugin.CaveModPlugin.ModPlugin;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

@ModPlugin(modid = ThaumcraftPlugin.MODID)
public final class ThaumcraftPlugin
{
	public static final String MODID = "Thaumcraft";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	private void invoke()
	{
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.caveworld_portal), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.MINE, 4));

		if (Config.rope)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.rope, 0, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MOTION, 1).add(Aspect.CLOTH, 1));
		}
	}
}