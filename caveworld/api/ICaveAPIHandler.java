package caveworld.api;

import net.minecraft.entity.Entity;

public interface ICaveAPIHandler
{
	/**
	 * Returns current mod version of Caveworld
	 */
	public String getVersion();

	/**
	 * Returns dimension id of the Caveworld dimension.
	 */
	public int getDimension();

	/**
	 * Returns dimension id of the Cavern dimension.
	 */
	public int getCavernDimension();

	/**
	 * Checks if entity is in Caveworld.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Caveworld dimension.
	 */
	public boolean isEntityInCaveworld(Entity entity);

	/**
	 * Checks if entity is in Cavern.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Cavern dimension.
	 */
	public boolean isEntityInCavern(Entity entity);

	/**
	 * Checks if entity is in dimensions for Caveworld mod.
	 * @param entity The entity
	 */
	public boolean isEntityInCaves(Entity entity);
}