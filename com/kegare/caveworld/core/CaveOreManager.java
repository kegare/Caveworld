/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.core;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.util.BlockEntry;

public class CaveOreManager
{
	private static final LinkedHashSet<CaveOre> CAVE_ORES = Sets.newLinkedHashSet();

	public static boolean addCaveOre(CaveOre ore)
	{
		for (CaveOre caveOre : CAVE_ORES)
		{
			if (caveOre.getBlock().equals(ore.getBlock()) && caveOre.getGenTargetBlock().equals(ore.genTargetBlock))
			{
				caveOre.genBlockCount += ore.getGenBlockCount();
				caveOre.genWeight += ore.getGenWeight();
				caveOre.genMinHeight = Math.min(caveOre.getGenMinHeight(), ore.getGenMinHeight());
				caveOre.genMaxHeight = Math.max(caveOre.getGenMaxHeight(), ore.getGenMaxHeight());
				caveOre.genBiomes = ArrayUtils.addAll(caveOre.getGenBiomes(), ore.getGenBiomes());

				return false;
			}
		}

		return CAVE_ORES.add(ore);
	}

	public static int removeCaveOre(Block block, int metadata)
	{
		Iterator<CaveOre> ores = CAVE_ORES.iterator();
		int count = 0;

		while (ores.hasNext())
		{
			CaveOre ore = ores.next();

			if (ore.getBlock().getBlock() == block && ore.getBlock().getMetadata() == metadata)
			{
				ores.remove();

				++count;
			}
		}

		return count;
	}

	public static boolean containsOre(Block block, int metadata)
	{
		for (CaveOre ore : CAVE_ORES)
		{
			if (ore.getBlock().getBlock() == block && ore.getBlock().getMetadata() == metadata)
			{
				return ore.getBlock().getBlock().getMaterial() == Material.rock;
			}
		}

		return false;
	}

	public static ImmutableSet<CaveOre> getCaveOres()
	{
		return new ImmutableSet.Builder<CaveOre>().addAll(CAVE_ORES).build();
	}

	public static void clearCaveOres()
	{
		CAVE_ORES.clear();
	}

	public static class CaveOre extends WorldGenerator
	{
		private BlockEntry block;
		private int genBlockCount;
		private int genWeight;
		private int genMinHeight;
		private int genMaxHeight;
		private BlockEntry genTargetBlock;
		private int[] genBiomes;

		public CaveOre(BlockEntry block, int count, int weight, int min, int max)
		{
			this.block = block;
			this.genBlockCount = count;
			this.genWeight = weight;
			this.genMinHeight = min;
			this.genMaxHeight = max;
			this.genBiomes = new int[] {};
		}

		public CaveOre(BlockEntry block, int count, int weight, int min, int max, BlockEntry target, Object... biomes)
		{
			this(block, count, weight, min, max);
			this.genTargetBlock = target;
			this.addGenBiomes(biomes);
		}

		@Override
		public String toString()
		{
			List<String> list = Lists.newArrayList();
			list.add(Block.blockRegistry.getNameForObject(getBlock().getBlock()));
			list.add(Integer.toString(getBlock().getMetadata()));
			list.add(Integer.toString(getGenBlockCount()));
			list.add(Integer.toString(getGenWeight()));
			list.add(Integer.toString(getGenMinHeight()));
			list.add(Integer.toString(getGenMaxHeight()));
			list.add(Block.blockRegistry.getNameForObject(getGenTargetBlock().getBlock()));
			list.add(Integer.toString(getGenTargetBlock().getMetadata()));

			if (getGenBiomes().length > 0)
			{
				List<String> biomes = Lists.newArrayList();

				for (int id : getGenBiomes())
				{
					biomes.add(Integer.toString(id));
				}

				list.add(Joiner.on('.').join(biomes));
			}

			return Joiner.on(',').join(list);
		}

		public CaveOre addGenBiomes(Object... objects)
		{
			for (Object obj : objects)
			{
				if (obj instanceof Type)
				{
					Type type = (Type)obj;

					for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
					{
						genBiomes = ArrayUtils.add(genBiomes, biome.biomeID);
					}
				}
				else if (obj instanceof BiomeGenBase)
				{
					genBiomes = ArrayUtils.add(genBiomes, ((BiomeGenBase)obj).biomeID);
				}
				else if (obj instanceof Integer)
				{
					BiomeGenBase biome = BiomeGenBase.getBiome((Integer)obj);

					if (biome != null)
					{
						genBiomes = ArrayUtils.add(genBiomes, biome.biomeID);
					}
				}
			}

			return this;
		}

