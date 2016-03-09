/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.common;

import caveworld.core.Caveworld;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HeldItemNBTAdjustMessage implements IMessage, IMessageHandler<HeldItemNBTAdjustMessage, IMessage>
{
	private NBTTagCompound nbt;

	public HeldItemNBTAdjustMessage() {}

	public HeldItemNBTAdjustMessage(ItemStack current)
	{
		this.nbt = current.getTagCompound();
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		nbt = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public IMessage onMessage(HeldItemNBTAdjustMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient())
		{
			EntityPlayer player = Caveworld.proxy.getClientPlayer();

			if (player != null)
			{
				ItemStack current = player.getCurrentEquippedItem();

				if (current != null)
				{
					current.setTagCompound(message.nbt);
				}
			}
		}
		else
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			ItemStack current = player.getCurrentEquippedItem();

			if (current != null)
			{
				current.setTagCompound(message.nbt);
			}
		}

		return null;
	}
}