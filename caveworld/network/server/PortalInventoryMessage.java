package caveworld.network.server;

import caveworld.block.CaveBlocks;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PortalInventoryMessage implements IMessage, IMessageHandler<PortalInventoryMessage, IMessage>
{
	private int portalX;
	private int portalY;
	private int portalZ;

	public PortalInventoryMessage() {}

	public PortalInventoryMessage(int x, int y, int z)
	{
		this.portalX = x;
		this.portalY = y;
		this.portalZ = z;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		portalX = buffer.readInt();
		portalY = buffer.readInt();
		portalZ = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(portalX);
		buffer.writeInt(portalY);
		buffer.writeInt(portalZ);
	}

	@Override
	public IMessage onMessage(PortalInventoryMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;

		if (player != null)
		{
			CaveBlocks.caveworld_portal.displayInventory(player, message.portalX, message.portalY, message.portalZ);
		}

		return null;
	}
}