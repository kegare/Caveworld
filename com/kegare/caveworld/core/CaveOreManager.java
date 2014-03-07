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

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kegare.caveworld.util.CaveLog;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

public class CaveOreManager
{
	private static final LinkedHashSet<CaveOre> CAVE_ORES = Sets.newLinkedHashSet();

	private static void initCaveOres()
	{
		clearCaveOres();

		addCaveOre(new CaveOre(Blocks.coal_ore).setGenBlockCount(16).setGenRarity(20));
		addCaveOre(new CaveOre(Blocks.iron_ore).setGenBlockCount(10).setGenRarity(28));
		addCaveOre(new CaveOre(Blocks.gold_ore).setGenBlockCount(8).setGenRarity(2).setGenMaxHeight(127));
		addCaveOre(new CaveOre(Blocks.redstone_ore).setGenBlockCount(7).setGenRarity(8).setGenMaxHeight(40));
		addCaveOre(new CaveOre(Blocks.lapis_ore).setGenBlockCount(5).setGenMaxHeight(40));
		addCaveOre(new CaveOre(Blocks.diamond_ore).setGenBlockCount(8).setGenMaxHeight(20));
		addCaveOre(new CaveOre(Blocks.emerald_ore).setGenBlockCount(5).setGenRarity(3).setGenMinHeight(50).addGenBiomes(Type.MOUNTAIN, Type.HILLS));
		addCaveOre(new CaveOre(Blocks.quartz_ore).setGenBlockCount(10).setGenRarity(16).setGenTargetBlock(Blocks.netherrack).addGenBiomes(Type.NETHER));
		addCaveOre(new CaveOre(Blocks.dirt).setGenBlockCount(24).setGenRarity(18));
		addCaveOre(new CaveOre(Blocks.gravel).setGenBlockCount(20).setGenRarity(6));
		addCaveOre(new CaveOre(Blocks.sand).setGenBlockCount(20).setGenRarity(8).addGenBiomes(Type.DESERT));
		addCaveOre(new CaveOre(Blocks.sand).setGenBlockCount(20).setGenRarity(8).setGenMinHeight(20).setGenTargetBlock(Blocks.gravel).addGenBiomes(Type.DESERT));
		addCaveOre(new CaveOre(Blocks.soul_sand).setGenBlockCount(20).setGenRarity(10).setGenTargetBlock(Blocks.netherrack).addGenBiomes(Type.NETHER));
		addCaveOre(new CaveOre(Blocks.hardened_clay).setBlockMetadata(1).setGenBlockCount(24).setGenRarity(20).setGenTargetBlock(Blocks.dirt).addGenBiomes(BiomeGenBase.mesa, BiomeGenBase.mesaPlateau, BiomeGenBase.mesaPlateau_F));
		addCaveOre(new CaveOre(Blocks.hardened_clay).setBlockMetadata(12).setGenBlockCount(24).setGenRarity(14).setGenTargetBlock(Blocks.dirt).addGenBiomes(BiomeGenBase.mesa, BiomeGenBase.mesaPlateau, BiomeGenBase.mesaPlateau_F));
	}

