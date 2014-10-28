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
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.client.gui.GuiSelectBreakable;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.recipe.RecipeMiningPickaxe;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.Roman;
import com.kegare.caveworld.util.breaker.AditBreakExecutor;
import com.kegare.caveworld.util.breaker.BreakPos;
import com.kegare.caveworld.util.breaker.IBreakExecutor;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
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
	public static final EnumRarity mining = EnumHelper.addRarity("mining", EnumChatFormatting.DARK_GRAY, "Mining");

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
			List<String> list = Lists.newArrayList();

			list.add(CaveUtils.toStringHelper(CaveBlocks.cavenium_ore, 0));
			list.add(CaveUtils.toStringHelper(CaveBlocks.cavenium_ore, 1));
			list.add(CaveUtils.toStringHelper(Blocks.coal_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.iron_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.gold_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.redstone_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.lit_redstone_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.lapis_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.emerald_ore, 0));
			list.add(CaveUtils.toStringHelper(Blocks.diamond_ore, 0));

			data.setString("Blocks", Joiner.on("|").join(list));
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

	public ItemTool getBaseTool(ItemStack itemstack)
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

		if (item == null || !(item instanceof ItemTool))
		{
			return this;
		}

		return (ItemTool)item;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		int refined = getRefined(itemstack);

		if (refined > 0)
		{
			return super.getItemStackDisplayName(itemstack) + " " + Roman.toRoman(getRefined(itemstack));
		}

		return super.getItemStackDisplayName(itemstack);
	}

	@Override
	public Set<String> getToolClasses(ItemStack itemstack)
	{
		ItemTool item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getToolClasses(itemstack);
		}

		return super.getToolClasses(itemstack);
	}

	@Override
	public int getHarvestLevel(ItemStack itemstack, String toolClass)
	{
		ItemTool item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getHarvestLevel(itemstack, toolClass);
		}

		return super.getHarvestLevel(itemstack, toolClass);
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack itemstack)
	{
		ItemTool item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.canHarvestBlock(block, itemstack);
		}

		return super.canHarvestBlock(block, itemstack);
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		ItemTool item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getMaxDamage(itemstack);
		}

		return super.getMaxDamage(itemstack);
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
	{
		ItemTool item = getBaseTool(itemstack);

		if (item != this)
		{
			return item.getDigSpeed(itemstack, block, metadata);
		}

		return super.getDigSpeed(itemstack, block, metadata);
	}

	@Override
	public int getItemEnchantability(ItemStack itemstack)
	{
		ItemTool item = getBaseTool(itemstack);

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
			IBreakExecutor executor;

			switch (mode)
			{
				case QUICK:
					executor = MultiBreakExecutor.getExecutor(world, player);
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
		return mining;
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