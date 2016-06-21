package caveworld.network.client;

import caveworld.client.gui.MenuType;
import caveworld.core.Caveworld;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class CaveworldMenuMessage implements IMessage, IMessageHandler<CaveworldMenuMessage, IMessage>
{
	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public void toBytes(ByteBuf buffer) {}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaveworldMenuMessage message, MessageContext ctx)
	{
		Caveworld.proxy.displayMenu(MenuType.DEFAULT);

		return null;
	}
}