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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.api.ICaveVeinManager;

public class CaveVeinManager implements ICaveVeinManager
{
	private final Map<String, CaveVein> CAVE_VEINS = Maps.newHashMap();

	public static Class veinEntryClass = null;

	@Override
	public boolean addCaveVein(String name, ICaveVein vein)
	{
		if (CAVE_VEINS.containsKey(name))
		{
			return false;
		}

		CAVE_VEINS.put(name, (CaveVein)vein);

		return true;
	}

	@Override
	public boolean addCaveVeinWithConfig(String name, ICaveVein vein)
	{
		String block = vein == null ? "" : Block.blockRegistry.getNameForObject(vein.getBlock().getBlock());
		int blockMetadata = vein == null ? -1 : vein.getBlock().getMetadata();
		int count = vein == null ? -1 : vein.getGenBlockCount();
		int weight = vein == null ? -1 : vein.getGenWeight();
		int min = vein == null ? -1 : vein.getGenMinHeight();
		int max = vein == null ? -1 : vein.getGenMaxHeight();
		String target = vein == null ? "" : Block.blockRegistry.getNameForObject(vein.getGenTargetBlock().getBlock());
		int targetMetadata = vein == null ? -1 : vein.getGenTargetBlock().getMetadata();
		int[] biomes = vein == null ? null : vein.getGenBiomes();

		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = Config.veinsCfg.get(name, "block", Block.blockRegistry.getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(Config.selectBlockEntryClass);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (!Strings.isNullOrEmpty(block)) prop.set(block);
		propOrder.add(prop.getName());
		block = prop.getString();
		prop = Config.veinsCfg.get(name, "blockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(veinEntryClass);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (blockMetadata >= 0) prop.set(MathHelper.clamp_int(blockMetadata, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		blockMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genBlockCount", 1);
		prop.setMinValue(1).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (count >= 0) prop.set(MathHelper.clamp_int(count, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		count = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genWeight", 1);
		prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (weight >= 0) prop.set(MathHelper.clamp_int(weight, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		weight = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genMinHeight", 0);
		prop.setMinValue(0).setMaxValue(254).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (min >= 0) prop.set(MathHelper.clamp_int(min, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		min = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genMaxHeight", 255);
		prop.setMinValue(1).setMaxValue(255).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (max >= 0) prop.set(MathHelper.clamp_int(max, min + 1, Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		max = MathHelper.clamp_int(prop.getInt(), min + 1, Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genTargetBlock", Block.blockRegistry.getNameForObject(Blocks.stone)).setConfigEntryClass(Config.selectBlockEntryClass);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [default: " + prop.getDefault() + "]";
		if (!Strings.isNullOrEmpty(target)) prop.set(target);
		propOrder.add(prop.getName());
		target = prop.getString();
		prop = Config.veinsCfg.get(name, "genTargetBlockMetadata", 0);
		prop.setMinValue(0).setMaxValue(15).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		if (targetMetadata >= 0) prop.set(MathHelper.clamp_int(targetMetadata, Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue())));
		propOrder.add(prop.getName());
		targetMetadata = MathHelper.clamp_int(prop.getInt(), Integer.valueOf(prop.getMinValue()), Integer.valueOf(prop.getMaxValue()));
		prop = Config.veinsCfg.get(name, "genBiomes", new int[0]);
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(Config.selectBiomeEntryClass);
		prop.comment = StatCollector.translateToLocal(prop.getLanguageKey() + ".tooltip");
		if (biomes != null) prop.set(biomes);
		propOrder.add(prop.getName());
		biomes = prop.getIntList();

		Config.veinsCfg.setCategoryPropertyOrder(name, propOrder);

		if (Config.veinsCfg.hasChanged())
		{
			Config.veinsCfg.save();
		}

		return addCaveVein(name, new CaveVein(new BlockEntry(block, blockMetadata), count, weight, min, max, new BlockEntry(target, targetMetadata), biomes));
	}

	@Override
	public boolean addCaveVeinFromConfig(String name)
	{
		return addCaveVeinWithConfig(name, null);
	}

	@Override
	public boolean removeCaveVein(String name)
	{
		if (CAVE_VEINS.containsKey(name))
		{
			CAVE_VEINS.remove(name);

			return true;
		}

		return false;
	}

	@Override
	public boolean removeCaveVeinWithConfig(String name)
	{
		return removeCaveVeinFromConfig(name) && removeCaveVein(name);
	}

	@Override
	public boolean removeCaveVeinFromConfig(String name)
	{
		if (Config.veinsCfg.hasCategory(name))
		{
			Config.veinsCfg.removeCategory(Config.veinsCfg.getCategory(name));

			if (Config.veinsCfg.hasChanged())
			{
				Config.veinsCfg.save();
			}

			return true;
		}

		return false;
	}

	@Override
	public int removeCaveVeins(Block block, int metadata)
	{
		CaveVein vein;
		int count = 0;

		for (Entry<String, CaveVein> entry : CAVE_VEINS.entrySet())
		{
			vein = entry.getValue();

			if (vein.getBlock().getBlock() == block && vein.getBlock().getMetadata() == metadata && removeCaveVein(entry.getKey()))
			{
				++count;
			}
		}

		return count;
	}

	@Override
	public ICaveVein getRandomCaveVein(Random random)
	{
		try
		{
			return (ICaveVein)WeightedRandom.getRandomItem(random, CAVE_VEINS.values());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public ICaveVein getCaveVein(String name)
	{
		return CAVE_VEINS.get(name);
	}

	@Override
	public ImmutableMap<String, ICaveVein> getCaveVeins()
	{
		return new ImmutableMap.Builder<String, ICaveVein>().putAll(CAVE_VEINS).build();
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
		private int genMinHeight;
		private int genMaxHeight;
		private BlockEntry genTargetBlock;
		private int[] genBiomes;

		public CaveVein(BlockEntry block, int count, int weight, int min, int max)
		{
			super(weight);
			this.block = block;
			this.genBlockCount = count;
			this.genMinHeight = min;
			this.genMaxHeight = max;
			this.genBiomes = new int[0];
		}

		public CaveVein(BlockEntry block, int count, int weight, int min, int max, BlockEntry target, Object... biomes)
		{
			this(block, count, weight, min, max);
			this.genTargetBlock = target;
			this.genBiomes = getBiomes(biomes);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ICaveVein)
			{
				ICaveVein vein = (ICaveVein)obj;

				if (getBlock().getBlock() != vein.getBlock().getBlock() || getBlock().getMetadata() != vein.getBlock().getMetadata())
				{
					return false;
				}
				else if (getGenBlockCount() != vein.getGenBlockCount())
				{
					return false;
				}
				else if (getGenMinHeight() != vein.getGenMinHeight() || getGenMaxHeight() != vein.getGenMaxHeight())
				{
					return false;
				}
				else if (getGenBiomes() != vein.getGenBiomes())
				{
					return false;
				}

				return true;
			}

			return false;
		}

		@Override
		public BlockEntry getBlock()
		{
			return block == null ? new BlockEntry(Blocks.stone, 0) : block;
		}

		@Override
		public int getGenBlockCount()
		{
			return MathHelper.clamp_int(genBlockCount, 1, 100);
		}

		@Override
		public int getGenWeight()
		{
			return MathHelper.clamp_int(itemWeight, 1, 100);
		}

		@Override
		public int getGenMinHeight()
		{
			return MathHelper.clamp_int(genMinHeight, 0, 254);
		}

		@Override
		public int getGenMaxHeight()
		{
			return MathHelper.clamp_int(genMaxHeight, 1, 255);
		}

		@Override
		public BlockEntry getGenTargetBlock()
		{
			return genTargetBlock == null ? new BlockEntry(Blocks.stone, 0) : genTargetBlock;
		}

		@Override
		public int[] getGenBiomes()
		{
			return genBiomes == null ? new int[0] : genBiomes;
		}

		private int[] getBiomes(Object... objects)
		{
			Set<Integer> biomes = Sets.newTreeSet();

			for (Object obj : objects)
			{
				if (obj instanceof BiomeGenBase)
				{
					biomes.add(((BiomeGenBase)obj).biomeID);
				}
				else if (obj instanceof Integer)
				{
					BiomeGenBase biome = BiomeGenBase.getBiome((Integer)obj);

					if (biome != null)
					{
						biomes.add(biome.biomeID);
					}
				}
				else if (obj instanceof Type)
				{
					Type type = (Type)obj;

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