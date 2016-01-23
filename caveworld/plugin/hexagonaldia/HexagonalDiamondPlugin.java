/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.hexagonaldia;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.core.CaveVeinManager.CaveVein;
import caveworld.core.Config;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

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
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 10);
		}
	}
}