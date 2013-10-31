package kegare.caveworld.world;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import kegare.caveworld.core.Caveworld;
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
	public void placeInPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw)
	{
		if (!placeInExistingPortal(entity, posX, posY, posZ, rotationYaw))
		{
			makePortal(entity);
			placeInExistingPortal(entity, posX, posY, posZ, rotationYaw);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw)
	{
		double var1 = -1.0D;
		int var2 = 0;
		int var3 = 0;
		int var4 = 0;
		int x = MathHelper.floor_double(entity.posX);
		int z = MathHelper.floor_double(entity.posZ);
		long var5 = ChunkCoordIntPair.chunkXZ2Int(x, z);
		boolean var6 = true;
		double var7;
		int var8;

		if (coordCache.containsItem(var5))
		{
			PortalPosition portal = (PortalPosition)coordCache.getValueByKey(var5);
			var1 = 0.0D;
			var2 = portal.posX;
			var3 = portal.posY;
			var4 = portal.posZ;
			portal.lastUpdateTime = worldObj.getTotalWorldTime();
			var6 = false;
		}
		else
		{
			for (var8 = x - 128; var8 <= x + 128; ++var8)
			{
				double var9 = (double)var8 + 0.5D - entity.posX;

				for (int var10 = z - 128; var10 <= z + 128; ++var10)
				{
					double var11 = (double)var10 + 0.5D - entity.posZ;

					for (int var12 = 126; var12 >= 0; --var12)
					{
						if (worldObj.getBlockId(var8, var12, var10) == Caveworld.portalCaveworld.blockID)
						{
							while (worldObj.getBlockId(var8, var12 - 1, var10) == Caveworld.portalCaveworld.blockID)
							{
								--var12;
							}

							var7 = (double)var12 + 0.5D - entity.posY;
							double var13 = var9 * var9 + var7 * var7 + var11 * var11;

							if (var1 < 0.0D || var13 < var1)
							{
								var1 = var13;
								var2 = var8;
								var3 = var12;
								var4 = var10;
							}
						}
					}
				}
			}
		}

		if (var1 >= 0.0D)
		{
			if (var6)
			{
				coordCache.add(var5, new PortalPosition(this, var2, var3, var4, worldObj.getTotalWorldTime()));
				coordKeys.add(Long.valueOf(var5));
			}

			double var9 = (double)var2 + 0.5D;
			double var10 = (double)var3 + 0.5D;
			var7 = (double)var4 + 0.5D;
			int var11 = -1;

			if (worldObj.getBlockId(var2 - 1, var3, var4) == Caveworld.portalCaveworld.blockID)
			{
				var11 = 2;
			}

			if (worldObj.getBlockId(var2 + 1, var3, var4) == Caveworld.portalCaveworld.blockID)
			{
				var11 = 0;
			}

			if (worldObj.getBlockId(var2, var3, var4 - 1) == Caveworld.portalCaveworld.blockID)
			{
				var11 = 3;
			}

			if (worldObj.getBlockId(var2, var3, var4 + 1) == Caveworld.portalCaveworld.blockID)
			{
				var11 = 1;
			}

			int var12 = entity.getTeleportDirection();

			if (var11 > -1)
			{
				int var13 = Direction.rotateLeft[var11];
				int var14 = Direction.offsetX[var11];
				int var15 = Direction.offsetZ[var11];
				int var16 = Direction.offsetX[var13];
				int var17 = Direction.offsetZ[var13];
				boolean var18 = !worldObj.isAirBlock(var2 + var14 + var16, var3, var4 + var15 + var17) || !worldObj.isAirBlock(var2 + var14 + var16, var3 + 1, var4 + var15 + var17);
				boolean var19 = !worldObj.isAirBlock(var2 + var14, var3, var4 + var15) || !worldObj.isAirBlock(var2 + var14, var3 + 1, var4 + var15);

				if (var18 && var19)
				{
					var11 = Direction.rotateOpposite[var11];
					var13 = Direction.rotateOpposite[var13];
					var14 = Direction.offsetX[var11];
					var15 = Direction.offsetZ[var11];
					var16 = Direction.offsetX[var13];
					var17 = Direction.offsetZ[var13];
					var8 = var2 - var16;
					var9 -= (double)var16;
					int var20 = var4 - var17;
					var7 -= (double)var17;
					var18 = !worldObj.isAirBlock(var8 + var14 + var16, var3, var20 + var15 + var17) || !worldObj.isAirBlock(var8 + var14 + var16, var3 + 1, var20 + var15 + var17);
					var19 = !worldObj.isAirBlock(var8 + var14, var3, var20 + var15) || !worldObj.isAirBlock(var8 + var14, var3 + 1, var20 + var15);
				}

				float var20 = 0.5F;
				float var21 = 0.5F;

				if (!var18 && var19)
				{
					var20 = 1.0F;
				}
				else if (var18 && !var19)
				{
					var20 = 0.0F;
				}
				else if (var18 && var19)
				{
					var21 = 0.0F;
				}

				var9 += (double)((float)var16 * var20 + var21 * (float)var14);
				var7 += (double)((float)var17 * var20 + var21 * (float)var15);
				float var23 = 0.0F;
				float var24 = 0.0F;
				float var25 = 0.0F;
				float var26 = 0.0F;

				if (var11 == var12)
				{
					var23 = 1.0F;
					var24 = 1.0F;
				}
				else if (var11 == Direction.rotateOpposite[var12])
				{
					var23 = -1.0F;
					var24 = -1.0F;
				}
				else if (var11 == Direction.rotateRight[var12])
				{
					var25 = 1.0F;
					var26 = -1.0F;
				}
				else
				{
					var25 = -1.0F;
					var26 = 1.0F;
				}

				double var27 = entity.motionX;
				double var28 = entity.motionZ;
				entity.motionX = var27 * (double)var23 + var28 * (double)var26;
				entity.motionZ = var27 * (double)var25 + var28 * (double)var24;
				entity.rotationYaw = rotationYaw - (float)(var12 * 90) + (float)(var11 * 90);
			}
			else
			{
				entity.motionX = entity.motionY = entity.motionZ = 0.0D;
			}

			entity.setLocationAndAngles(var9, var10, var7, entity.rotationYaw, entity.rotationPitch);

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
		double var1 = -1.0D;
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY);
		int z = MathHelper.floor_double(entity.posZ);
		int var2 = x;
		int var3 = y;
		int var4 = z;
		int var5 = 0;
		int var6 = random.nextInt(4);
		int var7;
		double var8;
		double var9;
		int var10;
		int var11;
		int var12;
		int var13;
		int var14;
		int var15;
		int var16;
		int var17;
		int var18;
		int var19;
		double var20;
		double var21;

		for (var7 = x - 16; var7 <= x + 16; ++var7)
		{
			var8 = (double)var7 + 0.5D - entity.posX;

			for (var10 = z - 16; var10 <= z + 16; ++var10)
			{
				var9 = (double)var10 + 0.5D - entity.posZ;
				label1:

				for (var11 = 126; var11 >= 0; --var11)
				{
					if (worldObj.isAirBlock(var7, var11, var10))
					{
						while (var11 > 0 && worldObj.isAirBlock(var7, var11 - 1, var10))
						{
							--var11;
						}

						for (var13 = var6; var13 < var6 + 4; ++var13)
						{
							var12 = var13 % 2;
							var15 = 1 - var12;

							if (var13 % 4 >= 2)
							{
								var12 = -var12;
								var15 = -var15;
							}

							for (var14 = 0; var14 < 3; ++var14)
							{
								for (var17 = 0; var17 < 4; ++var17)
								{
									for (var16 = -1; var16 < 4; ++var16)
									{
										var19 = var7 + (var17 - 1) * var12 + var14 * var15;
										var18 = var11 + var16;
										int var22 = var10 + (var17 - 1) * var15 - var14 * var12;

										if (var16 < 0 && !worldObj.getBlockMaterial(var19, var18, var22).isSolid() || var16 >= 0 && !worldObj.isAirBlock(var19, var18, var22))
										{
											continue label1;
										}
									}
								}
							}

							var21 = (double)var11 + 0.5D - entity.posY;
							var20 = var8 * var8 + var21 * var21 + var9 * var9;

							if (var1 < 0.0D || var20 < var1)
							{
								var1 = var20;
								var2 = var7;
								var3 = var11;
								var4 = var10;
								var5 = var13 % 4;
							}
						}
					}
				}
			}
		}

		if (var1 < 0.0D)
		{
			for (var7 = x - 16; var7 <= x + 16; ++var7)
			{
				var8 = (double)var7 + 0.5D - entity.posX;

				for (var10 = z - 16; var10 <= z + 16; ++var10)
				{
					var9 = (double)var10 + 0.5D - entity.posZ;
					label2:

					for (var11 = 126; var11 >= 0; --var11)
					{
						if (worldObj.isAirBlock(var7, var11, var10))
						{
							while (var11 > 0 && worldObj.isAirBlock(var7, var11 - 1, var10))
							{
								--var11;
							}

							for (var13 = var6; var13 < var6 + 2; ++var13)
							{
								var12 = var13 % 2;
								var15 = 1 - var12;

								for (var14 = 0; var14 < 4; ++var14)
								{
									for (var17 = -1; var17 < 4; ++var17)
									{
										var16 = var7 + (var14 - 1) * var12;
										var19 = var11 + var17;
										var18 = var10 + (var14 - 1) * var15;

										if (var17 < 0 && !worldObj.getBlockMaterial(var16, var19, var18).isSolid() || var17 >= 0 && !worldObj.isAirBlock(var16, var19, var18))
										{
											continue label2;
										}
									}
								}

								var21 = (double)var11 + 0.5D - entity.posY;
								var20 = var8 * var8 + var21 * var21 + var9 * var9;

								if (var1 < 0.0D || var20 < var1)
								{
									var1 = var20;
									var2 = var7;
									var3 = var11;
									var4 = var10;
									var5 = var13 % 2;
								}
							}
						}
					}
				}
			}
		}

		int var22 = var2;
		int var23 = var3;
		var10 = var4;
		int var24 = var5 % 2;
		int var25 = 1 - var24;

		if (var5 % 4 >= 2)
		{
			var24 = -var24;
			var25 = -var25;
		}

		boolean var26;

		if (var1 < 0.0D)
		{
			if (var3 < 30)
			{
				var3 = 30;
			}

			if (var3 > 115)
			{
				var3 = 115;
			}

			var23 = var3;

			for (var11 = -1; var11 <= 1; ++var11)
			{
				for (var13 = 1; var13 < 3; ++var13)
				{
					for (var12 = -1; var12 < 3; ++var12)
					{
						var15 = var22 + (var13 - 1) * var24 + var11 * var25;
						var14 = var23 + var12;
						var17 = var10 + (var13 - 1) * var25 - var11 * var24;
						var26 = var12 < 0;

						worldObj.setBlock(var15, var14, var17, var26 ? Block.cobblestoneMossy.blockID : 0);
					}
				}
			}
		}

		boolean var27 = false;

		for (var11 = 0; var11 < 4; ++var11)
		{
			for (var13 = 0; var13 < 4; ++var13)
			{
				for (var12 = -1; var12 < 4; ++var12)
				{
					var15 = var22 + (var13 - 1) * var24;
					var14 = var23 + var12;
					var17 = var10 + (var13 - 1) * var25;
					var26 = var13 == 0 || var13 == 3 || var12 == -1 || var12 == 3;

					if (var12 < 0 && worldObj.getBlockId(var15, var14, var17) == Block.bedrock.blockID)
					{
						var27 = true;
					}

					if (var27)
					{
						++var14;
					}

					worldObj.setBlock(var15, var14, var17, var26 ? Block.cobblestoneMossy.blockID : Caveworld.portalCaveworld.blockID, 0, 2);
				}
			}

			for (var13 = 0; var13 < 4; ++var13)
			{
				for (var12 = -1; var12 < 4; ++var12)
				{
					var15 = var22 + (var13 - 1) * var24;
					var14 = var23 + var12;
					var17 = var10 + (var13 - 1) * var25;

					if (var27)
					{
						++var14;
					}

					worldObj.notifyBlocksOfNeighborChange(var15, var14, var17, worldObj.getBlockId(var15, var14, var17));
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