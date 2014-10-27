/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.core;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Property;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.api.ICaveVeinManager;
import com.kegare.caveworld.util.ArrayListExtended;

import cpw.mods.fml.common.registry.GameData;

public class CaveVeinManager implements ICaveVeinManager
{
	private final ArrayListExtended<ICaveVein> CAVE_VEINS = new ArrayListExtended();

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

		String name = Integer.toString(CAVE_VEINS.size());

		if (vein == null && !Config.veinsCfg.hasCategory(name))
		{
			return false;
		}

		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = Config.veinsCfg.get(name, "block", GameData.getBlockRegistry().getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(Config.selectBlockEntryClass);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(block)) prop.set(block);
		propOrder.add(prop.getName());
		block = prop.getString();
		if (!GameData.getBlockRegistry().containsKey(Strings.nullToEmpty(block))) return false;

		prop = Config.veinsCfg.get(name, "blockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (blockMetadata >= 0) prop.set(MathHelper.clamp_int(blockMetadata, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		blockMetadata = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genBlockCount", 1);
		prop.setMinValue(1).setMaxValue(500).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (count >= 0) prop.set(MathHelper.clamp_int(count, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		count = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genWeight", 1);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (weight >= 0) prop.set(MathHelper.clamp_int(weight, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		weight = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genRate", 100);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (rate >= 0) prop.set(MathHelper.clamp_int(rate, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		rate = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genMinHeight", 0);
		prop.setMinValue(0).setMaxValue(254).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (min >= 0) prop.set(MathHelper.clamp_int(min, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		min = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (max >= 0) prop.set(MathHelper.clamp_int(max, min + 1, Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		max = MathHelper.clamp_int(prop.getInt(), min + 1, Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genTargetBlock", GameData.getBlockRegistry().getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(target)) prop.set(target);
		if (!GameData.getBlockRegistry().containsKey(prop.getString())) prop.setToDefault();
		propOrder.add(prop.getName());
		target = prop.getString();

		prop = Config.veinsCfg.get(name, "genTargetBlockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (targetMetadata >= 0) prop.set(MathHelper.clamp_int(targetMetadata, Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue())));
		propOrder.add(prop.getName());
		targetMetadata = MathHelper.clamp_int(prop.getInt(), Integer.parseInt(prop.getMinValue()), Integer.parseInt(prop.getMaxValue()));

		prop = Config.veinsCfg.get(name, "genBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (biomes != null) prop.set(biomes);
		propOrder.add(prop.getName());
		biomes = prop.getIntList();

		Config.veinsCfg.setCategoryPropertyOrder(name, propOrder);

		if (vein == null)
		{
			vein = new CaveVein(new BlockEntry(block, blockMetadata), count, weight, rate, min, max, new BlockEntry(target, targetMetadata), biomes);
		}

		return CAVE_VEINS.addIfAbsent(vein);
	}

	@Override
	public int removeCaveVeins(ICaveVein vein)
	{
		int prev = CAVE_VEINS.size();

		for (int i = CAVE_VEINS.indexOf(vein); i >= 0;)
		{
			CAVE_VEINS.remove(i);

			Config.veinsCfg.removeCategory(Config.veinsCfg.getCategory(Integer.toString(i)));
		}

		return Math.max(CAVE_VEINS.size(), prev);
	}

	@Override
	public int removeCaveVeins(Block block, int metadata)
	{
		ICaveVein vein;
		int prev = CAVE_VEINS.size();

		for (int i = 0; i < prev; ++i)
		{
			vein = CAVE_VEINS.get(i);

			if (vein.getBlock().getBlock() == block && vein.getBlock().getMetadata() == metadata)
			{
				CAVE_VEINS.remove(i);

				Config.veinsCfg.removeCategory(Config.veinsCfg.getCategory(Integer.toString(i)));
			}
		}

		return Math.max(CAVE_VEINS.size(), prev);
	}

	@Override
	public ICaveVein getRandomCaveVein(Random random)
	{
		try
		{
			return (ICaveVein)WeightedRandom.getRandomItem(random, CAVE_VEINS);
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
		CAVE_VEINS.clear();
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
	}
}