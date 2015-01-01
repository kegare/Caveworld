/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.network.client;

import io.netty.buffer.ByteBuf;

import com.kegare.caveworld.client.gui.GuiIngameCaveworldMenu;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CaveworldMenuMessage implements IMessage, IMessageHandler<CaveworldMenuMessage, IMessage>
{
	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public void toBytes(ByteBuf buffer) {}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaveworldMenuMessage message, MessageContext ctx)
	{
		FMLClientHandler.instance().getClient().displayGuiScreen(new GuiIngameCaveworldMenu());

		return null;
	}
}