/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenCaves;

public class MapGenCavesCaveworld extends MapGenCaves
{
	@Override
	protected void func_151541_a(long caveSeed, int chunkX, int chunkZ, Block[] blocks, double blockX, double blockY, double blockZ, float scale, float leftRightRadian, float upDownRadian, int currentY, int targetY, double scaleHeight)
	{
		int worldHeight = worldObj.getActualHeight();
		Random random = new Random(caveSeed);
		double centerX = chunkX * 16 + 8;
		double centerZ = chunkZ * 16 + 8;
		float leftRightChange = 0.0F;
		float upDownChange = 0.0F;

		if (targetY <= 0)
		{
			int blockRangeY = range * 16 - 16;
			targetY = blockRangeY - random.nextInt(blockRangeY / 4);
		}

		boolean createFinalRoom = false;

		if (currentY == -1)
		{
			currentY = targetY / 2;
			createFinalRoom = true;
		}

		int nextInterHeight = random.nextInt(targetY / 2) + targetY / 4;

		for (boolean chance = random.nextInt(6) == 0; currentY < targetY; ++currentY)
		{
			double roomWidth = 1.5D + MathHelper.sin(currentY * (float)Math.PI / targetY) * scale * 1.0F;
			double roomHeight = roomWidth * scaleHeight;
			float moveHorizontal = MathHelper.cos(upDownRadian);
			float moveVertical = MathHelper.sin(upDownRadian);
			blockX += MathHelper.cos(leftRightRadian) * moveHorizontal;
			blockY += moveVertical;
			blockZ += MathHelper.sin(leftRightRadian) * moveHorizontal;

			if (chance)
			{
				upDownRadian *= 0.92F;
			}
			else
			{
				upDownRadian *= 0.7F;
			}

			upDownRadian += upDownChange * 0.1F;
			leftRightRadian += leftRightChange * 0.1F;
			upDownChange *= 0.9F;
			leftRightChange *= 0.75F;
			upDownChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			leftRightChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

			if (!createFinalRoom && currentY == nextInterHeight && scale > 1.0F && targetY > 0)
			{
				func_151541_a(random.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, random.nextFloat() * 0.5F + 0.5F, leftRightRadian - (float)Math.PI / 2F, upDownRadian / 3.0F, currentY, targetY, 1.0D);
				func_151541_a(random.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, random.nextFloat() * 0.5F + 0.5F, leftRightRadian + (float)Math.PI / 2F, upDownRadian / 3.0F, currentY, targetY, 1.0D);

				return;
			}

			if (createFinalRoom || random.nextInt(4) != 0)
			{
				double distanceX = blockX - centerX;
				double distanceZ = blockZ - centerZ;
				double distanceY = targetY - currentY;
				double maxDistance = scale + 2.0F + 16.0F;

				if (distanceX * distanceX + distanceZ * distanceZ - distanceY * distanceY > maxDistance * maxDistance)
				{
					return;
				}

				if (blockX >= centerX - 16.0D - roomWidth * 2.0D && blockZ >= centerZ - 16.0D - roomWidth * 2.0D && blockX <= centerX + 16.0D + roomWidth * 2.0D && blockZ <= centerZ + 16.0D + roomWidth * 2.0D)
				{
					int xLow = Math.max(MathHelper.floor_double(blockX - roomWidth) - chunkX * 16 - 1, 0);
					int xHigh = Math.min(MathHelper.floor_double(blockX + roomWidth) - chunkX * 16 + 1, 16);
					int yLow = Math.max(MathHelper.floor_double(blockY - roomHeight) - 1, 1);
					int yHigh = Math.min(MathHelper.floor_double(blockY + roomHeight) + 1, worldHeight - 8);
					int zLow = Math.max(MathHelper.floor_double(blockZ - roomWidth) - chunkZ * 16 - 1, 0);
					int zHigh = Math.min(MathHelper.floor_double(blockZ + roomWidth) - chunkZ * 16 + 1, 16);

					for (int x = xLow; x < xHigh; ++x)
					{
						double xScale = (chunkX * 16 + x + 0.5D - blockX) / roomWidth;

						for (int z = zLow; z < zHigh; ++z)
						{
							double zScale = (chunkZ * 16 + z + 0.5D - blockZ) / roomWidth;
							int index = (x * 16 + z) * 256 + yHigh;

							if (xScale * xScale + zScale * zScale < 1.0D)
							{
								for (int y = yHigh - 1; y >= yLow; --y)
								{
									double yScale = (y + 0.5D - blockY) / roomHeight;

									if (yScale > -0.7D && xScale * xScale + yScale * yScale + zScale * zScale < 1.0D)
									{
										digBlock(blocks, index, x, y, z, chunkX, chunkZ, false);
									}

									--index;
								}
							}
						}
					}

					if (createFinalRoom)
					{
						break;
					}
				}
			}
		}
	}

	@Override
	protected void func_151538_a(World world, int x, int z, int chunkX, int chunkZ, Block[] blocks)
	{
		int worldHeight = world.provider.getActualHeight();
		int chance = rand.nextInt(rand.nextInt(rand.nextInt(15) + 1) + 1);

		if (rand.nextInt(6) != 0)
		{
			chance = 0;
		}

		for (int i = 0; i < chance; ++i)
		{
			double blockX = x * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight - 8) + 8);
			double blockZ = z * 16 + rand.nextInt(16);
			int count = 1;

			if (rand.nextInt(4) == 0)
			{
				func_151542_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ);

				count += rand.nextInt(4);
			}

			for (int j = 0; j < count; ++j)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = rand.nextFloat() * 2.5F + rand.nextFloat();

				if (rand.nextInt(10) == 0)
				{
					scale *= rand.nextFloat() * rand.nextFloat() * 3.5F + 1.0F;
				}

				func_151541_a(rand.nextLong(), chunkX, chunkZ, blocks, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 1.0D);
			}
		}
	}

	@Override
	protected void digBlock(Block[] blocks, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y < 10)
		{
			blocks[index] = Blocks.lava;
		}
		else
		{
			blocks[index] = null;
		}
	}
}