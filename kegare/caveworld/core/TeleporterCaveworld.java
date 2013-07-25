package kegare.caveworld.core;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.PortalPosition;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import com.google.common.collect.Lists;

public class TeleporterCaveworld extends Teleporter
{
	private final WorldServer worldObj;
	private final Random random;

	private final LongHashMap coordCache = new LongHashMap();
	private final List<Long> coordKeys = Lists.newArrayList();

	public TeleporterCaveworld(WorldServer worldServer)
	{
		super(worldServer);
		this.worldObj = worldServer;
		this.random = new Random(worldServer.getSeed());
	}

	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float yaw)
	{
		if (!placeInExistingPortal(entity, x, y, z, yaw))
		{
			makePortal(entity);
			placeInExistingPortal(entity, x, y, z, yaw);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw)
	{
		short var1 = 128;
		double var2 = -1.0D;
		int var3 = 0;
		int var4 = 0;
		int var5 = 0;
		int var6 = MathHelper.floor_double(entity.posX);
		int var7 = MathHelper.floor_double(entity.posZ);
		long var8 = ChunkCoordIntPair.chunkXZ2Int(var6, var7);
		boolean var9 = true;
		double var10;
		int var11;

		if (coordCache.containsItem(var8))
		{
			PortalPosition portal = (PortalPosition)coordCache.getValueByKey(var8);
			var2 = 0.0D;
			var3 = portal.posX;
			var4 = portal.posY;
			var5 = portal.posZ;
			portal.lastUpdateTime = worldObj.getTotalWorldTime();
			var9 = false;
		}
		else
		{
			for (var11 = var6 - var1; var11 <= var6 + var1; ++var11)
			{
				double var12 = (double)var11 + 0.5D - entity.posX;

				for (int var13 = var7 - var1; var13 <= var7 + var1; ++var13)
				{
					double var14 = (double)var13 + 0.5D - entity.posZ;

					for (int var15 = worldObj.getActualHeight() - 1; var15 >= 0; --var15)
					{
						if (worldObj.getBlockId(var11, var15, var13) == CaveBlock.portalCaveworld.blockID)
						{
							while (worldObj.getBlockId(var11, var15 - 1, var13) == CaveBlock.portalCaveworld.blockID)
							{
								--var15;
							}

							var10 = (double)var15 + 0.5D - entity.posY;
							double var16 = var12 * var12 + var10 * var10 + var14 * var14;

							if (var2 < 0.0D || var16 < var2)
							{
								var2 = var16;
								var3 = var11;
								var4 = var15;
								var5 = var13;
							}
						}
					}
				}
			}
		}

		if (var2 >= 0.0D)
		{
			if (var9)
			{
				coordCache.add(var8, new PortalPosition(this, var3, var4, var5, worldObj.getTotalWorldTime()));
				coordKeys.add(Long.valueOf(var8));
			}

			double var12 = (double)var3 + 0.5D;
			double var13 = (double)var4 + 0.5D;
			var10 = (double)var5 + 0.5D;
			int var14 = -1;

			if (worldObj.getBlockId(var3 - 1, var4, var5) == CaveBlock.portalCaveworld.blockID)
			{
				var14 = 2;
			}

			if (worldObj.getBlockId(var3 + 1, var4, var5) == CaveBlock.portalCaveworld.blockID)
			{
				var14 = 0;
			}

			if (worldObj.getBlockId(var3, var4, var5 - 1) == CaveBlock.portalCaveworld.blockID)
			{
				var14 = 3;
			}

			if (worldObj.getBlockId(var3, var4, var5 + 1) == CaveBlock.portalCaveworld.blockID)
			{
				var14 = 1;
			}

			int var15 = entity.getTeleportDirection();

			if (var14 > -1)
			{
				int var16 = Direction.rotateLeft[var14];
				int var17 = Direction.offsetX[var14];
				int var18 = Direction.offsetZ[var14];
				int var19 = Direction.offsetX[var16];
				int var20 = Direction.offsetZ[var16];
				boolean var21 = !worldObj.isAirBlock(var3 + var17 + var19, var4, var5 + var18 + var20) || !worldObj.isAirBlock(var3 + var17 + var19, var4 + 1, var5 + var18 + var20);
				boolean var22 = !worldObj.isAirBlock(var3 + var17, var4, var5 + var18) || !worldObj.isAirBlock(var3 + var17, var4 + 1, var5 + var18);

				if (var21 && var22)
				{
					var14 = Direction.rotateOpposite[var14];
					var16 = Direction.rotateOpposite[var16];
					var17 = Direction.offsetX[var14];
					var18 = Direction.offsetZ[var14];
					var19 = Direction.offsetX[var16];
					var20 = Direction.offsetZ[var16];
					var11 = var3 - var19;
					var12 -= (double)var19;
					int var23 = var5 - var20;
					var10 -= (double)var20;
					var21 = !worldObj.isAirBlock(var11 + var17 + var19, var4, var23 + var18 + var20) || !worldObj.isAirBlock(var11 + var17 + var19, var4 + 1, var23 + var18 + var20);
					var22 = !worldObj.isAirBlock(var11 + var17, var4, var23 + var18) || !worldObj.isAirBlock(var11 + var17, var4 + 1, var23 + var18);
				}

				float var23 = 0.5F;
				float var24 = 0.5F;

				if (!var21 && var22)
				{
					var23 = 1.0F;
				}
				else if (var21 && !var22)
				{
					var23 = 0.0F;
				}
				else if (var21 && var22)
				{
					var24 = 0.0F;
				}

				var12 += (double)((float)var19 * var23 + var24 * (float)var17);
				var10 += (double)((float)var20 * var23 + var24 * (float)var18);
				float var25 = 0.0F;
				float var26 = 0.0F;
				float var27 = 0.0F;
				float var28 = 0.0F;

				if (var14 == var15)
				{
					var25 = 1.0F;
					var26 = 1.0F;
				}
				else if (var14 == Direction.rotateOpposite[var15])
				{
					var25 = -1.0F;
					var26 = -1.0F;
				}
				else if (var14 == Direction.rotateRight[var15])
				{
					var27 = 1.0F;
					var28 = -1.0F;
				}
				else
				{
					var27 = -1.0F;
					var28 = 1.0F;
				}

				double var29 = entity.motionX;
				double var30 = entity.motionZ;
				entity.motionX = var29 * (double)var25 + var30 * (double)var28;
				entity.motionZ = var29 * (double)var27 + var30 * (double)var26;
				entity.rotationYaw = yaw - (float)(var15 * 90) + (float)(var14 * 90);
			}
			else
			{
				entity.motionX = entity.motionY = entity.motionZ = 0.0D;
			}

			entity.setLocationAndAngles(var12, var13, var10, entity.rotationYaw, entity.rotationPitch);

			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		byte var1 = 16;
		double var2 = -1.0D;
		int var3 = MathHelper.floor_double(entity.posX);
		int var4 = MathHelper.floor_double(entity.posY);
		int var5 = MathHelper.floor_double(entity.posZ);
		int var6 = var3;
		int var7 = var4;
		int var8 = var5;
		int var9 = 0;
		int var10 = random.nextInt(4);
		int var11;
		double var12;
		double var13;
		int var14;
		int var15;
		int var16;
		int var17;
		int var18;
		int var19;
		int var20;
		int var21;
		int var22;
		int var23;
		double var24;
		double var25;

		for (var11 = var3 - var1; var11 <= var3 + var1; ++var11)
		{
			var12 = (double)var11 + 0.5D - entity.posX;

			for (var14 = var5 - var1; var14 <= var5 + var1; ++var14)
			{
				var13 = (double)var14 + 0.5D - entity.posZ;
				label1:

				for (var15 = worldObj.getActualHeight() - 1; var15 >= 0; --var15)
				{
					if (worldObj.isAirBlock(var11, var15, var14))
					{
						while (var15 > 0 && worldObj.isAirBlock(var11, var15 - 1, var14))
						{
							--var15;
						}

						for (var17 = var10; var17 < var10 + 4; ++var17)
						{
							var16 = var17 % 2;
							var19 = 1 - var16;

							if (var17 % 4 >= 2)
							{
								var16 = -var16;
								var19 = -var19;
							}

							for (var18 = 0; var18 < 3; ++var18)
							{
								for (var21 = 0; var21 < 4; ++var21)
								{
									for (var20 = -1; var20 < 4; ++var20)
									{
										var23 = var11 + (var21 - 1) * var16 + var18 * var19;
										var22 = var15 + var20;
										int var26 = var14 + (var21 - 1) * var19 - var18 * var16;

										if (var20 < 0 && !worldObj.getBlockMaterial(var23, var22, var26).isSolid() || var20 >= 0 && !worldObj.isAirBlock(var23, var22, var26))
										{
											continue label1;
										}
									}
								}
							}

							var25 = (double)var15 + 0.5D - entity.posY;
							var24 = var12 * var12 + var25 * var25 + var13 * var13;

							if (var2 < 0.0D || var24 < var2)
							{
								var2 = var24;
								var6 = var11;
								var7 = var15;
								var8 = var14;
								var9 = var17 % 4;
							}
						}
					}
				}
			}
		}

		if (var2 < 0.0D)
		{
			for (var11 = var3 - var1; var11 <= var3 + var1; ++var11)
			{
				var12 = (double)var11 + 0.5D - entity.posX;

				for (var14 = var5 - var1; var14 <= var5 + var1; ++var14)
				{
					var13 = (double)var14 + 0.5D - entity.posZ;
					label2:

					for (var15 = worldObj.getActualHeight() - 1; var15 >= 0; --var15)
					{
						if (worldObj.isAirBlock(var11, var15, var14))
						{
							while (var15 > 0 && worldObj.isAirBlock(var11, var15 - 1, var14))
							{
								--var15;
							}

							for (var17 = var10; var17 < var10 + 2; ++var17)
							{
								var16 = var17 % 2;
								var19 = 1 - var16;

								for (var18 = 0; var18 < 4; ++var18)
								{
									for (var21 = -1; var21 < 4; ++var21)
									{
										var20 = var11 + (var18 - 1) * var16;
										var23 = var15 + var21;
										var22 = var14 + (var18 - 1) * var19;

										if (var21 < 0 && !worldObj.getBlockMaterial(var20, var23, var22).isSolid() || var21 >= 0 && !worldObj.isAirBlock(var20, var23, var22))
										{
											continue label2;
										}
									}
								}

								var25 = (double)var15 + 0.5D - entity.posY;
								var24 = var12 * var12 + var25 * var25 + var13 * var13;

								if (var2 < 0.0D || var24 < var2)
								{
									var2 = var24;
									var6 = var11;
									var7 = var15;
									var8 = var14;
									var9 = var17 % 2;
								}
							}
						}
					}
				}
			}
		}

		int var26 = var6;
		int var27 = var7;
		var14 = var8;
		int var28 = var9 % 2;
		int var29 = 1 - var28;

		if (var9 % 4 >= 2)
		{
			var28 = -var28;
			var29 = -var29;
		}

		boolean flag;

		if (var2 < 0.0D)
		{
			if (var7 < 70)
			{
				var7 = 70;
			}

			if (var7 > worldObj.getActualHeight() - 10)
			{
				var7 = worldObj.getActualHeight() - 10;
			}

			var27 = var7;

			for (var15 = -1; var15 <= 1; ++var15)
			{
				for (var17 = 1; var17 < 3; ++var17)
				{
					for (var16 = -1; var16 < 3; ++var16)
					{
						var19 = var26 + (var17 - 1) * var28 + var15 * var29;
						var18 = var27 + var16;
						var21 = var14 + (var17 - 1) * var29 - var15 * var28;
						flag = var16 < 0;

						worldObj.setBlock(var19, var18, var21, flag ? Block.cobblestoneMossy.blockID : 0);
					}
				}
			}
		}

		for (var15 = 0; var15 < 4; ++var15)
		{
			for (var17 = 0; var17 < 4; ++var17)
			{
				for (var16 = -1; var16 < 4; ++var16)
				{
					var19 = var26 + (var17 - 1) * var28;
					var18 = var27 + var16;
					var21 = var14 + (var17 - 1) * var29;
					flag = var17 == 0 || var17 == 3 || var16 == -1 || var16 == 3;

					worldObj.setBlock(var19, var18, var21, flag ? Block.cobblestoneMossy.blockID : CaveBlock.portalCaveworld.blockID, 0, 2);
				}
			}

			for (var17 = 0; var17 < 4; ++var17)
			{
				for (var16 = -1; var16 < 4; ++var16)
				{
					var19 = var26 + (var17 - 1) * var28;
					var18 = var27 + var16;
					var21 = var14 + (var17 - 1) * var29;

					worldObj.notifyBlocksOfNeighborChange(var19, var18, var21, worldObj.getBlockId(var19, var18, var21));
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
				Long var2 = iterator.next();
				PortalPosition portal = (PortalPosition)coordCache.getValueByKey(var2.longValue());

				if (portal == null || portal.lastUpdateTime < var1)
				{
					iterator.remove();
					coordCache.remove(var2.longValue());
				}
			}
		}
	}
}