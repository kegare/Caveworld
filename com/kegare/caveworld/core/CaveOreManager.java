package com.kegare.caveworld.core;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kegare.caveworld.util.CaveLog;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class CaveOreManager
{
	private static final LinkedHashSet<CaveOre> CAVE_ORES = Sets.newLinkedHashSet();

	private static void initCaveOres()
	{
		clearCaveOres();

		addCaveOre(new CaveOre(Blocks.coal_ore).setGenBlockCount(16).setGenRarity(20));
		addCaveOre(new CaveOre(Blocks.iron_ore).setGenBlockCount(10).setGenRarity(30));
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

	protected static void loadCaveOres()
	{
		try
		{
			File dir = new File(Loader.instance().getConfigDir(), "caveworld");

			if (!dir.exists())
			{
				dir.mkdirs();
			}

			File file = new File(dir, "caveworld-ores.cfg");

			if (file.createNewFile())
			{
				initCaveOres();

				FileOutputStream fos = new FileOutputStream(file);
				BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos));

				try
				{
					buffer.write("# Caveworld ores");
					buffer.newLine();
					buffer.write("#  Specify the ores to generate in Caveworld, and conditions.");
					buffer.newLine();
					buffer.newLine();
					buffer.write('[');
					buffer.newLine();

					Iterator<CaveOre> ores = CAVE_ORES.iterator();

					while (ores.hasNext())
					{
						CaveOre ore = ores.next();

						buffer.write("  {");
						buffer.newLine();
						buffer.write("    \"block\": \"" + Block.blockRegistry.getNameForObject(ore.block) + "\",");
						buffer.newLine();

						if (ore.blockMetadata != 0)
						{
							buffer.write("    \"blockMetadata\": \"" + ore.blockMetadata + "\",");
							buffer.newLine();
						}

						buffer.write("    \"genBlockCount\": \"" + ore.genBlockCount + "\",");
						buffer.newLine();
						buffer.write("    \"genRarity\": \"" + ore.genRarity + "\",");
						buffer.newLine();
						buffer.write("    \"genMinHeight\": \"" + ore.genMinHeight + "\",");
						buffer.newLine();
						buffer.write("    \"genMaxHeight\": \"" + ore.genMaxHeight + "\"");

						if (ore.genTargetBlock != Blocks.stone)
						{
							buffer.write(',');
							buffer.newLine();
							buffer.write("    \"genTargetBlock\": \"" + Block.blockRegistry.getNameForObject(ore.genTargetBlock) + "\"");
						}

						if (!ore.genBiomeTypes.isEmpty() || !ore.genBiomeIds.isEmpty())
						{
							buffer.write(',');
							buffer.newLine();
							buffer.write("    \"genBiomes\": \"");

							Iterator iterator = ore.genBiomeTypes.iterator();

							while (iterator.hasNext())
							{
								buffer.write(((Type)iterator.next()).name());

								if (iterator.hasNext() || !ore.genBiomeIds.isEmpty())
								{
									buffer.write(',');
								}
							}

							iterator = ore.genBiomeIds.iterator();

							while (iterator.hasNext())
							{
								buffer.write(String.valueOf(iterator.next()));

								if (iterator.hasNext())
								{
									buffer.write(',');
								}
							}

							buffer.write("\"");
						}

						buffer.newLine();
						buffer.write("  }");

						if (ores.hasNext())
						{
							buffer.write(',');
						}

						buffer.newLine();
					}

					buffer.write(']');
				}
				finally
				{
					buffer.close();
					fos.close();
				}
			}
			else if (file.exists() && file.canRead())
			{
				FileInputStream fis = new FileInputStream(file);
				String data = new String(ByteStreams.toByteArray(fis));
				fis.close();

				List<Map<String, String>> json = new Gson().fromJson(data, new TypeToken<List<Map<String, String>>>(){}.getType());

				for (Map<String, String> entry : json)
				{
					Block block = entry.containsKey("block") ? Block.getBlockFromName(entry.get("block")) : null;

					if (block != null)
					{
						CaveOre ore = new CaveOre(block);
						if (entry.containsKey("blockMetadata")) ore.setBlockMetadata(Integer.valueOf(entry.get("blockMetadata")));
						if (entry.containsKey("genBlockCount")) ore.setGenBlockCount(Integer.valueOf(entry.get("genBlockCount")));
						if (entry.containsKey("genRarity")) ore.setGenRarity(Integer.valueOf(entry.get("genRarity")));
						if (entry.containsKey("genMinHeight")) ore.setGenMinHeight(Integer.valueOf(entry.get("genMinHeight")));
						if (entry.containsKey("genMaxHeight")) ore.setGenMaxHeight(Integer.valueOf(entry.get("genMaxHeight")));
						if (entry.containsKey("genTargetBlock")) ore.setGenTargetBlock(Block.getBlockFromName(entry.get("genTargetBlock")));

						if (entry.containsKey("genBiomes"))
						{
							Pattern pattern = Pattern.compile("[^0-9]");

							for (String str : Splitter.on(',').omitEmptyStrings().split(entry.get("genBiomes")))
							{
								Type type = Strings.isNullOrEmpty(pattern.matcher(str).replaceAll("")) ? Type.valueOf(str) : null;

								if (type != null)
								{
									ore.addGenBiomes(type);
								}
								else
								{
									ore.addGenBiomes(Integer.valueOf(str));
								}
							}
						}

						addCaveOre(ore);
					}
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.severe(e);
		}
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

	public static void removeCaveOre(Block block, int metadata)
	{
		Iterator<CaveOre> ores = CAVE_ORES.iterator();

		while (ores.hasNext())
		{
			CaveOre ore = ores.next();

			if (ore.block == block && ore.blockMetadata == metadata)
			{
				ores.remove();
			}
		}
	}

	public static LinkedHashSet<CaveOre> getCaveOres()
	{
		return CAVE_ORES;
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

		private final TreeSet<Type> genBiomeTypes = Sets.newTreeSet();
		private final TreeSet<Integer> genBiomeIds = Sets.newTreeSet();

		public CaveOre(Block block)
		{
			this.block = block;
		}

		public CaveOre setBlockMetadata(int metadata)
		{
			blockMetadata = metadata;

			return this;
		}

		public CaveOre setGenBlockCount(int count)
		{
			genBlockCount = Math.min(Math.max(count, 1), 100);

			return this;
		}

		public CaveOre setGenRarity(int rarity)
		{
			genRarity = Math.min(Math.max(rarity, 1), 100);

			return this;
		}

		public CaveOre setGenMinHeight(int min)
		{
			genMinHeight = Math.min(Math.max(min, 0), 255);

			return this;
		}

		public CaveOre setGenMaxHeight(int max)
		{
			genMaxHeight = Math.min(Math.max(max, 1), 255);

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

		public int getGenBlockCount()
		{
			return Math.min(Math.max(genBlockCount, 1), 100);
		}

		public int getGenRarity()
		{
			return Math.min(Math.max(genRarity, 1), 100);
		}

		public int getGenMinHeight()
		{
			return Math.min(Math.max(genMinHeight, 0), 255);
		}

		public int getGenMaxHeight()
		{
			return Math.min(Math.max(genMaxHeight, 1), 255);
		}

		public Block getGenTargetBlock()
		{
			return genTargetBlock == null ? Blocks.stone : genTargetBlock;
		}

		public Set<BiomeGenBase> getGenBiomes()
		{
			return genBiomes;
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
				int var14 = MathHelper.floor_double(var8 - var12 / 2.0D);
				int var15 = MathHelper.floor_double(var9 - var13 / 2.0D);
				int var16 = MathHelper.floor_double(var10 - var12 / 2.0D);
				int var17 = MathHelper.floor_double(var8 + var12 / 2.0D);
				int var18 = MathHelper.floor_double(var9 + var13 / 2.0D);
				int var19 = MathHelper.floor_double(var10 + var12 / 2.0D);

				for (int blockX = var14; blockX <= var17; ++blockX)
				{
					double xScale = ((double)blockX + 0.5D - var8) / (var12 / 2.0D);

					if (xScale * xScale < 1.0D)
					{
						for (int blockY = var15; blockY <= var18; ++blockY)
						{
							double yScale = ((double)blockY + 0.5D - var9) / (var13 / 2.0D);

							if (xScale * xScale + yScale * yScale < 1.0D)
							{
								for (int blockZ = var16; blockZ <= var19; ++blockZ)
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