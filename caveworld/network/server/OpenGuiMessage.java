package caveworld.network.server;

import caveworld.core.Caveworld;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class OpenGuiMessage implements IMessage, IMessageHandler<OpenGuiMessage, IMessage>
{
	private int id;

	public OpenGuiMessage() {}

	public OpenGuiMessage(int id)
	{
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		id = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(id);
	}

	@Override
	public IMessage onMessage(OpenGuiMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;

		player.openGui(Caveworld.instance, message.id, player.worldObj, 0, 0, 0);

		return null;
	}
}