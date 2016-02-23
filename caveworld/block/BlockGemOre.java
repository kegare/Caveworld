/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import caveworld.api.CaveworldAPI;
import caveworld.api.event.RandomiteChanceEvent;
import caveworld.api.event.RandomiteChanceEvent.EventType;
import caveworld.core.CaveAchievementList;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.item.CaveItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockGemOre extends BlockOre implements IBlockRenderOverlay
{
	private final Random random = new Random();

	public final List<ItemStack> randomiteDrops = Lists.newArrayList();

	@SideOnly(Side.CLIENT)
	private IIcon[] oreIcons;
	@SideOnly(Side.CLIENT)
	private IIcon[] overlayIcons;

	public BlockGemOre(String name)
	{
		super();
		this.setBlockName(name);
		this.setResistance(5.0F);
		this.setHarvestLevel("pickaxe", 1);
		this.setHarvestLevel("pickaxe", 2, 0);
		this.setHarvestLevel("pickaxe", 2, 1);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@Override
	public int getRenderType()
	{
		return Config.RENDER_TYPE_OVERLAY;
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune)
	{
		switch (metadata)
		{
			case 0:
				return CaveItems.gem;
			default:
				return Item.getItemFromBlock(this);
		}
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random)
	{
		switch (meta)
		{
			case 2:
				return 0;
			default:
				return super.quantityDropped(meta, fortune, random);
		}
	}

	@Override
	public int damageDropped(int metadata)
	{
		return metadata;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		ArrayList<ItemStack> drops = super.getDrops(world, x, y, z, metadata, fortune);

		switch (metadata)
		{
			case 2:
				doRandomiteChance(world, x, y, z, fortune, drops);
				break;
		}

		return drops;
	}

	public void doRandomiteChance(World world, int x, int y, int z, int fortune, List<ItemStack> drops)
	{
		EntityPlayer player = harvesters.get();

		if (MinecraftForge.EVENT_BUS.post(new RandomiteChanceEvent.Pre(world, x, y, z, fortune, player)))
		{
			return;
		}

		EventType type = EventType.NONE;

		if (CaveworldAPI.isEntityInCaves(player))
		{
			player.triggerAchievement(CaveAchievementList.randomite);
		}

		boolean cavenia = CaveworldAPI.isEntityInCavenia(player);

		if (cavenia || player != null && random.nextInt(3) == 0)
		{
			if (player.getActivePotionEffects().size() < (cavenia ? 5 : 3))
			{
				Potion potion = null;

				while (potion == null || potion.getEffectiveness() <= 0.5D || player.isPotionActive(potion))
				{
					potion = Potion.potionTypes[player.getRNG().nextInt(Potion.potionTypes.length)];
				}

				if (potion != null)
				{
					player.addPotionEffect(new PotionEffect(potion.getId(), (cavenia ? MathHelper.getRandomIntegerInRange(random, 30, 60) : MathHelper.getRandomIntegerInRange(random, 10, 20)) * 20));
					world.playSoundAtEntity(player, "dig.glass", 0.75F, 2.0F);

					drops.clear();

					type = EventType.POTION;
				}
			}
		}

		if (type == null || type == EventType.NONE)
		{
			if (!randomiteDrops.isEmpty())
			{
				for (int i = 0; i < Math.min(Math.max(fortune, 1), 3); ++i)
				{
					ItemStack item = randomiteDrops.get(random.nextInt(randomiteDrops.size()));

					if (item != null && item.getItem() != null && item.stackSize > 0)
					{
						drops.add(item.copy());
					}
				}

				type = EventType.ITEM;
			}
		}

		MinecraftForge.EVENT_BUS.post(new RandomiteChanceEvent.Post(world, x, y, z, fortune, player, type));
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int bonus)
	{
		switch (metadata)
		{
			case 0:
				return MathHelper.getRandomIntegerInRange(random, 2, 6);
			case 2:
				return MathHelper.getRandomIntegerInRange(random, 1, 3);
			default:
				return 0;
		}
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		switch (meta)
		{
			case 0:
				return 3.0F;
			case 1:
				return 4.5F;
			case 2:
				return 6.0F;
			default:
				return super.getBlockHardness(world, x, y, z);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		switch (meta)
		{
			case 0:
			case 1:
				return 5;
			case 2:
				return 8;
			default:
				return super.getLightValue(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		oreIcons = new IIcon[3];
		oreIcons[0] = iconRegister.registerIcon("caveworld:aquamarine_ore");
		oreIcons[1] = iconRegister.registerIcon("caveworld:aquamarine_block");
		oreIcons[2] = iconRegister.registerIcon("caveworld:randomite_ore");
		overlayIcons = new IIcon[2];
		overlayIcons[0] = iconRegister.registerIcon("caveworld:aquamarine_ore_overlay");
		overlayIcons[1] = iconRegister.registerIcon("caveworld:randomite_ore_overlay");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		if (metadata < 0 || metadata >= oreIcons.length)
		{
			return super.getIcon(side, metadata);
		}

		return oreIcons[metadata];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getOverlayIcon(int metadata)
	{
		switch (metadata)
		{
			case 0:
				return overlayIcons[0];
			case 2:
				return overlayIcons[1];
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBaseIcon(int metadata)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		for (int i = 0; i < oreIcons.length; ++i)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}
}