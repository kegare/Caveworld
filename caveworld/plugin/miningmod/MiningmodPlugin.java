/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.miningmod;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.core.CaveVeinManager.CaveVein;
import caveworld.core.Config;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class MiningmodPlugin
{
	public static final String MODID = "miningmod";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Block block = GameRegistry.findBlock(MODID, "crystalOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 12, 100, 12, 64));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "copperOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 8, 10, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "tinOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 12, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "rubyOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 5, 100, 10, 64));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 2);
		}

		block = GameRegistry.findBlock(MODID, "sapphireOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 5, 100, 5, 64));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 2);
		}

		block = GameRegistry.findBlock(MODID, "silverOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 8, 6, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "mithrilOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 5, 5, 100, 0, 50));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}
	}
}