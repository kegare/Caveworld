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
import net.minecraft.world.World;

public class MapGenCaveniaCaves extends MapGenCavesCaveworld
{
	@Override
	protected void func_151538_a(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks)
	{
		int worldHeight = world.provider.getActualHeight();
		int chance = rand.nextInt(rand.nextInt(rand.nextInt(20) + 1) + 1);

		for (int i = 0; i < chance; ++i)
		{
			double blockX = x * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight - 50) + 50);
			double blockZ = z * 16 + rand.nextInt(16);
			int count = 1;

			if (rand.nextInt(5) == 0)
			{
				func_151542_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ);

				count += rand.nextInt(8);
			}

			for (int j = 0; j < count; ++j)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 5.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 3.0F / 8.0F;
				float scale = rand.nextFloat() * 3.25F + rand.nextFloat();

				if (rand.nextInt(5) == 0)
				{
					scale *= rand.nextFloat() * rand.nextFloat() * 3.5F + 1.0F;
				}

				func_151541_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, scale * 1.5F, leftRightRadian, upDownRadian, 0, 0, 1.15D);
			}
		}
	}

	@Override
	protected void digBlock(Block[] blocks, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y < 2 || y > worldObj.getActualHeight() - 3)
		{
			blocks[index] = Blocks.stone;
		}
		else
		{
			blocks[index] = null;
		}
	}
}