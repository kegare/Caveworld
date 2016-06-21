package caveworld.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import caveworld.api.CaveworldAPI;
import caveworld.api.event.RandomiteChanceEvent;
import caveworld.api.event.RandomiteChanceEvent.EventType;
import caveworld.config.Config;
import caveworld.core.CaveAchievementList;
import caveworld.core.Caveworld;
import caveworld.item.CaveItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
	/*
	 * Metadata
	 * 0: Aquamarine Ore
	 * 1: Block of Aquamarine
	 * 2: Randomite Ore
	 * 3: Magnite Ore
	 * 4: Block of Magnite
	 * 5: Hexcite Ore
	 * 6: Block of Hexcite
	 * 7: Infitite Ore
	 * 8: Block of Infitite
	 */

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
		this.setHarvestLevel("pickaxe", 2, 5);
		this.setHarvestLevel("pickaxe", 2, 6);
		this.setHarvestLevel("pickaxe", 3, 7);
		this.setHarvestLevel("pickaxe", 3, 8);
		this.setCreativeTab(Caveworld.tabCaveworld);
	}

	@Override
	public int getRenderType()
	{
		return Config.oreRenderOverlay ? Config.RENDER_TYPE_OVERLAY : super.getRenderType();
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune)
	{
		switch (metadata)
		{
			case 0:
			case 5:
			case 7:
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
			case 1:
			case 3:
			case 4:
			case 6:
			case 7:
			case 8:
				return 1;
		}

		return super.quantityDropped(meta, fortune, random);
	}

	@Override
	public int damageDropped(int metadata)
	{
		switch (metadata)
		{
			case 5:
				return 3;
			case 7:
				return 5;
			default:
				return metadata;
		}
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z);
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

		if (cavenia || random.nextInt(3) == 0)
		{
			if (player == null)
			{
				if (random.nextInt(4) == 0)
				{
					if (!world.isRemote)
					{
						world.newExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 1.5F, false, true);
					}

					drops.clear();
					type = EventType.OTHER;
				}
			}
			else if (player.getActivePotionEffects().size() < (cavenia ? 5 : 3))
			{
				if (!world.isRemote)
				{
					Potion potion = null;
					List<Potion> potions = getRandomitePotions();

					if (!potions.isEmpty())
					{
						while (potion == null || player.isPotionActive(potion))
						{
							potion = potions.get(player.getRNG().nextInt(potions.size()));
						}
					}

					if (potion != null)
					{
						player.addPotionEffect(new PotionEffect(potion.getId(), (cavenia ? MathHelper.getRandomIntegerInRange(random, 30, 60) : MathHelper.getRandomIntegerInRange(random, 10, 20)) * 20));
						world.playSoundAtEntity(player, "dig.glass", 0.75F, 2.0F);
					}
				}

				drops.clear();
				type = EventType.POTION;
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
			case 7:
				return MathHelper.getRandomIntegerInRange(random, 6, 10);
		}

		return 0;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		switch (meta)
		{
			case 0:
			case 3:
			case 5:
				return 3.0F;
			case 1:
			case 4:
			case 6:
				return 4.5F;
			case 2:
				return 6.0F;
			case 7:
				return 100.0F;
			case 8:
				return 10.0F;
		}

		return super.getBlockHardness(world, x, y, z);
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
	{
		int meta = world.getBlockMetadata(x, y, z);

		switch (meta)
		{
			case 7:
			case 8:
				return 500.0F;
		}

		return super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
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
		}

		return super.getLightValue(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		oreIcons = new IIcon[9];
		oreIcons[0] = iconRegister.registerIcon("caveworld:aquamarine_ore");
		oreIcons[1] = iconRegister.registerIcon("caveworld:aquamarine_block");
		oreIcons[2] = iconRegister.registerIcon("caveworld:randomite_ore");
		oreIcons[3] = iconRegister.registerIcon("caveworld:magnite_ore");
		oreIcons[4] = iconRegister.registerIcon("caveworld:magnite_block");
		oreIcons[5] = iconRegister.registerIcon("caveworld:hexcite_ore");
		oreIcons[6] = iconRegister.registerIcon("caveworld:hexcite_block");
		oreIcons[7] = iconRegister.registerIcon("caveworld:infitite_ore");
		oreIcons[8] = iconRegister.registerIcon("caveworld:infitite_block");
		overlayIcons = new IIcon[5];
		overlayIcons[0] = iconRegister.registerIcon("caveworld:aquamarine_ore_overlay");
		overlayIcons[1] = iconRegister.registerIcon("caveworld:randomite_ore_overlay");
		overlayIcons[2] = iconRegister.registerIcon("caveworld:magnite_ore_overlay");
		overlayIcons[3] = iconRegister.registerIcon("caveworld:hexcite_ore_overlay");
		overlayIcons[4] = iconRegister.registerIcon("caveworld:infitite_ore_overlay");
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
			case 3:
				return overlayIcons[2];
			case 5:
				return overlayIcons[3];
			case 7:
				return overlayIcons[4];
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

	public static List<Potion> getRandomitePotions()
	{
		List<Potion> potions = Lists.newArrayList();

		for (int id : Config.randomitePotions)
		{
			Potion potion = Potion.potionTypes[id];

			if (potion != null)
			{
				potions.add(potion);
			}
		}

		return potions;
	}
}