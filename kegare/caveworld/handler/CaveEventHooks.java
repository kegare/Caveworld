package kegare.caveworld.handler;

import com.google.common.base.Strings;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kegare.caveworld.block.BlockPortalCaveworld;
import kegare.caveworld.block.CaveBlock;
import kegare.caveworld.core.Config;
import kegare.caveworld.util.CaveLog;
import kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Random;

public class CaveEventHooks
{
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		try
		{
			event.manager.addSound("caveworld:caveworld_portal.ogg");
		}
		catch (Exception e)
		{
			CaveLog.severe(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe(priority = EventPriority.HIGH)
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

	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		int face = event.face;
		ItemStack current = player.getCurrentEquippedItem();

		if (!world.isRemote && event.action != Action.LEFT_CLICK_BLOCK)
		{
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
				if (world.getBlockId(x, y, z) == CaveBlock.portalCaveworld.blockID)
				{
					world.playSoundAtEntity(player, "random.click", 0.8F, 1.5F);

					player.displayGUIChest(BlockPortalCaveworld.getInventory());
				}
			}
			else if (current != null && current.itemID == Block.enderChest.blockID && CaveBlock.portalCaveworld.tryToCreatePortal(world, x, y, z))
			{
				world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "step.stone", 1.0F, 2.0F);
			}
		}
	}

	@ForgeSubscribe
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

	@ForgeSubscribe
	public void onLivingDrops(LivingDropsEvent event)
	{
		Random random = new Random();
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
				event.drops.add(new EntityItem(world, posX, posY + 0.5D, posZ, new ItemStack(Item.coal, random.nextInt(3) + Math.min(looting, 3))));
			}
		}
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			WorldProviderCaveworld.clearWorldData();
		}
	}

	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save event)
	{
		World world = event.world;

		if (!world.isRemote && world.provider.dimensionId == Config.dimensionCaveworld)
		{
			BlockPortalCaveworld.saveInventoryData();
		}
	}
}