/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.plugin.mceconomy;

import java.util.List;

import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import shift.mceconomy2.api.shop.IShop;

public interface IShopProductManager extends IShop
{
	public Configuration getConfig();

	public int getType();

	public boolean isReadOnly();

	public IShopProductManager setReadOnly(boolean flag);

	public boolean addShopProduct(IShopProduct product);

	public List<IShopProduct> getProducts();

	public void clearProducts();

	public NBTTagList saveNBTData();

	public void loadNBTData(NBTTagList list);
}