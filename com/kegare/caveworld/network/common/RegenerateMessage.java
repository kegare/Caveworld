/*
 * Caveworld
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.caveworld.network.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import com.kegare.caveworld.api.CaveworldAPI;
import com.kegare.caveworld.client.gui.GuiRegeneration;
import com.kegare.caveworld.core.Config;
import com.kegare.caveworld.util.CaveUtils;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RegenerateMessage implements IMessage, IMessageHandler<RegenerateMessage, IMessage>
{
	private boolean backup = true;
	private boolean caveworld = true, deep = true, aqua = true;

	public RegenerateMessage() {}

	public RegenerateMessage(boolean backup)
	{
		this.backup = backup;
	}

	public RegenerateMessage(boolean backup, boolean caveworld, boolean deep, boolean aqua)
	{
		this(backup);
		this.caveworld = caveworld;
		this.deep = deep;
		this.aqua = aqua;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		backup = buffer.readBoolean();
		caveworld = buffer.readBoolean();
		deep = buffer.readBoolean();
		aqua = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(backup);
		buffer.writeBoolean(caveworld);
		buffer.writeBoolean(deep);
		buffer.writeBoolean(aqua);
	}

	@Override
	public IMessage onMessage(RegenerateMessage message, MessageContext ctx)
	{
		if (ctx.side.isClient())
		{
			FMLCommonHandler.instance().showGuiScreen(new GuiRegeneration(message.backup));
		}
		else
		{
			boolean ret = Config.hardcore || Config.caveborn;

			if (message.caveworld)
			{
				CaveUtils.regenerateDimension(CaveworldAPI.getDimension(), message.backup, ret);
			}

			if (message.deep && CaveworldAPI.isDeepExist())
			{
				CaveUtils.regenerateDimension(CaveworldAPI.getDeepDimension(), message.backup, ret);
			}

			if (message.aqua && CaveworldAPI.isAquaExist())
			{
				CaveUtils.regenerateDimension(CaveworldAPI.getAquaDimension(), message.backup, ret);
			}
		}

		return null;
	}

	public static class ProgressNotify implements IMessage, IMessageHandler<ProgressNotify, IMessage>
	{
		private int task = -1;

		public ProgressNotify() {}

		public ProgressNotify(int task)
		{
			this.task = task;
		}

		@Override
		public void fromBytes(ByteBuf buffer)
		{
			task = buffer.readInt();
		}

		@Override
		public void toBytes(ByteBuf buffer)
		{
			buffer.writeInt(task);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public IMessage onMessage(ProgressNotify message, MessageContext ctx)
		{
			Minecraft mc = FMLClientHandler.instance().getClient();

			if (mc.currentScreen != null && mc.currentScreen instanceof GuiRegeneration)
			{
				((GuiRegeneration)mc.currentScreen).updateProgress(message.task);
			}

			return null;
		}
	}
}