/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kegare.caveworld.client.gui.GuiSelectBreakable;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.recipe.RecipeMiningPickaxe;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Roman;
import com.kegare.caveworld.util.breaker.AditBreakExecutor;
import com.kegare.caveworld.util.breaker.BreakPos;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
import com.kegare.caveworld.util.breaker.QuickBreakExecutor;
import com.kegare.caveworld.util.breaker.RangedBreakExecutor;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMiningPickaxe extends ItemPickaxe
{
	public enum BreakMode
	{
		NORMAL,
		QUICK,
		ADIT,
		RANGED
	}

	public static final ToolMaterial MINING = EnumHelper.addToolMaterial("MINING", 3, 300, 5.0F, 1.5F, 10);

	public static final Set<String> defaultBreakables = Sets.newHashSet();

	static
	{
		MINING.customCraftingMaterial = CaveItems.cavenium;
	}

	public int highlightTicks;

	public ItemMiningPickaxe(String name)
	{
		super(MINING);
		this.setUnlocalizedName(name);
		this.setTextureName("caveworld:mining_pickaxe");
		this.setCreativeTab(Caveworld.tabCaveworld);
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

		if (!data.hasKey("Blocks") || Strings.isNullOrEmpty(data.getString("Blocks")))
		{
			data.setString("Blocks", Joiner.on("|").join(defaultBreakables));
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

	public int getRefined(ItemStack itemstack)
	{
		if (itemstack.getItem() != this || itemstack.getTagCompound() == null)
		{
			return 0;
		}

		return itemstack.getTagCompound().getInteger("Refined");
	}

	public boolean canBreak(ItemStack itemstack, Block block, int metadata)
	{
		if (itemstack.getItem() != this || itemstack.getTagCompound() == null)
		{
			return false;
		}

		return itemstack.getTagCompound().getString("Blocks").contains(CaveUtils.toStringHelper(block, metadata));
	}

	public BreakMode getMode(ItemStack itemstack)
	{
		if (itemstack.getItem() != this || itemstack.getTagCompound() == null)
		{
			return BreakMode.NORMAL;
		}

		switch (itemstack.getTagCompound().getInteger("Mode"))
		{
			case 1:
				return BreakMode.QUICK;
			case 2:
				return BreakMode.ADIT;
			case 3:
				return BreakMode.RANGED;
			default:
				return BreakMode.NORMAL;
		}
	}

	public Item getBaseTool(ItemStack itemstack)
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
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getToolClasses(itemstack);
		}

		return super.getToolClasses(itemstack);
	}

	@Override
	public int getHarvestLevel(ItemStack itemstack, String toolClass)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getHarvestLevel(itemstack, toolClass);
		}

		return super.getHarvestLevel(itemstack, toolClass);
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.canHarvestBlock(block, itemstack);
		}

		return super.canHarvestBlock(block, itemstack);
	}

	@Override
	public int getItemStackLimit(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getItemStackLimit(itemstack);
		}

		return super.getItemStackLimit(itemstack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.showDurabilityBar(itemstack);
		}

		return super.showDurabilityBar(itemstack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getDurabilityForDisplay(itemstack);
		}

		return super.getDurabilityForDisplay(itemstack);
	}

	@Override
	public int getDamage(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getDamage(itemstack);
		}

		return super.getDamage(itemstack);
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getMaxDamage(itemstack);
		}

		return super.getMaxDamage(itemstack);
	}

	@Override
	public boolean isDamaged(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.isDamaged(itemstack);
		}

		return super.isDamaged(itemstack);
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getDigSpeed(itemstack, block, metadata);
		}

		return super.getDigSpeed(itemstack, block, metadata);
	}

	@Override
	public int getItemEnchantability(ItemStack itemstack)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getItemEnchantability(itemstack);
		}

		return super.getItemEnchantability(itemstack);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (player.isSneaking())
		{
			if (getMode(itemstack) == BreakMode.NORMAL)
			{
				return getBaseTool(itemstack).onItemRightClick(itemstack, world, player);
			}

			if (world.isRemote)
			{
				Caveworld.proxy.displayClientGuiScreen(new GuiSelectBreakable(itemstack));
			}
		}
		else
		{
			int i = itemstack.getTagCompound().getInteger("Mode");

			if (++i > BreakMode.values().length - 1 || i > getRefined(itemstack) + 2)
			{
				i = 0;
			}

			itemstack.getTagCompound().setInteger("Mode", i);

			if (world.isRemote)
			{
				highlightTicks = 800;
			}
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
			return getBaseTool(itemstack).onBlockDestroyed(itemstack, world, block, x, y, z, entity);
		}

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			MultiBreakExecutor executor;

			switch (mode)
			{
				case QUICK:
					executor = QuickBreakExecutor.getExecutor(world, player);
					break;
				case ADIT:
					executor = AditBreakExecutor.getExecutor(world, player);
					break;
				case RANGED:
					executor = RangedBreakExecutor.getExecutor(world, player);
					break;
				default:
					executor = null;
					break;
			}

			if (executor != null && !executor.getBreakPositions().isEmpty())
			{
				BreakPos origin = executor.getOriginPos();

				if (origin != null && x == origin.x && y == origin.y && z == origin.z)
				{
					executor.breakAll();

					return true;
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
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getPotionEffect(itemstack);
		}

		return super.getPotionEffect(itemstack);
	}

	@Override
	public int getEntityLifespan(ItemStack itemstack, World world)
	{
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getEntityLifespan(itemstack, world);
		}

		return super.getEntityLifespan(itemstack, world);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack itemstack, int pass)
	{
		Item item = getBaseTool(itemstack);

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
		Item item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getFontRenderer(itemstack);
		}

		return super.getFontRenderer(itemstack);
	}

	@SideOnly(Side.CLIENT)
	public String getModeInfomation(ItemStack itemstack)
	{
		String mode = I18n.format(getUnlocalizedName() + ".mode");

		switch (getMode(itemstack))
		{
			case QUICK:
				return mode + ": " + I18n.format(getUnlocalizedName() + ".mode.quick");
			case ADIT:
				return mode + ": " + I18n.format(getUnlocalizedName() + ".mode.adit");
			case RANGED:
				return mode + ": " + I18n.format(getUnlocalizedName() + ".mode.ranged");
			default:
				return mode + ": " + I18n.format(getUnlocalizedName() + ".mode.normal");
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advanced)
	{
		list.add(getModeInfomation(itemstack));

		Item item = getBaseTool(itemstack);

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
		List<ItemStack> items = Lists.newArrayList(RecipeMiningPickaxe.instance.getCenterItems());

		Collections.sort(items, CaveUtils.itemStackComparator);

		for (ItemStack center : items)
		{
			for (int i = 0; i <= 4; ++i)
			{
				ItemStack itemstack = new ItemStack(item, 1, 0);
				NBTTagCompound data = new NBTTagCompound();

				data.setString("BaseName", GameData.getItemRegistry().getNameForObject(center.getItem()));
				data.setInteger("Refined", i);

				itemstack.setTagCompound(data);

				list.add(itemstack);
			}
		}
	}
}