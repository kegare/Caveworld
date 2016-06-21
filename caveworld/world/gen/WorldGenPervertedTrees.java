package caveworld.world.gen;

import java.util.Random;

import caveworld.block.CaveBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldGenPervertedTrees extends WorldGenAbstractTree
{
	private final int minTreeHeight;
	private final boolean vinesGrow;
	private final int metaWood;
	private final int metaLeaves;

	public WorldGenPervertedTrees(boolean flag)
	{
		this(flag, 4, 0, 0, false);
	}

	public WorldGenPervertedTrees(boolean flag, int height, int metaWood, int metaLeaves, boolean vines)
	{
		super(flag);
		this.minTreeHeight = height;
		this.metaWood = metaWood;
		this.metaLeaves = metaLeaves;
		this.vinesGrow = vines;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		int treeHeight = random.nextInt(3) + minTreeHeight;
		boolean flag = true;

		if (y >= 1 && y + treeHeight + 1 <= 256)
		{
			byte range;
			int i;
			Block block;

			for (int j = y; j <= y + 1 + treeHeight; ++j)
			{
				range = 1;

				if (j == y)
				{
					range = 0;
				}

				if (j >= y + 1 + treeHeight - 2)
				{
					range = 2;
				}

				for (int k = x - range; k <= x + range && flag; ++k)
				{
					for (i = z - range; i <= z + range && flag; ++i)
					{
						if (j >= 0 && j < 256)
						{
							block = world.getBlock(k, j, i);

							if (!isReplaceable(world, k, j, i))
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
					range = 3;
					byte b1 = 0;
					int j;
					int k;
					int l;
					int m;

					for (i = y - range + treeHeight; i <= y + treeHeight; ++i)
					{
						m = i - (y + treeHeight);
						j = b1 + 1 - m / 2;

						for (k = x - j; k <= x + j; ++k)
						{
							l = k - x;

							for (int n = z - j; n <= z + j; ++n)
							{
								if (Math.abs(l) != j || Math.abs(n - z) != j || random.nextInt(2) != 0 && m != 0)
								{
									Block block1 = world.getBlock(k, i, n);

									if (block1.isAir(world, k, i, n) || block1.isLeaves(world, k, i, n))
									{
										setBlockAndNotifyAdequately(world, k, i, n, CaveBlocks.perverted_leaves, metaLeaves);
									}
								}
							}
						}
					}

					for (i = 0; i < treeHeight; ++i)
					{
						block = world.getBlock(x, y + i, z);

						if (block.isAir(world, x, y + i, z) || block.isLeaves(world, x, y + i, z))
						{
							setBlockAndNotifyAdequately(world, x, y + i, z, CaveBlocks.perverted_log, metaWood);

							if (vinesGrow && i > 0)
							{
								if (random.nextInt(3) > 0 && world.isAirBlock(x - 1, y + i, z))
								{
									setBlockAndNotifyAdequately(world, x - 1, y + i, z, Blocks.vine, 8);
								}

								if (random.nextInt(3) > 0 && world.isAirBlock(x + 1, y + i, z))
								{
									setBlockAndNotifyAdequately(world, x + 1, y + i, z, Blocks.vine, 2);
								}

								if (random.nextInt(3) > 0 && world.isAirBlock(x, y + i, z - 1))
								{
									setBlockAndNotifyAdequately(world, x, y + i, z - 1, Blocks.vine, 1);
								}

								if (random.nextInt(3) > 0 && world.isAirBlock(x, y + i, z + 1))
								{
									setBlockAndNotifyAdequately(world, x, y + i, z + 1, Blocks.vine, 4);
								}
							}
						}
					}

					if (vinesGrow)
					{
						for (i = y - 3 + treeHeight; i <= y + treeHeight; ++i)
						{
							m = i - (y + treeHeight);
							j = 2 - m / 2;

							for (k = x - j; k <= x + j; ++k)
							{
								for (l = z - j; l <= z + j; ++l)
								{
									if (world.getBlock(k, i, l).isLeaves(world, k, i, l))
									{
										if (random.nextInt(4) == 0 && world.getBlock(k - 1, i, l).isAir(world, k - 1, i, l))
										{
											growVines(world, k - 1, i, l, 8);
										}

										if (random.nextInt(4) == 0 && world.getBlock(k + 1, i, l).isAir(world, k + 1, i, l))
										{
											growVines(world, k + 1, i, l, 2);
										}

										if (random.nextInt(4) == 0 && world.getBlock(k, i, l - 1).isAir(world, k, i, l - 1))
										{
											growVines(world, k, i, l - 1, 1);
										}

										if (random.nextInt(4) == 0 && world.getBlock(k, i, l + 1).isAir(world, k, i, l + 1))
										{
											growVines(world, k, i, l + 1, 4);
										}
									}
								}
							}
						}

						if (random.nextInt(5) == 0 && treeHeight > 5)
						{
							for (i = 0; i < 2; ++i)
							{
								for (m = 0; m < 4; ++m)
								{
									if (random.nextInt(4 - i) == 0)
									{
										j = random.nextInt(3);
										setBlockAndNotifyAdequately(world, x + Direction.offsetX[Direction.rotateOpposite[m]], y + treeHeight - 5 + i, z + Direction.offsetZ[Direction.rotateOpposite[m]], Blocks.cocoa, j << 2 | m);
									}
								}
							}
						}
					}

					return true;
				}

				return false;
			}
		}

		return false;
	}

	private void growVines(World world, int x, int y, int z, int meta)
	{
		setBlockAndNotifyAdequately(world, x, y, z, Blocks.vine, meta);

		int i = 4;

		while (true)
		{
			--y;

			if (!world.getBlock(x, y, z).isAir(world, x, y, z) || i <= 0)
			{
				return;
			}

			setBlockAndNotifyAdequately(world, x, y, z, Blocks.vine, meta);

			--i;
		}
	}
}