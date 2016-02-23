package caveworld.api;

import net.minecraft.entity.Entity;

public interface ICaveAPIHandler
{
	public String getVersion();

	public int getDimension();

	public int getCavernDimension();

	public int getAquaCavernDimension();

	public int getCavelandDimension();

	public int getCaveniaDimension();

	public boolean isEntityInCaveworld(Entity entity);

	public boolean isEntityInCavern(Entity entity);

	public boolean isEntityInAquaCavern(Entity entity);

	public boolean isEntityInCaveland(Entity entity);

	public boolean isEntityInCavenia(Entity entity);

	public boolean isEntityInCaves(Entity entity);

	public boolean isCaveDimensions(int dim);

	public boolean isHardcore();

	public int getCaveborn();
}