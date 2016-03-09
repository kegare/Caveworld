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
import net.minecraft.item.ItemArmor.ArmorMaterial;
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
	public static final ToolMaterial MAGNITE = EnumHelper.addToolMaterial("MAGNITE", 3, 10, 100.0F, 11.0F, 50);
	public static final ToolMaterial HEXCITE = EnumHelper.addToolMaterial("HEXCITE", 3, 1041, 10.0F, 5.0F, 15);
	public static final ToolMaterial INFITITE = EnumHelper.addToolMaterial("INFITITE", 2, Integer.MAX_VALUE, 6.0F, 2.0F, 1);

	public static final ArmorMaterial HEXCITE_ARMOR = EnumHelper.addArmorMaterial("HEXCITE", 22, new int[] {5, 10, 8, 5}, 15);
	public static final ArmorMaterial INFITITE_ARMOR = EnumHelper.addArmorMaterial("INFITITE", Integer.MAX_VALUE, new int[] {2, 6, 5, 2}, 1);

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
	public static final ItemCaveSword magnite_sword = new ItemCaveSword("swordMagnite", "magnite_sword", MAGNITE);
	public static final ItemCavePickaxe magnite_pickaxe = new ItemCavePickaxe("pickaxeMagnite", "magnite_pickaxe", MAGNITE);
	public static final ItemCaveAxe magnite_axe = new ItemCaveAxe("axeMagnite", "magnite_axe", MAGNITE);
	public static final ItemCaveShovel magnite_shovel = new ItemCaveShovel("shovelMagnite", "magnite_shovel", MAGNITE);
	public static final ItemCaveSword hexcite_sword = new ItemCaveSword("swordHexcite", "hexcite_sword", HEXCITE);
	public static final ItemCavePickaxe hexcite_pickaxe = new ItemCavePickaxe("pickaxeHexcite", "hexcite_pickaxe", HEXCITE);
	public static final ItemCaveAxe hexcite_axe = new ItemCaveAxe("axeHexcite", "hexcite_axe", HEXCITE);
	public static final ItemCaveShovel hexcite_shovel = new ItemCaveShovel("shovelHexcite", "hexcite_shovel", HEXCITE);
	public static final ItemCaveHoe hexcite_hoe = new ItemCaveHoe("hoeHexcite", "hexcite_hoe", HEXCITE);
	public static final ItemCaveArmor hexcite_helmet = new ItemCaveArmor("helmetHexcite", "hexcite_helmet", "hexcite", HEXCITE_ARMOR, 0);
	public static final ItemCaveArmor hexcite_chestplate = new ItemCaveArmor("chestplateHexcite", "hexcite_chestplate", "hexcite", HEXCITE_ARMOR, 1);
	public static final ItemCaveArmor hexcite_leggings = new ItemCaveArmor("leggingsHexcite", "hexcite_leggings", "hexcite", HEXCITE_ARMOR, 2);
	public static final ItemCaveArmor hexcite_boots = new ItemCaveArmor("bootsHexcite", "hexcite_boots", "hexcite", HEXCITE_ARMOR, 3);
	public static final ItemInfititeSword infitite_sword = new ItemInfititeSword("swordInfitite");
	public static final ItemInfititePickaxe infitite_pickaxe = new ItemInfititePickaxe("pickaxeInfitite");
	public static final ItemInfititeAxe infitite_axe = new ItemInfititeAxe("axeInfitite");
	public static final ItemInfititeShovel infitite_shovel = new ItemInfititeShovel("shovelInfitite");
	public static final ItemInfititeHoe infitite_hoe = new ItemInfititeHoe("hoeInfitite");
	public static final ItemInfititeArmor infitite_helmet = new ItemInfititeArmor("helmetInfitite", "infitite_helmet", 0);
	public static final ItemInfititeArmor infitite_chestplate = new ItemInfititeArmor("chestplateInfitite", "infitite_chestplate", 1);
	public static final ItemInfititeArmor infitite_leggings = new ItemInfititeArmor("leggingsInfitite", "infitite_leggings", 2);
	public static final ItemInfititeArmor infitite_boots = new ItemInfititeArmor("bootsInfitite", "infitite_boots", 3);
	public static final ItemCaverBackpack caver_backpack = new ItemCaverBackpack("caverBackpack");
	public static final ItemAcresia acresia = new ItemAcresia("acresia");
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
		GameRegistry.registerItem(magnite_sword, "magnite_sword");
		GameRegistry.registerItem(magnite_pickaxe, "magnite_pickaxe");
		GameRegistry.registerItem(magnite_axe, "magnite_axe");
		GameRegistry.registerItem(magnite_shovel, "magnite_shovel");
		GameRegistry.registerItem(hexcite_sword, "hexcite_sword");
		GameRegistry.registerItem(hexcite_pickaxe, "hexcite_pickaxe");
		GameRegistry.registerItem(hexcite_axe, "hexcite_axe");
		GameRegistry.registerItem(hexcite_shovel, "hexcite_shovel");
		GameRegistry.registerItem(hexcite_hoe, "hexcite_hoe");
		GameRegistry.registerItem(hexcite_helmet, "hexcite_helmet");
		GameRegistry.registerItem(hexcite_chestplate, "hexcite_chestplate");
		GameRegistry.registerItem(hexcite_leggings, "hexcite_leggings");
		GameRegistry.registerItem(hexcite_boots, "hexcite_boots");
		GameRegistry.registerItem(infitite_sword, "infitite_sword");
		GameRegistry.registerItem(infitite_pickaxe, "infitite_pickaxe");
		GameRegistry.registerItem(infitite_axe, "infitite_axe");
		GameRegistry.registerItem(infitite_shovel, "infitite_shovel");
		GameRegistry.registerItem(infitite_hoe, "infitite_hoe");
		GameRegistry.registerItem(infitite_helmet, "infitite_helmet");
		GameRegistry.registerItem(infitite_chestplate, "infitite_chestplate");
		GameRegistry.registerItem(infitite_leggings, "infitite_leggings");
		GameRegistry.registerItem(infitite_boots, "infitite_boots");
		GameRegistry.registerItem(caver_backpack, "caver_backpack");
		GameRegistry.registerItem(acresia, "acresia");
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
		OreDictionary.registerOre("ingotMagnite", new ItemStack(gem, 1, 1));
		OreDictionary.registerOre("dustMagnite", new ItemStack(gem, 1, 2));
		OreDictionary.registerOre("hexcite", new ItemStack(gem, 1, 3));
		OreDictionary.registerOre("gemHexcite", new ItemStack(gem, 1, 3));
		OreDictionary.registerOre("infitite", new ItemStack(gem, 1, 4));
		OreDictionary.registerOre("gemInfitite", new ItemStack(gem, 1, 4));
		OreDictionary.registerOre("fragmentInfitite", new ItemStack(gem, 1, 5));
		OreDictionary.registerOre("swordMagnite", magnite_sword);
		OreDictionary.registerOre("pickaxeAquamarine", aquamarine_pickaxe);
		OreDictionary.registerOre("axeAquamarine", aquamarine_axe);
		OreDictionary.registerOre("shovelAquamarine", aquamarine_shovel);
		OreDictionary.registerOre("pickaxeMagnite", magnite_pickaxe);
		OreDictionary.registerOre("axeMagnite", magnite_axe);
		OreDictionary.registerOre("shovelMagnite", magnite_shovel);
		OreDictionary.registerOre("swordHexcite", hexcite_sword);
		OreDictionary.registerOre("pickaxeHexcite", hexcite_pickaxe);
		OreDictionary.registerOre("axeHexcite", hexcite_axe);
		OreDictionary.registerOre("shovelHexcite", hexcite_shovel);
		OreDictionary.registerOre("hoeHexcite", hexcite_hoe);
		OreDictionary.registerOre("helmetHexcite", hexcite_helmet);
		OreDictionary.registerOre("chestplateHexcite", hexcite_chestplate);
		OreDictionary.registerOre("leggingsHexcite", hexcite_leggings);
		OreDictionary.registerOre("bootsHexcite", hexcite_boots);
		OreDictionary.registerOre("swordInfitite", infitite_sword);
		OreDictionary.registerOre("pickaxeInfitite", infitite_pickaxe);
		OreDictionary.registerOre("axeInfitite", infitite_axe);
		OreDictionary.registerOre("shovelInfitite", infitite_shovel);
		OreDictionary.registerOre("hoeInfitite", infitite_hoe);
		OreDictionary.registerOre("helmetInfitite", infitite_helmet);
		OreDictionary.registerOre("chestplateInfitite", infitite_chestplate);
		OreDictionary.registerOre("leggingsInfitite", infitite_leggings);
		OreDictionary.registerOre("bootsInfitite", infitite_boots);
		OreDictionary.registerOre("caverBackpack", caver_backpack);
		OreDictionary.registerOre("acresia", new ItemStack(acresia, 1, 1));
		OreDictionary.registerOre("seedsAcresia", new ItemStack(acresia, 1, 0));
		OreDictionary.registerOre("fruitsAcresia", new ItemStack(acresia, 1, 1));

		CAVENIUM.setRepairItem(new ItemStack(cavenium, 1, OreDictionary.WILDCARD_VALUE));
		AQUAMARINE.setRepairItem(new ItemStack(gem, 1, 0));
		MAGNITE.setRepairItem(new ItemStack(gem, 1, 1));
		HEXCITE.setRepairItem(new ItemStack(gem, 1, 3));

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
		GameRegistry.addShapelessRecipe(new ItemStack(gem, 9, 1), new ItemStack(CaveBlocks.gem_ore, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(gem, 9, 3), new ItemStack(CaveBlocks.gem_ore, 1, 6));
		GameRegistry.addShapelessRecipe(new ItemStack(gem, 9, 4), new ItemStack(CaveBlocks.gem_ore, 1, 8));

		GameRegistry.addRecipe(new ItemStack(gem, 1, 4),
			"II", "II",
			'I', new ItemStack(gem, 1, 5)
		);

		GameRegistry.addRecipe(new ShapedOreRecipe(aquamarine_pickaxe,
			"AAA", " S ", " S ",
			'A', new ItemStack(gem, 1, 0),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(aquamarine_axe,
			"AA", "AS", " S",
			'A', new ItemStack(gem, 1, 0),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(aquamarine_shovel,
			"A", "S", "S",
			'A', new ItemStack(gem, 1, 0),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_sword,
			"M", "M", "S",
			'M', new ItemStack(gem, 1, 1),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_pickaxe,
			"MMM", " S ", " S ",
			'M', new ItemStack(gem, 1, 1),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_axe,
			"MM", "MS", " S",
			'M', new ItemStack(gem, 1, 1),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_shovel,
			"M", "S", "S",
			'M', new ItemStack(gem, 1, 1),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(hexcite_sword,
			"H", "H", "S",
			'H', new ItemStack(gem, 1, 3),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(hexcite_pickaxe,
			"HHH", " S ", " S ",
			'H', new ItemStack(gem, 1, 3),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(hexcite_axe,
			"HH", "HS", " S",
			'H', new ItemStack(gem, 1, 3),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(hexcite_shovel,
			"H", "S", "S",
			'H', new ItemStack(gem, 1, 3),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(hexcite_hoe,
			"HH", " S", " S",
			'H', new ItemStack(gem, 1, 3),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ItemStack(hexcite_helmet),
			"HHH", "H H",
			'H', new ItemStack(gem, 1, 3)
		);
		GameRegistry.addRecipe(new ItemStack(hexcite_chestplate),
			"H H", "HHH", "HHH",
			'H', new ItemStack(gem, 1, 3)
		);
		GameRegistry.addRecipe(new ItemStack(hexcite_leggings),
			"HHH", "H H", "H H",
			'H', new ItemStack(gem, 1, 3)
		);
		GameRegistry.addRecipe(new ItemStack(hexcite_boots),
			"H H", "H H",
			'H', new ItemStack(gem, 1, 3)
		);

		GameRegistry.addRecipe(new ShapedOreRecipe(infitite_sword,
			"I", "I", "S",
			'I', new ItemStack(gem, 1, 4),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(infitite_pickaxe,
			"III", " S ", " S ",
			'I', new ItemStack(gem, 1, 4),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(infitite_axe,
			"II", "IS", " S",
			'I', new ItemStack(gem, 1, 4),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(infitite_shovel,
			"I", "S", "S",
			'I', new ItemStack(gem, 1, 4),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(infitite_hoe,
			"II", " S", " S",
			'I', new ItemStack(gem, 1, 4),
			'S', "stickWood"
		));
		GameRegistry.addRecipe(new ItemStack(infitite_helmet),
			"III", "I I",
			'I', new ItemStack(gem, 1, 4)
		);
		GameRegistry.addRecipe(new ItemStack(infitite_chestplate),
			"I I", "III", "III",
			'I', new ItemStack(gem, 1, 4)
		);
		GameRegistry.addRecipe(new ItemStack(infitite_leggings),
			"III", "I I", "I I",
			'I', new ItemStack(gem, 1, 4)
		);
		GameRegistry.addRecipe(new ItemStack(infitite_boots),
			"I I", "I I",
			'I', new ItemStack(gem, 1, 4)
		);

		GameRegistry.addRecipe(new ItemStack(caver_backpack),
			"LCL", "CLC", "LCL",
			'L', Items.leather,
			'C', new ItemStack(cavenium, 1, 0)
		);

		FurnaceRecipes.smelting().func_151394_a(new ItemStack(gem, 1, 2), new ItemStack(gem, 1, 1), 0.5F);
	}

	public static void addChestContents()
	{
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 0), 3, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 0), 3, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 1), 1, 3, 5));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(cavenium, 1, 1), 1, 3, 5));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(cavenic_bow), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(cavenic_bow), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(ore_compass), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(ore_compass), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(gem, 1, 1), 3, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(gem, 1, 1), 3, 5, 10));
		ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(new ItemStack(caver_backpack), 1, 1, 1));
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(caver_backpack), 1, 1, 1));
	}
}