package com.kegare.caveworld.api;

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
	 * Checks if entity is in Caveworld.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Caveworld dimension.
	 */
	public boolean isEntityInCaveworld(Entity entity);
}