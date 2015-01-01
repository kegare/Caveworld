/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.kegare.caveworld.api.BlockEntry;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.ArrayListExtended;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Roman;
import com.kegare.caveworld.util.breaker.BreakPos;
import com.kegare.caveworld.util.breaker.LumberBreakExecutor;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
import com.kegare.caveworld.util.breaker.QuickBreakExecutor;
import com.kegare.caveworld.util.breaker.RangedBreakExecutor;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLumberingAxe extends ItemAxe implements ICaveniumTool
{
	public enum BreakMode implements IBreakMode
	{
		NORMAL(MultiBreakExecutor.class),
		LUMBERING(LumberBreakExecutor.class),
		QUICK(QuickBreakExecutor.class),
		RANGED(RangedBreakExecutor.class);

		public static final EnumMap<BreakMode, Map<EntityPlayer, MultiBreakExecutor>> executors = Maps.newEnumMap(BreakMode.class);

		private final Class<? extends MultiBreakExecutor> executor;

		private BreakMode(Class<? extends MultiBreakExecutor> executor)
		{
			this.executor = executor;
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
		public boolean clear(EntityPlayer player)
		{
			Map<EntityPlayer, MultiBreakExecutor> map = executors.get(this);

			if (map == null || map.isEmpty())
			{
				return false;
			}

			MultiBreakExecutor result = map.get(player);

			if (result != null)
			{
				result.clear();

				return true;
			}

			return false;
		}
	}

	public static final ArrayListExtended<BlockEntry> breakableBlocks = new ArrayListExtended();

	public long highlightStart;

	public ItemLumberingAxe(String name)
	{
		super(ItemMiningPickaxe.CAVENIUM);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:lumbering_axe");
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@Override
	public String getToolClass()
	{
		return "axe";
	}

	protected void initializeItemStackNBT(ItemStack itemstack)
	{
		if (itemstack == null || itemstack.getItem() == null)
		{
			return;
		}

		if (itemstack.getTagCompound() == null)
		{
			itemstack.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound data = itemstack.getTagCompound();
		String full = null;

		for (BreakMode mode : BreakMode.values())
		{
			if (mode != BreakMode.NORMAL)
			{
				String key = mode.name() + ":Blocks";

				if (!data.hasKey(key))
				{
					if (Strings.isNullOrEmpty(full))
					{
						Collection<String> blocks = Collections2.transform(breakableBlocks, new Function<BlockEntry, String>()
						{
							@Override
							public String apply(BlockEntry entry)
							{
								return CaveUtils.toStringHelper(entry.getBlock(), entry.getMetadata());
							}
						});

						full = Joiner.on("|").join(blocks);
					}

					data.setString(key, full);
				}
			}
		}
	}

	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer player)
	{
		initializeItemStackNBT(itemstack);
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean selected)
	{
		initializeItemStackNBT(itemstack);
	}

	@Override
	public List<BlockEntry> getBreakableBlocks()
	{
		return breakableBlocks;
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

	@Override
	public int getRefined(ItemStack itemstack)
	{
		if (itemstack.getItem() != this || itemstack.getTagCompound() == null)
		{
			return 0;
		}

		return itemstack.getTagCompound().getInteger("Refined");
	}

	@Override
	public boolean canBreak(ItemStack itemstack, Block block, int metadata)
	{
		if (itemstack.getItem() != this || itemstack.getTagCompound() == null)
		{
			return false;
		}

		return itemstack.getTagCompound().getString(getModeName(itemstack) + ":Blocks").contains(CaveUtils.toStringHelper(block, metadata));
	}

	@Override
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

	@Override
	public BreakMode getMode(ItemStack itemstack)
	{
		if (itemstack.getItem() != this || itemstack.getTagCompound() == null)
		{
			return BreakMode.NORMAL;
		}

		switch (itemstack.getTagCompound().getInteger("Mode"))
		{
			case 1:
				return BreakMode.LUMBERING;
			case 2:
				return BreakMode.QUICK;
			case 3:
				return BreakMode.RANGED;
			default:
				return BreakMode.NORMAL;
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		String name = super.getItemStackDisplayName(itemstack);
		int refined = getRefined(itemstack);

		if (refined > 0)
		{
			return name + " " + Roman.toRoman(getRefined(itemstack));
		}

		return name;
	}

	@Override
	public Set<String> getToolClasses(ItemStack itemstack)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getToolClasses(itemstack);
		}

		return super.getToolClasses(itemstack);
	}

	@Override
	public int getHarvestLevel(ItemStack itemstack, String toolClass)
	{
		Item item = getBase(itemstack);

		if (item != this)
		{
			return item.getHarvestLevel(itemstack, toolClass);
		}

		return super.getHarvestLevel(itemstack, toolClass);
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
		if (player.isSneaking() && getMode(itemstack) == BreakMode.NORMAL)
		{
			Item base = getBase(itemstack);

			if (base != this)
			{
				return base.onItemRightClick(itemstack, world, player);
			}
		}

		int i = itemstack.getTagCompound().getInteger("Mode");

		if (++i > BreakMode.values().length - 1 || i > getRefined(itemstack) + 2)
		{
			i = 0;
		}

		itemstack.getTagCompound().setInteger("Mode", i);

		if (world.isRemote)
		{
			highlightStart = System.currentTimeMillis();
		}

		world.playSoundAtEntity(player, "random.click", 0.6F, 1.7F);

		return itemstack;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase entity)
	{
		BreakMode mode = getMode(itemstack);

		if (entity.isSneaking() && mode == BreakMode.NORMAL)
		{
			Item base = getBase(itemstack);

			if (base != this)
			{
				return base.onBlockDestroyed(itemstack, world, block, x, y, z, entity);
			}
		}

		if (entity instanceof EntityPlayerMP)
		{
			MultiBreakExecutor executor = mode.getExecutor((EntityPlayerMP)entity);

			if (executor != null && !executor.getBreakPositions().isEmpty())
			{
				BreakPos origin = executor.getOriginPos();

				if (x == origin.x && y == origin.y && z == origin.z)
				{
					executor.breakAll();
				}
			}
		}

		return super.onBlockDestroyed(itemstack, world, block, x, y, z, entity);
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
		return StatCollector.translateToLocal("caveworld.breakmode." + getModeName(itemstack).toLowerCase(Locale.ENGLISH));
	}

	@Override
	public String getModeInfomation(ItemStack itemstack)
	{
		return StatCollector.translateToLocal("caveworld.breakmode") + ": " + getModeDisplayName(itemstack);
	}

	@Override
	public Set<Item> getBaseableItems()
	{
		return Collections.unmodifiableSet(CaveUtils.axeItems);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(getModeInfomation(itemstack));

		Item item = getBase(itemstack);

		if (item != this)
		{
			list.add(I18n.format(CaveItems.mining_pickaxe.getUnlocalizedName() + ".base") + ": " + item.getItemStackDisplayName(itemstack));

			item.addInformation(itemstack, player, list, advanced);
		}

		super.addInformation(itemstack, player, list, advanced);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (Item base : CaveUtils.axeItems)
		{
			String name = GameData.getItemRegistry().getNameForObject(base);

			if (Strings.isNullOrEmpty(name) || base == this)
			{
				continue;
			}

			for (int i = 0; i <= 4; ++i)
			{
				ItemStack itemstack = new ItemStack(item);
				NBTTagCompound data = new NBTTagCompound();

				data.setString("BaseName", name);
				data.setInteger("Refined", i);

				itemstack.setTagCompound(data);

				list.add(itemstack);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int pass)
	{
		if (!Config.fakeLumberingAxe)
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
		if (!Config.fakeLumberingAxe)
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