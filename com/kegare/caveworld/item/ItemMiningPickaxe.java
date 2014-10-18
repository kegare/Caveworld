/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.client.gui.GuiSelectBreakable;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.util.CaveUtils;
import com.kegare.caveworld.util.breaker.BreakPos;
import com.kegare.caveworld.util.breaker.IBreakExecutor;
import com.kegare.caveworld.util.breaker.MultiBreakExecutor;
import com.kegare.caveworld.util.breaker.RangedBreakExecutor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMiningPickaxe extends ItemPickaxe
{
	public enum BreakMode
	{
		NORMAL,
		QUICK,
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

		if (!data.hasKey("MaxUses") || data.getInteger("MaxUses") <= 0)
		{
			data.setInteger("MaxUses", MINING.getMaxUses());
		}

		if (!data.hasKey("Efficiency") || data.getFloat("Efficiency") <= 0.0F)
		{
			data.setFloat("Efficiency", MINING.getEfficiencyOnProperMaterial());
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
				return BreakMode.RANGED;
			default:
				return BreakMode.NORMAL;
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack)
	{
		switch (getRefined(itemstack))
		{
			case 1:
				return super.getItemStackDisplayName(itemstack) + " I";
			case 2:
				return super.getItemStackDisplayName(itemstack) + " II";
			case 3:
				return super.getItemStackDisplayName(itemstack) + " III";
			case 4:
				return super.getItemStackDisplayName(itemstack) + " IV";
			default:
				return super.getItemStackDisplayName(itemstack);
		}
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		NBTTagCompound data = itemstack.getTagCompound();

		if (data == null)
		{
			return super.getMaxDamage(itemstack);
		}

		int maxUses = data.getInteger("MaxUses");

		if (maxUses <= 0)
		{
			maxUses = MINING.getMaxUses();

			data.setInteger("MaxUses", maxUses);
		}

		return maxUses;
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
	{
		NBTTagCompound data = itemstack.getTagCompound();

		if (data == null)
		{
			return super.getDigSpeed(itemstack, block, metadata);
		}

		float efficiency = data.getFloat("Efficiency");

		if (efficiency <= 0.0F)
		{
			efficiency = MINING.getDamageVsEntity();

			data.setFloat("Efficiency", efficiency);
		}

		return ForgeHooks.isToolEffective(itemstack, block, metadata) || func_150897_b(block) || block instanceof BlockOre || block instanceof BlockRedstoneOre ? efficiency : 1.0F;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (player.isSneaking())
		{
			if (world.isRemote)
			{
				Caveworld.proxy.displayClientGuiScreen(new GuiSelectBreakable(itemstack));
			}
		}
		else
		{
			int i = itemstack.getTagCompound().getInteger("Mode");

			if (++i > BreakMode.values().length - 1 || i > getRefined(itemstack) + 1)
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
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			IBreakExecutor executor;

			switch (getMode(itemstack))
			{
				case QUICK:
					executor = MultiBreakExecutor.getExecutor(world, player);
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

		super.addInformation(itemstack, player, list, advanced);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i <= 4; ++i)
		{
			ItemStack itemstack = new ItemStack(item, 1, 0);
			NBTTagCompound data = new NBTTagCompound();
			data.setInteger("Refined", i);
			itemstack.setTagCompound(data);

			list.add(itemstack);
		}
	}
}