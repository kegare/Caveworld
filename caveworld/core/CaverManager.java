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
import caveworld.item.CaveItems;
import caveworld.network.client.CaverAdjustMessage;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
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
	public int getRank(Entity entity)
	{
		return getCaver(entity).getRank();
	}

	@Override
	public void setRank(Entity entity, int rank)
	{
		getCaver(entity).setRank(rank);
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

	public enum MinerRank
	{
		BEGINNER(0, 0, "beginner", Items.wooden_pickaxe),
		STONE_MINER(1, 50, "stoneMiner", Items.stone_pickaxe),
		IRON_MINER(2, 100, "ironMiner", Items.iron_pickaxe),
		GOLD_MINER(3, 1000, "goldMiner", Items.golden_pickaxe),
		AQUA_MINER(4, 3000, "aquaMiner", CaveItems.aquamarine_pickaxe),
		DIAMOND_MINER(5, 10000, "diamondMiner", Items.diamond_pickaxe),
		THE_MINER(6, 20000, "theMiner", CaveItems.mining_pickaxe),
		CRAZY_MINER(7, 50000, "crazyMiner", CaveItems.mining_pickaxe);

		private int rank;
		private int phase;
		private String name;
		private Item pickaxe;
		private ItemStack renderItemStack;

		private MinerRank(int rank, int phase, String name, Item pickaxe)
		{
			this.rank = rank;
			this.phase = phase;
			this.name = name;
			this.pickaxe = pickaxe;
		}

		public int getRank()
		{
			return rank;
		}

		public int getPhase()
		{
			return phase;
		}

		public String getUnlocalizedName()
		{
			return "caveworld.minerrank." + name;
		}

		public Item getPickaxe()
		{
			return pickaxe;
		}

		public ItemStack getRenderItemStack()
		{
			if (renderItemStack == null)
			{
				renderItemStack = new ItemStack(pickaxe);
			}

			return renderItemStack;
		}
	}

	public static MinerRank getRank(int rank)
	{
		if (rank < 0)
		{
			rank = 0;
		}

		int max = MinerRank.values().length - 1;

		if (rank > max)
		{
			rank = max;
		}

		return MinerRank.values()[rank];
	}

	public static class Caver implements IExtendedEntityProperties
	{
		private final Entity entity;

		private int point;
		private int rank;
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
			data.setInteger("Rank", rank);

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
			rank = data.getInteger("Rank");

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

			MinerRank current = CaverManager.getRank(rank);
			boolean promoted = false;

			if (current.getRank() < 7)
			{
				MinerRank next = CaverManager.getRank(rank + 1);

				if (point >= next.getPhase())
				{
					++rank;

					promoted = true;
					current = next;

					setMiningPoint(point - current.getPhase());
				}
			}

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

				if (promoted && player instanceof EntityPlayerMP)
				{
					EntityPlayerMP thePlayer = (EntityPlayerMP)player;
					IChatComponent component = new ChatComponentTranslation("caveworld.minerrank.promoted", thePlayer.getDisplayName(), StatCollector.translateToLocal(current.getUnlocalizedName()));
					component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);

					thePlayer.mcServer.getConfigurationManager().sendChatMsg(component);
				}

				if (point >= 1000)
				{
					player.triggerAchievement(CaveAchievementList.theMiner);
				}
			}
		}

		public int getRank()
		{
			return rank;
		}

		public void setRank(int value)
		{
			if (value < 0)
			{
				value = 0;
			}

			int max = MinerRank.values().length - 1;

			if (value > max)
			{
				value = max;
			}

			rank = value;

			adjustData();
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