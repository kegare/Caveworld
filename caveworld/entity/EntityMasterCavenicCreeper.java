/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.entity;

import caveworld.core.CaveAchievementList;
import caveworld.item.CaveItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EntityMasterCavenicCreeper extends EntityCavenicCreeper implements IBossDisplayData
{
	public EntityMasterCavenicCreeper(World world)
	{
		super(world);
		this.experienceValue = 50;
	}

	@Override
	public IChatComponent func_145748_c_()
	{
		IChatComponent name = new ChatComponentTranslation("entity." + getEntityString() +  ".name");
		name.getChatStyle().setColor(EnumChatFormatting.GRAY);

		return name;
	}

	@Override
	protected void applyCustomValues()
	{
		fuseTime = 100;
		explosionRadius = 30;

		super.applyCustomValues();
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0D + 50.0D * rand.nextInt(3));
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.29778D);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data)
	{
		return data;
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
			((EntityPlayer)entity).triggerAchievement(CaveAchievementList.masterCavenicCreeperSlayer);
		}
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}
}