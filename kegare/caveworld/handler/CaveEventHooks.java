package kegare.caveworld.handler;

import kegare.caveworld.core.CaveBlock;
import kegare.caveworld.core.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class CaveEventHooks
{
	@ForgeSubscribe
	public void doCreatePortal(PlayerInteractEvent event)
	{
		EntityPlayer player = event.entityPlayer;
		ItemStack itemstack = player.getCurrentEquippedItem();
		World world = player.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		int face = event.face;

		if (event.action == Action.RIGHT_CLICK_BLOCK && (player.dimension == 0 || player.dimension == Config.dimensionCaveworld))
		{
			if (itemstack != null && itemstack.itemID == Item.emerald.itemID && world.getBlockId(x, y, z) == Block.cobblestoneMossy.blockID)
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

				if (player.canPlayerEdit(x, y, z, face, itemstack) && world.isAirBlock(x, y, z) && CaveBlock.portalCaveworld.tryToCreatePortal(world, x, y, z))
				{
					world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "step.stone", 1.0F, 2.0F);

					if (!player.capabilities.isCreativeMode)
					{
						--itemstack.stackSize;
					}
				}
			}
		}
	}
}