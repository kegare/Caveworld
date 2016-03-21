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

import caveworld.api.CaverAPI;
import caveworld.core.Config;
import caveworld.entity.TileEntityUniversalChest;
import caveworld.item.CaveItems;
import caveworld.item.ItemCavePortal;
import caveworld.item.ItemCaveniumOre;
import caveworld.item.ItemGemOre;
import caveworld.item.ItemPerverted;
import caveworld.item.ItemRope;
import caveworld.item.ItemUniversalChest;
import caveworld.util.CaveLog;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.oredict.OreDictionary;
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
	public static final BlockAcresia acresia_crops = new BlockAcresia("acresia");

	public static void registerBlocks()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemCavePortal.class, "caveworld_portal");
		GameRegistry.registerBlock(rope, ItemRope.class, "rope");
		GameRegistry.registerBlock(rope_ladder, ItemRope.class, "rope_ladder");
		GameRegistry.registerBlock(cavenium_ore, ItemCaveniumOre.class, "cavenium_ore");
		GameRegistry.registerBlock(universal_chest, ItemUniversalChest.class, "universal_chest");
		GameRegistry.registerBlock(gem_ore, ItemGemOre.class, "gem_ore");
		GameRegistry.registerBlock(cavern_portal, ItemCavePortal.class, "cavern_portal");
		GameRegistry.registerBlock(perverted_log, ItemPerverted.class, "perverted_log");
		GameRegistry.registerBlock(perverted_leaves, ItemPerverted.class, "perverted_leaves");
		GameRegistry.registerBlock(perverted_sapling, ItemPerverted.class, "perverted_sapling");
		GameRegistry.registerBlock(aqua_cavern_portal, ItemCavePortal.class, "aqua_cavern_portal");
		GameRegistry.registerBlock(caveland_portal, ItemCavePortal.class, "caveland_portal");
		GameRegistry.registerBlock(cavenia_portal, ItemCavePortal.class, "cavenia_portal");
		GameRegistry.registerBlock(acresia_crops, "acresia_crops");

		GameRegistry.registerTileEntity(TileEntityUniversalChest.class, "UniversalChest");

		OreDictionary.registerOre("rope", new ItemStack(rope));
		OreDictionary.registerOre("ropeLadder", new ItemStack(rope_ladder));
		OreDictionary.registerOre("oreCavenium", new ItemStack(cavenium_ore, 1, 0));
		OreDictionary.registerOre("oreRefinedCavenium", new ItemStack(cavenium_ore, 1, 1));
		OreDictionary.registerOre("blockCavenium", new ItemStack(cavenium_ore, 1, 2));
		OreDictionary.registerOre("blockRefinedCavenium", new ItemStack(cavenium_ore, 1, 3));
		OreDictionary.registerOre("oreAquamarine", new ItemStack(gem_ore, 1, 0));
		OreDictionary.registerOre("blockAquamarine", new ItemStack(gem_ore, 1, 1));
		OreDictionary.registerOre("oreRandomite", new ItemStack(gem_ore, 1, 2));
		OreDictionary.registerOre("oreMagnite", new ItemStack(gem_ore, 1, 3));
		OreDictionary.registerOre("blockMagnite", new ItemStack(gem_ore, 1, 4));
		OreDictionary.registerOre("oreHexcite", new ItemStack(gem_ore, 1, 5));
		OreDictionary.registerOre("blockHexcite", new ItemStack(gem_ore, 1, 6));
		OreDictionary.registerOre("oreInfitite", new ItemStack(gem_ore, 1, 7));
		OreDictionary.registerOre("blockInfitite", new ItemStack(gem_ore, 1, 8));
		OreDictionary.registerOre("logWoodPerverted", new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("treeLeavesPerverted", new ItemStack(perverted_leaves, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("treeSaplingPerverted", new ItemStack(perverted_sapling, 1, OreDictionary.WILDCARD_VALUE));

		CaverAPI.setMiningPointAmount(cavenium_ore, 0, 2);
		CaverAPI.setMiningPointAmount(cavenium_ore, 1, 3);
		CaverAPI.setMiningPointAmount(gem_ore, 0, 2);
		CaverAPI.setMiningPointAmount(gem_ore, 2, 2);
		CaverAPI.setMiningPointAmount(gem_ore, 3, 1);
		CaverAPI.setMiningPointAmount(gem_ore, 5, 5);
		CaverAPI.setMiningPointAmount(gem_ore, 7, 10);

		Blocks.fire.setFireInfo(rope, 15, 100);
		Blocks.fire.setFireInfo(rope_ladder, 15, 80);
		Blocks.fire.setFireInfo(perverted_log, 15, 15);
		Blocks.fire.setFireInfo(perverted_leaves, 90, 180);
		Blocks.fire.setFireInfo(perverted_sapling, 60, 180);
		Blocks.fire.setFireInfo(acresia_crops, 60, 100);

		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(rope), rope.new DispenceRope());
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveworld_portal), caveworld_portal.new DispencePortal());
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(cavern_portal), cavern_portal.new DispencePortal());
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(aqua_cavern_portal), aqua_cavern_portal.new DispencePortal());
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveland_portal), caveland_portal.new DispencePortal());
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(cavenia_portal), cavenia_portal.new DispencePortal());

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

	public static void registerRecipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(rope), Items.string, Items.string, Items.string, Items.leather);

		GameRegistry.addRecipe(new ItemStack(rope_ladder, 4),
			"R R", "RRR", "R R",
			'R', rope
		);

		GameRegistry.addRecipe(new ItemStack(cavenium_ore, 1, 2),
			"CCC", "CCC", "CCC",
			'C', new ItemStack(CaveItems.cavenium, 1, 0)
		);
		GameRegistry.addRecipe(new ItemStack(cavenium_ore, 1, 3),
			"CCC", "CCC", "CCC",
			'C', new ItemStack(CaveItems.cavenium, 1, 1)
		);

		GameRegistry.addShapedRecipe(new ItemStack(universal_chest),
			"CCC", "CEC", "CCC",
			'C', new ItemStack(cavenium_ore, 1, 3),
			'E', Items.ender_eye
		);

		GameRegistry.addRecipe(new ItemStack(gem_ore, 1, 1),
			"AAA", "AAA", "AAA",
			'A', new ItemStack(CaveItems.gem, 1, 0)
		);
		GameRegistry.addRecipe(new ItemStack(gem_ore, 1, 4),
			"MMM", "MMM", "MMM",
			'M', new ItemStack(CaveItems.gem, 1, 1)
		);
		GameRegistry.addRecipe(new ItemStack(gem_ore, 1, 6),
			"HHH", "HHH", "HHH",
			'H', new ItemStack(CaveItems.gem, 1, 3)
		);
		GameRegistry.addRecipe(new ItemStack(gem_ore, 1, 8),
			"III", "III", "III",
			'I', new ItemStack(CaveItems.gem, 1, 4)
		);

		GameRegistry.addShapelessRecipe(new ItemStack(Items.stick, 8), new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE));

		for (int i = 0; i < BlockPervertedLog.types.length; ++i)
		{
			GameRegistry.addRecipe(new ItemStack(Blocks.planks, 4, i),
				"LL", "LL",
				'L', new ItemStack(perverted_log, 1, i)
			);
		}

		for (int i = 0; i < BlockPervertedLog.types.length; ++i)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(perverted_sapling, 1, i), new ItemStack(Blocks.sapling, 1, i), Items.fermented_spider_eye);
		}

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.mossy_cobblestone), Blocks.vine, "cobblestone"));
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, 1), Blocks.vine, new ItemStack(Blocks.stonebrick, 1, 0));

		FurnaceRecipes.smelting().func_151394_a(new ItemStack(cavenium_ore, 1, 0), new ItemStack(CaveItems.cavenium, 1, 0), 0.5F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(cavenium_ore, 1, 1), new ItemStack(CaveItems.cavenium, 1, 1), 0.75F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(gem_ore, 1, 0), new ItemStack(CaveItems.gem, 1, 0), 0.5F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(gem_ore, 1, 3), new ItemStack(CaveItems.gem, 1, 1), 1.0F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(gem_ore, 1, 5), new ItemStack(CaveItems.gem, 1, 3), 1.0F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(gem_ore, 1, 7), new ItemStack(CaveItems.gem, 1, 5), 1.0F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.coal, 1, 1), 0.0F);
	}

	public static void addChestContents()
	{
		ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(Item.getItemFromBlock(rope), 0, 2, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(Item.getItemFromBlock(rope), 0, 3, 6, 10));
		ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(Item.getItemFromBlock(rope), 0, 3, 6, 10));
	}
}