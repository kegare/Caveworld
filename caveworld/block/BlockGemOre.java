/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.block;

import java.util.List;
import java.util.Random;

import caveworld.api.event.RandomiteChanceEvent;
import caveworld.api.event.RandomiteChanceEvent.EventType;
import caveworld.core.CaveAchievementList;
import caveworld.core.Caveworld;
import caveworld.core.Config;
import caveworld.item.CaveItems;
import caveworld.item.ICaveniumTool;
import caveworld.util.ArrayListExtended;
import caveworld.util.SubItemHelper;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
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

	private ArrayListExtended<Item> cachedItems;

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
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune)
	{
		super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);

		switch (metadata)
		{
			case 2:
				doRandomiteChance(world, x, y, z, fortune);
				break;
		}
	}

	public void doRandomiteChance(World world, int x, int y, int z, int fortune)
	{
		EntityPlayer player = harvesters.get();

		if (MinecraftForge.EVENT_BUS.post(new RandomiteChanceEvent.Pre(world, x, y, z, fortune, player)))
		{
			return;
		}

		EventType type = EventType.NONE;

		if (!world.isRemote)
		{
			if (player != null)
			{
				player.triggerAchievement(CaveAchievementList.randomite);
			}

			switch (random.nextInt(3))
			{
				case 0:
					if (player != null && player.getActivePotionEffects().size() < 3)
					{
						Potion potion = null;

						while (potion == null || potion.getEffectiveness() <= 0.5D || player.isPotionActive(potion))
						{
							potion = Potion.potionTypes[player.getRNG().nextInt(Potion.potionTypes.length)];
						}

						if (potion != null)
						{
							player.addPotionEffect(new PotionEffect(potion.id, MathHelper.getRandomIntegerInRange(random, 10, 20) * 20));
							world.playSoundAtEntity(player, "dig.glass", 0.75F, 2.0F);

							type = EventType.POTION;
							break;
						}
					}
				default:
					for (int i = 0; i < Math.min(Math.max(fortune, 1), 3); ++i)
					{
						float f = random.nextFloat() * 0.8F + 0.1F;
						float f1 = random.nextFloat() * 0.8F + 0.1F;
						float f2 = random.nextFloat() * 0.8F + 0.1F;
						EntityItem entityitem = new EntityItem(world, x + f, y + f1, z + f2);

						if (cachedItems == null || cachedItems.isEmpty())
						{
							cachedItems = new ArrayListExtended();

							for (Item entry : GameData.getItemRegistry().typeSafeIterable())
							{
								cachedItems.addIfAbsent(entry);
							}
						}
						Item item;

						do
						{
							item = cachedItems.get(random.nextInt(cachedItems.size()));
						}
						while (item == null || item == Item.getItemFromBlock(Blocks.bedrock) || item instanceof ItemMonsterPlacer || item instanceof ICaveniumTool);

						if (item.isDamageable())
						{
							entityitem.setEntityItemStack(new ItemStack(item));
						}
						else
						{
							List<ItemStack> list = SubItemHelper.getSubItems(item);

							if (list.isEmpty())
							{
								entityitem.setEntityItemStack(new ItemStack(item));
							}
							else
							{
								if (list.size() == 1)
								{
									entityitem.setEntityItemStack(list.get(0));
								}
								else
								{
									entityitem.setEntityItemStack(list.get(random.nextInt(list.size())));
								}
							}
						}

						f = 0.05F;
						entityitem.motionX = (float)random.nextGaussian() * f;
						entityitem.motionY = (float)random.nextGaussian() * f + 0.2F;
						entityitem.motionZ = (float)random.nextGaussian() * f;

						world.spawnEntityInWorld(entityitem);
					}

					type = EventType.ITEM;
					break;
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