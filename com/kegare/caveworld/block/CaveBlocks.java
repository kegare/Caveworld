/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.block;

import java.io.File;
import java.io.FileInputStream;

import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.Level;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.entity.TileEntityUniversalChest;
import com.kegare.caveworld.item.ItemCaveniumOre;
import com.kegare.caveworld.item.ItemPortalCaveworld;
import com.kegare.caveworld.item.ItemRope;
import com.kegare.caveworld.item.ItemUniversalChest;
import com.kegare.caveworld.util.CaveLog;

import cpw.mods.fml.common.registry.GameRegistry;

public class CaveBlocks
{
	public static final BlockPortalCaveworld caveworld_portal = new BlockPortalCaveworld("portalCaveworld");
	public static final BlockRope rope = new BlockRope("rope");
	public static final BlockRopeLadder rope_ladder = new BlockRopeLadder("ropeLadder");
	public static final BlockCaveniumOre cavenium_ore = new BlockCaveniumOre("oreCavenium");
	public static final BlockUniversalChest universal_chest = new BlockUniversalChest("universalChest");

	public static void registerBlocks()
	{
		GameRegistry.registerBlock(caveworld_portal, ItemPortalCaveworld.class, "caveworld_portal");

		OreDictionary.registerOre("portalCaveworld", caveworld_portal);

		if (Config.portalCraftRecipe)
		{
			GameRegistry.addShapedRecipe(new ItemStack(caveworld_portal),
				" E ", "EPE", " D ",
				'E', Items.emerald,
				'P', Items.ender_pearl,
				'D', Items.diamond
			);
		}

		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(caveworld_portal), caveworld_portal.new DispencePortal());

		if (Config.rope)
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

		if (Config.ropeLadder)
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

		if (Config.oreCavenium)
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
			item = new ItemStack(cavenium_ore, 1, 3);
			OreDictionary.registerOre("blockRefinedCavenium", item);

			CaveworldAPI.setMiningPointAmount(cavenium_ore, 0, 2);
			CaveworldAPI.setMiningPointAmount(cavenium_ore, 1, 3);
		}

		if (Config.universalChest)
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
					universal_chest.inventory.loadInventoryFromNBT((NBTTagList)data.getTag("ChestItems"));
				}
			}
		}

		if (Config.mossStoneCraftRecipe)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Blocks.mossy_cobblestone),
				" V ", "VCV", " V ",
				'V', Blocks.vine,
				'C', Blocks.cobblestone
			);
		}
	}
}