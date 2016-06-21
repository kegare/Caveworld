package caveworld.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

public interface IBlockPortal
{
	@SideOnly(Side.CLIENT)
	public IIcon getPortalIcon();
}