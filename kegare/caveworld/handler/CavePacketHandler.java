package kegare.caveworld.handler;

import kegare.caveworld.core.Config;
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
		if ("caveworld.config".equals(packet.channel))
		{
			ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
			Config.dimensionCaveworld = dat.readInt();
			Config.biomeCaveworld = dat.readInt();
			Config.generateCaves = dat.readBoolean();
			Config.generateLakes = dat.readBoolean();
			Config.generateRavine = dat.readBoolean();
			Config.generateMineshaft = dat.readBoolean();
			Config.generateDungeon = dat.readBoolean();
		}
	}

	public static Packet getPacketConfigSync()
	{
		ByteArrayDataOutput dat = ByteStreams.newDataOutput();
		dat.writeInt(Config.dimensionCaveworld);
		dat.writeInt(Config.biomeCaveworld);
		dat.writeBoolean(Config.generateCaves);
		dat.writeBoolean(Config.generateLakes);
		dat.writeBoolean(Config.generateRavine);
		dat.writeBoolean(Config.generateMineshaft);
		dat.writeBoolean(Config.generateDungeon);

		return new Packet250CustomPayload("caveworld.config", dat.toByteArray());
	}
}