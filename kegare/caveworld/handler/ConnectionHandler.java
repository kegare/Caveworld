package kegare.caveworld.handler;

import kegare.caveworld.core.Config;
import kegare.caveworld.util.Version;
import kegare.caveworld.util.Color;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler
{
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
		//NOOP
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
	{
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
	{
		//NOOP
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
	{
		//NOOP
	}

	@Override
	public void connectionClosed(INetworkManager manager)
	{
		//NOOP
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
	{
		if (Config.versionCheck && Version.isOutdated())
		{
			StringBuilder message = new StringBuilder();
			message.append(" A new ").append(Color.AQUA).append("Caveworld").append(Color.WHITE);
			message.append(" version is available : ").append(Color.YELLOW).append(Version.LATEST);

			clientHandler.getPlayer().addChatMessage(message.toString());
		}
	}
}