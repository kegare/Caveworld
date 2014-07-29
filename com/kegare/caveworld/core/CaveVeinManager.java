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
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Property;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.config.Config;
import com.kegare.caveworld.config.Config.ConfigEntryClass;
import com.kegare.caveworld.util.BlockEntry;

public class CaveVeinManager
{
	private static final LinkedHashSet<CaveVein> CAVE_VEINS = Sets.newLinkedHashSet();

	public static boolean addCaveVein(CaveVein vein)
	{
		if (vein.genWeight <= 0)
		{
			return false;
		}

		for (CaveVein entry : CAVE_VEINS)
		{
			if (entry.getBlock().equals(vein.getBlock()) && entry.getGenTargetBlock().equals(vein.getGenTargetBlock()))
			{
				entry.genBlockCount += vein.getGenBlockCount();
				entry.genWeight += vein.getGenWeight();
				entry.genMinHeight = Math.min(entry.getGenMinHeight(), vein.getGenMinHeight());
				entry.genMaxHeight = Math.max(entry.getGenMaxHeight(), vein.getGenMaxHeight());
				entry.genBiomes = ArrayUtils.addAll(entry.getGenBiomes(), vein.getGenBiomes());

				return false;
			}
		}

		return CAVE_VEINS.add(vein);
	}

	public static boolean addCaveVeinWithConfig(String name, CaveVein vein)
	{
		String block = Block.blockRegistry.getNameForObject(vein.getBlock().getBlock());
		int blockMetadata = vein.getBlock().getMetadata();
		int count = vein.getGenBlockCount();
		int weight = vein.getGenWeight();
		int min = vein.getGenMinHeight();
		int max = vein.getGenMaxHeight();
		String target = Block.blockRegistry.getNameForObject(vein.getGenTargetBlock().getBlock());
		int targetMetadata = vein.getGenTargetBlock().getMetadata();
		int[] biomes = vein.getGenBiomes();

		return addCaveVeinWithConfig(name, block, blockMetadata, count, weight, min, max, target, targetMetadata, biomes);
	}

	public static boolean addCaveVeinWithConfig(String name, String block, int blockMetadata, int count, int weight, int min, int max, String target, int targetMetadata, int[] biomes)
	{
		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = Config.veinsCfg.get(name, "block", Block.blockRegistry.getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(block) && !block.equals(prop.getString())) prop.set(block);
		propOrder.add(prop.getName());
		block = prop.getString();
		prop = Config.veinsCfg.get(name, "blockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (blockMetadata >= 0 && blockMetadata != prop.getInt()) prop.set(blockMetadata);
		propOrder.add(prop.getName());
		blockMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genBlockCount", 1);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (count >= 0 && count != prop.getInt()) prop.set(count);
		propOrder.add(prop.getName());
		count = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genWeight", 1);
		prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(ConfigEntryClass.VEIN_CONFIG.get());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (weight >= 0 && weight != prop.getInt()) prop.set(weight);
		propOrder.add(prop.getName());
		weight = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genMinHeight", 0);
		prop.setMinValue(0).setMaxValue(254).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (min >= 0 && min != prop.getInt()) prop.set(min);
		propOrder.add(prop.getName());
		min = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (max >= 0 && max != prop.getInt()) prop.set(max);
		propOrder.add(prop.getName());
		max = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(min + 1), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(target) && !target.equals(prop.getString())) prop.set(target);
		propOrder.add(prop.getName());
		target = prop.getString();
		prop = Config.veinsCfg.get(name, "genTargetBlockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (targetMetadata >= 0 && targetMetadata != prop.getInt()) prop.set(targetMetadata);
		propOrder.add(prop.getName());
		targetMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genBiomes", new int[] {});
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (biomes != null && biomes.length != prop.getIntList().length) prop.set(biomes);
		propOrder.add(prop.getName());
		biomes = prop.getIntList();

		Config.veinsCfg.setCategoryPropertyOrder(name, propOrder);

		return addCaveVein(new CaveVein(new BlockEntry(block, blockMetadata, Blocks.stone), count, weight, min, max, new BlockEntry(target, targetMetadata, Blocks.stone), biomes));
	}

	public static boolean addCaveVeinFromConfig(String name)
	{
		return addCaveVeinWithConfig(name, null, -1, -1, -1, -1,-1, null, -1, null);
	}

	public static boolean removeCaveVein(CaveVein vein)
	{
		return CAVE_VEINS.remove(vein);
	}

	public static int removeCaveVein(Block block, int metadata)
	{
		CaveVein entry;
		int count = 0;

		for (Iterator<CaveVein> veins = CAVE_VEINS.iterator(); veins.hasNext();)
		{
			entry = veins.next();

			if (entry.getBlock().getBlock() == block && entry.getBlock().getMetadata() == metadata)
			{
				veins.remove();

				++count;
			}
		}

		return count;
	}

	public static boolean removeCaveVeinWithConfig(String name, CaveVein vein)
	{
		return removeCaveVeinFromConfig(name) && removeCaveVein(vein);
	}

	public static boolean removeCaveVeinFromConfig(String name)
	{
		if (Config.veinsCfg.hasCategory(name))
		{
			Config.veinsCfg.removeCategory(Config.veinsCfg.getCategory(name));

			return true;
		}

		return false;
	}

	public static ImmutableSet<CaveVein> getCaveVeins()
	{
		return new ImmutableSet.Builder<CaveVein>().addAll(CAVE_VEINS).build();
	}

	public static void clearCaveVeins()
	{
		CAVE_VEINS.clear();
	}

	public static class CaveVein extends WorldGenerator
	{
		private BlockEntry block;
		private int genBlockCount;
		private int genWeight;
		private int genMinHeight;
		private int genMaxHeight;
		private BlockEntry genTargetBlock;
		private int[] genBiomes;

		public CaveVein(BlockEntry block, int count, int weight, int min, int max)
		{
			this.block = block;
			this.genBlockCount = count;
			this.genWeight = weight;
			this.genMinHeight = min;
			this.genMaxHeight = max;
			this.genBiomes = new int[] {};
		}

		public CaveVein(BlockEntry block, int count, int weight, int min, int max, BlockEntry target, Object... biomes)
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

		public CaveVein addGenBiomes(Object... objects)
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