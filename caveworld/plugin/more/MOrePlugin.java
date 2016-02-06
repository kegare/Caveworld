/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.more;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.core.CaveVeinManager.CaveVein;
import caveworld.core.Config;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MOrePlugin implements ICavePlugin
{
	public static final String MODID = "more";

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
		Block block = GameRegistry.findBlock(MODID, "CopperOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 16, 20, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "TinOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 14, 24, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "UranOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 10, 7, 100, 0, 50));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "RubyOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 4, 100, 0, 50));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "SapphireOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 4, 100, 0, 64));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "SilverOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 15, 100, 0, 255));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "PlatinumOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 5, 5, 100, 0, 30));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "TitaniumOre");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				 CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 3, 100, 0, 24));
			}

			CaveworldAPI.setMiningPointAmount(block, 0, 1);
		}

		block = GameRegistry.findBlock(MODID, "BlockGranite");

		if (block != null)
		{
			if (Config.veinsAutoRegister)
			{
				CaveworldAPI.addCaveVein(new CaveVein(new BlockEntry(block, 0), 6, 20, 100, 0, 255, null, Type.HILLS));
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