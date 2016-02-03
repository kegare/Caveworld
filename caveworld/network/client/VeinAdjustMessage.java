/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveVeinManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class VeinAdjustMessage implements IMessage, IMessageHandler<VeinAdjustMessage, IMessage>
{
	private int type;
	private NBTTagCompound data;

	public VeinAdjustMessage() {}

	public VeinAdjustMessage(ICaveVeinManager manager)
	{
		this.type = manager.getType();
		this.data = new NBTTagCompound();
		this.data.setTag("Entries", manager.saveNBTData());
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		type = buffer.readInt();
		data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(type);
		ByteBufUtils.writeTag(buffer, data);
	}

	@Override
	public IMessage onMessage(VeinAdjustMessage message, MessageContext ctx)
	{
		ICaveVeinManager manager;

		switch (message.type)
		{
			case 1:
				manager = CaveworldAPI.veinCavernManager;
				break;
			default:
				manager = CaveworldAPI.veinManager;
				break;
		}

		manager.clearCaveVeins();
		manager.loadNBTData(message.data.getTagList("Entries", NBT.TAG_COMPOUND));
		manager.setReadOnly(true);

		return null;
	}
}
