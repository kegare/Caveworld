package kegare.caveworld.handler;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import kegare.caveworld.core.Config;
import kegare.caveworld.util.Version;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.classloading.FMLForgePlugin;

public class CaveConnectionHandler implements IConnectionHandler
{
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
	{
		PacketDispatcher.sendPacketToPlayer(CavePacketHandler.getPacketDataSync(), player);
	}

	@Override
	public String connectionReceived(NetLoginHandler loginHandler, INetworkManager manager)
	{
		return null;
	}

	@Override
	public void connectionOpened(NetHandler clientHandler, String server, int port, INetworkManager manager) {}

	@Override
	public void connectionOpened(NetHandler clientHandler, MinecraftServer server, INetworkManager manager) {}

	@Override
	public void connectionClosed(INetworkManager manager) {}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
	{
		if (!FMLForgePlugin.RUNTIME_DEOBF || Config.versionNotify && Version.isOutdated())
		{
			clientHandler.getPlayer().sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocalFormatted("caveworld.version.message", EnumChatFormatting.AQUA + "Caveworld" + EnumChatFormatting.RESET) + " : " + EnumChatFormatting.YELLOW + Version.LATEST.or(Version.CURRENT.orNull())));
		}
	}
}