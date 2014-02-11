package com.kegare.caveworld.core;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kegare.caveworld.util.CaveLog;
import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class CaveOreManager
{
	private static final List<CaveOre> CAVE_ORES = Lists.newArrayList();

	protected static void initCaveOres()
	{
		clearCaveOres();

		addCaveOre(Blocks.dirt).setGenBlockCount(24).setGenRarity(18);
		addCaveOre(Blocks.gravel).setGenBlockCount(20).setGenRarity(6);
		addCaveOre(Blocks.sand).setGenBlockCount(20).setGenRarity(8).addGenBiomes(Type.DESERT);
		addCaveOre(Blocks.sand).setGenBlockCount(20).setGenRarity(8).setGenMinHeight(20).setGenTargetBlock(Blocks.gravel).addGenBiomes(Type.DESERT);
		addCaveOre(Blocks.soul_sand).setGenBlockCount(20).setGenRarity(10).setGenTargetBlock(Blocks.netherrack).addGenBiomes(Type.NETHER);
		addCaveOre(Blocks.coal_ore).setGenBlockCount(16).setGenRarity(20);
		addCaveOre(Blocks.iron_ore).setGenBlockCount(10).setGenRarity(30);
		addCaveOre(Blocks.gold_ore).setGenBlockCount(8).setGenRarity(2).setGenMaxHeight(127);
		addCaveOre(Blocks.redstone_ore).setGenBlockCount(7).setGenRarity(8).setGenMaxHeight(63);
		addCaveOre(Blocks.lapis_ore).setGenBlockCount(5).setGenMaxHeight(40);
		addCaveOre(Blocks.diamond_ore).setGenBlockCount(8).setGenMaxHeight(20);
		addCaveOre(Blocks.emerald_ore).setGenBlockCount(5).setGenRarity(2).setGenMinHeight(50);
		addCaveOre(Blocks.quartz_ore).setGenBlockCount(10).setGenRarity(16).setGenTargetBlock(Blocks.netherrack).addGenBiomes(Type.NETHER);
		addCaveOre(Blocks.hardened_clay).setBlockMetadata(1).setGenBlockCount(24).setGenRarity(20).setGenTargetBlock(Blocks.dirt).addGenBiomes(BiomeGenBase.mesa, BiomeGenBase.mesaPlateau, BiomeGenBase.mesaPlateau_F);
		addCaveOre(Blocks.hardened_clay).setBlockMetadata(12).setGenBlockCount(24).setGenRarity(14).setGenTargetBlock(Blocks.dirt).addGenBiomes(BiomeGenBase.mesa, BiomeGenBase.mesaPlateau, BiomeGenBase.mesaPlateau_F);
	}

	@SuppressWarnings("unchecked")
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

								if (iterator.hasNext())
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
						CaveOre ore = addCaveOre(block);
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

							ore.refreshGenBiomes();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			CaveLog.severe(e);
		}
		finally
		{
			if (CAVE_ORES.isEmpty())
			{
				initCaveOres();
			}
		}
	}

	@SuppressWarnings("unused")
	public static CaveOre addCaveOre(Block block)
	{
		CaveOre ore = new CaveOre(block);

		CAVE_ORES.add(ore);

		return ore;
	}

	@SuppressWarnings("unused")
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

	public static List<CaveOre> getCaveOreList()
	{
		return CAVE_ORES;
	}

	public static void clearCaveOres()
	{
		CAVE_ORES.clear();
	}

	public static class CaveOre
	{
		public final Block block;
		public int blockMetadata = 0;
		public int genBlockCount = 1;
		public int genRarity = 1;
		public int genMinHeight = 0;
		public int genMaxHeight = 255;
		public Block genTargetBlock = Blocks.stone;
		public final Set<BiomeGenBase> genBiomes = Sets.newHashSet();

		final TreeSet<Type> genBiomeTypes = Sets.newTreeSet();
		final TreeSet<Integer> genBiomeIds = Sets.newTreeSet();

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
					genBiomeTypes.add((Type)obj);
				}
				else if (obj instanceof BiomeGenBase)
				{
					genBiomeIds.add(((BiomeGenBase)obj).biomeID);
				}
				else if (obj instanceof Integer)
				{
					genBiomeIds.add((Integer)obj);
				}
			}

			return this;
		}

		public Set<BiomeGenBase> refreshGenBiomes()
		{
			genBiomes.clear();

			for (Type type : genBiomeTypes)
			{
				Collections.addAll(genBiomes, BiomeDictionary.getBiomesForType(type));
			}

			for (int id : genBiomeIds)
			{
				BiomeGenBase biome = BiomeGenBase.getBiome(id);

				if (biome != null)
				{
					genBiomes.add(biome);
				}
			}

			return genBiomes;
		}
	}
}