package caveworld.network.common;

import caveworld.config.Config;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class OpRemoteCheckMessage implements IMessage, IMessageHandler<OpRemoteCheckMessage, IMessage>
{
	public static boolean operator;

	private boolean op;

	public OpRemoteCheckMessage() {}

	public OpRemoteCheckMessage(boolean op)
	{
		this.op = op;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		op = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(op);
	}

	@Override
	public IMessage onMessage(OpRemoteCheckMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient())
		{
			operator = message.op;

			return null;
		}

		EntityPlayerMP player = ctx.getServerHandler().playerEntity;

		return new OpRemoteCheckMessage(Config.remoteConfig && player.mcServer.getConfigurationManager().func_152596_g(player.getGameProfile()));
	}
}