package kegare.caveworld.handler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import kegare.caveworld.core.Config;
import kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class CavePacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if ("caveworld.sync".equals(packet.channel))
		{
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			Config.dimensionCaveworld = dat.readInt();
			Config.subsurfaceHeight = dat.readInt();
			Config.generateCaves = dat.readBoolean();
			Config.generateLakes = dat.readBoolean();
			Config.generateRavine = dat.readBoolean();
			Config.generateMineshaft = dat.readBoolean();
			Config.generateDungeon = dat.readBoolean();
			WorldProviderCaveworld.dimensionSeed = dat.readLong();
			WorldProviderCaveworld.subsurfaceHeight = dat.readInt();
		}
	}

	public static Packet getPacketDataSync()
	{
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeInt(Config.dimensionCaveworld);
		dat.writeInt(Config.subsurfaceHeight);
		dat.writeBoolean(Config.generateCaves);
		dat.writeBoolean(Config.generateLakes);
		dat.writeBoolean(Config.generateRavine);
		dat.writeBoolean(Config.generateMineshaft);
		dat.writeBoolean(Config.generateDungeon);
		dat.writeLong(WorldProviderCaveworld.dimensionSeed);
		dat.writeInt(WorldProviderCaveworld.subsurfaceHeight);

		return new Packet250CustomPayload("caveworld.sync", dat.toByteArray());
	}
}