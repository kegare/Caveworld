package caveworld.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class MapGenAquaRavine extends MapGenRavineCaveworld
{
	@Override
	protected void func_151538_a(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks)
	{
		if (rand.nextInt(700) == 0)
		{
			double blockX = x * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(10) + world.provider.getAverageGroundLevel());
			double blockZ = z * 16 + rand.nextInt(16);

			for (int i = 0; i < 2; ++i)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 6.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 9.0F;

				func_151540_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 20.0D);
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
			blocks[index] = Blocks.water;
		}
	}
}