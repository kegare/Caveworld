package kegare.caveworld.block;

import com.google.common.io.Files;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kegare.caveworld.core.Caveworld;
import kegare.caveworld.core.Config;
import kegare.caveworld.inventory.InventoryCaveworldPortal;
import kegare.caveworld.renderer.RenderPortalCaveworld;
import kegare.caveworld.world.TeleporterCaveworld;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Random;

public class BlockPortalCaveworld extends Block
{
	private static InventoryCaveworldPortal inventory;

	public static InventoryCaveworldPortal getInventory()
	{
		if (inventory == null)
		{
			inventory = new InventoryCaveworldPortal();

			try
			{
				File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "data");

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				File file = new File(dir, "caveworld_portal_inventory.dat");
				DataInputStream dis = new DataInputStream(Files.newInputStreamSupplier(file).getInput());

				if (file.exists() && file.canRead())
				{
					inventory.loadInventoryFromNBT((NBTTagList)NBTBase.readNamedTag(dis));

					dis.close();
				}
			}
			catch (Exception ignored) {}
		}

		return inventory;
	}

	public static void saveInventoryData()
	{
		if (inventory != null)
		{
			try
			{
				File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "data");

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				File file = new File(dir, "caveworld_portal_inventory.dat");
				DataOutputStream dos = new DataOutputStream(Files.newOutputStreamSupplier(file).getOutput());

				NBTBase.writeNamedTag(inventory.saveInventoryToNBT(), dos);

				dos.close();
				dos.flush();
			}
			catch (Exception ignored) {}
		}
	}

	@SideOnly(Side.CLIENT)
	public Icon portalIcon;

	public BlockPortalCaveworld(int blockID, String name)
	{
		super(blockID, Material.portal);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:portal_caveworld");
		this.setBlockUnbreakable();
		this.setLightOpacity(3);
		this.setLightValue(0.6F);
		this.setStepSound(soundGlassFootstep);
		this.setTickRandomly(true);
		this.disableStats();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(getTextureName());
		portalIcon = iconRegister.registerIcon("caveworld:caveworld_portal");
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return RenderPortalCaveworld.renderIdPortal;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		if (world.getBlockId(x, y, z) == blockID)
		{
			return false;
		}
		else
		{
			boolean var1 = world.getBlockId(x - 1, y, z) == blockID && world.getBlockId(x - 2, y, z) != blockID;
			boolean var2 = world.getBlockId(x + 1, y, z) == blockID && world.getBlockId(x + 2, y, z) != blockID;
			boolean var3 = world.getBlockId(x, y, z - 1) == blockID && world.getBlockId(x, y, z - 2) != blockID;
			boolean var4 = world.getBlockId(x, y, z + 1) == blockID && world.getBlockId(x, y, z + 2) != blockID;
			boolean var5 = var1 || var2;
			boolean var6 = var3 || var4;

			return var5 && side == 4 || (var5 && side == 5 || (var6 && side == 2 || var6 && side == 3));
		}
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}

	@Override
	public int quantityDropped(int metadata, int fortune, Random random)
	{
		return 0;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		if (world.getBlockId(x - 1, y, z) != blockID && world.getBlockId(x + 1, y, z) != blockID)
		{
			setBlockBounds(0.35F, 0.0F, 0.0F, 0.65F, 1.0F, 1.0F);
		}
		else
		{
			setBlockBounds(0.0F, 0.0F, 0.35F, 1.0F, 1.0F, 0.65F);
		}
	}

	public boolean tryToCreatePortal(World world, int x, int y, int z)
	{
		if (world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID || world.getBlockId(x, y, z - 1) == blockID || world.getBlockId(x, y, z + 1) == blockID)
		{
			return false;
		}

		byte var1 = 0;
		byte var2 = 1;

		if (world.getBlockId(x - 1, y, z) == Block.cobblestoneMossy.blockID || world.getBlockId(x + 1, y, z) == Block.cobblestoneMossy.blockID)
		{
			var1 = 1;
			var2 = 0;
		}

		if (world.isAirBlock(x - var1, y, z - var2))
		{
			x -= var1;
			z -= var2;
		}

		byte var3;
		byte var4;

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
				}
			}
		}

		for (var3 = 0; var3 < 2; ++var3)
		{
			for (var4 = 0; var4 < 3; ++var4)
			{
				if (!world.isAirBlock(x + var1 * var3, y + var4, z + var2 * var3))
				{
					return false;
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

		int var3 = y;

		while (world.getBlockId(x, var3 - 1, z) == blockID)
		{
			--var3;
		}

		if (world.getBlockId(x, var3 - 1, z) != Block.cobblestoneMossy.blockID)
		{
			world.setBlockToAir(x, y, z);
		}
		else
		{
			int var4 = 1;

			while (var4 < 4 && world.getBlockId(x, var3 + var4, z) == blockID)
			{
				++var4;
			}

			if (var4 == 3 && world.getBlockId(x, var3 + var4, z) == Block.cobblestoneMossy.blockID)
			{
				if ((world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID) && (world.getBlockId(x, y, z - 1) == blockID || world.getBlockId(x, y, z + 1) == blockID))
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
		if (!world.isRemote && entity.isEntityAlive() && entity.riddenByEntity == null && entity.ridingEntity == null)
		{
			MinecraftServer server = Caveworld.proxy.getServer();
			int dimOld = entity.dimension;
			int dimNew = dimOld == 0 ? Config.dimensionCaveworld : 0;
			WorldServer worldOld = server.worldServerForDimension(dimOld);
			WorldServer worldNew = server.worldServerForDimension(dimNew);
			Teleporter teleporter = new TeleporterCaveworld(worldNew);

			if (entity.timeUntilPortal <= 0 && (dimOld == 0 || dimOld == Config.dimensionCaveworld))
			{
				if (entity instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entity;

					if (!player.isSneaking() && !player.isPotionActive(Potion.confusion))
					{
						worldOld.playSoundToNearExcept(player, "caveworld:caveworld_portal", 0.5F, 1.0F);

						server.getConfigurationManager().transferPlayerToDimension(player, dimNew, teleporter);

						player.addExperienceLevel(0);
						player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 120));
						player.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 20));

						worldNew.playSoundAtEntity(player, "caveworld:caveworld_portal", 0.75F, 1.0F);

						player.timeUntilPortal = player.getPortalCooldown();
					}
				}
				else
				{
					entity.dimension = dimNew;
					server.getConfigurationManager().transferEntityToWorld(entity, dimOld, worldOld, worldNew, teleporter);

					Entity target = EntityList.createEntityByID(EntityList.getEntityID(entity), worldNew);

					if (target != null)
					{
						target.copyDataFrom(entity, true);
						target.isDead = false;
						target.forceSpawn = true;
						target.timeUntilPortal = target.getPortalCooldown();

						worldNew.spawnEntityInWorld(target);
						worldNew.updateEntity(target);
					}

					entity.setDead();

					worldOld.resetUpdateEntityTick();
					worldNew.resetUpdateEntityTick();
				}
			}
			else
			{
				entity.timeUntilPortal = entity.getPortalCooldown();
			}
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote && world.provider.dimensionId == 0 && !world.isDaytime() && random.nextInt(300) < world.difficultySetting)
		{
			while (!world.doesBlockHaveSolidTopSurface(x, y, z) && y > 0)
			{
				--y;
			}

			if (y > 0 && !world.isBlockNormalCube(x, y + 1, z))
			{
				Entity entity = ItemMonsterPlacer.spawnCreature(world, 65, (double)x + 0.5D, (double)y + 1.0D, (double)z + 0.5D);

				if (entity != null)
				{
					entity.timeUntilPortal = entity.getPortalCooldown();
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		if (random.nextInt(200) == 0)
		{
			world.playSound((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "ambient.cave.cave", 0.25F, random.nextFloat() * 0.4F + 0.8F, false);
		}

		if (random.nextInt(3) == 0)
		{
			double ptX = (double)((float)x + random.nextFloat());
			double ptY = (double)y + 0.5D;
			double ptZ = (double)((float)z + random.nextFloat());
			EntityFX entityFX = new EntityReddustFX(world, ptX, ptY, ptZ, 0.5F, 1.0F, 1.0F);

			Caveworld.proxy.addEffect(entityFX);
		}
	}
}