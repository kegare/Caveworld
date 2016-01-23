/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.moreinventory;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class MIMPlugin
{
	public static final String MODID = "MoreInventoryMod";

	public static Item torchHolder;
	public static Item arrowHolder;

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		torchHolder = GameRegistry.findItem(MODID, "torchholder");
		arrowHolder = GameRegistry.findItem(MODID, "arrowholder");
	}
}