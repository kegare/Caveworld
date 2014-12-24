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
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.kegare.caveworld.client.gui.GuiSelectBreakable;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
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
		NORMAL(MultiBreakExecutor.class),
		QUICK(QuickBreakExecutor.class),
		ADIT(AditBreakExecutor.class),
		RANGED(RangedBreakExecutor.class);

		private final Class<? extends MultiBreakExecutor> executor;

		private BreakMode(Class<? extends MultiBreakExecutor> executor)
		{
			this.executor = executor;
		}

		public MultiBreakExecutor getExecutor(EntityPlayer player)
		{
			try
			{
				Object obj = executor.getMethod("getExecutor", EntityPlayer.class).invoke(null, player);

				if (obj != null && obj instanceof MultiBreakExecutor)
				{
					return (MultiBreakExecutor)obj;
				}
			}
			catch (Exception ignored) {}

			return null;
		}
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
		float result;

		if (item != this)
		{
			result = item.getDigSpeed(itemstack, block, metadata);
		}

		result = super.getDigSpeed(itemstack, block, metadata);

		if (getMode(itemstack) != BreakMode.NORMAL)
		{
			int i = getRefined(itemstack);

			if (i >= 4 || getHarvestLevel(itemstack, "pickaxe") >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemstack) >= 4)
			{
				return result;
			}

			result = Math.min(result / (MultiBreakExecutor.positionsCount.get() * (0.5F - i * 0.1245F)), result);
		}

		return result;
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

		if (!world.isRemote && entity instanceof EntityPlayer)
		{
			MultiBreakExecutor executor = mode.getExecutor((EntityPlayer)entity);

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
		for (ItemStack center : RecipeMiningPickaxe.instance.getCenterItems())
		{
			String name = GameData.getItemRegistry().getNameForObject(center.getItem());

			if (Strings.isNullOrEmpty(name) || center.getItem() == this)
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
		if (!Config.fakeMiningPickaxe)
		{
			return super.getIcon(itemstack, pass);
		}

		Item base = getBaseTool(itemstack);

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
		if (!Config.fakeMiningPickaxe)
		{
			return super.getIconIndex(itemstack);
		}

		Item base = getBaseTool(itemstack);

		if (base != this)
		{
			return base.getIconIndex(itemstack);
		}

		return super.getIconIndex(itemstack);
	}
}