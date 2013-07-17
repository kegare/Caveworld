package kegare.caveworld.block;

import java.util.Random;

import kegare.caveworld.core.Caveworld;
import kegare.caveworld.core.Config;
import kegare.caveworld.core.TeleporterCaveworld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortalCaveworld extends BlockPortal
{
	public BlockPortalCaveworld(int blockID, String name)
	{
		super(blockID);
		this.setUnlocalizedName(name);
		this.func_111022_d("caveworld:portal_caveworld");
		this.setLightValue(0.5F);
		this.setHardness(-1.0F);
		this.setResistance(5000000.0F);
		this.setStepSound(Block.soundGlassFootstep);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(func_111023_E());
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		//NOOP
	}

	@Override
	public boolean tryToCreatePortal(World world, int x, int y, int z)
	{
		byte var1 = 0;
		byte var2 = 0;

		if (world.getBlockId(x - 1, y, z) == Block.cobblestoneMossy.blockID || world.getBlockId(x + 1, y, z) == Block.cobblestoneMossy.blockID)
		{
			var1 = 1;
		}

		if (world.getBlockId(x, y, z - 1) == Block.cobblestoneMossy.blockID || world.getBlockId(x, y, z + 1) == Block.cobblestoneMossy.blockID)
		{
			var2 = 1;
		}

		if (var1 == var2)
		{
			return false;
		}
		else
		{
			if (world.isAirBlock(x - var1, y, z - var2))
			{
				x -= var1;
				z -= var2;
			}

			int var3;
			int var4;

			for (var3 = -1; var3 <= 2; ++var3)
			{
				for (var4 = -1; var4 <= 3; ++var4)
				{
					if (var3 != -1 && var3 != 2 || var4 != -1 && var4 != 3)
					{
						if (var3 == -1 || var3 == 2 || var4 == -1 || var4 == 3)
						{
							if (world.getBlockId(x + var1 * var3, y + var4, z + var2 * var3) != Block.cobblestoneMossy.blockID)
							{
								return false;
							}
						}
						else if (!world.isAirBlock(x + var1 * var3, y + var4, z + var2 * var3))
						{
							return false;
						}
					}
				}
			}

			for (var3 = 0; var3 < 2; ++var3)
			{
				for (var4 = 0; var4 < 3; ++var4)
				{
					world.setBlock(x + var1 * var3, y + var4, z + var2 * var3, blockID, 0, 2);
				}
			}

			return true;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId)
	{
		byte var1 = 0;
		byte var2 = 1;

		if (world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID)
		{
			var1 = 1;
			var2 = 0;
		}

		int var3;

		for (var3 = y; world.getBlockId(x, var3 - 1, z) == blockID; --var3)
		{
			;
		}

		if (world.getBlockId(x, var3 - 1, z) != Block.cobblestoneMossy.blockID)
		{
			world.setBlockToAir(x, y, z);
		}
		else
		{
			int var4;

			for (var4 = 1; var4 < 4 && world.getBlockId(x, var3 + var4, z) == blockID; ++var4)
			{
				;
			}

			if (var4 == 3 && world.getBlockId(x, var3 + var4, z) == Block.cobblestoneMossy.blockID)
			{
				boolean var5 = world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID;
				boolean var6 = world.getBlockId(x, y, z - 1) == blockID || world.getBlockId(x, y, z + 1) == blockID;

				if (var5 && var6)
				{
					world.setBlockToAir(x, y, z);
				}
				else
				{
					if ((world.getBlockId(x + var1, y, z + var2) != Block.cobblestoneMossy.blockID || world.getBlockId(x - var1, y, z - var2) != blockID) && (world.getBlockId(x - var1, y, z - var2) != Block.cobblestoneMossy.blockID || world.getBlockId(x + var1, y, z + var2) != blockID))
					{
						world.setBlockToAir(x, y, z);
					}
				}
			}
			else
			{
				world.setBlockToAir(x, y, z);
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!world.isRemote && entity.isEntityAlive() && !entity.isRiding() && !entity.isSneaking())
		{
			if (entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;

				if (player.timeUntilPortal == 0)
				{
					MinecraftServer server = player.mcServer;
					int dimension = player.dimension == 0 ? Config.dimensionCaveworld : 0;

					server.getConfigurationManager().transferPlayerToDimension(player, dimension, new TeleporterCaveworld(server.worldServerForDimension(dimension)));
					player.addExperienceLevel(0);

					player.timeUntilPortal = 10;
				}
				else
				{
					player.timeUntilPortal = 10;
				}
			}
			else
			{
				if (entity.timeUntilPortal == 0)
				{
					MinecraftServer server = Caveworld.proxy.getServer();
					int dimensionOld = entity.dimension;
					int dimensionNew = entity.dimension == 0 ? Config.dimensionCaveworld : 0;
					WorldServer worldOld = server.worldServerForDimension(dimensionOld);
					WorldServer worldNew = server.worldServerForDimension(dimensionNew);

					entity.dimension = dimensionNew;
					worldOld.removeEntity(entity);

					entity.isDead = false;

					server.getConfigurationManager().transferEntityToWorld(entity, dimensionOld, worldOld, worldNew, new TeleporterCaveworld(worldNew));

					Entity target = EntityList.createEntityByName(EntityList.getEntityString(entity), worldNew);

					if (target != null)
					{
						target.copyDataFrom(entity, true);
						worldNew.spawnEntityInWorld(target);

						target.timeUntilPortal = 10;
					}

					entity.isDead = true;

					worldOld.resetUpdateEntityTick();
					worldNew.resetUpdateEntityTick();
				}
				else
				{
					entity.timeUntilPortal = 10;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		if (random.nextInt(200) == 0)
		{
			world.playSound((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "ambient.cave.cave", 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
		}

		if (random.nextInt(6) == 0)
		{
			double ptX = (double)((float)x + random.nextFloat());
			double ptY = (double)((float)y + 0.8F);
			double ptZ = (double)((float)z + random.nextFloat());

			world.spawnParticle("smoke", ptX, ptY, ptZ, 0.0D, 0.0D, 0.0D);
		}
	}
}