/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import com.google.common.base.Predicate;

import caveworld.block.CaveBlocks;
import caveworld.recipe.RecipeCaveniumTool;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CaveItems
{
	public static final ToolMaterial CAVENIUM = EnumHelper.addToolMaterial("CAVENIUM", 3, 300, 5.0F, 1.5F, 10);
	public static final ToolMaterial AQUAMARINE = EnumHelper.addToolMaterial("AQUAMARINE", 2, 200, 8.0F, 1.5F, 15);

	public static final ItemCavenium cavenium = new ItemCavenium("cavenium");
	public static final ItemMiningPickaxe mining_pickaxe = new ItemMiningPickaxe("pickaxeMining");
	public static final ItemLumberingAxe lumbering_axe = new ItemLumberingAxe("axeLumbering");
	public static final ItemDiggingShovel digging_shovel = new ItemDiggingShovel("shovelDigging");
	public static final ItemCavenicBow cavenic_bow = new ItemCavenicBow("bowCavenic");
	public static final ItemOreCompass ore_compass = new ItemOreCompass("oreCompass");
	public static final ItemGem gem = new ItemGem("gem");
	public static final ItemAquamarinePickaxe aquamarine_pickaxe = new ItemAquamarinePickaxe("pickaxeAquamarine");
	public static final ItemAquamarineAxe aquamarine_axe = new ItemAquamarineAxe("axeAquamarine");
	public static final ItemAquamarineShovel aquamarine_shovel = new ItemAquamarineShovel("shovelAquamarine");
	public static final ItemCaverBackpack caver_backpack = new ItemCaverBackpack("caverBackpack");

	public static void registerItems()
	{
		{
			GameRegistry.registerItem(cavenium, "cavenium");

			ItemStack item = new ItemStack(cavenium, 1, 0);
			OreDictionary.registerOre("cavenium", item);
			OreDictionary.registerOre("gemCavenium", item);

			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 3, 5, 10));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(item, 3, 5, 10));

			item = new ItemStack(cavenium, 1, 1);
			OreDictionary.registerOre("refinedCavenium", item);
			OreDictionary.registerOre("gemRefinedCavenium", item);

			ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(item, 1, 3, 3));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(item, 1, 3, 3));

			item = new ItemStack(cavenium, 9, 0);
			GameRegistry.addRecipe(new ShapelessOreRecipe(item, "blockCavenium"));
			GameRegistry.addRecipe(new ShapelessOreRecipe(item, "caveniumBlock"));
			item = new ItemStack(cavenium, 9, 1);
			GameRegistry.addRecipe(new ShapelessOreRecipe(item, "blockRefinedCavenium"));
			GameRegistry.addRecipe(new ShapelessOreRecipe(item, "refinedCaveniumBlock"));

			FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.cavenium_ore, 1, 0), new ItemStack(cavenium, 1, 0), 0.5F);
			FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.cavenium_ore, 1, 1), new ItemStack(cavenium, 1, 1), 0.75F);

			CAVENIUM.setRepairItem(new ItemStack(cavenium, 1, OreDictionary.WILDCARD_VALUE));
		}

		{
			GameRegistry.registerItem(mining_pickaxe, "mining_pickaxe");

			OreDictionary.registerOre("pickaxeMining", mining_pickaxe);
			OreDictionary.registerOre("miningPickaxe", mining_pickaxe);

			GameRegistry.addRecipe(new RecipeCaveniumTool(new ItemStack(mining_pickaxe), new Predicate<ItemStack>()
			{
				@Override
				public boolean apply(ItemStack itemstack)
				{
					return CaveUtils.isItemPickaxe(itemstack);
				}
			}));
		}

		{
			GameRegistry.registerItem(lumbering_axe, "lumbering_axe");

			OreDictionary.registerOre("axeLumbering", lumbering_axe);
			OreDictionary.registerOre("lumberingAxe", lumbering_axe);

			GameRegistry.addRecipe(new RecipeCaveniumTool(new ItemStack(lumbering_axe), new Predicate<ItemStack>()
			{
				@Override
				public boolean apply(ItemStack itemstack)
				{
					return CaveUtils.isItemAxe(itemstack);
				}
			}));
		}

		{
			GameRegistry.registerItem(digging_shovel, "digging_shovel");

			OreDictionary.registerOre("shovelDigging", digging_shovel);
			OreDictionary.registerOre("diggingShovel", digging_shovel);

			GameRegistry.addRecipe(new RecipeCaveniumTool(new ItemStack(digging_shovel), new Predicate<ItemStack>()
			{
				@Override
				public boolean apply(ItemStack itemstack)
				{
					return CaveUtils.isItemShovel(itemstack);
				}
			}));
		}

		{
			GameRegistry.registerItem(cavenic_bow, "cavenic_bow");

			OreDictionary.registerOre("bow", cavenic_bow);
			OreDictionary.registerOre("bowCavenic", cavenic_bow);
			OreDictionary.registerOre("cavenicBow", cavenic_bow);
		}

		{
			GameRegistry.registerItem(ore_compass, "ore_compass");

			OreDictionary.registerOre("oreCompass", ore_compass);
			OreDictionary.registerOre("compassOre", ore_compass);

			GameRegistry.addRecipe(new ShapedOreRecipe(ore_compass,
				" C ", "CXC", " C ",
				'C', "refinedCavenium",
				'X', Items.compass
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(ore_compass,
				" C ", "CXC", " C ",
				'C', "gemRefinedCavenium",
				'X', Items.compass
			));
		}

		{
			GameRegistry.registerItem(gem, "gem");

			ItemStack item = new ItemStack(gem, 1, 0);
			OreDictionary.registerOre("aquamarine", item);
			OreDictionary.registerOre("gemAquamarine", item);

			item.stackSize = 9;
			GameRegistry.addRecipe(new ShapelessOreRecipe(item, "blockAquamarine"));
			GameRegistry.addRecipe(new ShapelessOreRecipe(item, "aquamarineBlock"));
		}

		{
			GameRegistry.registerItem(aquamarine_pickaxe, "aquamarine_pickaxe");

			OreDictionary.registerOre("pickaxeAquamarine", aquamarine_pickaxe);
			OreDictionary.registerOre("aquamarinePickaxe", aquamarine_pickaxe);

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_pickaxe),
				"AAA", " S ", " S ",
				'A', "aquamarine",
				'S', "stickWood"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_pickaxe),
				"AAA", " S ", " S ",
				'A', "gemAquamarine",
				'S', "stickWood"
			));
		}

		{
			GameRegistry.registerItem(aquamarine_axe, "aquamarine_axe");

			OreDictionary.registerOre("axeAquamarine", aquamarine_axe);
			OreDictionary.registerOre("aquamarineAxe", aquamarine_axe);

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_axe),
				"AA", "AS", " S",
				'A', "aquamarine",
				'S', "stickWood"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_axe),
				"AA", "AS", " S",
				'A', "gemAquamarine",
				'S', "stickWood"
			));
		}

		{
			GameRegistry.registerItem(aquamarine_shovel, "aquamarine_shovel");

			OreDictionary.registerOre("shovelAquamarine", aquamarine_shovel);
			OreDictionary.registerOre("aquamarineShovel", aquamarine_shovel);

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_shovel),
				"A", "S", "S",
				'A', "aquamarine",
				'S', "stickWood"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_shovel),
				"A", "S", "S",
				'A', "gemAquamarine",
				'S', "stickWood"
			));
		}

		{
			GameRegistry.registerItem(caver_backpack, "caver_backpack");

			OreDictionary.registerOre("caverBackpack", caver_backpack);

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(caver_backpack),
				"LCL", "CLC", "LCL",
				'L', Items.leather,
				'C', "cavenium"
			));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(caver_backpack),
				"LCL", "CLC", "LCL",
				'L', Items.leather,
				'C', "gemCavenium"
			));
		}
	}
}