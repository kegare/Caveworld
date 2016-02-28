/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import cpw.mods.fml.common.Optional.Interface;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shift.mceconomy2.api.shop.IProduct;

@Interface(iface = "shift.mceconomy2.api.shop.IProduct", modid = MCEconomyPlugin.MODID, striprefs = true)
public interface IShopProduct extends IProduct
{
	public ItemStack setItem(ItemStack item);

	public ItemStack getItem();

	public int setCost(int cost);

	public int getCost();

	public int setMinerRank(int rank);

	public int getMinerRank();

	public void loadFromNBT(NBTTagCompound nbt);

	public NBTTagCompound saveToNBT();
}