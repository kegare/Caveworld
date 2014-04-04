/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package com.kegare.caveworld.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.inventory.InventoryCaveworldPortal;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.world.TeleporterCaveworld;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPortalCaveworld extends BlockPortal
{
	public final InventoryCaveworldPortal inventory = new InventoryCaveworldPortal();

	@SideOnly(Side.CLIENT)
	public IIcon portalIcon;

	public BlockPortalCaveworld(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("caveworld:caveworld_portal");
		this.setBlockUnbreakable();
		this.setLightOpacity(3);
		this.setLightLevel(0.6F);
		this.setStepSound(soundTypeGlass);
		this.disableStats();
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(getTextureName() + "_block");
		portalIcon = iconRegister.registerIcon(getTextureName());
	}

	@Override
	public int getRenderType()
	{
		return Config.RENDER_TYPE_PORTAL;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int metadata = func_149999_b(world.getBlockMetadata(x, y, z));

		if (metadata == 0)
		{
			if (world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this)
			{
				metadata = 2;
			}
			else
			{
				metadata = 1;
			}

			if (world instanceof World && !((World)world).isRemote)
			{
				((World)world).setBlockMetadataWithNotify(x, y, z, metadata, 2);
			}
		}

		float var1 = 0.15F;
		float var2 = 0.15F;

		if (metadata == 1)
		{
			var1 = 0.5F;
		}
		else if (metadata == 2)
		{
			var2 = 0.5F;
		}

		setBlockBounds(0.5F - var1, 0.0F, 0.5F - var2, 0.5F + var1, 1.0F, 0.5F + var2);
	}

	@Override
	public boolean func_150000_e(World world, int x, int y, int z)
	{
		if (world.provider.dimensionId == 1)
		{
			world.newExplosion(null, x, y, z, 4.5F, true, true);

			return false;
		}

		Size size1 = new Size(world, x, y, z, 1);
		Size size2 = new Size(world, x, y, z, 2);

		if (size1.canCreatePortal() && size1.portalBlockCount == 0)
		{
			size1.setPortalBlocks();

			return true;
		}
		else if (size2.canCreatePortal() && size2.portalBlockCount == 0)
		{
			size2.setPortalBlocks();

			return true;
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		int metadata = func_149999_b(world.getBlockMetadata(x, y, z));
		Size size1 = new Size(world, x, y, z, 1);
		Size size2 = new Size(world, x, y, z, 2);

		if (metadata == 1 && (!size1.canCreatePortal() || size1.portalBlockCount < size1.portalWidth * size1.portalHeight))
		{
			world.setBlock(x, y, z, Blocks.air);
		}
		else if (metadata == 2 && (!size2.canCreatePortal() || size2.portalBlockCount < size2.portalWidth * size2.portalHeight))
		{
			world.setBlock(x, y, z, Blocks.air);
		}
		else if (metadata == 0 && !size1.canCreatePortal() && !size2.canCreatePortal())
		{
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote && side >= 2)
		{
			world.playSoundAtEntity(player, "random.click", 0.8F, 1.5F);

			player.displayGUIChest(inventory.setPortalPosition(x, y, z));
		}

		return true;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!world.isRemote && entity.isEntityAlive() && (entity.dimension != Config.dimensionCaveworld || !Config.hardcoreEnabled))
		{
			if (entity.timeUntilPortal <= 0)
			{
				MinecraftServer server = Caveworld.proxy.getServer();
				int dimOld = entity.dimension;
				int dimNew = dimOld == Config.dimensionCaveworld ? entity.getEntityData().getInteger("Caveworld:LastDim") : Config.dimensionCaveworld;
				WorldServer worldOld = server.worldServerForDimension(dimOld);
				WorldServer worldNew = server.worldServerForDimension(dimNew);
				Teleporter teleporter = new TeleporterCaveworld(worldNew);

				entity.worldObj.removeEntity(entity);
				entity.isDead = false;

				if (entity instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entity;

					if (!player.isSneaking() && !player.isPotionActive(Potion.confusion))
					{
						player.closeScreen();

						worldOld.playSoundToNearExcept(player, "caveworld:caveworld_portal", 0.5F, 1.0F);

						server.getConfigurationManager().transferPlayerToDimension(player, dimNew, teleporter);

						player.addExperienceLevel(0);
						player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 120));
						player.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 20));

						worldNew.playSoundAtEntity(player, "caveworld:caveworld_portal", 0.75F, 1.0F);

						player.getEntityData().setInteger("Caveworld:LastDim", dimOld);
					}

					player.timeUntilPortal = player.getPortalCooldown();
				}
				else
				{
					entity.dimension = dimNew;

					server.getConfigurationManager().transferEntityToWorld(entity, dimOld, worldOld, worldNew, teleporter);

					Entity target = EntityList.createEntityByName(EntityList.getEntityString(entity), worldNew);

					if (target != null)
					{
						worldOld.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "caveworld:caveworld_portal", 0.25F, 1.15F);

						target.copyDataFrom(entity, true);
						target.forceSpawn = true;
						target.timeUntilPortal = target.getPortalCooldown();

						worldNew.spawnEntityInWorld(target);
						worldNew.playSoundAtEntity(target, "caveworld:caveworld_portal", 0.5F, 1.15F);

						target.forceSpawn = false;
						target.getEntityData().setInteger("Caveworld:LastDim", dimOld);
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
		if (!world.isRemote && world.provider.isSurfaceWorld())
		{
			if (world.isDaytime() || !world.getGameRules().getGameRuleBooleanValue("doMobSpawning"))
			{
				return;
			}
			else if (!world.isBlockNormalCubeDefault(x, y + 1, z, false) && random.nextInt(300) < world.difficultySetting.getDifficultyId())
			{
				EntityLiving entity = CaveUtils.createEntity(EntityBat.class, world);

				if (entity != null)
				{
					entity.setLocationAndAngles(x + 0.5D, y + 0.75D, z + 0.5D, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
					entity.rotationYawHead = entity.renderYawOffset = entity.rotationYaw;
					entity.timeUntilPortal = entity.getPortalCooldown();
					entity.onSpawnWithEgg(null);

					if (world.spawnEntityInWorld(entity))
					{
						entity.playLivingSound();
						entity.getEntityData().setBoolean("Caveworld:CaveBat", true);
					}
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

			world.spawnParticle("reddust", ptX, ptY, ptZ, 0.5D, 1.0D, 1.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return Item.getItemFromBlock(this);
	}

	public static class Size
	{
		private final World worldObj;
		private final int portalMetadata;
		private final int field_150863_d;
		private final int field_150866_c;

		private ChunkCoordinates portalCoord;
		private int portalWidth;
		private int portalHeight;

		private int portalBlockCount = 0;

		public Size(World world, int x, int y, int z, int metadata)
		{
			this.worldObj = world;
			this.portalMetadata = metadata;
			this.field_150863_d = BlockPortal.field_150001_a[metadata][0];
			this.field_150866_c = BlockPortal.field_150001_a[metadata][1];

			int i = y;

			while (y > i - 21 && y > 0 && isReplaceablePortal(world.getBlock(x, y - 1, z)))
			{
				--y;
			}

			i = getPortalWidth(x, y, z, field_150863_d) - 1;

			if (i >= 0)
			{
				this.portalCoord = new ChunkCoordinates(x + i * Direction.offsetX[field_150863_d], y, z + i * Direction.offsetZ[field_150863_d]);
				this.portalWidth = getPortalWidth(portalCoord.posX, portalCoord.posY, portalCoord.posZ, field_150866_c);

				if (portalWidth < 2 || portalWidth > 21)
				{
					this.portalCoord = null;
					this.portalWidth = 0;
				}
			}

			if (portalCoord != null)
			{
				this.portalHeight = getPortalHeight();
			}
		}

		protected int getPortalWidth(int x, int y, int z, int par4)
		{
			int var1 = Direction.offsetX[par4];
			int var2 = Direction.offsetZ[par4];
			int i;

			for (i = 0; i < 22; ++i)
			{
				if (!isReplaceablePortal(worldObj.getBlock(x + var1 * i, y, z + var2 * i)))
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

		protected int getPortalHeight()
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

					if (!isReplaceablePortal(block))
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

		protected boolean isReplaceablePortal(Block block)
		{
			return block.getMaterial() == Material.air || block == CaveBlocks.caveworld_portal;
		}

		public boolean canCreatePortal()
		{
			if (portalCoord != null && portalWidth >= 2 && portalWidth <= 21 && portalHeight >= 3 && portalHeight <= 21)
			{
				for (int i = 0; i < portalWidth; ++i)
				{
					int x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
					int z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

					for (int j = 0; j < portalHeight; ++j)
					{
						if (portalMetadata == 1)
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
						else if (portalMetadata == 2)
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
					worldObj.setBlock(x, portalCoord.posY + j, z, CaveBlocks.caveworld_portal, portalMetadata, 2);
				}
			}
		}
	}
}