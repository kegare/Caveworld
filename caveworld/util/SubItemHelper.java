package caveworld.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SubItemHelper
{
	public static final Map<Block, List<ItemStack>> cachedSubBlocks = Maps.newHashMap();
	public static final Map<Item, List<ItemStack>> cachedSubItems = Maps.newHashMap();

	public static void cacheSubBlocks(Side side)
	{
		cachedSubBlocks.clear();

		for (Block block : GameData.getBlockRegistry().typeSafeIterable())
		{
			try
			{
				List<ItemStack> list = Lists.newArrayList();
				Item item = Item.getItemFromBlock(block);

				if (item == null)
				{
					cachedSubBlocks.put(block, list);
					continue;
				}

				if (side == Side.CLIENT)
				{
					CreativeTabs tab = block.getCreativeTabToDisplayOn();

					if (tab == null)
					{
						tab = CreativeTabs.tabAllSearch;
					}

					block.getSubBlocks(item, tab, list);
				}

				if (list.isEmpty())
				{
					ItemStack stack;
					String last = null;
					String name = null;

					for (int i = 0; i < 16; ++i)
					{
						stack = new ItemStack(block, 1, i);
						name = stack.getDisplayName();

						if (Strings.isNullOrEmpty(last))
						{
							list.add(stack);
						}
						else if (!last.equals(name))
						{
							list.add(stack);
						}

						last = name;
					}

					if (list.size() > 1)
					{
						list.remove(list.size() - 1);
					}
				}

				cachedSubBlocks.put(block, list);
			}
			catch (Throwable e) {}
		}
	}

	public static void cacheSubItems(Side side)
	{
		cachedSubItems.clear();

		for (Item item : GameData.getItemRegistry().typeSafeIterable())
		{
			try
			{
				List<ItemStack> list = Lists.newArrayList();

				if (item.isDamageable())
				{
					list.add(new ItemStack(item));
					cachedSubItems.put(item, list);
					continue;
				}

				if (side == Side.CLIENT)
				{
					CreativeTabs tab = item.getCreativeTab();

					if (tab == null)
					{
						tab = CreativeTabs.tabAllSearch;
					}

					item.getSubItems(item, tab, list);
				}

				if (list.isEmpty())
				{
					ItemStack stack;
					String last = null;
					String name = null;

					for (int i = 0; i < 32767; ++i)
					{
						stack = new ItemStack(item, 1, i);
						name = stack.getDisplayName();

						if (Strings.isNullOrEmpty(last))
						{
							list.add(stack);
						}
						else if (!last.equals(name))
						{
							list.add(stack);
						}

						last = name;
					}

					if (list.size() > 1)
					{
						list.remove(list.size() - 1);
					}
				}

				cachedSubItems.put(item, list);
			}
			catch (Throwable e) {}
		}
	}

	public static List<ItemStack> getSubBlocks(Block block)
	{
		return block == null || !cachedSubBlocks.containsKey(block) ? new ArrayList<ItemStack>() : cachedSubBlocks.get(block);
	}

	public static List<ItemStack> getSubItems(Item item)
	{
		return item == null || !cachedSubItems.containsKey(item) ? new ArrayList<ItemStack>() : cachedSubItems.get(item);
	}
}