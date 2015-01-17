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
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.Config;
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
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.caveworld_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.MINE, 4));

		if (Config.rope)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.rope, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MOTION, 1).add(Aspect.CLOTH, 1));
		}

		if (Config.ropeLadder)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.rope_ladder, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MOTION, 1).add(Aspect.CLOTH, 1));
		}

		if (Config.oreCavenium)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 0), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 2));
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 1), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 3));
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 2), new AspectList().add(Aspect.CRYSTAL, 5));
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 3), new AspectList().add(Aspect.CRYSTAL, 6));

			ItemStack key = new ItemStack(CaveBlocks.cavenium_ore, 1, 2);
			ItemStack result = new ItemStack(CaveBlocks.cavenium_ore, 1, 3);
			AspectList aspects = new AspectList().add(Aspect.MAGIC, 32).add(Aspect.EXCHANGE, 32).add(Aspect.CRYSTAL, 32);
			CrucibleRecipe recipe = ThaumcraftApi.addCrucibleRecipe("Refined Cavenium Block", result.copy(), key.copy(), aspects.copy());

			new ResearchItem("Refined Cavenium Block", "ALCHEMY", aspects.copy(), -5, 5, 3, result.copy()).setSecondary().setPages(new ResearchPage(recipe)).registerResearchItem();
		}

		if (Config.universalChest)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.universal_chest, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.VOID, 2).add(Aspect.EXCHANGE, 2));
		}

		if (Config.oreGem)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 0), new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 2).add(Aspect.CRYSTAL, 1));
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 1), new AspectList().add(Aspect.CRYSTAL, 3).add(Aspect.WATER, 1));
		}

		if (Config.cavenium)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.cavenium, 1, 0), new AspectList().add(Aspect.CRYSTAL, 2));
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.cavenium, 1, 1), new AspectList().add(Aspect.CRYSTAL, 3));

			ItemStack key = new ItemStack(CaveItems.cavenium, 1, 0);
			ItemStack result = new ItemStack(CaveItems.cavenium, 1, 1);
			AspectList aspects = new AspectList().add(Aspect.MAGIC, 4).add(Aspect.EXCHANGE, 4).add(Aspect.CRYSTAL, 4);
			CrucibleRecipe recipe = ThaumcraftApi.addCrucibleRecipe("Refined Cavenium", result.copy(), key.copy(), aspects.copy());

			new ResearchItem("Refined Cavenium", "ALCHEMY", aspects.copy(), -3, 3, 3, result.copy()).setSecondary().setPages(new ResearchPage(recipe)).registerResearchItem();
		}

		if (Config.pickaxeMining)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.mining_pickaxe, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.MINE, 2).add(Aspect.TOOL, 2));
		}

		if (Config.axeLumbering)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.lumbering_axe, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.TOOL, 2));
		}

		if (Config.shovelDigging)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.digging_shovel, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.TOOL, 2));
		}

		if (Config.bowCavenic)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.cavenic_bow, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 3).add(Aspect.TOOL, 3));
		}

		if (Config.oreCompass)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.ore_compass, 1, 0), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.MECHANISM, 2));
		}

		if (Config.gem)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.gem, 1, 0), new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.WATER, 3));
		}

		if (Config.pickaxeAquamarine)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.aquamarine_pickaxe, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.WATER, 1).add(Aspect.TOOL, 2));
		}

		if (Config.axeAquamarine)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.aquamarine_axe, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 2).add(Aspect.WATER, 1).add(Aspect.TOOL, 2));
		}

		if (Config.shovelAquamarine)
		{
			ThaumcraftApi.registerObjectTag(new ItemStack(CaveItems.aquamarine_shovel, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 1).add(Aspect.WATER, 1).add(Aspect.TOOL, 2));
		}

		ThaumcraftApi.registerEntityTag("Caveman", new AspectList().add(Aspect.LIFE, 3).add(Aspect.VOID, 3).add(Aspect.ELDRITCH, 2));
		ThaumcraftApi.registerEntityTag("ArcherZombie", new AspectList().add(Aspect.LIFE, 2).add(Aspect.TOOL, 1));
		ThaumcraftApi.registerEntityTag("CavenicSkeleton", new AspectList().add(Aspect.LIFE, 5).add(Aspect.CRYSTAL, 3).add(Aspect.VOID, 4).add(Aspect.TOOL, 1));
		ThaumcraftApi.registerEntityTag("MasterCavenicSkeleton", new AspectList().add(Aspect.LIFE, 50).add(Aspect.CRYSTAL, 30).add(Aspect.VOID, 30).add(Aspect.TOOL, 5));
	}
}