/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.plugin.hexagonaldia;

import net.minecraft.block.Block;

import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.CaveVeinManager.CaveVein;
import com.kegare.caveworld.core.Config;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;

public class HexagonalDiamondPlugin
{
	public static final String MODID = "HexagonalDia";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Block block = GameRegistry.findBlock(MODID, "hexagonaldiaore");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 1, 1, 10, 1, 10));
				CaveworldAPI.addCaveDeepVein(new CaveVein(new BlockEntry(block, 0), 1, 1, 100, 0, 255));
				CaveworldAPI.addCaveAquaVein(new CaveVein(new BlockEntry(block, 0), 2, 1, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 10);
		}
	}
}