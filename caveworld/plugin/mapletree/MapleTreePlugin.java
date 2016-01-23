/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mapletree;

import caveworld.api.CaveworldAPI;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class MapleTreePlugin
{
	public static final String MODID = "mod_ecru_MapleTree";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		Block block = GameRegistry.findBlock(MODID, "ecru_BlockOreBlobk");

		if (block != null)
		{
			CaveworldAPI.setMiningPointAmount(block, 0, 2);
			CaveworldAPI.setMiningPointAmount(block, 1, 1);
			CaveworldAPI.setMiningPointAmount(block, 2, 4);
		}
	}
}