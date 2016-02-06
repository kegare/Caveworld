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
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class MiningmodPlugin implements ICavePlugin
{
	public static final String MODID = "miningmod";

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