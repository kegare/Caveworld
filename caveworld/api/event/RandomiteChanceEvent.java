package caveworld.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class RandomiteChanceEvent extends Event
{
	public enum EventType
	{
		NONE,
		ITEM,
		POTION,
		EXPLODE
	}

	public final World world;
	public final int blockX;
	public final int blockY;
	public final int blockZ;
	public final int fortune;
	public final EntityPlayer player; //Should check for null

	public RandomiteChanceEvent(World world, int x, int y, int z, int fortune, EntityPlayer player)
	{
		this.world = world;
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
		this.fortune = fortune;
		this.player = player;
	}

	/**
	 * RandomiteChanceEvent.Pre is fired when harvests a Randomite Ore.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	@Cancelable
	public static class Pre extends RandomiteChanceEvent
	{
		public Pre(World world, int x, int y, int z, int fortune, EntityPlayer player)
		{
			super(world, x, y, z, fortune, player);
		}
	}

	/**
	 * RandomiteChanceEvent.Post is fired when randomite chance event finished.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	public static class Post extends RandomiteChanceEvent
	{
		public final EventType type;

		public Post(World world, int x, int y, int z, int fortune, EntityPlayer player, EventType type)
		{
			super(world, x, y, z, fortune, player);
			this.type = type;
		}
	}
}