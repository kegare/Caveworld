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
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CaverAdjustMessage implements IMessage, IMessageHandler<CaverAdjustMessage, IMessage>
{
	private Entity entity;
	private int entityId;
	private NBTTagCompound data = new NBTTagCompound();

	public CaverAdjustMessage() {}

	public CaverAdjustMessage(Entity entity)
	{
		this.entity = entity;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		entityId = buffer.readInt();
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		CaverAPI.saveData(entity, data);

		buffer.writeInt(entity.getEntityId());
		ByteBufUtils.writeTag(buffer, data);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaverAdjustMessage message, MessageContext ctx)
	{
		Entity ent = FMLClientHandler.instance().getWorldClient().getEntityByID(message.entityId);

		if (ent != null)
		{
			CaverAPI.loadData(ent, message.data);
		}
		else
		{
			FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", message.entityId);
		}

		return null;
	}
}