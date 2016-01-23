/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import caveworld.api.CaveworldAPI;
import caveworld.item.CaveItems;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityMasterCavenicSkeleton extends EntityCavenicSkeleton implements IBossDisplayData
{
	private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 1, 2, 14.0F);

	public EntityMasterCavenicSkeleton(World world)
	{
		super(world);
		this.experienceValue = 50;
		this.setSize(0.95F, 2.65F);

		ObfuscationReflectionHelper.setPrivateValue(EntitySkeleton.class, this, aiArrowAttack, "aiArrowAttack", "field_85037_d");
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(rand.nextInt(100) + 1 + 500.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(3.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.29778D);
	}

	@Override
	protected void addRandomArmor()
	{
		super.addRandomArmor();

		setCurrentItemOrArmor(0, new ItemStack(CaveItems.cavenic_bow));
		setEquipmentDropChance(0, 2.0F);
	}

	@Override
	public void setSkeletonType(int type)
	{
		super.setSkeletonType(type);

		setSize(0.95F, 2.65F);
	}

	@Override
	public void setCombatTask()
	{
		tasks.removeTask(aiArrowAttack);

		ItemStack itemstack = getHeldItem();

		if (itemstack != null && (itemstack.getItem() == Items.bow || itemstack.getItem() == CaveItems.cavenic_bow))
		{
			tasks.addTask(4, aiArrowAttack);
		}
	}

	@Override
	protected void dropFewItems(boolean par1, int looting)
	{
		super.dropFewItems(par1, looting);

		for (int i = 0; i < rand.nextInt(10) + 15; ++i)
		{
			entityDropItem(new ItemStack(CaveItems.cavenium, 1, 1), rand.nextFloat() + 0.1F);
		}
	}

	@Override
	protected boolean isValidLightLevel()
	{
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(boundingBox.minY);
		int k = MathHelper.floor_double(posZ);

		if (worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > rand.nextInt(32))
		{
			return false;
		}

		int l = worldObj.getBlockLightValue(i, j, k);

		if (worldObj.isThundering())
		{
			int prev = worldObj.skylightSubtracted;
			worldObj.skylightSubtracted = 10;
			l = worldObj.getBlockLightValue(i, j, k);
			worldObj.skylightSubtracted = prev;
		}

		return l <= this.rand.nextInt(11);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		if ((CaveworldAPI.isEntityInCaveworld(this) || CaveworldAPI.isEntityInCavern(this)) && worldObj.difficultySetting != EnumDifficulty.PEACEFUL && isValidLightLevel())
		{
			if (worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox))
			{
				int i = MathHelper.floor_double(posX);
				int j = MathHelper.floor_double(boundingBox.minY);
				int k = MathHelper.floor_double(posZ);

				return getBlockPathWeight(i, j, k) >= 0.0F;
			}
		}

		return false;
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}
}