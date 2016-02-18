/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class MapGenCaveniaCaves extends MapGenAquaCaves
{
	@Override
	protected void digBlock(Block[] blocks, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y < 2 || y > worldObj.getActualHeight() - 5)
		{
			blocks[index] = Blocks.stone;
		}
		else
		{
			blocks[index] = null;
		}
	}
}
