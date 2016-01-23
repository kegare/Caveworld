/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import caveworld.api.BlockEntry;
import caveworld.api.ICaveVein;
import caveworld.api.ICaveVeinManager;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CaveVeinManager implements ICaveVeinManager
{
	private final List<ICaveVein> CAVE_VEINS = Lists.newArrayList();

	@Override
	public Configuration getConfig()
	{
		return Config.veinsCfg;
	}

	@Override
	public boolean addCaveVein(ICaveVein vein)
	{
		String block = vein == null ? null : GameData.getBlockRegistry().getNameForObject(vein.getBlock().getBlock());
		int blockMetadata = vein == null ? -1 : vein.getBlock().getMetadata();
		int count = vein == null ? -1 : vein.getGenBlockCount();
		int weight = vein == null ? -1 : vein.getGenWeight();
		int rate = vein == null ? -1 : vein.getGenRate();
		int min = vein == null ? -1 : vein.getGenMinHeight();
		int max = vein == null ? -1 : vein.getGenMaxHeight();
		String target = vein == null ? null : GameData.getBlockRegistry().getNameForObject(vein.getGenTargetBlock().getBlock());
		int targetMetadata = vein == null ? -1 : vein.getGenTargetBlock().getMetadata();
		int[] biomes = vein == null ? null : vein.getGenBiomes();

		String name = Integer.toString(getCaveVeins().size());

		if (vein == null && !getConfig().hasCategory(name))
		{
			return false;
		}

		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = getConfig().get(name, "block", GameData.getBlockRegistry().getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(block)) prop.set(block);
		propOrder.add(prop.getName());
		block = prop.getString();
		if (!GameData.getBlockRegistry().containsKey(Strings.nullToEmpty(block))) return false;

		prop = getConfig().get(name, "blockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (blockMetadata >= 0) prop.set(MathHelper.clamp_int(blockMetadata, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		blockMetadata = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genBlockCount", 1);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (count >= 0) prop.set(MathHelper.clamp_int(count, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		count = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genWeight", 1);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (weight >= 0) prop.set(MathHelper.clamp_int(weight, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genRate", 100);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (rate >= 0) prop.set(MathHelper.clamp_int(rate, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		rate = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genMinHeight", 0);
		prop.setMinValue(0).setMaxValue(254).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (min >= 0) prop.set(MathHelper.clamp_int(min, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		min = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (max >= 0) prop.set(MathHelper.clamp_int(max, min + 1, Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		max = MathHelper.clamp_int(prop.getInt(), min + 1, Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genTargetBlock", GameData.getBlockRegistry().getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(target)) prop.set(target);
		if (!GameData.getBlockRegistry().containsKey(prop.getString())) prop.setToDefault();
		propOrder.add(prop.getName());
		target = prop.getString();

		prop = getConfig().get(name, "genTargetBlockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (targetMetadata >= 0) prop.set(MathHelper.clamp_int(targetMetadata, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		targetMetadata = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = getConfig().get(name, "genBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (biomes != null) prop.set(biomes);
		propOrder.add(prop.getName());
		biomes = prop.getIntList();

		getConfig().setCategoryPropertyOrder(name, propOrder);

		if (vein == null)
		{
			vein = new CaveVein(new BlockEntry(block, blockMetadata), count, weight, rate, min, max, new BlockEntry(target, targetMetadata), biomes);
		}

		return getCaveVeins().add(vein);
	}

	@Override
	public int removeCaveVeins(ICaveVein vein)
	{
		int prev = getCaveVeins().size();

		for (int i = getCaveVeins().indexOf(vein); i >= 0;)
		{
			getCaveVeins().remove(i);

			getConfig().removeCategory(getConfig().getCategory(Integer.toString(i)));
		}

		return Math.max(getCaveVeins().size(), prev);
	}

	@Override
	public int removeCaveVeins(Block block, int metadata)
	{
		ICaveVein vein;
		int prev = getCaveVeins().size();

		for (int i = 0; i < prev; ++i)
		{
			vein = getCaveVeins().get(i);

			if (vein.getBlock().getBlock() == block && vein.getBlock().getMetadata() == metadata)
			{
				getCaveVeins().remove(i);

				getConfig().removeCategory(getConfig().getCategory(Integer.toString(i)));
			}
		}

		return Math.max(getCaveVeins().size(), prev);
	}

	@Override
	public ICaveVein getRandomCaveVein(Random random)
	{
		try
		{
			return (ICaveVein)WeightedRandom.getRandomItem(random, getCaveVeins());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public List<ICaveVein> getCaveVeins()
	{
		return CAVE_VEINS;
	}

	@Override
	public void clearCaveVeins()
	{
		getCaveVeins().clear();
	}

	public static class CaveVein extends WeightedRandom.Item implements ICaveVein
	{
		private BlockEntry block;
		private int genBlockCount;
		private int genRate;
		private int genMinHeight;
		private int genMaxHeight;
		private BlockEntry genTargetBlock;
		private int[] genBiomes;

		public CaveVein()
		{
			super(0);
		}

		public CaveVein(BlockEntry block, int count, int weight, int rate, int min, int max)
		{
			super(weight);
			this.block = block;
			this.genBlockCount = count;
			this.genRate = rate;
			this.genMinHeight = min;
			this.genMaxHeight = max;
			this.genBiomes = new int[0];
		}

		public CaveVein(BlockEntry block, int count, int weight, int rate, int min, int max, BlockEntry target, int[] biomes)
		{
			this(block, count, weight, rate, min, max);
			this.genTargetBlock = target;
			this.genBiomes = biomes;
		}

		public CaveVein(BlockEntry block, int count, int weight, int rate, int min, int max, BlockEntry target, Object... biomes)
		{
			this(block, count, weight, rate, min, max);
			this.genTargetBlock = target;
			this.genBiomes = getBiomes(biomes);
		}

		public CaveVein(ICaveVein vein)
		{
			this(vein.getBlock(), vein.getGenBlockCount(), vein.getGenWeight(), vein.getGenRate(), vein.getGenMinHeight(), vein.getGenMaxHeight(), vein.getGenTargetBlock(), vein.getGenBiomes());
		}

		@Override
		public BlockEntry setBlock(BlockEntry entry)
		{
			return block = entry;
		}

		@Override
		public BlockEntry getBlock()
		{
			return block == null ? new BlockEntry(Blocks.stone, 0) : block;
		}

		@Override
		public int setGenBlockCount(int count)
		{
			return genBlockCount = count;
		}

		@Override
		public int getGenBlockCount()
		{
			return MathHelper.clamp_int(genBlockCount, 1, 500);
		}

		@Override
		public int setGenWeight(int weight)
		{
			return itemWeight = weight;
		}

		@Override
		public int getGenWeight()
		{
			return MathHelper.clamp_int(itemWeight, 1, 100);
		}

		@Override
		public int setGenRate(int rate)
		{
			return genRate = rate;
		}

		@Override
		public int getGenRate()
		{
			return MathHelper.clamp_int(genRate, 1, 100);
		}

		@Override
		public int setGenMinHeight(int height)
		{
			return genMinHeight = height;
		}

		@Override
		public int getGenMinHeight()
		{
			return MathHelper.clamp_int(genMinHeight, 0, 254);
		}

		@Override
		public int setGenMaxHeight(int height)
		{
			return genMaxHeight = height;
		}

		@Override
		public int getGenMaxHeight()
		{
			return MathHelper.clamp_int(genMaxHeight, 1, 255);
		}

		@Override
		public BlockEntry setGenTargetBlock(BlockEntry entry)
		{
			return genTargetBlock = entry;
		}

		@Override
		public BlockEntry getGenTargetBlock()
		{
			return genTargetBlock == null ? new BlockEntry(Blocks.stone, 0) : genTargetBlock;
		}

		@Override
		public int[] setGenBiomes(int[] biomes)
		{
			return genBiomes = biomes;
		}

		@Override
		public int[] getGenBiomes()
		{
			return genBiomes == null ? new int[0] : genBiomes;
		}

		private int[] getBiomes(Object... objects)
		{
			Set<Integer> biomes = Sets.newTreeSet();

			for (Object element : objects)
			{
				if (element instanceof BiomeGenBase)
				{
					biomes.add(((BiomeGenBase)element).biomeID);
				}
				else if (element instanceof Integer)
				{
					BiomeGenBase biome = BiomeGenBase.getBiome((Integer)element);

					if (biome != null)
					{
						biomes.add(biome.biomeID);
					}
				}
				else if (element instanceof Type)
				{
					Type type = (Type)element;

					for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
					{
						biomes.add(biome.biomeID);
					}
				}
			}

			return Ints.toArray(biomes);
		}

		@Override
		public void generateVein(World world, Random random, int chunkX, int chunkZ)
		{
			int worldHeight = world.getActualHeight();
			BlockEntry block = getBlock();
			int count = getGenBlockCount();
			int weight = getGenWeight();
			int rate = getGenRate();
			int min = getGenMinHeight();
			int max = getGenMaxHeight();
			BlockEntry target = getGenTargetBlock();
			int[] biomes = getGenBiomes();

			if (weight > 0 && min < worldHeight && max < worldHeight && min < max)
			{
				for (int i = 0; i < weight; ++i)
				{
					if (random.nextInt(100) + 1 > rate)
					{
						continue;
					}

					int x = chunkX + random.nextInt(16);
					int y = random.nextInt(Math.min(max, worldHeight - 1) - min) + min;
					int z = chunkZ + random.nextInt(16);
					float var1 = random.nextFloat() * (float)Math.PI;
					double var2 = x + 8 + MathHelper.sin(var1) * count / 8.0F;
					double var3 = x + 8 - MathHelper.sin(var1) * count / 8.0F;
					double var4 = z + 8 + MathHelper.cos(var1) * count / 8.0F;
					double var5 = z + 8 - MathHelper.cos(var1) * count / 8.0F;
					double var6 = y + random.nextInt(3) - 2;
					double var7 = y + random.nextInt(3) - 2;
					int gen = 0;

					for (int j = 0; gen <= count && j <= count; ++j)
					{
						double var8 = var2 + (var3 - var2) * j / count;
						double var9 = var6 + (var7 - var6) * j / count;
						double var10 = var4 + (var5 - var4) * j / count;
						double var11 = random.nextDouble() * count / 16.0D;
						double var12 = (MathHelper.sin(j * (float)Math.PI / count) + 1.0F) * var11 + 1.0D;
						double var13 = (MathHelper.sin(j * (float)Math.PI / count) + 1.0F) * var11 + 1.0D;

						for (x = MathHelper.floor_double(var8 - var12 / 2.0D); gen <= count && x <= MathHelper.floor_double(var8 + var12 / 2.0D); ++x)
						{
							double xScale = (x + 0.5D - var8) / (var12 / 2.0D);

							if (xScale * xScale < 1.0D)
							{
								for (y = MathHelper.floor_double(var9 - var13 / 2.0D); gen <= count && y <= MathHelper.floor_double(var9 + var13 / 2.0D); ++y)
								{
									double yScale = (y + 0.5D - var9) / (var13 / 2.0D);

									if (xScale * xScale + yScale * yScale < 1.0D)
									{
										for (z = MathHelper.floor_double(var10 - var12 / 2.0D); gen < count && z <= MathHelper.floor_double(var10 + var12 / 2.0D); ++z)
										{
											double zScale = (z + 0.5D - var10) / (var12 / 2.0D);

											if (xScale * xScale + yScale * yScale + zScale * zScale < 1.0D)
											{
												if (target == null)
												{
													if (!world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, Blocks.stone))
													{
														continue;
													}
												}
												else if (!world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, target.getBlock()) || world.getBlockMetadata(x, y, z) != target.getMetadata())
												{
													continue;
												}

												if (biomes == null || biomes.length <= 0 || ArrayUtils.contains(biomes, world.getBiomeGenForCoords(x, z).biomeID))
												{
													if (world.setBlock(x, y, z, block.getBlock(), block.getMetadata(), 2))
													{
														++gen;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}