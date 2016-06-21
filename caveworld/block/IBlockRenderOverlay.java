package caveworld.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

public interface IBlockRenderOverlay
{
	@SideOnly(Side.CLIENT)
	public IIcon getOverlayIcon(int metadata);

	@SideOnly(Side.CLIENT)
	public IIcon getBaseIcon(int metadata);
}