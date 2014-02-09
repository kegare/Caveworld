package com.kegare.caveworld.block;

import java.io.File;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.inventory.InventoryCaveworldPortal;
import com.kegare.caveworld.renderer.RenderPortalCaveworld;
import com.kegare.caveworld.world.TeleporterCaveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortalCaveworld extends BlockPortal
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

				File file = new File(dir, "caveworld_portal.dat");

				if (file.exists() && file.canRead())
				{
					inventory.loadInventoryFromNBT((NBTTagList)CompressedStreamTools.read(file).getTag("PortalItems"));
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
				NBTTagCompound data = new NBTTagCompound();
				File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), "data");

				if (!dir.exists())
				{
					dir.mkdirs();
				}

				data.setTag("PortalItems", inventory.saveInventoryToNBT());

				CompressedStreamTools.write(data, new File(dir, "caveworld_portal.dat"));
			}
			catch (Exception ignored) {}
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon portalIcon;

	public BlockPortalCaveworld(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:portal_caveworld");
		this.setBlockUnbreakable();
		this.setLightOpacity(3);
		this.setLightLevel(0.6F);
		this.setStepSound(soundTypeGlass);
		this.disableStats();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(getTextureName());
		portalIcon = iconRegister.registerIcon("caveworld:caveworld_portal");
	}

	@Override
	public int getRenderType()
	{
		return RenderPortalCaveworld.renderIdPortal;
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		int metadata = func_149999_b(world.getBlockMetadata(x, y, z));
		Size size1 = new Size(world, x, y, z, 1);
		Size size2 = new Size(world, x, y, z, 2);

		if (metadata == 1 && (!size1.func_150860_b() || size1.portalBlockCount < size1.portalWidth * size1.portalHeight))
		{
			world.setBlock(x, y, z, Blocks.air);
		}
		else if (metadata == 2 && (!size2.func_150860_b() || size2.portalBlockCount < size2.portalWidth * size2.portalHeight))
		{
			world.setBlock(x, y, z, Blocks.air);
		}
		else if (metadata == 0 && !size1.func_150860_b() && !size2.func_150860_b())
		{
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public boolean func_150000_e(World world, int x, int y, int z)
	{
		Size size1 = new Size(world, x, y, z, 1);
		Size size2 = new Size(world, x, y, z, 2);

		if (size1.func_150860_b() && size1.portalBlockCount == 0)
		{
			size1.setPortalBlocks();

			return true;
		}
		else if (size2.func_150860_b() && size2.portalBlockCount == 0)
		{
			size2.setPortalBlocks();

			return true;
		}

		return false;
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
		if (!world.isRemote && world.provider.dimensionId == 0 && world.getGameRules().getGameRuleBooleanValue("doMobSpawning") && !world.isDaytime() && random.nextInt(300) < world.difficultySetting.getDifficultyId())
		{
			while (!world.doesBlockHaveSolidTopSurface(world, x, y, z) && y > 0)
			{
				--y;
			}

			if (y > 0 && !world.getBlock(x, y + 1, z).isBlockNormalCube())
			{
				Entity entity = ItemMonsterPlacer.spawnCreature(world, 65, x + 0.5D, y + 1.0D, z + 0.5D);

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
			world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "ambient.cave.cave", 0.25F, random.nextFloat() * 0.4F + 0.8F, false);
		}

		if (random.nextInt(3) == 0)
		{
			double ptX = x + random.nextFloat();
			double ptY = y + 0.5D;
			double ptZ = z + random.nextFloat();
			EntityFX entityFX = new EntityReddustFX(world, ptX, ptY, ptZ, 0.5F, 1.0F, 1.0F);

			Caveworld.proxy.addEffect(entityFX);
		}
	}

	public static class Size
	{
		private final World worldObj;
		private final int blockMetadata;
		private final int field_150863_d;
		private final int field_150866_c;

		private ChunkCoordinates portalCoord;
		private int portalWidth;
		private int portalHeight;

		private int portalBlockCount = 0;

		public Size(World world, int x, int y, int z, int metadata)
		{
			this.worldObj = world;
			this.blockMetadata = metadata;
			this.field_150863_d = BlockPortal.field_150001_a[metadata][0];
			this.field_150866_c = BlockPortal.field_150001_a[metadata][1];

			for (int i = y; y > i - 21 && y > 0 && func_150857_a(world.getBlock(x, y - 1, z)); --y)
			{
				;
			}

			int var1 = func_150853_a(x, y, z, field_150863_d) - 1;

			if (var1 >= 0)
			{
				this.portalCoord = new ChunkCoordinates(x + var1 * Direction.offsetX[field_150863_d], y, z + var1 * Direction.offsetZ[field_150863_d]);
				this.portalWidth = func_150853_a(portalCoord.posX, portalCoord.posY, portalCoord.posZ, field_150866_c);

				if (portalWidth < 2 || portalWidth > 21)
				{
					this.portalCoord = null;
					this.portalWidth = 0;
				}
			}

			if (portalCoord != null)
			{
				this.portalHeight = func_150858_a();
			}
		}

		protected int func_150853_a(int x, int y, int z, int par4)
		{
			int var1 = Direction.offsetX[par4];
			int var2 = Direction.offsetZ[par4];
			int i;

			for (i = 0; i < 22; ++i)
			{
				if (!func_150857_a(worldObj.getBlock(x + var1 * i, y, z + var2 * i)))
				{
					break;
				}

				if (worldObj.getBlock(x + var1 * i, y - 1, z + var2 * i) != Blocks.mossy_cobblestone)
				{
					break;
				}
			}

			return worldObj.getBlock(x + var1 * i, y, z + var2 * i) == Blocks.mossy_cobblestone ? i : 0;
		}

		protected int func_150858_a()
		{
			int i, j, k, l;

			label:
			for (portalHeight = 0; portalHeight < 21; ++portalHeight)
			{
				i = portalCoord.posY + portalHeight;

				for (j = 0; j < portalWidth; ++j)
				{
					k = portalCoord.posX + j * Direction.offsetX[field_150866_c];
					l = portalCoord.posZ + j * Direction.offsetZ[field_150866_c];
					Block block = worldObj.getBlock(k, i, l);

					if (!func_150857_a(block))
					{
						break label;
					}

					if (block == CaveBlocks.caveworld_portal)
					{
						++portalBlockCount;
					}

					if (j == 0)
					{
						block = worldObj.getBlock(k + Direction.offsetX[field_150863_d], i, l + Direction.offsetZ[field_150863_d]);

						if (block != Blocks.mossy_cobblestone)
						{
							break label;
						}
					}
					else if (j == portalWidth - 1)
					{
						block = worldObj.getBlock(k + Direction.offsetX[field_150866_c], i, l + Direction.offsetZ[field_150866_c]);

						if (block != Blocks.mossy_cobblestone)
						{
							break label;
						}
					}
				}
			}

			for (i = 0; i < portalWidth; ++i)
			{
				j = portalCoord.posX + i * Direction.offsetX[field_150866_c];
				k = portalCoord.posY + portalHeight;
				l = portalCoord.posZ + i * Direction.offsetZ[field_150866_c];

				if (worldObj.getBlock(j, k, l) != Blocks.mossy_cobblestone)
				{
					portalHeight = 0;

					break;
				}
			}

			if (portalHeight <= 21 && portalHeight >= 3)
			{
				return portalHeight;
			}
			else
			{
				portalCoord = null;
				portalWidth = 0;
				portalHeight = 0;

				return 0;
			}
		}

		protected boolean func_150857_a(Block block)
		{
			return block.getMaterial() == Material.air || block == CaveBlocks.caveworld_portal;
		}

		public boolean func_150860_b()
		{
			if (portalCoord != null && portalWidth >= 2 && portalWidth <= 21 && portalHeight >= 3 && portalHeight <= 21)
			{
				for (int i = 0; i < portalWidth; ++i)
				{
					int x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
					int z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

					for (int j = 0; j < portalHeight; ++j)
					{
						if (blockMetadata == 1)
						{
							if (worldObj.getBlock(x, portalCoord.posY + j, z + 1) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
							else if (worldObj.getBlock(x, portalCoord.posY + j, z - 1) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
						}
						else if (blockMetadata == 2)
						{
							if (worldObj.getBlock(x + 1, portalCoord.posY + j, z) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
							else if (worldObj.getBlock(x - 1, portalCoord.posY + j, z) == CaveBlocks.caveworld_portal)
							{
								return false;
							}
						}
					}
				}

				return true;
			}

			return false;
		}

		public void setPortalBlocks()
		{
			for (int i = 0; i < portalWidth; ++i)
			{
				int x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
				int z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

				for (int j = 0; j < portalHeight; ++j)
				{
					worldObj.setBlock(x, portalCoord.posY + j, z, CaveBlocks.caveworld_portal, blockMetadata, 2);
				}
			}
		}
	}
}