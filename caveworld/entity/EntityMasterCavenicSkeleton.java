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
import caveworld.core.CaveAchievementList;
import caveworld.item.CaveItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EntityMasterCavenicSkeleton extends EntityCavenicSkeleton implements IBossDisplayData
{
	public EntityMasterCavenicSkeleton(World world)
	{
		super(world);
		this.experienceValue = 50;
		this.setSize(0.95F, 2.65F);
	}

	public float getHealthScale()
	{
		return getHealth() / getMaxHealth();
	}

	@Override
	public IChatComponent func_145748_c_()
	{
		IChatComponent name = new ChatComponentTranslation("entity." + getEntityString() + ".name");
		name.getChatStyle().setColor(EnumChatFormatting.GRAY);

		return name;
	}

	@Override
	protected void applyCustomValues()
	{
		aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 1, 2, 8.0F);

		super.applyCustomValues();
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D + 100.0D * rand.nextInt(3));
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.29778D);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data)
	{
		tasks.addTask(4, aiArrowAttack);
		addRandomArmor();
		enchantEquipment();

		return data;
	}

	@Override
	public void onStruckByLightning(EntityLightningBolt thunder) {}

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
	protected void dropFewItems(boolean par1, int looting)
	{
		super.dropFewItems(par1, looting);

		for (int i = 0; i < rand.nextInt(10) + 15; ++i)
		{
			entityDropItem(new ItemStack(CaveItems.cavenium, 1, 1), rand.nextFloat() + 0.1F);
		}
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase entity, float power)
	{
		EntityArrow arrow = new EntityCavenicArrow(worldObj, this, entity, 1.6F, 14 - worldObj.difficultySetting.getDifficultyId() * 4);
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, getHeldItem());
		int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, getHeldItem());
		arrow.setDamage(power * 2.5F + rand.nextGaussian() * 0.25D + worldObj.difficultySetting.getDifficultyId() * 0.11F);

		if (i > 0)
		{
			arrow.setDamage(arrow.getDamage() + i * 0.5D + 1.0D);
		}

		if (j > 0)
		{
			arrow.setKnockbackStrength(j + 1);
		}

		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, getHeldItem()) > 0 || getSkeletonType() == 1)
		{
			arrow.setFire(100);
		}

		playSound("random.bow", 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
		worldObj.spawnEntityInWorld(arrow);
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
			((EntityPlayer)entity).triggerAchievement(CaveAchievementList.masterCavenicSkeletonSlayer);
		}
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}

	@Override
	protected boolean canDespawn()
	{
		return !CaveworldAPI.isEntityInCavenia(this);
	}
}