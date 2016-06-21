package caveworld.plugin.mceconomy;

import java.util.List;

import cpw.mods.fml.common.Optional.Interface;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import shift.mceconomy2.api.shop.IShop;

@Interface(iface = "shift.mceconomy2.api.shop.IShop", modid = MCEconomyPlugin.MODID, striprefs = true)
public interface IShopProductManager extends IShop
{
	public Configuration getConfig();

	public int getType();

	public boolean isReadOnly();

	public IShopProductManager setReadOnly(boolean flag);

	public boolean addShopProduct(IShopProduct product);

	public List<IShopProduct> getProducts();

	public void clearProducts();

	public void loadFromNBT(NBTTagList list);

	public NBTTagList saveToNBT();
}