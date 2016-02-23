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
import caveworld.recipe.RecipeCavenicBow;
import caveworld.recipe.RecipeCaveniumTool;
import caveworld.recipe.RecipeFarmingHoe;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CaveItems
{
	public static final ToolMaterial CAVENIUM = EnumHelper.addToolMaterial("CAVENIUM", 3, 300, 5.0F, 1.5F, 10);
	public static final ToolMaterial AQUAMARINE = EnumHelper.addToolMaterial("AQUAMARINE", 2, 200, 8.0F, 1.5F, 15);

	public static final ItemCavenium cavenium = new ItemCavenium("cavenium");
	public static final ItemMiningPickaxe mining_pickaxe = new ItemMiningPickaxe("pickaxeMining");
	public static final ItemLumberingAxe lumbering_axe = new ItemLumberingAxe("axeLumbering");
	public static final ItemDiggingShovel digging_shovel = new ItemDiggingShovel("shovelDigging");
	public static final ItemFarmingHoe farming_hoe = new ItemFarmingHoe("hoeFarming");
	public static final ItemCavenicBow cavenic_bow = new ItemCavenicBow("bowCavenic");
	public static final ItemOreCompass ore_compass = new ItemOreCompass("oreCompass");
	public static final ItemGem gem = new ItemGem("gem");
	public static final ItemAquamarinePickaxe aquamarine_pickaxe = new ItemAquamarinePickaxe("pickaxeAquamarine");
	public static final ItemAquamarineAxe aquamarine_axe = new ItemAquamarineAxe("axeAquamarine");
	public static final ItemAquamarineShovel aquamarine_shovel = new ItemAquamarineShovel("shovelAquamarine");
	public static final ItemCaverBackpack caver_backpack = new ItemCaverBackpack("caverBackpack");
	public static final ItemCaveMobPlacer spawn_egg = new ItemCaveMobPlacer();

	public static void registerItems()
	{
		GameRegistry.registerItem(cavenium, "cavenium");
		GameRegistry.registerItem(mining_pickaxe, "mining_pickaxe");
		GameRegistry.registerItem(lumbering_axe, "lumbering_axe");
		GameRegistry.registerItem(digging_shovel, "digging_shovel");
		GameRegistry.registerItem(farming_hoe, "farming_hoe");
		GameRegistry.registerItem(cavenic_bow, "cavenic_bow");
		GameRegistry.registerItem(ore_compass, "ore_compass");
		GameRegistry.registerItem(gem, "gem");
		GameRegistry.registerItem(aquamarine_pickaxe, "aquamarine_pickaxe");
		GameRegistry.registerItem(aquamarine_axe, "aquamarine_axe");
		GameRegistry.registerItem(aquamarine_shovel, "aquamarine_shovel");
		GameRegistry.registerItem(caver_backpack, "caver_backpack");
		GameRegistry.registerItem(spawn_egg, "spawn_egg");

		OreDictionary.registerOre("cavenium", new ItemStack(cavenium, 1, 0));
		OreDictionary.registerOre("gemCavenium", new ItemStack(cavenium, 1, 0));
		OreDictionary.registerOre("refinedCavenium", new ItemStack(cavenium, 1, 1));
		OreDictionary.registerOre("gemRefinedCavenium", new ItemStack(cavenium, 1, 1));
		OreDictionary.registerOre("pickaxeMining", mining_pickaxe);
		OreDictionary.registerOre("miningPickaxe", mining_pickaxe);
		OreDictionary.registerOre("axeLumbering", lumbering_axe);
		OreDictionary.registerOre("lumberingAxe", lumbering_axe);
		OreDictionary.registerOre("shovelDigging", digging_shovel);
		OreDictionary.registerOre("diggingShovel", digging_shovel);
		OreDictionary.registerOre("hoeFarming", farming_hoe);
		OreDictionary.registerOre("farmingHoe", farming_hoe);
		OreDictionary.registerOre("bowCavenic", cavenic_bow);
		OreDictionary.registerOre("cavenicBow", cavenic_bow);
		OreDictionary.registerOre("oreCompass", ore_compass);
		OreDictionary.registerOre("compassOre", ore_compass);
		OreDictionary.registerOre("aquamarine", new ItemStack(gem, 1, 0));
		OreDictionary.registerOre("gemAquamarine", new ItemStack(gem, 1, 0));
		OreDictionary.registerOre("pickaxeAquamarine", aquamarine_pickaxe);
		OreDictionary.registerOre("aquamarinePickaxe", aquamarine_pickaxe);
		OreDictionary.registerOre("axeAquamarine", aquamarine_axe);
		OreDictionary.registerOre("aquamarineAxe", aquamarine_axe);
		OreDictionary.registerOre("shovelAquamarine", aquamarine_shovel);
		OreDictionary.registerOre("aquamarineShovel", aquamarine_shovel);
		OreDictionary.registerOre("caverBackpack", caver_backpack);

		CAVENIUM.setRepairItem(new ItemStack(cavenium, 1, OreDictionary.WILDCARD_VALUE));
		AQUAMARINE.setRepairItem(new ItemStack(gem, 1, 0));

		BlockDispenser.dispenseBehaviorRegistry.putObject(spawn_egg, spawn_egg.new DispenceEgg());
	}

	public static void registerRecipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(cavenium, 9, 0), new ItemStack(CaveBlocks.cavenium_ore, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(cavenium, 9, 1), new ItemStack(CaveBlocks.cavenium_ore, 1, 3));

		GameRegistry.addRecipe(new RecipeCaveniumTool(new ItemStack(mining_pickaxe), new Predicate<ItemStack>()
		{
			@Override
			public boolean apply(ItemStack itemstack)
			{
				return CaveUtils.isItemPickaxe(itemstack);
			}
		}));
		GameRegistry.addRecipe(new RecipeCaveniumTool(new ItemStack(lumbering_axe), new Predicate<ItemStack>()
		{
			@Override
			public boolean apply(ItemStack itemstack)
			{
				return CaveUtils.isItemAxe(itemstack);
			}
		}));
		GameRegistry.addRecipe(new RecipeCaveniumTool(new ItemStack(digging_shovel), new Predicate<ItemStack>()
		{
			@Override
			public boolean apply(ItemStack itemstack)
			{
				return CaveUtils.isItemShovel(itemstack);
			}
		}));
		GameRegistry.addRecipe(new RecipeFarmingHoe(new ItemStack(farming_hoe), new Predicate<ItemStack>()
		{
			@Override
			public boolean apply(ItemStack itemstack)
			{
				return CaveUtils.isItemHoe(itemstack);
			}
		}));

		GameRegistry.addRecipe(new RecipeCavenicBow(new ItemStack(cavenic_bow)));

		GameRegistry.addRecipe(new ItemStack(ore_compass),
			" C ", "CXC", " C ",
			'C', new ItemStack(cavenium, 1, 1),
			'X', Items.compass
		);

		GameRegistry.addShapelessRecipe(new ItemStack(gem, 9, 0), new ItemStack(CaveBlocks.gem_ore, 1, 1));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_pickaxe),
			"AAA", " S ", " S ",
			'A', new ItemStack(gem, 1, 0),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_axe),
			"AA", "AS", " S",
			'A', new ItemStack(gem, 1, 0),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(aquamarine_shovel),
			"A", "S", "S",
			'A', new ItemStack(gem, 1, 0),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ItemStack(caver_backpack),
			"LCL", "CLC", "LCL",
			'L', Items.leather,
			'C', new ItemStack(cavenium, 1, 0)
		);

		FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.cavenium_ore, 1, 0), new ItemStack(cavenium, 1, 0), 0.5F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.cavenium_ore, 1, 1), new ItemStack(cavenium, 1, 1), 0.75F);
		FurnaceRecipes.smelting().func_151394_a(new ItemStack(CaveBlocks.gem_ore, 1, 0), new ItemStack(gem, 1, 0), 0.5F);
	}

	public static void addChestContents()
	{
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 0), 3, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 0), 3, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 1), 1, 3, 5));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 1), 1, 3, 5));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(cavenic_bow), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(cavenic_bow), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(ore_compass), 1, 1, 2));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(ore_compass), 1, 1, 2));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(caver_backpack), 1, 1, 2));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(caver_backpack), 1, 1, 2));
	}
}