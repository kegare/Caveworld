package kegare.caveworld.handler;

import kegare.caveworld.core.Caveworld;
import kegare.caveworld.util.CaveLog;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveEventHooks
{
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onSoundLoad(SoundLoadEvent event)
	{
		try
		{
			event.manager.addSound("caveworld:portal/travel.ogg");
		}
		catch (Exception e)
		{
			CaveLog.exception(e);
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onRenderOverlayText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (Caveworld.showDebugDim && mc.gameSettings.showDebugInfo)
		{
			event.left.add("dim: " + mc.thePlayer.worldObj.provider.getDimensionName());
		}
	}

	@ForgeSubscribe
	public void onChunkLoad(ChunkEvent.Load event)
	{
		World world = event.world;
		Chunk chunk = event.getChunk();

		if (!world.isRemote && chunk.isChunkLoaded)
		{
			if (world.provider.dimensionId == Caveworld.dimensionCaveworld)
			{
				for (int x = 0; x < 16; ++x)
				{
					for (int z = 0; z < 16; ++z)
					{
						if (chunk.getBlockID(x, 127, z) != Block.bedrock.blockID)
						{
							chunk.setBlockIDWithMetadata(x, 127, z, Block.bedrock.blockID, 0);
						}
					}
				}
			}
		}
	}
}