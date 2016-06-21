package caveworld.network.client;

import caveworld.api.CaverAPI;
import caveworld.config.manager.CaverManager.Caver;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class CaverAdjustMessage implements IMessage, IMessageHandler<CaverAdjustMessage, IMessage>
{
	private int point, rank;

	public CaverAdjustMessage() {}

	public CaverAdjustMessage(Caver caver)
	{
		this.point = caver.getMiningPoint();
		this.rank = caver.getRank();
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		point = buffer.readInt();
		rank = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(point);
		buffer.writeInt(rank);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaverAdjustMessage message, MessageContext ctx)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (player != null)
		{
			CaverAPI.setMiningPoint(player, message.point);
			CaverAPI.setMinerRank(player, message.rank);
		}

		return null;
	}
}