/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.entity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCaveArrow extends EntityArrow implements IThrowableEntity
{
	protected int xTile = -1;
	protected int yTile = -1;
	protected int zTile = -1;
	protected Block inTile;
	protected int inData = 0;
	protected boolean inGround = false;

	protected MovingObjectPosition mop = null;

	public int canBePickedUp = 0;
	public int arrowShake = 0;

	public Entity shootingEntity;
	protected int ticksInGround;
	protected int ticksInAir = 0;
	protected double damage = 2.0D;

	protected int knockbackStrength;

	protected EntityCaveArrow(World world)
	{
		super(world);
		this.setSize(0.5F, 0.5F);
	}

	public EntityCaveArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
		this.renderDistanceWeight = 10.0D;
		this.setSize(0.5F, 0.5F);
		this.setPosition(x, y, z);
		this.yOffset = 0.0F;
	}

	public EntityCaveArrow(World world, EntityLivingBase player, float par3)
	{
		super(world, player, par3);
		this.shootingEntity = player;

		if (player instanceof EntityPlayer)
		{
			this.canBePickedUp = 1;
		}

		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(player.posX, player.posY + player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
		this.posX -= MathHelper.cos(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
		this.posY -= 0.10000000149011612D;
		this.posZ -= MathHelper.sin(rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
		this.setPosition(posX, posY, posZ);
		this.yOffset = 0.0F;
		this.motionX = -MathHelper.sin(rotationYaw   / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		this.motionZ = MathHelper.cos(rotationYaw   / 180.0F * (float)Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float)Math.PI);
		this.motionY = -MathHelper.sin(rotationPitch / 180.0F * (float)Math.PI);
		this.setThrowableHeading(motionX, motionY, motionZ, par3 * 1.5F, 1.0F);
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(16, Byte.valueOf((byte)0));
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float par7, float par8)
	{
		float var9 = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= var9;
		y /= var9;
		z /= var9;
		x += rand.nextGaussian() * 0.007499999832361937D * par8;
		y += rand.nextGaussian() * 0.007499999832361937D * par8;
		z += rand.nextGaussian() * 0.007499999832361937D * par8;
		x *= par7;
		y *= par7;
		z *= par7;
		motionX = x;
		motionY = y;
		motionZ = z;
		float var10 = MathHelper.sqrt_double(x * x + z * z);
		prevRotationYaw   = rotationYaw   = (float)(Math.atan2(x,       z) * 180.0D / Math.PI);
		prevRotationPitch = rotationPitch = (float)(Math.atan2(y, var10) * 180.0D / Math.PI);
		ticksInGround = 0;
	}

	@Override
	public Entity getThrower()
	{
		return shootingEntity;
	}

	@Override
	public void setThrower(Entity entity)
	{
		if (entity != null && entity instanceof EntityPlayer)
		{
			shootingEntity = entity;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int par9)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double vx, double vy, double vz)
	{
		motionX = vx;
		motionY = vy;
		motionZ = vz;

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float var7 = MathHelper.sqrt_double(vx * vx + vz * vz);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(vx, vz) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(vy, var7) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
			ticksInGround = 0;
		}
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float var1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
			prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
			prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, var1) * 180.0D / Math.PI);
		}

		Block block = worldObj.getBlock(xTile, yTile, zTile);

		if (block.getMaterial() != Material.air)
		{
			block.setBlockBoundsBasedOnState(worldObj, xTile, yTile, zTile);
			AxisAlignedBB var2 = block.getCollisionBoundingBoxFromPool(worldObj, xTile, yTile, zTile);

			if (var2 != null && var2.isVecInside(Vec3.createVectorHelper(posX, posY, posZ)))
			{
				inGround = true;
			}
		}

		if (arrowShake > 0)
		{
			--arrowShake;
		}

		if (inGround)
		{
			onHit(worldObj.getBlock(xTile, yTile, zTile), worldObj.getBlockMetadata(xTile, yTile, zTile));
		}
		else
		{
			++ticksInAir;
			Vec3 var17 = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 var3 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
			MovingObjectPosition var4 = worldObj.func_147447_a(var17, var3, false, true, false);
			var17 = Vec3.createVectorHelper(posX, posY, posZ);
			var3 = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);

			if (var4 != null)
			{
				var3 = Vec3.createVectorHelper(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);
			}

			Entity var5 = null;
			List var6 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
			double var7 = 0.0D;
			Iterator var9 = var6.iterator();
			float var11;

			while (var9.hasNext())
			{
				Entity var10 = (Entity)var9.next();

				if (var10.canBeCollidedWith() && (var10 != shootingEntity || ticksInAir >= 5))
				{
					var11 = 0.3F;
					AxisAlignedBB var12 = var10.boundingBox.expand(var11, var11, var11);
					MovingObjectPosition var13 = var12.calculateIntercept(var17, var3);

					if (var13 != null)
					{
						double var14 = var17.distanceTo(var13.hitVec);

						if (var14 < var7 || var7 == 0.0D)
						{
							var5 = var10;
							var7 = var14;
						}
					}
				}
			}

			if (var5 != null)
			{
				var4 = new MovingObjectPosition(var5);
			}

			float var20;

			if (var4 != null)
			{
				if (var4.entityHit != null)
				{
					var20 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					int var24 = MathHelper.ceiling_double_int(var20 * damage);

					if (getIsCritical())
					{
						var24 += rand.nextInt(var24 / 2 + 2);
					}

					DamageSource var22 = null;

					if (shootingEntity == null)
					{
						var22 = DamageSource.causeThrownDamage(this, this);
					}
					else
					{
						var22 = DamageSource.causeThrownDamage(this, shootingEntity);
					}

					if (isBurning())
					{
						var4.entityHit.setFire(5);
					}

					if (var4.entityHit.attackEntityFrom(var22, var24))
					{
						if (var4.entityHit instanceof EntityLiving)
						{
							if (!worldObj.isRemote)
							{
								EntityLiving entity = (EntityLiving)var4.entityHit;

								entity.setArrowCountInEntity(entity.getArrowCountInEntity() + 1);
							}

							if (knockbackStrength > 0)
							{
								float var25 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

								if (var25 > 0.0F)
								{
									var4.entityHit.addVelocity(motionX * knockbackStrength * 0.6000000238418579D / var25, 0.1D, motionZ * knockbackStrength * 0.6000000238418579D / var25);
								}
							}

						}

						worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
						setDead();
					}
					else
					{
						motionX *= -0.10000000149011612D;
						motionY *= -0.10000000149011612D;
						motionZ *= -0.10000000149011612D;
						rotationYaw += 180.0F;
						prevRotationYaw += 180.0F;
						ticksInAir = 0;
					}
				}
				else
				{
					xTile = var4.blockX;
					yTile = var4.blockY;
					zTile = var4.blockZ;
					inTile = worldObj.getBlock(xTile, yTile, zTile);
					inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
					motionX = (float)(var4.hitVec.xCoord - posX);
					motionY = (float)(var4.hitVec.yCoord - posY);
					motionZ = (float)(var4.hitVec.zCoord - posZ);
					var20 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
					posX -= motionX / var20 * 0.05000000074505806D;
					posY -= motionY / var20 * 0.05000000074505806D;
					posZ -= motionZ / var20 * 0.05000000074505806D;
					worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (rand.nextFloat() * 0.2F + 0.9F));
					inGround = true;
					arrowShake = 7;
					setIsCritical(false);
					mop = var4;
				}
			}

			if (getIsCritical())
			{
				for (int var21 = 0; var21 < 4; ++var21)
				{
					worldObj.spawnParticle("crit", posX + motionX * var21 / 4.0D, posY + motionY * var21 / 4.0D, posZ + motionZ * var21 / 4.0D, -motionX, -motionY + 0.2D, -motionZ);
				}
			}

			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			var20 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

			rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

			for (rotationPitch = (float)(Math.atan2(motionY, var20) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
			{
				;
			}

			while (rotationPitch - prevRotationPitch >= 180.0F)
			{
				prevRotationPitch += 360.0F;
			}

			while (rotationYaw - prevRotationYaw < -180.0F)
			{
				prevRotationYaw -= 360.0F;
			}

			while (rotationYaw - prevRotationYaw >= 180.0F)
			{
				prevRotationYaw += 360.0F;
			}

			rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
			rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;

			float var23 = 0.99F;
			var11 = 0.05F;

			if (isInWater())
			{
				for (int var26 = 0; var26 < 4; ++var26)
				{
					float var27 = 0.25F;
					worldObj.spawnParticle("bubble", posX - motionX * var27, posY - motionY * var27, posZ - motionZ * var27, motionX, motionY, motionZ);
				}

				var23 = 0.8F;
			}

			motionX *= var23;
			motionY *= var23;
			motionZ *= var23;
			motionY -= var11;
			setPosition(posX, posY, posZ);
			func_145775_I();
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setShort("xTile", (short)xTile);
		nbt.setShort("yTile", (short)yTile);
		nbt.setShort("zTile", (short)zTile);
		nbt.setString("inTile", GameData.getBlockRegistry().getNameForObject(inTile));
		nbt.setByte("inData", (byte)inData);
		nbt.setByte("shake", (byte)arrowShake);
		nbt.setByte("inGround", (byte)(inGround ? 1 : 0));
		nbt.setByte("pickup", (byte)canBePickedUp);
		nbt.setDouble("damage", damage);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		xTile = nbt.getShort("xTile");
		yTile = nbt.getShort("yTile");
		zTile = nbt.getShort("zTile");
		inTile = GameData.getBlockRegistry().getObject(nbt.getString("inTile"));
		inData = nbt.getByte("inData") & 255;
		arrowShake = nbt.getByte("shake") & 255;
		inGround = nbt.getByte("inGround") == 1;

		if (nbt.hasKey("damage"))
		{
			damage = nbt.getDouble("damage");
		}

		if (nbt.hasKey("pickup"))
		{
			canBePickedUp = nbt.getByte("pickup");
		}
		else if (nbt.hasKey("player"))
		{
			canBePickedUp = nbt.getBoolean("player") ? 1 : 0;
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player)
	{
		if (!worldObj.isRemote && inGround && arrowShake <= 0)
		{
			boolean var2 = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;

			if (canBePickedUp == 1 && !addItemStackToInventory(player))
			{
				var2 = false;
			}

			if (var2)
			{
				worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				setDead();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getShadowSize()
	{
		return 0.0F;
	}

	@Override
	public void setDamage(double par1)
	{
		damage = par1;
	}

	@Override
	public double getDamage()
	{
		return damage;
	}

	@Override
	public void setKnockbackStrength(int par1)
	{
		knockbackStrength = par1;
	}

	@Override
	public boolean canAttackWithItem()
	{
		return false;
	}

	@Override
	public void setIsCritical(boolean par1)
	{
		byte var2 = dataWatcher.getWatchableObjectByte(16);

		if (par1)
		{
			dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 | 1)));
		}
		else
		{
			dataWatcher.updateObject(16, Byte.valueOf((byte)(var2 & -2)));
		}
	}


	@Override
	public boolean getIsCritical()
	{
		byte var1 = dataWatcher.getWatchableObjectByte(16);
		return (var1 & 1) != 0;
	}

	protected void onHit(Block block, int metadata)
	{
		if (block == inTile && metadata == inData && mop != null)
		{
			++ticksInGround;

			if (tryPlaceBlock() || ticksInGround == 1200)
			{
				setDead();
			}
		}
		else
		{
			inGround = false;
			motionX *= rand.nextFloat() * 0.2F;
			motionY *= rand.nextFloat() * 0.2F;
			motionZ *= rand.nextFloat() * 0.2F;
			ticksInGround = 0;
			ticksInAir = 0;
		}
	}

	protected boolean tryPlaceBlock()
	{
		return true;
	}

	protected boolean addItemStackToInventory(EntityPlayer player)
	{
		return true;
	}
}