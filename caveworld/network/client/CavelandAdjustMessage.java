package caveworld.network.client;

import caveworld.world.ChunkProviderCaveland;
import caveworld.world.WorldProviderCaveland;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class CavelandAdjustMessage implements IMessage, IMessageHandler<CavelandAdjustMessage, IMessage>
{
	private int dimensionId;
	private NBTTagCompound data;

	public CavelandAdjustMessage() {}

	public CavelandAdjustMessage(int dim, NBTTagCompound data)
	{
		this.dimensionId = dim;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		dimensionId = buffer.readInt();
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(dimensionId);
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	public IMessage onMessage(CavelandAdjustMessage message, MessageContext ctx)
	{
		ChunkProviderCaveland.dimensionId = message.dimensionId;
		WorldProviderCaveland.loadDimData(message.data);

		return null;
	}
}