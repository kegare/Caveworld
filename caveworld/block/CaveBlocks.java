/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.Level;

import caveworld.api.CaveworldAPI;
import caveworld.core.Config;
import caveworld.entity.TileEntityUniversalChest;
import caveworld.item.ItemCavePortal;
import caveworld.item.ItemCaveniumOre;
import caveworld.item.ItemGemOre;
import caveworld.item.ItemPervertedLeaves;
import caveworld.item.ItemPervertedLog;
import caveworld.item.ItemPervertedSapling;
import caveworld.item.ItemRope;
import caveworld.item.ItemUniversalChest;
import caveworld.util.CaveLog;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CaveBlocks
{
	public static final BlockPortalCaveworld caveworld_portal = new BlockPortalCaveworld("portalCaveworld");
	public static final BlockRope rope = new BlockRope("rope");
	public static final BlockRopeLadder rope_ladder = new BlockRopeLadder("ropeLadder");
	public static final BlockCaveniumOre cavenium_ore = new BlockCaveniumOre("oreCavenium");
	public static final BlockUniversalChest universal_chest = new BlockUniversalChest("universalChest");
	public static final BlockGemOre gem_ore = new BlockGemOre("oreGem");
	public static final BlockPortalCavern cavern_portal = new BlockPortalCavern("portalCavern");
	public static final BlockPervertedLog perverted_log = new BlockPervertedLog("pervertedLog");
	public static final BlockPervertedLeaves perverted_leaves = new BlockPervertedLeaves("pervertedLeaves");
	public static final BlockPervertedSapling perverted_sapling = new BlockPervertedSapling("pervertedSapling");
	public static final BlockPortalAquaCavern aqua_cavern_portal = new BlockPortalAquaCavern("portalAquaCavern");
	public static final BlockPortalCaveland caveland_portal = new BlockPortalCaveland("portalCaveland");
	public static final BlockPortalCavenia cavenia_portal = new BlockPortalCavenia("portalCavenia");

	public static void registerBlocks()
	{
		{
			GameRegistry.registerBlock(caveworld_portal, ItemCavePortal.class, "caveworld_portal");

			OreDictionary.registerOre("portalCaveworld", caveworld_portal);

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveworld_portal), caveworld_portal.new DispencePortal());
		}

		{
			GameRegistry.registerBlock(rope, ItemRope.class, "rope");

			Blocks.fire.setFireInfo(rope, 15, 100);

			OreDictionary.registerOre("rope", new ItemStack(rope));

			GameRegistry.addShapelessRecipe(new ItemStack(rope), Items.string, Items.string, Items.string, Items.leather);

			Item item = Item.getItemFromBlock(rope);
			ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(item, 0, 2, 5, 10));
			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 0, 3, 6, 10));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(item, 0, 3, 6, 10));

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(rope), rope.new DispenceRope());
		}

		{
			GameRegistry.registerBlock(rope_ladder, ItemRope.class, "rope_ladder");

			Blocks.fire.setFireInfo(rope_ladder, 15, 80);

			OreDictionary.registerOre("ladder", new ItemStack(rope_ladder));
			OreDictionary.registerOre("ropeLadder", new ItemStack(rope_ladder));
			OreDictionary.registerOre("ladderRope", new ItemStack(rope_ladder));

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rope_ladder, 4),
				"R R", "RRR", "R R",
				'R', "rope"
			));
		}

		{
			GameRegistry.registerBlock(cavenium_ore, ItemCaveniumOre.class, "cavenium_ore");

			ItemStack item = new ItemStack(cavenium_ore, 1, 0);
			OreDictionary.registerOre("oreCavenium", item);
			OreDictionary.registerOre("caveniumOre", item);
			item = new ItemStack(cavenium_ore, 1, 1);
			OreDictionary.registerOre("oreRefinedCavenium", item);
			OreDictionary.registerOre("refinedCaveniumOre", item);

			item = new ItemStack(cavenium_ore, 1, 2);
			OreDictionary.registerOre("blockCavenium", item);
			OreDictionary.registerOre("caveniumBlock", item);

			GameRegistry.addRecipe(new ShapedOreRecipe(item,
				"CCC", "CCC", "CCC",
				'C', "cavenium"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(item,
				"CCC", "CCC", "CCC",
				'C', "gemCavenium"
			));

			item = new ItemStack(cavenium_ore, 1, 3);
			OreDictionary.registerOre("blockRefinedCavenium", item);
			OreDictionary.registerOre("refinedCaveniumBlock", item);

			GameRegistry.addRecipe(new ShapedOreRecipe(item,
				"CCC", "CCC", "CCC",
				'C', "refinedCavenium"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(item,
				"CCC", "CCC", "CCC",
				'C', "gemRefinedCavenium"
			));

			CaveworldAPI.setMiningPointAmount(cavenium_ore, 0, 2);
			CaveworldAPI.setMiningPointAmount(cavenium_ore, 1, 3);
		}

		{
			GameRegistry.registerBlock(universal_chest, ItemUniversalChest.class, "universal_chest");
			GameRegistry.registerTileEntity(TileEntityUniversalChest.class, "UniversalChest");

			GameRegistry.addShapedRecipe(new ItemStack(universal_chest),
				"CCC", "CEC", "CCC",
				'C', new ItemStack(cavenium_ore, 1, 3),
				'E', Items.ender_eye
			);

			File file = new File(Config.getConfigDir(), "UniversalChest.dat");
			NBTTagCompound data;

			if (!file.exists() || !file.isFile() || !file.canRead())
			{
				data = null;
			}
			else try (FileInputStream input = new FileInputStream(file))
			{
				data = CompressedStreamTools.readCompressed(input);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading Universal Chest data");

				data = null;
			}

			if (data != null)
			{
				universal_chest.setData(data);

				if (data.hasKey("ChestItems"))
				{
					universal_chest.inventory.loadInventoryFromNBT(data.getTagList("ChestItems", NBT.TAG_COMPOUND));
				}
			}
		}

		{
			GameRegistry.registerBlock(gem_ore, ItemGemOre.class, "gem_ore");

			ItemStack item = new ItemStack(gem_ore, 1, 0);
			OreDictionary.registerOre("oreAquamarine", item);
			OreDictionary.registerOre("aquamarineOre", item);

			item = new ItemStack(gem_ore, 1, 1);
			OreDictionary.registerOre("blockAquamarine", item);
			OreDictionary.registerOre("aquamarineBlock", item);

			GameRegistry.addRecipe(new ShapedOreRecipe(item,
				"AAA", "AAA", "AAA",
				'A', "aquamarine"
			));

			item = new ItemStack(gem_ore, 1, 2);
			OreDictionary.registerOre("oreRandomite", item);
			OreDictionary.registerOre("randomiteOre", item);

			CaveworldAPI.setMiningPointAmount(gem_ore, 0, 2);
			CaveworldAPI.setMiningPointAmount(gem_ore, 2, 2);
		}

		{
			GameRegistry.registerBlock(cavern_portal, ItemCavePortal.class, "cavern_portal");

			OreDictionary.registerOre("portalCavern", cavern_portal);

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(cavern_portal), cavern_portal.new DispencePortal());
		}

		{
			GameRegistry.registerBlock(perverted_log, ItemPervertedLog.class, "perverted_log");

			GameRegistry.addShapelessRecipe(new ItemStack(Items.stick, 8), new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE));

			for (int i = 0; i < BlockPervertedLog.types.length; ++i)
			{
				GameRegistry.addRecipe(new ItemStack(Blocks.planks, 4, i),
					"LL", "LL",
					'L', new ItemStack(perverted_log, 1, i)
				);
			}

			OreDictionary.registerOre("logWoodPerverted", new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE));

			Blocks.fire.setFireInfo(perverted_log, 15, 15);
		}

		{
			GameRegistry.registerBlock(perverted_leaves, ItemPervertedLeaves.class, "perverted_leaves");

			OreDictionary.registerOre("treeLeavesPerverted", new ItemStack(perverted_leaves, 1, OreDictionary.WILDCARD_VALUE));

			Blocks.fire.setFireInfo(perverted_leaves, 90, 180);
		}

		{
			GameRegistry.registerBlock(perverted_sapling, ItemPervertedSapling.class, "perverted_sapling");

			for (int i = 0; i < BlockPervertedLog.types.length; ++i)
			{
				GameRegistry.addShapelessRecipe(new ItemStack(perverted_sapling, 1, i), new ItemStack(Blocks.sapling, 1, i), Items.fermented_spider_eye);
			}

			OreDictionary.registerOre("treeSaplingPerverted", new ItemStack(perverted_sapling, 1, OreDictionary.WILDCARD_VALUE));

			Blocks.fire.setFireInfo(perverted_sapling, 60, 180);
		}

		{
			GameRegistry.registerBlock(aqua_cavern_portal, ItemCavePortal.class, "aqua_cavern_portal");

			OreDictionary.registerOre("portalAquaCavern", aqua_cavern_portal);

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(aqua_cavern_portal), aqua_cavern_portal.new DispencePortal());
		}

		{
			GameRegistry.registerBlock(caveland_portal, ItemCavePortal.class, "caveland_portal");

			OreDictionary.registerOre("portalCaveland", caveland_portal);

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveland_portal), caveland_portal.new DispencePortal());
		}

		{
			GameRegistry.registerBlock(cavenia_portal, ItemCavePortal.class, "cavenia_portal");

			OreDictionary.registerOre("portalCavenia", cavenia_portal);

			BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(cavenia_portal), cavenia_portal.new DispencePortal());
		}

		if (Config.mossStoneCraftRecipe)
		{
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.mossy_cobblestone), Blocks.vine, "cobblestone"));
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), Blocks.vine, new ItemStack(Blocks.stonebrick, 1, 0));
		}
	}
}