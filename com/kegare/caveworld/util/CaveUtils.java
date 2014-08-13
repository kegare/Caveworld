/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.util;

import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.google.common.base.Strings;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.api.ICaveVein;
import com.kegare.caveworld.core.Caveworld;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class CaveUtils
{
	public static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

	public static String stripControlCodes(String str)
	{
		return patternControlCode.matcher(str).replaceAll("");
	}

	public static ModContainer getModContainer()
	{
		ModContainer mod = Loader.instance().getIndexedModList().get(Caveworld.MODID);

		if (mod == null)
		{
			mod = Loader.instance().activeModContainer();

			if (mod == null || mod.getModId() != Caveworld.MODID)
			{
				return null;
			}
		}

		return mod;
	}

	public static boolean isOreBlock(Block block, int metadata)
	{
		if (block != null && block.getMaterial().isSolid() && !block.getMaterial().isToolNotRequired())
		{
			if (block instanceof BlockOre || block instanceof BlockRedstoneOre)
			{
				return true;
			}

			for (ICaveVein vein : CaveworldAPI.getCaveVeins())
			{
				if (vein.getBlock().getBlock() == block && vein.getBlock().getMetadata() == metadata)
				{
					return vein.getBlock().getBlock().getMaterial() == Material.rock;
				}
			}
		}

		return false;
	}

	public static boolean isItemPickaxe(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (item != null && item.isItemTool(itemstack))
			{
				if (item instanceof ItemPickaxe)
				{
					return true;
				}
				else if (item.getToolClasses(itemstack).contains("pickaxe"))
				{
					return true;
				}
				else if (ForgeHooks.isToolEffective(itemstack, Blocks.stone, 0))
				{
					return true;
				}
			}
		}

		return false;
	}

	public static <T extends Entity> T createEntity(Class<T> clazz, World world)
	{
		try
		{
			String name = String.valueOf(EntityList.classToStringMapping.get(clazz));
			Entity entity = EntityList.createEntityByName(Strings.nullToEmpty(name), world);

			if (entity == null || entity.getClass() != clazz)
			{
				return null;
			}

			return (T)entity;
		}
		catch (Exception e)
		{
			CaveLog.warning("Failed to create entity: %s", clazz.getSimpleName());

			return null;
		}
	}
}