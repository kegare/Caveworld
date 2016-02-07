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

import caveworld.block.CaveBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenPervertedTaiga extends WorldGenAbstractTree
{
	public WorldGenPervertedTaiga(boolean flag)
	{
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		int treeHeight = random.nextInt(4) + 6;
		int i1 = 1 + random.nextInt(2);
		int j1 = treeHeight - i1;
		int k1 = 2 + random.nextInt(2);
		boolean flag = true;

		if (y >= 1 && y + treeHeight + 1 <= 256)
		{
			int i;
			int range;

			for (int j = y; j <= y + 1 + treeHeight && flag; ++j)
			{
				if (j - y < i1)
				{
					range = 0;
				}
				else
				{
					range = k1;
				}

				for (i = x - range; i <= x + range && flag; ++i)
				{
					for (int k = z - range; k <= z + range && flag; ++k)
					{
						if (j >= 0 && j < 256)
						{
							Block block = world.getBlock(i, j, k);

							if (!block.isAir(world, i, j, k) && !block.isLeaves(world, i, j, k))
							{
								flag = false;
							}
						}
						else
						{
							flag = false;
						}
					}
				}
			}

			if (!flag)
			{
				return false;
			}
			else
			{
				Block block1 = world.getBlock(x, y - 1, z);
				boolean isSoil = block1.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (BlockSapling)Blocks.sapling);

				if (isSoil && y < 256 - treeHeight - 1)
				{
					block1.onPlantGrow(world, x, y - 1, z, x, y, z);
					range = random.nextInt(2);
					i = 1;
					byte b0 = 0;
					int ty;
					int j;

					for (j = 0; j <= j1; ++j)
					{
						ty = y + treeHeight - j;

						for (int l2 = x - range; l2 <= x + range; ++l2)
						{
							int i3 = l2 - x;

							for (int j3 = z - range; j3 <= z + range; ++j3)
							{
								int k3 = j3 - z;

								if ((Math.abs(i3) != range || Math.abs(k3) != range || range <= 0) && world.getBlock(l2, ty, j3).canBeReplacedByLeaves(world, l2, ty, j3))
								{
									this.setBlockAndNotifyAdequately(world, l2, ty, j3, CaveBlocks.perverted_leaves, 1);
								}
							}
						}

						if (range >= i)
						{
							range = b0;
							b0 = 1;
							++i;

							if (i > k1)
							{
								i = k1;
							}
						}
						else
						{
							++range;
						}
					}

					j = random.nextInt(3);

					for (ty = 0; ty < treeHeight - j; ++ty)
					{
						Block block2 = world.getBlock(x, y + ty, z);

						if (block2.isAir(world, x, y + ty, z) || block2.isLeaves(world, x, y + ty, z))
						{
							setBlockAndNotifyAdequately(world, x, y + ty, z, CaveBlocks.perverted_log, 1);
						}
					}

					return true;
				}

				return false;
			}
		}

		return false;
	}
}