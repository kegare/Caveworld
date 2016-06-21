package caveworld.network.client;

import caveworld.world.ChunkProviderAquaCavern;
import caveworld.world.ChunkProviderCaveland;
import caveworld.world.ChunkProviderCavenia;
import caveworld.world.ChunkProviderCavern;
import caveworld.world.ChunkProviderCaveworld;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BrightnessAdjustMessage implements IMessage, IMessageHandler<BrightnessAdjustMessage, IMessage>
{
	private float[] brightness;

	public BrightnessAdjustMessage() {}

	public BrightnessAdjustMessage(float... brightness)
	{
		this.brightness = brightness;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		brightness = new float[buffer.readInt()];

		for (int i = 0; i < brightness.length; ++i)
		{
			brightness[i] = buffer.readFloat();
		}
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(brightness.length);

		for (int i = 0; i < brightness.length; ++i)
		{
			buffer.writeFloat(brightness[i]);
		}
	}

	@Override
	public IMessage onMessage(BrightnessAdjustMessage message, MessageContext ctx)
	{
		ChunkProviderCaveworld.caveBrightness = message.brightness[0];
		ChunkProviderCavern.caveBrightness = message.brightness[1];
		ChunkProviderAquaCavern.caveBrightness = message.brightness[2];
		ChunkProviderCaveland.caveBrightness = message.brightness[3];
		ChunkProviderCavenia.caveBrightness = message.brightness[4];

		return null;
	}
}