/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.ArrayUtils;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.item.CaveItems;
import com.kegare.caveworld.util.CaveUtils;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.EntityRegistry;

public class EntityCavenicSkeleton extends EntitySkeleton
{
	public static int spawnWeight;
	public static int spawnMinHeight;
	public static int spawnMaxHeight;
	public static int spawnInChunks;
	public static int[] spawnBiomes;

	public static void refreshSpawn()
	{
		BiomeGenBase[] def = CaveUtils.getBiomes().toArray(new BiomeGenBase[0]);
		BiomeGenBase[] biomes = new BiomeGenBase[0];
		BiomeGenBase biome;

		for (int i : spawnBiomes)
		{
			if (i >= 0 && i < BiomeGenBase.getBiomeGenArray().length)
			{
				biome = BiomeGenBase.getBiome(i);

				if (biome != null)
				{
					biomes = ArrayUtils.add(biomes, biome);
				}
			}
		}

		if (ArrayUtils.isEmpty(biomes))
		{
			biomes = def;
		}

		EntityRegistry.removeSpawn(EntityCavenicSkeleton.class, EnumCreatureType.monster, def);

		if (spawnWeight > 0)
		{
			EntityRegistry.addSpawn(EntityCavenicSkeleton.class, spawnWeight, 4, 4, EnumCreatureType.monster, biomes);
		}
	}

	public EntityCavenicSkeleton(World world)
	{
		super(world);
		this.experienceValue = rand.nextInt(10) + 3;

		ObfuscationReflectionHelper.setPrivateValue(EntitySkeleton.class, this, new EntityAIArrowAttack(this, 0.95D, 1, 3, 6.0F), "aiArrowAttack", "field_85037_d");
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D + rand.nextInt(15));
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.175D);
	}

	@Override
	protected void dropFewItems(boolean par1, int looting)
	{
		super.dropFewItems(par1, looting);

		if (Config.cavenium)
		{
			entityDropItem(new ItemStack(CaveItems.cavenium, 1, rand.nextInt(2)), 0.5F);
		}
	}

	@Override
	public boolean getCanSpawnHere()
	{
		int y = MathHelper.floor_double(boundingBox.minY);

		return CaveworldAPI.isEntityInCaveworld(this) && y >= spawnMinHeight && y <= spawnMaxHeight && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return spawnInChunks;
	}
}