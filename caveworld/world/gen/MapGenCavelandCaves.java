package caveworld.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class MapGenCavelandCaves extends MapGenCavesCaveworld
{
	@Override
	protected void func_151538_a(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks)
	{
		int worldHeight = world.provider.getActualHeight();
		int heightHalf = worldHeight / 2;
		int chance = rand.nextInt(rand.nextInt(rand.nextInt(30) + 1) + 1);

		for (int i = 0; i < chance; ++i)
		{
			double blockX = x * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight - heightHalf) + heightHalf);
			double blockZ = z * 16 + rand.nextInt(16);
			int count = 1;

			if (rand.nextInt(5) == 0)
			{
				func_151542_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ);

				count += rand.nextInt(3);
			}

			for (int j = 0; j < count; ++j)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI ;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = rand.nextFloat() * 8.0F + rand.nextFloat();

				if (rand.nextInt(6) == 0)
				{
					scale *= rand.nextFloat() * rand.nextFloat() * 3.0F + 1.0F;
				}

				func_151541_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 1.25D);
			}
		}
	}

	@Override
	protected void digBlock(Block[] blocks, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y < 5)
		{
			blocks[index] = Blocks.stone;
		}
		else if (y < 8)
		{
			blocks[index] = Blocks.dirt;
		}
		else if (y == 8)
		{
			blocks[index] = Blocks.grass;
		}
		else
		{
			blocks[index] = null;
		}
	}
}