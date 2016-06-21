package caveworld.world.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MapGenCavelandRavine extends MapGenRavineCaveworld
{
	@Override
	protected void func_151538_a(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks)
	{
		if (rand.nextInt(25) == 0)
		{
			int worldHeight = world.getActualHeight();
			double blockX = x * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight / 2) + world.provider.getAverageGroundLevel() + 10);
			double blockZ = z * 16 + rand.nextInt(16);
			float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
			float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
			float scale = (rand.nextFloat() * 3.0F + rand.nextFloat()) * 2.0F;

			if (blockY > worldHeight - 40)
			{
				blockY = world.provider.getAverageGroundLevel() + rand.nextInt(10);
			}

			func_151540_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 2.0D);
		}
	}

	@Override
	protected void digBlock(Block[] blocks, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y < 5)
		{
			blocks[index] = Blocks.stone;
		}
		else if (y == 5)
		{
			blocks[index] = Blocks.gravel;
		}
		else if (y < 8)
		{
			BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
			Block block = Blocks.flowing_water;

			if (BiomeDictionary.isBiomeOfType(biome, Type.COLD))
			{
				block = rand.nextInt(3) == 0 ? Blocks.flowing_water : Blocks.ice;
			}

			blocks[index] = block;
		}
		else
		{
			blocks[index] = null;
		}
	}
}