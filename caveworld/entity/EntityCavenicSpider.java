/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import org.apache.commons.lang3.ArrayUtils;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICavenicMob;
import caveworld.core.CaveAchievementList;
import caveworld.item.CaveItems;
import caveworld.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class EntityCavenicSpider extends EntitySpider implements ICavenicMob
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

		CaveEntityRegistry.removeSpawn(EntityCavenicSpider.class, def);

		if (spawnWeight > 0)
		{
			CaveEntityRegistry.addSpawn(EntityCavenicSpider.class, spawnWeight, 1, 2, biomes);
		}
	}

	public EntityCavenicSpider(World world)
	{
		super(world);
		this.experienceValue = 10;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D + 5.0D * rand.nextInt(3));
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(3.0D);
	}

	@Override
	protected Entity findPlayerToAttack()
	{
		return worldObj.getClosestVulnerablePlayerToEntity(this, 20.0D);
	}

	@Override
	protected void dropFewItems(boolean par1, int looting)
	{
		super.dropFewItems(par1, looting);

		entityDropItem(new ItemStack(CaveItems.cavenium, 1, rand.nextInt(2)), 0.5F);
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (super.attackEntityAsMob(entity))
		{
			if (entity instanceof EntityLivingBase)
			{
				byte sec = 3;

				if (worldObj.difficultySetting == EnumDifficulty.NORMAL)
				{
					sec = 7;
				}
				else if (worldObj.difficultySetting == EnumDifficulty.HARD)
				{
					sec = 14;
				}

				if (sec > 0)
				{
					((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.blindness.getId(), sec * 20));
				}
			}

			return true;
		}

		return false;
	}

	@Override
	protected void attackEntity(Entity entity, float distance)
	{
		if (distance > 2.0F && distance < 6.0F && rand.nextInt(10) == 0)
		{
			if (onGround)
			{
				double d0 = entity.posX - posX;
				double d1 = entity.posZ - posZ;
				float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1);

				motionX = d0 / f * 0.5D * 0.800000011920929D + motionX * 0.20000000298023224D;
				motionZ = d1 / f * 0.5D * 0.800000011920929D + motionZ * 0.20000000298023224D;
				motionY = 0.4000000059604645D;
			}
		}
		else
		{
			if (attackTime <= 0 && distance < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
			{
				attackTime = 20;

				attackEntityAsMob(entity);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		return !source.isFireDamage() && source != DamageSource.fall && super.attackEntityFrom(source, damage);
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);

		Entity entity = source.getEntity();

		if (entity == null)
		{
			entity = source.getSourceOfDamage();
		}

		if (entity != null && entity instanceof EntityPlayer)
		{
			((EntityPlayer)entity).triggerAchievement(CaveAchievementList.cavenicSpiderSlayer);
		}
	}

	public boolean isValidHeight()
	{
		int y = MathHelper.floor_double(boundingBox.minY);

		return y >= spawnMinHeight && y <= spawnMaxHeight;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		if (CaveworldAPI.isEntityInCaves(this) && !CaveworldAPI.isEntityInCavern(this))
		{
			return isValidHeight() && super.getCanSpawnHere() || CaveworldAPI.isEntityInCavenia(this) && rand.nextInt(10) == 0;
		}

		return false;
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return CaveworldAPI.isEntityInCavenia(this) ? 8 : spawnInChunks;
	}
}