package caveworld.plugin.thaumcraft;

import caveworld.block.CaveBlocks;
import caveworld.plugin.ICavePlugin;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApi.EntityTagsNBT;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ThaumcraftPlugin implements ICavePlugin
{
	public static final String MODID = "Thaumcraft";

	public static boolean pluginState = true;

	public static boolean enabled()
	{
		return pluginState && Loader.isModLoaded(MODID);
	}

	@Override
	public String getModId()
	{
		return MODID;
	}

	@Override
	public boolean getPluginState()
	{
		return pluginState;
	}

	@Override
	public boolean setPluginState(boolean state)
	{
		return pluginState = state;
	}

	@Method(modid = MODID)
	@Override
	public void invoke()
	{
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.caveworld_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.MINE, 4));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavern_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 2).add(Aspect.MINE, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.aqua_cavern_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 3).add(Aspect.MINE, 3).add(Aspect.WATER, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.caveland_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 3).add(Aspect.MINE, 1).add(Aspect.EARTH, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenia_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.MINE, 1).add(Aspect.AURA, 5).add(Aspect.VOID, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.rope, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MOTION, 1).add(Aspect.CLOTH, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.rope_ladder, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MOTION, 1).add(Aspect.CLOTH, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 0), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 1), new AspectList().add(Aspect.EARTH, 3).add(Aspect.CRYSTAL, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 2), new AspectList().add(Aspect.CRYSTAL, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.cavenium_ore, 1, 3), new AspectList().add(Aspect.CRYSTAL, 6));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.universal_chest, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL, 15).add(Aspect.VOID, 2).add(Aspect.EXCHANGE, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 0), new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 2).add(Aspect.CRYSTAL, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 1), new AspectList().add(Aspect.CRYSTAL, 3).add(Aspect.WATER, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 2), new AspectList().add(Aspect.EARTH, 1).add(Aspect.MAGIC, 2).add(Aspect.ELDRITCH, 1).add(Aspect.GREED, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 3), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ENERGY, 2).add(Aspect.ENTROPY, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 4), new AspectList().add(Aspect.ENERGY, 2).add(Aspect.ENTROPY, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 5), new AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 6), new AspectList().add(Aspect.CRYSTAL, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 7), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 10).add(Aspect.CRYSTAL, 10));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.gem_ore, 1, 8), new AspectList().add(Aspect.CRYSTAL, 20).add(Aspect.ELDRITCH, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.perverted_log, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.TREE, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.perverted_leaves, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.perverted_sapling, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 2).add(Aspect.ELDRITCH, 1).add(Aspect.PLANT, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(CaveBlocks.acresia_crops, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.PLANT, 2).add(Aspect.CROP, 2));

		ThaumcraftApi.registerEntityTag("caveworld.Caveman", new AspectList().add(Aspect.LIFE, 6).add(Aspect.VOID, 3).add(Aspect.ELDRITCH, 2));
		ThaumcraftApi.registerEntityTag("caveworld.Caveman", new AspectList().add(Aspect.LIFE, 10).add(Aspect.VOID, 5).add(Aspect.ELDRITCH, 1).add(Aspect.CRYSTAL, 5), new EntityTagsNBT("Type", (byte)1));
		ThaumcraftApi.registerEntityTag("caveworld.Caveman", new AspectList().add(Aspect.LIFE, 20).add(Aspect.VOID, 10).add(Aspect.ELDRITCH, 1).add(Aspect.CRYSTAL, 15), new EntityTagsNBT("Type", (byte)2));
		ThaumcraftApi.registerEntityTag("caveworld.Caveman", new AspectList().add(Aspect.LIFE, 3).add(Aspect.VOID, 2).add(Aspect.ELDRITCH, 1), new EntityTagsNBT("Type", (byte)3));
		ThaumcraftApi.registerEntityTag("caveworld.ArcherZombie", new AspectList().add(Aspect.DEATH, 5).add(Aspect.TOOL, 1));
		ThaumcraftApi.registerEntityTag("caveworld.CavenicSkeleton", new AspectList().add(Aspect.DEATH, 8).add(Aspect.CRYSTAL, 3).add(Aspect.VOID, 4).add(Aspect.TOOL, 1));
		ThaumcraftApi.registerEntityTag("caveworld.MasterCavenicSkeleton", new AspectList().add(Aspect.DEATH, 15).add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 10).add(Aspect.TOOL, 4));
		ThaumcraftApi.registerEntityTag("caveworld.CrazyCavenicSkeleton", new AspectList().add(Aspect.DEATH, 100).add(Aspect.CRYSTAL, 10).add(Aspect.VOID, 30).add(Aspect.TOOL, 8));
		ThaumcraftApi.registerEntityTag("caveworld.CrazyCavenicSkeleton", new AspectList().add(Aspect.DEATH, 200).add(Aspect.CRYSTAL, 13).add(Aspect.VOID, 40).add(Aspect.TOOL, 10), new EntityTagsNBT("Type", (byte)1));
		ThaumcraftApi.registerEntityTag("caveworld.CrazyCavenicSkeleton", new AspectList().add(Aspect.DEATH, 300).add(Aspect.CRYSTAL, 15).add(Aspect.VOID, 50).add(Aspect.TOOL, 12), new EntityTagsNBT("Type", (byte)2));
		ThaumcraftApi.registerEntityTag("caveworld.CavenicCreeper", new AspectList().add(Aspect.DEATH, 7).add(Aspect.CRYSTAL, 3).add(Aspect.VOID, 5));
		ThaumcraftApi.registerEntityTag("caveworld.MasterCavenicCreeper", new AspectList().add(Aspect.DEATH, 14).add(Aspect.CRYSTAL, 5).add(Aspect.VOID, 10));
		ThaumcraftApi.registerEntityTag("caveworld.CavenicZombie", new AspectList().add(Aspect.DEATH, 10).add(Aspect.CRYSTAL, 3).add(Aspect.VOID, 5));
		ThaumcraftApi.registerEntityTag("caveworld.CavenicSpider", new AspectList().add(Aspect.DEATH, 6).add(Aspect.CRYSTAL, 3).add(Aspect.VOID, 4));
		ThaumcraftApi.registerEntityTag("caveworld.AquaSquid", new AspectList().add(Aspect.LIFE, 2).add(Aspect.WATER, 5));
	}
}