/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.item;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.util.CaveUtils;
import caveworld.util.breaker.MultiBreakExecutor;
import caveworld.util.breaker.QuickBreakExecutor;
import caveworld.util.breaker.RangedBreakExecutor;
import caveworld.util.farmer.MultiFarmExecutor;
import caveworld.util.farmer.QuickFarmExecutor;
import caveworld.util.farmer.RangedFarmExecutor;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFarmingHoe extends ItemHoe implements IModeItem
{
	public enum FarmMode implements IFarmMode
	{
		NORMAL(MultiBreakExecutor.class, MultiFarmExecutor.class),
		QUICK(QuickBreakExecutor.class, QuickFarmExecutor.class),
		RANGED(RangedBreakExecutor.class, RangedFarmExecutor.class);

		public static final EnumMap<FarmMode, Map<EntityPlayer, MultiBreakExecutor>> executors = Maps.newEnumMap(FarmMode.class);
		public static final EnumMap<FarmMode, Map<EntityPlayer, MultiFarmExecutor>> farmExecutors = Maps.newEnumMap(FarmMode.class);

		private final Class<? extends MultiBreakExecutor> executor;
		private final Class<? extends MultiFarmExecutor> farmExecutor;

		private FarmMode(Class<? extends MultiBreakExecutor> executor, Class<? extends MultiFarmExecutor> farmExecutor)
		{
			this.executor = executor;
			this.farmExecutor = farmExecutor;
		}

		@Override
		public MultiBreakExecutor getExecutor(EntityPlayer player)
		{
			Map<EntityPlayer, MultiBreakExecutor> map = executors.get(this);

			if (map == null)
			{
				map = Maps.newHashMap();

				executors.put(this, map);
			}

			MultiBreakExecutor result = map.get(player);

			if (result == null)
			{
				try
				{
					result = executor.getConstructor(EntityPlayer.class).newInstance(player);
				}
				catch (Exception ignored) {}

				if (result != null)
				{
					map.put(player, result);
				}
			}

			return result;
		}

		@Override
		public MultiFarmExecutor getFarmExecutor(EntityPlayer player)
		{
			Map<EntityPlayer, MultiFarmExecutor> map = farmExecutors.get(this);

			if (map == null)
			{
				map = Maps.newHashMap();

				farmExecutors.put(this, map);
			}

			MultiFarmExecutor result = map.get(player);

			if (result == null)
			{
				try
				{
					result = farmExecutor.getConstructor(EntityPlayer.class).newInstance(player);
				}
				catch (Exception ignored) {}

				if (result != null)
				{
					map.put(player, result);
				}
			}

			return result;
		}

		@Override
		public void clear(EntityPlayer player)
		{
			Map<EntityPlayer, MultiBreakExecutor> map = executors.get(this);

			if (map != null && !map.isEmpty())
			{
				MultiBreakExecutor result = map.get(player);

				if (result != null)
				{
					result.clear();
				}
			}

			Map<EntityPlayer, MultiFarmExecutor> farmMap = farmExecutors.get(this);

			if (farmMap != null && !farmMap.isEmpty())
			{
				MultiFarmExecutor result = farmMap.get(player);

				if (result != null)
				{
					result.clear();
				}
			}
		}
	}

	public long highlightStart;

	public ItemFarmingHoe(String name)
	{
		super(CaveItems.CAVENIUM);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:farming_hoe");
		this.setCreativeTab(Caveworld.tabFarmingHoe);
	}

	@Override
	public long getHighlightStart()
	{
		return highlightStart;
	}

	@Override
	public void setHighlightStart(long time)
	{
		highlightStart = time;
	}

	public Item getBase(ItemStack itemstack)
	{
		NBTTagCompound data = itemstack.getTagCompound();

		if (data == null)
		{
			return this;
		}

		String name = data.getString("BaseName");

		if (Strings.isNullOrEmpty(name))
		{
			return this;
		}

		Item item = GameData.getItemRegistry().getObject(name);

		return item == null ? this : item;
	}

	public FarmMode getMode(ItemStack itemstack)
	{
		if (itemstack.getTagCompound() == null)
		{
			return FarmMode.NORMAL;
		}

		FarmMode[] modes = FarmMode.values();
		int mode = MathHelper.clamp_int(itemstack.getTagCompound().getInteger("Mode"), 0, modes.length - 1);

		return modes[mode];
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.canHarvestBlock(block, itemstack);
		}

		return super.canHarvestBlock(block, itemstack);
	}

	@Override
	public int getItemStackLimit(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getItemStackLimit(itemstack);
		}

		return super.getItemStackLimit(itemstack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.showDurabilityBar(itemstack);
		}

		return super.showDurabilityBar(itemstack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getDurabilityForDisplay(itemstack);
		}

		return super.getDurabilityForDisplay(itemstack);
	}

	@Override
	public int getDamage(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getDamage(itemstack);
		}

		return super.getDamage(itemstack);
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getMaxDamage(itemstack);
		}

		return super.getMaxDamage(itemstack);
	}

	@Override
	public boolean isDamaged(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.isDamaged(itemstack);
		}

		return super.isDamaged(itemstack);
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getDigSpeed(itemstack, block, metadata);
		}

		return super.getDigSpeed(itemstack, block, metadata);
	}

	@Override
	public int getItemEnchantability(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getItemEnchantability(itemstack);
		}

		return super.getItemEnchantability(itemstack);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		Item base = getBase(itemstack);

		if (base != this)
		{
			return base.onItemRightClick(itemstack, world, player);
		}

		return super.onItemRightClick(itemstack, world, player);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase entity)
	{
		FarmMode mode = getMode(itemstack);

		if (mode == FarmMode.NORMAL)
		{
			Item base = getBase(itemstack);

			if (base != this)
			{
				return base.onBlockDestroyed(itemstack, world, block, x, y, z, entity);
			}

			return super.onBlockDestroyed(itemstack, world, block, x, y, z, entity);
		}

		if (block instanceof IGrowable)
		{
			IGrowable growable = (IGrowable)block;

			if (!growable.func_149851_a(world, x, y, z, world.isRemote))
			{
				if (entity instanceof EntityPlayerMP)
				{
					MultiBreakExecutor executor = mode.getExecutor((EntityPlayerMP)entity);

					if (executor != null)
					{
						executor.setOriginPos(x, y, z).setBreakPositions();
						executor.breakAll();
					}
				}
			}
		}

		return super.onBlockDestroyed(itemstack, world, block, x, y, z, entity);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		FarmMode mode = getMode(itemstack);

		if (mode == FarmMode.NORMAL)
		{
			Item base = getBase(itemstack);

			if (base != this)
			{
				return base.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
			}

			return super.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
		}

		if (super.onItemUse(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ))
		{
			if (player instanceof EntityPlayerMP)
			{
				MultiFarmExecutor executor = mode.getFarmExecutor(player);

				if (executor != null)
				{
					executor.setOriginPos(x, y, z).setFarmPositions();
					executor.farmAll();
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemstack)
	{
		Item base = getBase(itemstack);

		if (base != this)
		{
			return base.onEntitySwing(entity, itemstack);
		}

		return super.onEntitySwing(entity, itemstack);
	}

	@Override
	public EnumRarity getRarity(ItemStack itemstack)
	{
		return ItemCavenium.cavenium;
	}

	@Override
	public String getPotionEffect(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getPotionEffect(itemstack);
		}

		return super.getPotionEffect(itemstack);
	}

	@Override
	public int getEntityLifespan(ItemStack itemstack, World world)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getEntityLifespan(itemstack, world);
		}

		return super.getEntityLifespan(itemstack, world);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.onBlockStartBreak(itemstack, x, y, z, player);
		}

		return super.onBlockStartBreak(itemstack, x, y, z, player);
	}

	@Override
	public void onUsingTick(ItemStack itemstack, EntityPlayer player, int count)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			item.onUsingTick(itemstack, player, count);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack itemstack, int pass)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.hasEffect(itemstack, pass);
		}

		return super.hasEffect(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public FontRenderer getFontRenderer(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getFontRenderer(itemstack);
		}

		return super.getFontRenderer(itemstack);
	}

	@Override
	public String getModeName(ItemStack itemstack)
	{
		return getMode(itemstack).name();
	}

	@Override
	public String getModeDisplayName(ItemStack itemstack)
	{
		return StatCollector.translateToLocal("caveworld.farmmode." + getModeName(itemstack).toLowerCase(Locale.ENGLISH));
	}

	@Override
	public String getModeInfomation(ItemStack itemstack)
	{
		return StatCollector.translateToLocal("caveworld.farmmode") + ": " + getModeDisplayName(itemstack);
	}

	public Set<Item> getBaseableItems()
	{
		return Collections.unmodifiableSet(CaveUtils.hoeItems);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(getModeInfomation(itemstack));

		Item item = getBase(itemstack);

		if (item != this)
		{
			list.add(I18n.format(getUnlocalizedName() + ".base") + ": " + item.getItemStackDisplayName(itemstack));

			item.addInformation(itemstack, player, list, advanced);
		}

		super.addInformation(itemstack, player, list, advanced);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (Item base : CaveUtils.hoeItems)
		{
			String name = GameData.getItemRegistry().getNameForObject(base);

			if (Strings.isNullOrEmpty(name) || base == this)
			{
				continue;
			}

			ItemStack itemstack = new ItemStack(item);
			NBTTagCompound data = new NBTTagCompound();

			data.setString("BaseName", name);

			itemstack.setTagCompound(data);

			list.add(itemstack);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int pass)
	{
		if (!Config.fakeFarmingHoe)
		{
			return super.getIcon(itemstack, pass);
		}

		Item base = getBase(itemstack);

		if (base != this)
		{
			return base.getIcon(itemstack, pass);
		}

		return super.getIcon(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconIndex(ItemStack itemstack)
	{
		if (!Config.fakeFarmingHoe)
		{
			return super.getIconIndex(itemstack);
		}

		Item base = getBase(itemstack);

		if (base != this)
		{
			return base.getIconIndex(itemstack);
		}

		return super.getIconIndex(itemstack);
	}
}