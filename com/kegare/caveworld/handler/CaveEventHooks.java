package com.kegare.caveworld.handler;

import com.google.common.base.Strings;
import com.kegare.caveworld.block.BlockPortalCaveworld;
import com.kegare.caveworld.block.CaveBlocks;
import com.kegare.caveworld.core.Caveworld;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.Version;
import com.kegare.caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;

import java.security.SecureRandom;
import java.util.Random;

public class CaveEventHooks
{
	public static final CaveEventHooks instance = new CaveEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRenderOverlayText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc != null && mc.gameSettings.showDebugInfo && mc.thePlayer.dimension == Config.dimensionCaveworld)
		{
			for (String str : event.left)
			{
				if (!Strings.isNullOrEmpty(str) && str.startsWith("dim:"))
				{
					return;
				}
			}

			event.left.add("dim: " + mc.thePlayer.worldObj.provider.getDimensionName());
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			Caveworld.packetPipeline.sendPacketToPlayer(new Config.ConfigSyncPacket(), player);
			Caveworld.packetPipeline.sendPacketToPlayer(new WorldProviderCaveworld.DataSyncPacket(), player);

			if (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated())
			{
				ChatStyle style = new ChatStyle();
				style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Caveworld.metadata.url));
				StringBuilder message = new StringBuilder();
				message.append(StatCollector.translateToLocalFormatted("caveworld.version.message", EnumChatFormatting.AQUA + "Caveworld" + EnumChatFormatting.RESET));
				message.append(" : ").append(EnumChatFormatting.YELLOW).append(Version.LATEST.or(Version.CURRENT.orNull())).append(EnumChatFormatting.RESET);

				player.addChatMessage(new ChatComponentText(message.toString()).setChatStyle(style));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			if (player.dimension == Config.dimensionCaveworld)
			{
				player.setSpawnChunk(null, true, player.dimension);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		World world = event.world;
		Entity entity = event.entity;

		if (!world.isRemote && entity.dimension == Config.dimensionCaveworld)
		{
			if (entity instanceof EntityLiving && MathHelper.floor_double(entity.posY) >= world.provider.getActualHeight())
			{
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		Random random = new SecureRandom();
		EntityLivingBase living = event.entityLiving;
		World world = living.worldObj;
		double posX = living.posX;
		double posY = living.posY;
		double posZ = living.posZ;
		int looting = event.lootingLevel;

		if (!world.isRemote && living.dimension == Config.dimensionCaveworld)
		{
			if (living instanceof EntityBat)
			{
				event.drops.add(new EntityItem(world, posX, posY + 0.5D, posZ, new ItemStack(Items.coal, random.nextInt(3) + Math.min(looting, 3))));
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP && event.action != Action.LEFT_CLICK_BLOCK)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			WorldServer world = player.getServerForPlayer();
			int x = event.x;
			int y = event.y;
			int z = event.z;
			int face = event.face;
			ItemStack current = player.getCurrentEquippedItem();

			if (player.dimension == Config.dimensionCaveworld && world.getBlock(x, y, z) == Blocks.bed && world.isAirBlock(x, y + 1, z))
			{
				if (player.capabilities.isCreativeMode || player.getEntityData().getLong("Caveworld:LastSetTime") + 6000L < world.getTotalWorldTime())
				{
					player.getEntityData().setLong("Caveworld:LastSetTime", world.getTotalWorldTime());

					player.setSpawnChunk(new ChunkCoordinates(x, y + 1, z), true, player.dimension);
					player.addChatMessage(new ChatComponentTranslation("[%s] Respawn point set: %s, %s, %s", EnumChatFormatting.AQUA + "Caveworld" + EnumChatFormatting.RESET, x, y + 1, z));
				}

				event.setCanceled(true);

				return;
			}

			if (face == 0)
			{
				--y;
			}
			else if (face == 1)
			{
				++y;
			}
			else if (face == 2)
			{
				--z;
			}
			else if (face == 3)
			{
				++z;
			}
			else if (face == 4)
			{
				--x;
			}
			else if (face == 5)
			{
				++x;
			}

			if (player.isSneaking())
			{
				if (world.getBlock(x, y, z) == CaveBlocks.caveworld_portal)
				{
					world.playSoundAtEntity(player, "random.click", 0.8F, 1.5F);

					player.displayGUIChest(BlockPortalCaveworld.getInventory());
				}
			}
			else if (current != null && current.getItem() == Item.getItemFromBlock(Blocks.ender_chest) && CaveBlocks.caveworld_portal.func_150000_e(world, x, y, z))
			{
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "step.stone", 1.0F, 2.0F);
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			WorldProviderCaveworld.saveDimensionData();
			WorldProviderCaveworld.clearDimensionData();

			BlockPortalCaveworld.saveInventoryData();
		}
	}
}