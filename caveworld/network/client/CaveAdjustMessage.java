package caveworld.network.client;

import caveworld.world.CaveSaveHandler;
import caveworld.world.WorldProviderAquaCavern;
import caveworld.world.WorldProviderCaveland;
import caveworld.world.WorldProviderCavenia;
import caveworld.world.WorldProviderCavern;
import caveworld.world.WorldProviderCaveworld;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CaveAdjustMessage implements IMessage, IMessageHandler<CaveAdjustMessage, IMessage>
{
	private int type;
	private CaveSaveHandler handler;

	public CaveAdjustMessage() {}

	public CaveAdjustMessage(int type, CaveSaveHandler handler)
	{
		this.type = type;
		this.handler = handler;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		type = buffer.readInt();

		switch (type)
		{
			case WorldProviderCaveworld.TYPE:
				handler = WorldProviderCaveworld.saveHandler;
				break;
			case WorldProviderCavern.TYPE:
				handler = WorldProviderCavern.saveHandler;
				break;
			case WorldProviderAquaCavern.TYPE:
				handler = WorldProviderAquaCavern.saveHandler;
				break;
			case WorldProviderCaveland.TYPE:
				handler = WorldProviderCaveland.saveHandler;
				break;
			case WorldProviderCavenia.TYPE:
				handler = WorldProviderCavenia.saveHandler;
				break;
		}

		handler.readFromBuffer(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(type);
		handler.writeToBuffer(buffer);
	}

	@Override
	public IMessage onMessage(CaveAdjustMessage message, MessageContext ctx)
	{
		return null;
	}
}