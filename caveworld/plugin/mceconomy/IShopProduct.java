/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shift.mceconomy2.api.shop.IProduct;

public interface IShopProduct extends IProduct
{
	public ItemStack setItem(ItemStack item);

	public ItemStack getItem();

	public int setCost(int cost);

	public int getCost();

	public NBTTagCompound saveNBTData();

	public void loadNBTData(NBTTagCompound data);
}