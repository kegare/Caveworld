/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.more;

import net.minecraft.block.Block;
import net.minecraftforge.common.BiomeDictionary.Type;

import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Config;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;

public class MOrePlugin
{
	public static final String MODID = "more";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Block block = GameRegistry.findBlock(MODID, "CopperOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 16, 20, 100, 0, 255));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 16, 40, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "TinOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 14, 24, 100, 0, 255));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 14, 48, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "UranOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 10, 7, 100, 0, 50));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 10, 14, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "RubyOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 4, 100, 0, 50));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 6, 8, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "SapphireOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 4, 100, 0, 64));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 6, 8, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "SilverOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 15, 100, 0, 255));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 6, 30, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "PlatinumOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 5, 5, 100, 0, 30));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 5, 10, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "TitaniumOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 3, 100, 0, 24));
				 CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 6, 6, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "BlockGranite");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 20, 100, 0, 255, null, Type.HILLS));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 6, 40, 100, 0, 255));
			}
		}

		block = GameRegistry.findBlock(MODID, "ChromeOre");

		if (block != null)
		{
			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "ChromeEndOre");

		if (block != null)
		{
			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}
	}
}