/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCaveLava extends WorldGenerator
{
	private Block block;
	private boolean flag;

	public WorldGenCaveLava(Block block, boolean flag)
	{
		this.block = block;
		this.flag = flag;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		if (world.getBlock(x, y + 1, z) != Blocks.stone)
		{
			return false;
		}
		else if (world.getBlock(x, y, z).getMaterial() != Material.air && world.getBlock(x, y, z) != Blocks.stone)
		{
			return false;
		}
		else
		{
			int i = 0;

			if (world.getBlock(x - 1, y, z) == Blocks.stone)
			{
				++i;
			}

			if (world.getBlock(x + 1, y, z) == Blocks.stone)
			{
				++i;
			}

			if (world.getBlock(x, y, z - 1) == Blocks.stone)
			{
				++i;
			}

			if (world.getBlock(x, y, z + 1) == Blocks.stone)
			{
				++i;
			}

			if (world.getBlock(x, y - 1, z) == Blocks.stone)
			{
				++i;
			}

			int j = 0;

			if (world.isAirBlock(x - 1, y, z))
			{
				++j;
			}

			if (world.isAirBlock(x + 1, y, z))
			{
				++j;
			}

			if (world.isAirBlock(x, y, z - 1))
			{
				++j;
			}

			if (world.isAirBlock(x, y, z + 1))
			{
				++j;
			}

			if (world.isAirBlock(x, y - 1, z))
			{
				++j;
			}

			if (!flag && i == 4 && j == 1 || i == 5)
			{
				world.setBlock(x, y, z, block, 0, 2);
				world.scheduledUpdatesAreImmediate = true;
				block.updateTick(world, x, y, z, random);
				world.scheduledUpdatesAreImmediate = false;
			}

			return true;
		}
	}
}