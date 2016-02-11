/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

public interface IBlockRenderOverlay
{
	@SideOnly(Side.CLIENT)
	public IIcon getOverlayIcon(int metadata);

	@SideOnly(Side.CLIENT)
	public IIcon getBaseIcon(int metadata);
}