		public BlockEntry getBlock()
		{
			return block == null ? new BlockEntry(Blocks.stone, 0) : block;
		}

		public int getGenBlockCount()
		{
			return MathHelper.clamp_int(genBlockCount, 1, 100);
		}

		public int getGenWeight()
		{
			return MathHelper.clamp_int(genWeight, 1, 100);
		}

		public int getGenMinHeight()
		{
			return MathHelper.clamp_int(genMinHeight, 0, 254);
		}

		public int getGenMaxHeight()
		{
			return MathHelper.clamp_int(genMaxHeight, 1, 255);
		}

		public BlockEntry getGenTargetBlock()
		{
			return genTargetBlock == null ? new BlockEntry(Blocks.stone, 0) : genTargetBlock;
		}

		public int[] getGenBiomes()
		{
			return genBiomes == null ? new int[] {} : genBiomes;
		}

		@Override
		public boolean generate(World world, Random random, int x, int y, int z)
		{
			float var1 = random.nextFloat() * (float)Math.PI;
			double var2 = x + 8 + MathHelper.sin(var1) * getGenBlockCount() / 8.0F;
			double var3 = x + 8 - MathHelper.sin(var1) * getGenBlockCount() / 8.0F;
			double var4 = z + 8 + MathHelper.cos(var1) * getGenBlockCount() / 8.0F;
			double var5 = z + 8 - MathHelper.cos(var1) * getGenBlockCount() / 8.0F;
			double var6 = y + random.nextInt(3) - 2;
			double var7 = y + random.nextInt(3) - 2;

			for (int count = 0; count <= getGenBlockCount(); ++count)
			{
				double var8 = var2 + (var3 - var2) * count / getGenBlockCount();
				double var9 = var6 + (var7 - var6) * count / getGenBlockCount();
				double var10 = var4 + (var5 - var4) * count / getGenBlockCount();
				double var11 = random.nextDouble() * getGenBlockCount() / 16.0D;
				double var12 = (MathHelper.sin(count * (float)Math.PI / getGenBlockCount()) + 1.0F) * var11 + 1.0D;
				double var13 = (MathHelper.sin(count * (float)Math.PI / getGenBlockCount()) + 1.0F) * var11 + 1.0D;
				int minX = MathHelper.floor_double(var8 - var12 / 2.0D);
				int maxX = MathHelper.floor_double(var8 + var12 / 2.0D);
				int minY = MathHelper.floor_double(var9 - var13 / 2.0D);
				int maxY = MathHelper.floor_double(var9 + var13 / 2.0D);
				int minZ = MathHelper.floor_double(var10 - var12 / 2.0D);
				int maxZ = MathHelper.floor_double(var10 + var12 / 2.0D);

				for (int blockX = minX; blockX <= maxX; ++blockX)
				{
					double xScale = (blockX + 0.5D - var8) / (var12 / 2.0D);

					if (xScale * xScale < 1.0D)
					{
						for (int blockY = minY; blockY <= maxY; ++blockY)
						{
							double yScale = (blockY + 0.5D - var9) / (var13 / 2.0D);

							if (xScale * xScale + yScale * yScale < 1.0D)
							{
								for (int blockZ = minZ; blockZ <= maxZ; ++blockZ)
								{
									double zScale = (blockZ + 0.5D - var10) / (var12 / 2.0D);

									if (xScale * xScale + yScale * yScale + zScale * zScale < 1.0D)
									{
										if (genTargetBlock == null && !world.getBlock(blockX, blockY, blockZ).isReplaceableOreGen(world, blockX, blockY, blockZ, Blocks.stone))
										{
											continue;
										}
										else if (!world.getBlock(blockX, blockY, blockZ).isReplaceableOreGen(world, blockX, blockY, blockZ, getGenTargetBlock().getBlock()) || world.getBlockMetadata(blockX, blockY, blockZ) != getGenTargetBlock().getMetadata())
										{
											continue;
										}

										BiomeGenBase biome = world.getBiomeGenForCoords(blockX, blockZ);
										boolean flag = false;

										if (getGenBiomes().length <= 0)
										{
											flag = true;
										}
										else
										{
											for (int id : getGenBiomes())
											{
												if (id == biome.biomeID)
												{
													flag = true;

													break;
												}
											}
										}

										if (flag)
										{
											world.setBlock(blockX, blockY, blockZ, block.getBlock(), block.getMetadata(), 2);
										}
									}
								}
							}
						}
					}
				}
			}

			return true;
		}
	}
}