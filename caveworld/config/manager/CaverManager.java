package caveworld.config.manager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import caveworld.api.BlockEntry;
import caveworld.api.ICaverManager;
import caveworld.api.event.MiningPointEvent.RankPromote;
import caveworld.core.CaveAchievementList;
import caveworld.item.CaveItems;
import caveworld.network.CaveNetworkRegistry;
import caveworld.network.client.CaverAdjustMessage;
import caveworld.plugin.mceconomy.MCEconomyPlugin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy2.api.MCEconomyAPI;

public class CaverManager implements ICaverManager
{
	public static final String CAVER_TAG = "Caveworld:Caver";

	private static final Table<Block, Integer, Integer> pointAmounts = HashBasedTable.create();

	public static BlockEntry lastMine;
	public static int lastMinePoint;
	public static long mineHighlightStart;

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
		if (metadata == OreDictionary.WILDCARD_VALUE)
		{
			for (int meta = 0; meta < 16; ++meta)
			{
				pointAmounts.put(block, meta, amount);
			}
		}
		else
		{
			if (metadata < 0 || metadata > 16)
			{
				metadata = 0;
			}

			pointAmounts.put(block, metadata, amount);
		}
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

				if (block != Blocks.air)
				{
					setMiningPointAmount(block, entry.getItemDamage(), amount);
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
	public int getMinerRank(Entity entity)
	{
		return getCaver(entity).getRank();
	}

	@Override
	public String getMinerRankName(Entity entity)
	{
		return getRank(getMinerRank(entity)).getName();
	}

	@Override
	public void setMinerRank(Entity entity, int rank)
	{
		getCaver(entity).setRank(rank);
	}

	@Override
	public Map<Integer, Pair<String, Integer>> getMinerRanks()
	{
		Map<Integer, Pair<String, Integer>> result = Maps.newHashMap();

		for (MinerRank rank : MinerRank.values())
		{
			result.put(rank.getRank(), Pair.of(rank.getName(), rank.getPhase()));
		}

		return result;
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
	public int getAquaCavernLastDimension(Entity entity)
	{
		return getCaver(entity).getAquaCavernLastDimension();
	}

	@Override
	public void setAquaCavernLastDimension(Entity entity, int dimension)
	{
		getCaver(entity).setAquaCavernLastDimension(dimension);
	}

	@Override
	public int getCavelandLastDimension(Entity entity)
	{
		return getCaver(entity).getCavelandLastDimension();
	}

	@Override
	public void setCavelandLastDimension(Entity entity, int dimension)
	{
		getCaver(entity).setCavelandLastDimension(dimension);
	}

	@Override
	public int getCaveniaLastDimension(Entity entity)
	{
		return getCaver(entity).getCaveniaLastDimension();
	}

	@Override
	public void setCaveniaLastDimension(Entity entity, int dimension)
	{
		getCaver(entity).setCaveniaLastDimension(dimension);
	}

	@Override
	public long getLastSleepTime(Entity entity)
	{
		return getCaver(entity).getLastSleepTime();
	}

	@Override
	public long getLastSleepTime(Entity entity, int dimension)
	{
		return getCaver(entity).getLastSleepTime(dimension);
	}

	@Override
	public void setLastSleepTime(Entity entity, long time)
	{
		getCaver(entity).setLastSleepTime(time);
	}

	@Override
	public void setLastSleepTime(Entity entity, int dimension, long time)
	{
		getCaver(entity).setLastSleepTime(dimension, time);
	}

	@Override
	public ChunkCoordinates getLastPos(Entity entity, int type)
	{
		return getCaver(entity).getLastPos(type);
	}

	@Override
	public ChunkCoordinates getLastPos(Entity entity, int dimension, int type)
	{
		return getCaver(entity).getLastPos(dimension, type);
	}

	@Override
	public void setLastPos(Entity entity, int type, ChunkCoordinates coord)
	{
		getCaver(entity).setLastPos(type, coord);
	}

	@Override
	public void setLastPos(Entity entity, int dimension, int type, ChunkCoordinates coord)
	{
		getCaver(entity).setLastPos(dimension, type, coord);
	}

	@Override
	public void saveData(Entity entity, NBTTagCompound compound)
	{
		getCaver(entity).saveNBTData(compound);
	}

	@Override
	public void loadData(Entity entity, NBTTagCompound compound)
	{
		Caver caver = getCaver(entity);

		caver.loadNBTData(compound);
		caver.adjustData();
	}

	@Override
	public void adjustData(Entity entity)
	{
		getCaver(entity).adjustData();
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

		@SideOnly(Side.CLIENT)
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

		public String getName()
		{
			return name;
		}

		public String getUnlocalizedName()
		{
			return "caveworld.minerrank." + name;
		}

		@SideOnly(Side.CLIENT)
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
		private int caveworld, cavern, aqua, caveland, cavenia;

		private final Map<Integer, Long> lastSleepTime = Maps.newHashMap();
		private final Table<Integer, Integer, ChunkCoordinates> lastPos = HashBasedTable.create();

		public Caver(Entity entity)
		{
			this.entity = entity;
		}

		public Entity getEntity()
		{
			return entity;
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
			data.setInteger(tag + "AquaCavern", aqua);
			data.setInteger(tag + "Caveland", caveland);
			data.setInteger(tag + "Cavenia", cavenia);

			NBTTagList list = new NBTTagList();

			for (Entry<Integer, Long> entry : lastSleepTime.entrySet())
			{
				NBTTagCompound nbt = new NBTTagCompound();

				nbt.setInteger("Dim", entry.getKey());
				nbt.setLong("Time", entry.getValue());

				list.appendTag(nbt);
			}

			data.setTag("LastSleepTime", list);

			list = new NBTTagList();

			for (Cell<Integer, Integer, ChunkCoordinates> entry : lastPos.cellSet())
			{
				NBTTagCompound nbt = new NBTTagCompound();

				nbt.setInteger("Dim", entry.getRowKey());
				nbt.setInteger("Type", entry.getColumnKey());
				nbt.setInteger("PosX", entry.getValue().posX);
				nbt.setInteger("PosY", entry.getValue().posY);
				nbt.setInteger("PosZ", entry.getValue().posZ);

				list.appendTag(nbt);
			}

			data.setTag("LastPos", list);
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
			aqua = data.getInteger(tag + "AquaCavern");
			caveland = data.getInteger(tag + "Caveland");
			cavenia = data.getInteger(tag + "Cavenia");

			NBTTagList list = data.getTagList("LastSleepTime", NBT.TAG_COMPOUND);

			lastSleepTime.clear();

			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				int dim = nbt.getInteger("Dim");
				long time = nbt.getLong("Time");

				lastSleepTime.put(dim, time);
			}

			list = data.getTagList("LastPos", NBT.TAG_COMPOUND);

			lastPos.clear();

			for (int i = 0; i < list.tagCount(); ++i)
			{
				NBTTagCompound nbt = list.getCompoundTagAt(i);
				int dim = nbt.getInteger("Dim");
				int type = nbt.getInteger("Type");
				int x = nbt.getInteger("PosX");
				int y = nbt.getInteger("PosY");
				int z = nbt.getInteger("PosZ");

				lastPos.put(dim, type, new ChunkCoordinates(x, y, z));
			}
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

			while (current.getRank() < 7)
			{
				MinerRank next = CaverManager.getRank(rank + 1);

				if (point >= next.getPhase())
				{
					++rank;

					promoted = true;
					current = next;

					setMiningPoint(point - current.getPhase());
				}
				else break;
			}

			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				if (value > 0 && point > 0 && point % 100 == 0)
				{
					player.addExperience(player.xpBarCap() / 2);

					if (MCEconomyPlugin.enabled())
					{
						MCEconomyAPI.addPlayerMP(player, 10 + point / 100 - 1, false);
					}
				}

				if (promoted)
				{
					RankPromote event = new RankPromote(player, rank);
					MinecraftForge.EVENT_BUS.post(event);

					if (rank != event.newAmount)
					{
						rank = event.newAmount;

						current = CaverManager.getRank(rank);
					}

					if (player instanceof EntityPlayerMP)
					{
						EntityPlayerMP thePlayer = (EntityPlayerMP)player;
						IChatComponent name = new ChatComponentTranslation(current.getUnlocalizedName());
						name.getChatStyle().setBold(true);
						IChatComponent component = new ChatComponentTranslation("caveworld.minerrank.promoted", thePlayer.getDisplayName(), name);
						component.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);

						thePlayer.mcServer.getConfigurationManager().sendChatMsg(component);
						thePlayer.getServerForPlayer().playSoundAtEntity(thePlayer, "caveworld:minerrank.promoted", 1.0F, 1.0F);
					}
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
				CaveNetworkRegistry.sendTo(new CaverAdjustMessage(this), (EntityPlayerMP)entity);
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

		public int getAquaCavernLastDimension()
		{
			return aqua;
		}

		public void setAquaCavernLastDimension(int dim)
		{
			aqua = dim;
		}

		public int getCavelandLastDimension()
		{
			return caveland;
		}

		public void setCavelandLastDimension(int dim)
		{
			caveland = dim;
		}

		public int getCaveniaLastDimension()
		{
			return cavenia;
		}

		public void setCaveniaLastDimension(int dim)
		{
			cavenia = dim;
		}

		public long getLastSleepTime()
		{
			return getLastSleepTime(entity.dimension);
		}

		public long getLastSleepTime(int dim)
		{
			Long ret = lastSleepTime.get(dim);

			return ret == null ? 0L : ret.longValue();
		}

		public void setLastSleepTime(long time)
		{
			setLastSleepTime(entity.dimension, time);
		}

		public void setLastSleepTime(int dim, long time)
		{
			lastSleepTime.put(dim, time);
		}

		public ChunkCoordinates getLastPos(int type)
		{
			return getLastPos(entity.dimension, type);
		}

		public ChunkCoordinates getLastPos(int dim, int type)
		{
			return lastPos.get(dim, type);
		}

		public void setLastPos(int type, ChunkCoordinates coord)
		{
			setLastPos(entity.dimension, type, coord);
		}

		public void setLastPos(int dim, int type, ChunkCoordinates coord)
		{
			lastPos.put(dim, type, coord);
		}
	}
}