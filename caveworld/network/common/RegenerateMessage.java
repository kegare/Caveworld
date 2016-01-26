/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.common;

import caveworld.api.CaveworldAPI;
import caveworld.client.gui.GuiRegeneration;
import caveworld.core.Config;
import caveworld.util.CaveUtils;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class RegenerateMessage implements IMessage, IMessageHandler<RegenerateMessage, IMessage>
{
	private boolean backup = true;
	private boolean caveworld = true;
	private boolean cavern = true;

	public RegenerateMessage() {}

	public RegenerateMessage(boolean backup)
	{
		this.backup = backup;
	}

	public RegenerateMessage(boolean backup, boolean caveworld, boolean cavern)
	{
		this(backup);
		this.caveworld = caveworld;
		this.cavern = cavern;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		backup = buffer.readBoolean();
		caveworld = buffer.readBoolean();
		cavern = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(backup);
		buffer.writeBoolean(caveworld);
		buffer.writeBoolean(cavern);
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
			if (message.caveworld)
			{
				CaveUtils.regenerateDimension(CaveworldAPI.getDimension(), message.backup, Config.hardcore || Config.caveborn);
			}

			if (message.cavern)
			{
				CaveUtils.regenerateDimension(CaveworldAPI.getCavernDimension(), message.backup, Config.hardcore || Config.caveborn);
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