	static boolean loadCaveOres()
	{
		File file = Config.getConfigFile("ores");

		try
		{
			if (file.exists() && file.canRead())
			{
				String data = Files.readLines(file, Charsets.US_ASCII, new LineProcessor<String>()
				{
					private final StringBuilder builder = new StringBuilder();

					@Override
					public boolean processLine(String line) throws IOException
					{
						if (!Strings.isNullOrEmpty(line))
						{
							line = StringUtils.deleteWhitespace(line);

							if (!line.startsWith("#"))
							{
								builder.append(line);
							}
						}

						return true;
					}

					@Override
					public String getResult()
					{
						return builder.toString();
					}
				});

				if (!Strings.isNullOrEmpty(data) && loadCaveOresFromString(data))
				{
					return true;
				}
			}

			initCaveOres();

			return !CAVE_ORES.isEmpty();
		}
		catch (Exception e)
		{
			if (CAVE_ORES.isEmpty())
			{
				initCaveOres();
			}

			File dest = new File(file.getParentFile(), file.getName() + ".bak");

			if (dest.exists())
			{
				dest.delete();
			}

			file.renameTo(dest);

			CaveLog.severe("A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName(), e);
		}
		finally
		{
			CaveLog.info("Loaded %d cave ores", CAVE_ORES.size());

			saveCaveOres();
		}

		return false;
	}

	public static boolean loadCaveOresFromString(String data)
	{
		List<Map<String, Object>> json = new Gson().fromJson(data, new TypeToken<List<Map<String, Object>>>(){}.getType());

		for (Map<String, Object> entry : json)
		{
			Block block = entry.containsKey("block") ? Block.getBlockFromName((String)entry.get("block")) : null;

			if (block == null || block.getMaterial().isLiquid() || !block.getMaterial().isSolid() || block.getMaterial().isReplaceable())
			{
				continue;
			}

			CaveOre ore = new CaveOre(block);
			if (entry.containsKey("blockMetadata")) ore.setBlockMetadata(((Number)entry.get("blockMetadata")).intValue());
			if (entry.containsKey("genBlockCount")) ore.setGenBlockCount(((Number)entry.get("genBlockCount")).intValue());
			if (entry.containsKey("genRarity")) ore.setGenRarity(((Number)entry.get("genRarity")).intValue());
			if (entry.containsKey("genMinHeight")) ore.setGenMinHeight(((Number)entry.get("genMinHeight")).intValue());
			if (entry.containsKey("genMaxHeight")) ore.setGenMaxHeight(((Number)entry.get("genMaxHeight")).intValue());
			if (entry.containsKey("genTargetBlock")) ore.setGenTargetBlock(Block.getBlockFromName((String)entry.get("genTargetBlock")));

			if (entry.containsKey("genBiomes"))
			{
				for (String str : Splitter.on(',').omitEmptyStrings().split((String)entry.get("genBiomes")))
				{
					if (str.matches("^[0-9]{1,3}$"))
					{
						int id = MathHelper.parseIntWithDefault(str, -1);

						if (id >= 0 && id < 256)
						{
							ore.addGenBiomes(id);
						}
					}
					else
					{
						Type type = Type.valueOf(str.toUpperCase(Locale.ENGLISH));

						if (type != null)
						{
							ore.addGenBiomes(type);
						}
					}
				}
			}

			addCaveOre(ore);
		}

		return !CAVE_ORES.isEmpty();
	}

	static boolean saveCaveOres()
	{
		try
		{
			File file = Config.getConfigFile("ores");
			String dest = null;

			if (file.exists() && file.canRead())
			{
				dest = FileUtils.readFileToString(file);
			}

			StrBuilder builder = new StrBuilder(dest == null ? 2048 : dest.length());

			builder.appendln("# Configuration file - Caveworld ores");
			builder.appendNewLine();
			builder.appendln('[');

			for (Iterator<CaveOre> ores = CAVE_ORES.iterator(); ores.hasNext();)
			{
				CaveOre ore = ores.next();

				builder.appendln("  {");
				builder.append("    \"block\": \"").append(Block.blockRegistry.getNameForObject(ore.block)).appendln("\",");

				if (ore.blockMetadata != 0)
				{
					builder.append("    \"blockMetadata\": ").append(ore.blockMetadata).appendln(',');
				}

				builder.append("    \"genBlockCount\": ").append(ore.genBlockCount).appendln(',');
				builder.append("    \"genRarity\": ").append(ore.genRarity).appendln(',');
				builder.append("    \"genMinHeight\": ").append(ore.genMinHeight).appendln(',');
				builder.append("    \"genMaxHeight\": ").append(ore.genMaxHeight);

				if (ore.genTargetBlock != Blocks.stone)
				{
					builder.appendln(',');
					builder.append("    \"genTargetBlock\": \"").append(Block.blockRegistry.getNameForObject(ore.genTargetBlock)).append("\"");
				}

				if (!ore.genBiomeTypes.isEmpty() || !ore.genBiomeIds.isEmpty())
				{
					builder.appendln(',');
					builder.append("    \"genBiomes\": \"");

					for (Iterator<Type> types = ore.genBiomeTypes.iterator(); types.hasNext();)
					{
						builder.append(types.next().name());

						if (types.hasNext() || !ore.genBiomeIds.isEmpty())
						{
							builder.append(',');
						}
					}

					for (Iterator<Integer> ids = ore.genBiomeIds.iterator(); ids.hasNext();)
					{
						builder.append(String.valueOf(ids.next()));

						if (ids.hasNext())
						{
							builder.append(',');
						}
					}

					builder.append("\"");
				}

				builder.appendNewLine();
				builder.append("  }");

				if (ores.hasNext())
				{
					builder.append(',');
				}

				builder.appendNewLine();
			}

			String data = builder.append(']').toString();

			if (dest != null && data.equals(dest))
			{
				return false;
			}

			FileUtils.writeStringToFile(file, data);

			return true;
		}
		catch (Exception e)
		{
			CaveLog.log(Level.ERROR, e, "An error occurred trying to saving cave ores");
		}

		return false;
	}

	public static boolean addCaveOre(CaveOre ore)
	{
		for (CaveOre caveOre : CAVE_ORES)
		{
			if (caveOre.block == ore.block && caveOre.blockMetadata == ore.blockMetadata && caveOre.genTargetBlock == ore.genTargetBlock)
			{
				caveOre.genBlockCount += ore.genBlockCount;
				caveOre.genRarity += ore.genRarity;
				caveOre.setGenMinHeight(Math.min(caveOre.genMinHeight, ore.genMinHeight));
				caveOre.setGenMaxHeight(Math.max(caveOre.genMaxHeight, ore.genMaxHeight));

				for (BiomeGenBase biome : ore.genBiomes)
				{
					caveOre.addGenBiomes(biome);
				}

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

			if (ore.block == block && ore.blockMetadata == metadata)
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
			if (ore.block == block && ore.blockMetadata == metadata)
			{
				return ore.block.getMaterial() == Material.rock;
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
		private final Block block;
		private int blockMetadata = 0;
		private int genBlockCount = 1;
		private int genRarity = 1;
		private int genMinHeight = 0;
		private int genMaxHeight = 255;
		private Block genTargetBlock = Blocks.stone;
		private final Set<BiomeGenBase> genBiomes = Sets.newHashSet();

		private final SortedSet<Type> genBiomeTypes = Sets.newTreeSet();
		private final SortedSet<Integer> genBiomeIds = Sets.newTreeSet();

		public CaveOre(Block block)
		{
			this.block = block;
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(128);

			builder.append('{');
			builder.append("\"block\":\"").append(Block.blockRegistry.getNameForObject(block)).append("\",");
			builder.append("\"blockMetadata\":").append(blockMetadata).append(',');
			builder.append("\"genBlockCount\":").append(genBlockCount).append(',');
			builder.append("\"genRarity\":").append(genRarity).append(',');
			builder.append("\"genMinHeight\":").append(genMinHeight).append(',');
			builder.append("\"genMaxHeight\":").append(genMaxHeight).append(',');
			builder.append("\"genTargetBlock\":\"").append(Block.blockRegistry.getNameForObject(genTargetBlock)).append('\"');

			if (!genBiomeTypes.isEmpty() || !genBiomeIds.isEmpty())
			{
				builder.append(',').append("\"genBiomes\":\"");

				for (Iterator<Type> types = genBiomeTypes.iterator(); types.hasNext();)
				{
					builder.append(types.next().name());

					if (types.hasNext() || !genBiomeIds.isEmpty())
					{
						builder.append(',');
					}
				}

				for (Iterator<Integer> ids = genBiomeIds.iterator(); ids.hasNext();)
				{
					builder.append(ids.next());

					if (ids.hasNext())
					{
						builder.append(',');
					}
				}

				builder.append('\"');
			}

			builder.append('}');

			return builder.toString();
		}

		public CaveOre setBlockMetadata(int metadata)
		{
			blockMetadata = metadata;

			return this;
		}

		public CaveOre setGenBlockCount(int count)
		{
			genBlockCount = MathHelper.clamp_int(count, 1, 100);

			return this;
		}

		public CaveOre setGenRarity(int rarity)
		{
			genRarity = MathHelper.clamp_int(rarity, 1, 100);

			return this;
		}

		public CaveOre setGenMinHeight(int min)
		{
			genMinHeight = MathHelper.clamp_int(min, 0, 255);

			return this;
		}

		public CaveOre setGenMaxHeight(int max)
		{
			genMaxHeight = MathHelper.clamp_int(max, 1, 255);

			return this;
		}

		public CaveOre setGenTargetBlock(Block target)
		{
			genTargetBlock = target == null ? Blocks.stone : target;

			return this;
		}

		public CaveOre addGenBiomes(Object... objects)
		{
			for (Object obj : objects)
			{
				if (obj instanceof Type)
				{
					Type type = (Type)obj;

					Collections.addAll(genBiomes, BiomeDictionary.getBiomesForType(type));

					genBiomeTypes.add(type);
				}
				else if (obj instanceof BiomeGenBase)
				{
					BiomeGenBase biome = (BiomeGenBase)obj;

					genBiomes.add(biome);
					genBiomeIds.add(biome.biomeID);
				}
				else if (obj instanceof Integer)
				{
					BiomeGenBase biome = BiomeGenBase.getBiome((Integer)obj);

					if (biome != null)
					{
						genBiomes.add(biome);
						genBiomeIds.add(biome.biomeID);
					}
				}
			}

			return this;
		}

		public Block getBlock()
		{
			return block;
		}

		public int getBlockMetadata()
		{
			return blockMetadata;
		}

		public int getGenBlockCount()
		{
			return MathHelper.clamp_int(genBlockCount, 1, 100);
		}

		public int getGenRarity()
		{
			return MathHelper.clamp_int(genRarity, 1, 100);
		}

		public int getGenMinHeight()
		{
			return MathHelper.clamp_int(genMinHeight, 0, 255);
		}

		public int getGenMaxHeight()
		{
			return MathHelper.clamp_int(genMaxHeight, 1, 255);
		}

		public Block getGenTargetBlock()
		{
			return genTargetBlock == null ? Blocks.stone : genTargetBlock;
		}

		public ImmutableSet<BiomeGenBase> getGenBiomes()
		{
			return new ImmutableSet.Builder<BiomeGenBase>().addAll(genBiomes).build();
		}

		public void clearGenBiomes(boolean flag)
		{
			genBiomes.clear();

			if (flag)
			{
				genBiomeTypes.clear();
				genBiomeIds.clear();
			}
		}

		@Override
		public boolean generate(World world, Random random, int x, int y, int z)
		{
			float var1 = random.nextFloat() * (float)Math.PI;
			double var2 = x + 8 + MathHelper.sin(var1) * genBlockCount / 8.0F;
			double var3 = x + 8 - MathHelper.sin(var1) * genBlockCount / 8.0F;
			double var4 = z + 8 + MathHelper.cos(var1) * genBlockCount / 8.0F;
			double var5 = z + 8 - MathHelper.cos(var1) * genBlockCount / 8.0F;
			double var6 = y + random.nextInt(3) - 2;
			double var7 = y + random.nextInt(3) - 2;

			for (int count = 0; count <= genBlockCount; ++count)
			{
				double var8 = var2 + (var3 - var2) * count / genBlockCount;
				double var9 = var6 + (var7 - var6) * count / genBlockCount;
				double var10 = var4 + (var5 - var4) * count / genBlockCount;
				double var11 = random.nextDouble() * genBlockCount / 16.0D;
				double var12 = (MathHelper.sin(count * (float)Math.PI / genBlockCount) + 1.0F) * var11 + 1.0D;
				double var13 = (MathHelper.sin(count * (float)Math.PI / genBlockCount) + 1.0F) * var11 + 1.0D;
				int minX = MathHelper.floor_double(var8 - var12 / 2.0D);
				int maxX = MathHelper.floor_double(var8 + var12 / 2.0D);
				int minY = MathHelper.floor_double(var9 - var13 / 2.0D);
				int maxY = MathHelper.floor_double(var9 + var13 / 2.0D);
				int minZ = MathHelper.floor_double(var10 - var12 / 2.0D);
				int maxZ = MathHelper.floor_double(var10 + var12 / 2.0D);

				for (int blockX = minX; blockX <= maxX; ++blockX)
				{
					double xScale = ((double)blockX + 0.5D - var8) / (var12 / 2.0D);

					if (xScale * xScale < 1.0D)
					{
						for (int blockY = minY; blockY <= maxY; ++blockY)
						{
							double yScale = ((double)blockY + 0.5D - var9) / (var13 / 2.0D);

							if (xScale * xScale + yScale * yScale < 1.0D)
							{
								for (int blockZ = minZ; blockZ <= maxZ; ++blockZ)
								{
									double zScale = ((double)blockZ + 0.5D - var10) / (var12 / 2.0D);

									if (xScale * xScale + yScale * yScale + zScale * zScale < 1.0D && world.getBlock(blockX, blockY, blockZ).isReplaceableOreGen(world, blockX, blockY, blockZ, genTargetBlock))
									{
										BiomeGenBase biome = world.getBiomeGenForCoords(blockX, blockZ);
										boolean flag = false;

										if (genBiomes.isEmpty())
										{
											flag = true;
										}
										else
										{
											for (BiomeGenBase obj : genBiomes)
											{
												if (obj.biomeID == biome.biomeID)
												{
													flag = true;

													break;
												}
											}
										}

										if (flag)
										{
											world.setBlock(blockX, blockY, blockZ, block, blockMetadata, 2);
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