package caveworld.block;

import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.client.gui.MenuType;
import caveworld.world.TeleporterCaveland;
import caveworld.world.WorldProviderCaveland;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlockPortalCaveland extends BlockCavePortal
{
	public BlockPortalCaveland(String name)
	{
		super(name);
		this.setBlockTextureName("caveworld:caveland_portal");
	}

	@Override
	public int getType()
	{
		return WorldProviderCaveland.TYPE;
	}

	@Override
	public MenuType getMenuType()
	{
		return MenuType.CAVELAND_PORTAL;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CaveworldAPI.isEntityInCaveland(entity);
	}

	@Override
	public int getDimension()
	{
		return CaveworldAPI.getCavelandDimension();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return CaverAPI.getCavelandLastDimension(entity);
	}

	@Override
	public void setLastDimension(Entity entity, int dim)
	{
		CaverAPI.setCavelandLastDimension(entity, dim);
	}

	@Override
	public Teleporter getTeleporter(WorldServer worldServer, boolean brick)
	{
		return new TeleporterCaveland(worldServer, brick);
	}
}