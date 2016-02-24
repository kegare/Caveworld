/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.api.CaverAPI;
import caveworld.core.CaverManager.Caver;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class CaverAdjustMessage implements IMessage, IMessageHandler<CaverAdjustMessage, IMessage>
{
	private int entityId, point, rank;

	public CaverAdjustMessage() {}

	public CaverAdjustMessage(Caver caver)
	{
		this.entityId = caver.getEntity().getEntityId();
		this.point = caver.getMiningPoint();
		this.rank = caver.getRank();
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		entityId = buffer.readInt();
		point = buffer.readInt();
		rank = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(entityId);
		buffer.writeInt(point);
		buffer.writeInt(rank);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaverAdjustMessage message, MessageContext ctx)
	{
		Entity entity = FMLClientHandler.instance().getWorldClient().getEntityByID(message.entityId);

		if (entity != null)
		{
			CaverAPI.setMiningPoint(entity, message.point);
			CaverAPI.setMinerRank(entity, message.rank);
		}
		else
		{
			FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", message.entityId);
		}

		return null;
	}
}