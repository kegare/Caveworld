/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mapletree;

import caveworld.api.CaverAPI;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class MapleTreePlugin implements ICavePlugin
{
	public static final String MODID = "mod_ecru_MapleTree";

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
		Block block = GameRegistry.findBlock(MODID, "ecru_BlockOreBlobk");

		if (block != null)
		{
			CaverAPI.setMiningPointAmount(block, 0, 2);
			CaverAPI.setMiningPointAmount(block, 1, 1);
			CaverAPI.setMiningPointAmount(block, 2, 4);
		}
	}
}