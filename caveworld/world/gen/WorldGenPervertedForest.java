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

public class WorldGenPervertedForest extends WorldGenAbstractTree
{
	public WorldGenPervertedForest(boolean flag)
	{
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		int treeHeight = random.nextInt(3) + 5;
		boolean flag = true;

		if (y >= 1 && y + treeHeight + 1 <= 256)
		{
			int i;
			int j;

			for (int k = y; k <= y + 1 + treeHeight; ++k)
			{
				byte range = 1;

				if (k == y)
				{
					range = 0;
				}

				if (k >= y + 1 + treeHeight - 2)
				{
					range = 2;
				}

				for (i = x - range; i <= x + range && flag; ++i)
				{
					for (j = z - range; j <= z + range && flag; ++j)
					{
						if (k >= 0 && k < 256)
						{
							if (!isReplaceable(world, i, k, j))
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
				Block block2 = world.getBlock(x, y - 1, z);
				boolean isSoil = block2.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (BlockSapling)Blocks.sapling);

				if (isSoil && y < 256 - treeHeight - 1)
				{
					block2.onPlantGrow(world, x, y - 1, z, x, y, z);
					int l;

					for (l = y - 3 + treeHeight; l <= y + treeHeight; ++l)
					{
						i = l - (y + treeHeight);
						j = 1 - i / 2;

						for (int m = x - j; m <= x + j; ++m)
						{
							int var1 = m - x;

							for (int n = z - j; n <= z + j; ++n)
							{
								int var2 = n - z;

								if (Math.abs(var1) != j || Math.abs(var2) != j || random.nextInt(2) != 0 && i != 0)
								{
									Block block1 = world.getBlock(m, l, n);

									if (block1.isAir(world, m, l, n) || block1.isLeaves(world, m, l, n))
									{
										setBlockAndNotifyAdequately(world, m, l, n, CaveBlocks.perverted_leaves, 2);
									}
								}
							}
						}
					}

					for (l = 0; l < treeHeight; ++l)
					{
						Block block3 = world.getBlock(x, y + l, z);

						if (block3.isAir(world, x, y + l, z) || block3.isLeaves(world, x, y + l, z))
						{
							setBlockAndNotifyAdequately(world, x, y + l, z, CaveBlocks.perverted_log, 2);
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