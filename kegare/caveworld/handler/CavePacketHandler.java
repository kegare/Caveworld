package kegare.caveworld.handler;

import kegare.caveworld.core.Caveworld;
import kegare.caveworld.world.WorldProviderCaveworld;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class CavePacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if ("caveworld.sync".equals(packet.channel))
		{
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			Caveworld.dimensionCaveworld = dat.readInt();
			Caveworld.generateCaves = dat.readBoolean();
			Caveworld.generateLakes = dat.readBoolean();
			Caveworld.generateRavine = dat.readBoolean();
			Caveworld.generateMineshaft = dat.readBoolean();
			Caveworld.generateDungeon = dat.readBoolean();
			WorldProviderCaveworld.dimensionSeed = dat.readLong();
		}
	}

	public static Packet getPacketConfigSync()
	{
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeInt(Caveworld.dimensionCaveworld);
		dat.writeBoolean(Caveworld.generateCaves);
		dat.writeBoolean(Caveworld.generateLakes);
		dat.writeBoolean(Caveworld.generateRavine);
		dat.writeBoolean(Caveworld.generateMineshaft);
		dat.writeBoolean(Caveworld.generateDungeon);
		dat.writeLong(WorldProviderCaveworld.dimensionSeed);

		return new Packet250CustomPayload("caveworld.sync", dat.toByteArray());
	}
}