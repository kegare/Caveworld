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
	 * Returns dimension id of the Aqua Cavern dimension.
	 */
	public int getAquaCavernDimension();

	/**
	 * Returns dimension id of the Caveland dimension.
	 */
	public int getCavelandDimension();

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
	 * Checks if entity is in Aqua Cavern.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Aqua Cavern dimension.
	 */
	public boolean isEntityInAquaCavern(Entity entity);

	/**
	 * Checks if entity is in Caveland.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is in the Caveland dimension.
	 */
	public boolean isEntityInCaveland(Entity entity);

	/**
	 * Checks if entity is in dimensions for Caveworld mod.
	 * @param entity The entity
	 */
	public boolean isEntityInCaves(Entity entity);

	/**
	 * Checks if the dimension is dimensions for Caveworld mod.
	 * @param dim The dimension
	 */
	public boolean isCaveDimensions(int dim);

	/**
	 * Returns true if hardcore option.
	 */
	public boolean isHardcore();

	/**
	 * Returns caveborn type.
	 * @return 0: Disabled, 1: Caveworld, 2: Cavern, 3:Aqua Cavern, 4:Caveland
	 */
	public int getCaveborn();
}