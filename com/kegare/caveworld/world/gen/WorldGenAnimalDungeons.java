/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world.gen;

import static net.minecraftforge.common.ChestGenHooks.*;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraftforge.common.ChestGenHooks;

import com.kegare.caveworld.util.ArrayListExtended;

public class WorldGenAnimalDungeons extends WorldGenDungeons
{
	private final ArrayListExtended<String> spawnerMobs = new ArrayListExtended();

	{
		Class[] classes =
		{
			EntityPig.class, EntitySheep.class, EntityCow.class, EntityChicken.class
		};

		for (Class clazz : classes)
		{
			spawnerMobs.addIfAbsent((String)EntityList.classToStringMapping.get(clazz));
		}
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		byte var1 = 3;
		int var2 = random.nextInt(2) + 2;
		int var3 = random.nextInt(2) + 2;
		int var4 = 0;
		int blockX;
		int blockY;
		int blockZ;

		for (blockX = x - var2 - 1; blockX <= x + var2 + 1; ++blockX)
		{
			for (blockY = y - 1; blockY <= y + var1 + 1; ++blockY)
			{
				for (blockZ = z - var3 - 1; blockZ <= z + var3 + 1; ++blockZ)
				{
					Material material = world.getBlock(blockX, blockY, blockZ).getMaterial();

					if (blockY == y - 1 && !material.isSolid())
					{
						return false;
					}

					if (blockY == y + var1 + 1 && !material.isSolid())
					{
						return false;
					}

					if ((blockX == x - var2 - 1 || blockX == x + var2 + 1 || blockZ == z - var3 - 1 || blockZ == z + var3 + 1) && blockY == y && world.isAirBlock(blockX, blockY, blockZ) && world.isAirBlock(blockX, blockY + 1, blockZ))
					{
						++var4;
					}
				}
			}
		}

		if (var4 >= 1 && var4 <= 5)
		{
			for (blockX = x - var2 - 1; blockX <= x + var2 + 1; ++blockX)
			{
				for (blockY = y + var1; blockY >= y - 1; --blockY)
				{
					for (blockZ = z - var3 - 1; blockZ <= z + var3 + 1; ++blockZ)
					{
						if (blockX != x - var2 - 1 && blockY != y - 1 && blockZ != z - var3 - 1 && blockX != x + var2 + 1 && blockY != y + var1 + 1 && blockZ != z + var3 + 1)
						{
							world.setBlockToAir(blockX, blockY, blockZ);
						}
						else if (blockY >= 0 && !world.getBlock(blockX, blockY - 1, blockZ).getMaterial().isSolid())
						{
							world.setBlockToAir(blockX, blockY, blockZ);
						}
						else if (world.getBlock(blockX, blockY, blockZ).getMaterial().isSolid())
						{
							if (blockY == y - 1)
							{
								world.setBlock(blockX, blockY, blockZ, Blocks.grass, 0, 2);
							}
							else if (blockY == y && random.nextInt(5) == 0)
							{
								world.setBlock(blockX, blockY, blockZ, Blocks.torch, 0, 2);
							}
							else
							{
								world.setBlock(blockX, blockY, blockZ, Blocks.dirt, 0, 2);
							}
						}
					}
				}
			}

			blockX = 0;

			while (blockX < 2)
			{
				blockY = 0;

				while (true)
				{
					if (blockY < 3)
					{
						outside:
						{
							blockZ = x + random.nextInt(var2 * 2 + 1) - var2;
							int var5 = z + random.nextInt(var3 * 2 + 1) - var3;

							if (world.isAirBlock(blockZ, y, var5))
							{
								int i = 0;

								if (world.getBlock(blockZ - 1, y, var5).getMaterial().isSolid())
								{
									++i;
								}

								if (world.getBlock(blockZ + 1, y, var5).getMaterial().isSolid())
								{
									++i;
								}

								if (world.getBlock(blockZ, y, var5 - 1).getMaterial().isSolid())
								{
									++i;
								}

								if (world.getBlock(blockZ, y, var5 + 1).getMaterial().isSolid())
								{
									++i;
								}

								if (i == 1)
								{
									world.setBlock(blockZ, y, var5, Blocks.chest, 0, 2);
									TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(blockZ, y, var5);

									if (tileentitychest != null)
									{
										WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(DUNGEON_CHEST, random), tileentitychest, ChestGenHooks.getCount(DUNGEON_CHEST, random));
									}

									break outside;
								}
							}

							++blockY;
							continue;
						}
					}

					++blockX;
					break;
				}
			}

			world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)world.getTileEntity(x, y, z);

			if (spawner != null)
			{
				spawner.func_145881_a().setEntityName(spawnerMobs.get(random.nextInt(spawnerMobs.size()), "Cow"));
			}
			else
			{
				System.err.println("Failed to fetch mob spawner entity at (" + x + ", " + y + ", " + z + ")");
			}

			return true;
		}

		return false;
	}
}