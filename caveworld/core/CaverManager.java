/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.core;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import caveworld.api.ICaverManager;
import caveworld.network.client.CaverAdjustMessage;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy2.api.MCEconomyAPI;

public class CaverManager implements ICaverManager
{
	public static final String CAVER_TAG = "Caveworld:Caver";

	private static final Map<String, NBTTagCompound> caverData = Maps.newHashMap();
	private static final Table<Block, Integer, Integer> pointAmounts = HashBasedTable.create();

	private Caver getCaver(Entity entity)
	{
		if (entity.getExtendedProperties(CAVER_TAG) == null)
		{
			entity.registerExtendedProperties(CAVER_TAG, new Caver(entity));
		}

		return (Caver)entity.getExtendedProperties(CAVER_TAG);
	}

	@Override
	public int getMiningPoint(Entity entity)
	{
		return getCaver(entity).getMiningPoint();
	}

	@Override
	public void setMiningPoint(Entity entity, int value)
	{
		getCaver(entity).setMiningPoint(value);
	}

	@Override
	public void addMiningPoint(Entity entity, int value)
	{
		getCaver(entity).addMiningPoint(value);
	}

	@Override
	public int getMiningPointAmount(Block block, int metadata)
	{
		return pointAmounts.contains(block, metadata) ? pointAmounts.get(block, metadata) : 0;
	}

	@Override
	public void setMiningPointAmount(Block block, int metadata, int amount)
	{
		pointAmounts.put(block, metadata, amount);
	}

	@Override
	public void setMiningPointAmount(String oredict, int amount)
	{
		ArrayList<ItemStack> ores = OreDictionary.getOres(oredict);

		if (!ores.isEmpty())
		{
			for (ItemStack entry : ores)
			{
				Block block = Block.getBlockFromItem(entry.getItem());
				int meta = entry.getItemDamage();

				if (block != Blocks.air && !pointAmounts.contains(block, meta))
				{
					setMiningPointAmount(block, meta, amount);
				}
			}
		}
	}

	@Override
	public void clearMiningPointAmounts()
	{
		pointAmounts.clear();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return getCaver(entity).getLastDimension();
	}

	@Override
	public void setLastDimension(Entity entity, int dimension)
	{
		getCaver(entity).setLastDimension(dimension);
	}

	@Override
	public int getCavernLastDimension(Entity entity)
	{
		return getCaver(entity).getCavernLastDimension();
	}

	@Override
	public void setCavernLastDimension(Entity entity, int dimension)
	{
		getCaver(entity).setCavernLastDimension(dimension);
	}

	@Override
	public void saveData(Entity entity, NBTTagCompound compound)
	{
		Caver caver = getCaver(entity);

		if (compound == null)
		{
			NBTTagCompound data = new NBTTagCompound();

			caver.saveNBTData(data);

			caverData.put(entity.getUniqueID().toString(), data);
		}
		else
		{
			caver.saveNBTData(compound);
		}
	}

	@Override
	public void loadData(Entity entity, NBTTagCompound compound)
	{
		Caver caver = getCaver(entity);

		if (compound == null)
		{
			caver.loadNBTData(caverData.remove(entity.getUniqueID().toString()));
		}
		else
		{
			caver.loadNBTData(compound);
		}

		caver.adjustData();
	}

	public static class Caver implements IExtendedEntityProperties
	{
		private final Entity entity;

		private int point;
		private int caveworld;
		private int cavern;

		public Caver(Entity entity)
		{
			this.entity = entity;
		}

		@Override
		public void saveNBTData(NBTTagCompound compound)
		{
			NBTTagCompound data = new NBTTagCompound();

			data.setInteger("MiningPoint", point);

			String tag = "LastDimension.";

			data.setInteger(tag + "Caveworld", caveworld);
			data.setInteger(tag + "Cavern", cavern);

			compound.setTag(CAVER_TAG, data);
		}

		@Override
		public void loadNBTData(NBTTagCompound compound)
		{
			if (compound == null || !compound.hasKey(CAVER_TAG))
			{
				return;
			}

			NBTTagCompound data = compound.getCompoundTag(CAVER_TAG);

			point = data.getInteger("MiningPoint");

			String tag = "LastDimension.";

			caveworld = data.getInteger(tag + "Caveworld");
			cavern = data.getInteger(tag + "Cavern");
		}

		@Override
		public void init(Entity entity, World world) {}

		public int getMiningPoint()
		{
			return point;
		}

		public void setMiningPoint(int value)
		{
			point = Math.max(value, 0);

			adjustData();
		}

		public void addMiningPoint(int value)
		{
			setMiningPoint(point + value);

			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				if (point > 0 && point % 100 == 0)
				{
					player.addExperience(player.xpBarCap() / 2);

					if (MCEconomyPlugin.enabled())
					{
						MCEconomyAPI.addPlayerMP(player, 10 + point / 100 - 1, false);
					}
				}

				if (point >= 1000)
				{
					player.triggerAchievement(CaveAchievementList.theMiner);
				}
			}
		}

		public void adjustData()
		{
			if (entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;

				Caveworld.network.sendTo(new CaverAdjustMessage(player), player);

				return;
			}

			if (!entity.worldObj.isRemote)
			{
				Caveworld.network.sendToDimension(new CaverAdjustMessage(entity), entity.dimension);
			}
		}

		public int getLastDimension()
		{
			return caveworld;
		}

		public void setLastDimension(int dim)
		{
			caveworld = dim;
		}

		public int getCavernLastDimension()
		{
			return cavern;
		}

		public void setCavernLastDimension(int dim)
		{
			cavern = dim;
		}
	}
}