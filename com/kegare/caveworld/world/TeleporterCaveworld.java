/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.world;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.block.CaveBlocks;

public class TeleporterCaveworld extends Teleporter
{
	private final WorldServer worldObj;
	private final Random random;

	private final Map<Long, PortalPosition> coordCache = Maps.newHashMap();
	private final Set<Long> coordKeys = Sets.newHashSet();

	public TeleporterCaveworld(WorldServer worldServer)
	{
		super(worldServer);
		this.worldObj = worldServer;
		this.worldObj.customTeleporters.add(this);
		this.random = new Random(worldServer.getSeed());
	}

	@Override
	public void placeInPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw)
	{
		if (!placeInExistingPortal(entity, posX, posY, posZ, rotationYaw))
		{
			makePortal(entity);

			placeInExistingPortal(entity, posX, posY, posZ, rotationYaw);
		}

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;

			player.addExperienceLevel(0);
			player.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 25));

			if (CaveworldAPI.isEntityInCaveworld(player))
			{
				WorldProviderCaveworld.recentTeleportPos = player.getPlayerCoordinates();
			}
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw)
	{
		int x = MathHelper.floor_double(entity.posX);
		int z = MathHelper.floor_double(entity.posZ);
		long chunkSeed = ChunkCoordIntPair.chunkXZ2Int(x, z);
		int blockX = 0;
		int blockY = worldObj.provider.getAverageGroundLevel();
		int blockZ = 0;
		boolean flag = true;
		double var1 = -1.0D;

		if (coordCache.containsKey(chunkSeed))
		{
			PortalPosition portal = coordCache.get(chunkSeed);
			var1 = 0.0D;
			blockX = portal.posX;
			blockY = portal.posY;
			blockZ = portal.posZ;
			portal.lastUpdateTime = worldObj.getTotalWorldTime();
			flag = false;
		}
		else
		{
			for (int var2 = x - 128; var2 <= x + 128; ++var2)
			{
				double xScale = var2 + 0.5D - entity.posX;

				for (int var3 = z - 128; var3 <= z + 128; ++var3)
				{
					double zScale = var3 + 0.5D - entity.posZ;

					for (int y = worldObj.getActualHeight() - 1; y >= 0; --y)
					{
						if (worldObj.getBlock(var2, y, var3) == CaveBlocks.caveworld_portal)
						{
							while (worldObj.getBlock(var2, y - 1, var3) == CaveBlocks.caveworld_portal)
							{
								--y;
							}

							double yScale = y + 0.5D - entity.posY;
							double var4 = xScale * xScale + yScale * yScale + zScale * zScale;

							if (var1 < 0.0D || var4 < var1)
							{
								var1 = var4;
								blockX = var2;
								blockY = y;
								blockZ = var3;
							}
						}
					}
				}
			}
		}

		if (var1 >= 0.0D)
		{
			if (flag)
			{
				coordCache.put(chunkSeed, new PortalPosition(blockX, blockY, blockZ, worldObj.getTotalWorldTime()));
				coordKeys.add(chunkSeed);
			}

			double var2 = blockX + 0.5D;
			double var3 = blockY + 0.5D;
			double var4 = blockZ + 0.5D;
			int var5 = -1;

			if (worldObj.getBlock(blockX - 1, blockY, blockZ) == CaveBlocks.caveworld_portal)
			{
				var5 = 2;
			}

			if (worldObj.getBlock(blockX + 1, blockY, blockZ) == CaveBlocks.caveworld_portal)
			{
				var5 = 0;
			}

			if (worldObj.getBlock(blockX, blockY, blockZ - 1) == CaveBlocks.caveworld_portal)
			{
				var5 = 3;
			}

			if (worldObj.getBlock(blockX, blockY, blockZ + 1) == CaveBlocks.caveworld_portal)
			{
				var5 = 1;
			}

			if (var5 > -1)
			{
				int var6 = entity.getTeleportDirection();
				int var7 = Direction.rotateLeft[var5];
				int var8 = Direction.offsetX[var5];
				int var9 = Direction.offsetZ[var5];
				int var10 = Direction.offsetX[var7];
				int var11 = Direction.offsetZ[var7];
				boolean var12 = !worldObj.isAirBlock(blockX + var8 + var10, blockY, blockZ + var9 + var11) || !worldObj.isAirBlock(blockX + var8 + var10, blockY + 1, blockZ + var9 + var11);
				boolean var13 = !worldObj.isAirBlock(blockX + var8, blockY, blockZ + var9) || !worldObj.isAirBlock(blockX + var8, blockY + 1, blockZ + var9);

				if (var12 && var13)
				{
					var5 = Direction.rotateOpposite[var5];
					var7 = Direction.rotateOpposite[var7];
					var8 = Direction.offsetX[var5];
					var9 = Direction.offsetZ[var5];
					var10 = Direction.offsetX[var7];
					var11 = Direction.offsetZ[var7];
					x = blockX - var10;
					var2 -= var10;
					z = blockZ - var11;
					var4 -= var11;
					var12 = !worldObj.isAirBlock(x + var8 + var10, blockY, z + var9 + var11) || !worldObj.isAirBlock(x + var8 + var10, blockY + 1, z + var9 + var11);
					var13 = !worldObj.isAirBlock(x + var8, blockY, z + var9) || !worldObj.isAirBlock(x + var8, blockY + 1, z + var9);
				}

				float var14 = 0.5F;
				float var15 = 0.5F;

				if (!var12 && var13)
				{
					var14 = 1.0F;
				}
				else if (var12 && !var13)
				{
					var14 = 0.0F;
				}
				else if (var12)
				{
					var15 = 0.0F;
				}

				var2 += var10 * var14 + var15 * var8;
				var4 += var11 * var14 + var15 * var9;
				var14 = 0.0F;
				var15 = 0.0F;
				float var16 = 0.0F;
				float var17 = 0.0F;

				if (var5 == var6)
				{
					var14 = 1.0F;
					var15 = 1.0F;
				}
				else if (var5 == Direction.rotateOpposite[var6])
				{
					var14 = -1.0F;
					var15 = -1.0F;
				}
				else if (var5 == Direction.rotateRight[var6])
				{
					var16 = 1.0F;
					var17 = -1.0F;
				}
				else
				{
					var16 = -1.0F;
					var17 = 1.0F;
				}

				double var18 = entity.motionX;
				double var19 = entity.motionZ;
				entity.motionX = var18 * var14 + var19 * var17;
				entity.motionZ = var18 * var16 + var19 * var15;
				entity.rotationYaw = rotationYaw - var6 * 90 + var5 * 90;
			}
			else
			{
				entity.motionX = entity.motionY = entity.motionZ = 0.0D;
			}

			if (entity instanceof EntityPlayerMP)
			{
				((EntityPlayerMP)entity).playerNetServerHandler.setPlayerLocation(var2, var3, var4, entity.rotationYaw, entity.rotationPitch);
			}
			else
			{
				entity.setLocationAndAngles(var2, var3, var4, entity.rotationYaw, entity.rotationPitch);
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		int worldHeight = worldObj.getActualHeight();
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY);
		int z = MathHelper.floor_double(entity.posZ);
		int blockX;
		int blockY;
		int blockZ;
		double xScale;
		double zScale;
		double var1 = -1.0D;
		int var2 = x;
		int var3 = y;
		int var4 = z;
		int var5 = 0;
		int var6 = random.nextInt(4);
		int var7;
		int var8;
		int var9;
		int var10;
		int var11;
		int var12;
		int var13;
		int var14;
		double var15;
		double var16;

		for (blockX = x - 16; blockX <= x + 16; ++blockX)
		{
			xScale = blockX + 0.5D - entity.posX;

			for (blockZ = z - 16; blockZ <= z + 16; ++blockZ)
			{
				zScale = blockZ + 0.5D - entity.posZ;

				outside: for (blockY = worldHeight - 2; blockY >= 0; --blockY)
				{
					if (worldObj.isAirBlock(blockX, blockY, blockZ))
					{
						while (blockY > 0 && worldObj.isAirBlock(blockX, blockY - 1, blockZ))
						{
							--blockY;
						}

						for (var8 = var6; var8 < var6 + 4; ++var8)
						{
							var7 = var8 % 2;
							var10 = 1 - var7;

							if (var8 % 4 >= 2)
							{
								var7 = -var7;
								var10 = -var10;
							}

							for (var9 = 0; var9 < 3; ++var9)
							{
								for (var12 = 0; var12 < 4; ++var12)
								{
									for (var11 = -1; var11 < 4; ++var11)
									{
										var14 = blockX + (var12 - 1) * var7 + var9 * var10;
										var13 = blockY + var11;
										int var17 = blockZ + (var12 - 1) * var10 - var9 * var7;

										if (var11 < 0 && !worldObj.getBlock(var14, var13, var17).getMaterial().isSolid() || var11 >= 0 && !worldObj.isAirBlock(var14, var13, var17))
										{
											continue outside;
										}
									}
								}
							}

							var16 = blockY + 0.5D - entity.posY;
							var15 = xScale * xScale + var16 * var16 + zScale * zScale;

							if (var1 < 0.0D || var15 < var1)
							{
								var1 = var15;
								var2 = blockX;
								var3 = blockY;
								var4 = blockZ;
								var5 = var8 % 4;
							}
						}
					}
				}
			}
		}

		if (var1 < 0.0D)
		{
			for (blockX = x - 16; blockX <= x + 16; ++blockX)
			{
				xScale = blockX + 0.5D - entity.posX;

				for (blockZ = z - 16; blockZ <= z + 16; ++blockZ)
				{
					zScale = blockZ + 0.5D - entity.posZ;

					outside: for (blockY = worldHeight - 2; blockY >= 0; --blockY)
					{
						if (worldObj.isAirBlock(blockX, blockY, blockZ))
						{
							while (blockY > 0 && worldObj.isAirBlock(blockX, blockY - 1, blockZ))
							{
								--blockY;
							}

							for (var8 = var6; var8 < var6 + 2; ++var8)
							{
								var7 = var8 % 2;
								var10 = 1 - var7;

								for (var9 = 0; var9 < 4; ++var9)
								{
									for (var12 = -1; var12 < 4; ++var12)
									{
										var11 = blockX + (var9 - 1) * var7;
										var14 = blockY + var12;
										var13 = blockZ + (var9 - 1) * var10;

										if (var12 < 0 && !worldObj.getBlock(var11, var14, var13).getMaterial().isSolid() || var12 >= 0 && !worldObj.isAirBlock(var11, var14, var13))
										{
											continue outside;
										}
									}
								}

								var16 = blockY + 0.5D - entity.posY;
								var15 = xScale * xScale + var16 * var16 + zScale * zScale;

								if (var1 < 0.0D || var15 < var1)
								{
									var1 = var15;
									var2 = blockX;
									var3 = blockY;
									var4 = blockZ;
									var5 = var8 % 2;
								}
							}
						}
					}
				}
			}
		}

		var13 = var5 % 2;
		var14 = 1 - var13;

		if (var5 % 4 >= 2)
		{
			var13 = -var13;
			var14 = -var14;
		}

		boolean flag;

		if (var1 < 0.0D)
		{
			var3 = MathHelper.clamp_int(var3, 10, worldHeight - 10);

			for (var6 = -1; var6 <= 1; ++var6)
			{
				for (var7 = 1; var7 < 3; ++var7)
				{
					for (var8 = -1; var8 < 3; ++var8)
					{
						var10 = var2 + (var7 - 1) * var13 + var6 * var14;
						var9 = var3 + var8;
						var12 = var4 + (var7 - 1) * var14 - var6 * var13;
						flag = var8 < 0;

						worldObj.setBlock(var10, var9, var12, flag ? Blocks.mossy_cobblestone : Blocks.air);
					}
				}
			}
		}

		for (var6 = 0; var6 < 4; ++var6)
		{
			for (var7 = -1; var7 < 4; ++var7)
			{
				var10 = var2 + (var6 - 1) * var13;
				var9 = var3 + var7;
				var12 = var4 + (var6 - 1) * var14;
				flag = var6 == 0 || var6 == 3 || var7 == -1 || var7 == 3;

				if (worldObj.getBlock(var10, var3 - 1, var12) == Blocks.bedrock)
				{
					++var9;
				}

				worldObj.setBlock(var10, var9, var12, flag ? Blocks.mossy_cobblestone : CaveBlocks.caveworld_portal, 0, 2);

				if (!flag)
				{
					CaveBlocks.caveworld_portal.setBlockBoundsBasedOnState(worldObj, var10, var9, var12);
				}
			}
		}

		return true;
	}

	@Override
	public void removeStalePortalLocations(long time)
	{
		if (time % 100L == 0L)
		{
			Iterator<Long> iterator = coordKeys.iterator();
			long var1 = time - 600L;

			while (iterator.hasNext())
			{
				long chunkSeed = iterator.next();
				PortalPosition portal = coordCache.get(chunkSeed);

				if (portal == null || portal.lastUpdateTime < var1)
				{
					iterator.remove();
					coordCache.remove(chunkSeed);
				}
			}
		}
	}
}