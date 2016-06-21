package caveworld.network.client;

import caveworld.util.breaker.MultiBreakExecutor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MultiBreakCountMessage implements IMessage, IMessageHandler<MultiBreakCountMessage, IMessage>
{
	private int count;

	public MultiBreakCountMessage () {}

	public MultiBreakCountMessage(int count)
	{
		this.count = count;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		count = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(count);
	}

	@Override
	public IMessage onMessage(MultiBreakCountMessage message, MessageContext ctx)
	{
		MultiBreakExecutor.positionsCount.set(message.count);

		return null;
	}
}