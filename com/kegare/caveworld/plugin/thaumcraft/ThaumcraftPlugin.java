/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.item.CaveItems;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public final class ThaumcraftPlugin
{
	public static final String MODID = "Thaumcraft";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.caveworld_portal, 0, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.MINE, 4));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.rope, 0, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MOTION, 1).add(Aspect.CLOTH, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 0, 0), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 0, 1), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 0, 2), new AspectList().add(Aspect.CRYSTAL, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 0, 3), new AspectList().add(Aspect.CRYSTAL, 6));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.cavenium, 0, 0), new AspectList().add(Aspect.CRYSTAL, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.cavenium, 0, 1), new AspectList().add(Aspect.CRYSTAL, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.mining_pickaxe, 0, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.MINE, 2).add(Aspect.TOOL, 2));

		ThaumcraftApi.registerEntityTag("Caveman", new AspectList().add(Aspect.LIFE, 3).add(Aspect.VOID, 3).add(Aspect.ELDRITCH, 2));
	}
}