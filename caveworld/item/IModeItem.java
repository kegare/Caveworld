package caveworld.item;

import net.minecraft.item.ItemStack;

public interface IModeItem
{
	public long getHighlightStart();

	public void setHighlightStart(long time);

	public String getModeName(ItemStack itemstack);

	public String getModeDisplayName(ItemStack itemstack);

	public String getModeInfomation(ItemStack itemstack);
}