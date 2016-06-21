package caveworld.block;

import caveworld.api.CaverAPI;
import caveworld.api.CaveworldAPI;
import caveworld.client.gui.MenuType;
import caveworld.world.TeleporterAquaCavern;
import caveworld.world.WorldProviderAquaCavern;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlockPortalAquaCavern extends BlockCavePortal
{
	public BlockPortalAquaCavern(String name)
	{
		super(name);
		this.setBlockTextureName("caveworld:aqua_cavern_portal");
	}

	@Override
	public int getType()
	{
		return WorldProviderAquaCavern.TYPE;
	}

	@Override
	public MenuType getMenuType()
	{
		return MenuType.AQUA_CAVERN_PORTAL;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CaveworldAPI.isEntityInAquaCavern(entity);
	}

	@Override
	public int getDimension()
	{
		return CaveworldAPI.getAquaCavernDimension();
	}

	@Override
	public int getLastDimension(Entity entity)
	{
		return CaverAPI.getAquaCavernLastDimension(entity);
	}

	@Override
	public void setLastDimension(Entity entity, int dim)
	{
		CaverAPI.setAquaCavernLastDimension(entity, dim);
	}

	@Override
	public Teleporter getTeleporter(WorldServer worldServer, boolean brick)
	{
		return new TeleporterAquaCavern(worldServer, brick);
	}
}