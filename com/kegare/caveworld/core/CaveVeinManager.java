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

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.api.ICaveVeinManager;

public class CaveVeinManager implements ICaveVeinManager
{
	private final LinkedHashSet<CaveVein> CAVE_VEINS = Sets.newLinkedHashSet();

	@Override
	public boolean addCaveVein(ICaveVein vein)
	{
		return CAVE_VEINS.add((CaveVein)vein);
	}

	@Override
	public boolean addCaveVeinWithConfig(String name, ICaveVein vein)
	{
		if (Strings.isNullOrEmpty(name))
		{
			return addCaveVein(vein);
		}

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

	private boolean addCaveVeinWithConfig(String name, String block, int blockMetadata, int count, int weight, int min, int max, String target, int targetMetadata, int[] biomes)
	{
		String category = "veins";
		Property prop;
		List<String> propOrder = Lists.newArrayList();

		prop = Config.veinsCfg.get(name, "block", Block.blockRegistry.getNameForObject(Blocks.stone));
		prop.setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName()).setConfigEntryClass(Config.VEIN_ENTRY.orNull());
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
		prop.setMinValue(0).setMaxValue(100).setLanguageKey(Caveworld.CONFIG_LANG + category + '.' + prop.getName());
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

		return addCaveVein(new CaveVein(new BlockEntry(block, blockMetadata), count, weight, min, max, new BlockEntry(target, targetMetadata), biomes));
	}

	@Override
	public boolean addCaveVeinFromConfig(String name)
	{
		return addCaveVeinWithConfig(name, "", -1, -1, -1, -1,-1, "", -1, null);
	}

	@Override
	public boolean removeCaveVein(ICaveVein vein)
	{
		return CAVE_VEINS.remove(vein);
	}

	@Override
	public boolean removeCaveVeinWithConfig(String name, ICaveVein vein)
	{
		return removeCaveVeinFromConfig(name) && removeCaveVein(vein);
	}

	@Override
	public boolean removeCaveVeinFromConfig(String name)
	{
		if (Config.veinsCfg.hasCategory(name))
		{
			Config.veinsCfg.removeCategory(Config.veinsCfg.getCategory(name));

			return true;
		}

		return false;
	}

	@Override
	public int removeCaveVeins(Block block, int metadata)
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

	@Override
	public ICaveVein getRandomVein(Random random)
	{
		try
		{
			return (ICaveVein)WeightedRandom.getRandomItem(random, CAVE_VEINS);
		}
		catch (Exception e)
		{
			return new CaveVein(new BlockEntry(Blocks.stone, 0), 0, 0, 0, 0);
		}
	}

	@Override
	public ImmutableSet<ICaveVein> getCaveVeins()
	{
		return new ImmutableSet.Builder<ICaveVein>().addAll(CAVE_VEINS).build();
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
			this.genBiomes = new int[] {};
		}

		public CaveVein(BlockEntry block, int count, int weight, int min, int max, BlockEntry target, Object... biomes)
		{
			this(block, count, weight, min, max);
			this.genTargetBlock = target;
			this.genBiomes = getBiomes(biomes);
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
			return genBiomes == null ? new int[] {} : genBiomes;
		}

		private int[] getBiomes(Object... objects)
		{
			Set<BiomeGenBase> biomes = new TreeSet<BiomeGenBase>(new Comparator<BiomeGenBase>()
			{
				@Override
				public int compare(BiomeGenBase o1, BiomeGenBase o2)
				{
					return Integer.valueOf(o1.biomeID).compareTo(o2.biomeID);
				}
			});

			for (Object obj : objects)
			{
				if (obj instanceof BiomeGenBase)
				{
					biomes.add((BiomeGenBase)obj);
				}
				else if (obj instanceof Integer)
				{
					BiomeGenBase biome = BiomeGenBase.getBiome((Integer)obj);

					if (biome != null)
					{
						biomes.add(biome);
					}
				}
				else if (obj instanceof Type)
				{
					Type type = (Type)obj;

					for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
					{
						biomes.add(biome);
					}
				}
			}

			Object[] temp = biomes.toArray();
			int[] ids = new int[temp.length];

			for (int i = 0; i < temp.length; ++i)
			{
				ids[i] = ((BiomeGenBase)temp[i]).biomeID;
			}

			return ids;
		}
	}
}