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
import net.minecraft.entity.player.EntityPlayerMP;

public class RegenerateMessage implements IMessage, IMessageHandler<RegenerateMessage, IMessage>
{
	private boolean backup = true;
	private boolean caveworld = true;
	private boolean cavern = true;
	private boolean aquaCavern = true;
	private boolean caveland = true;

	public RegenerateMessage() {}

	public RegenerateMessage(boolean backup)
	{
		this.backup = backup;
	}

	public RegenerateMessage(boolean backup, boolean caveworld, boolean cavern, boolean aquaCavern, boolean caveland)
	{
		this(backup);
		this.caveworld = caveworld;
		this.cavern = cavern;
		this.aquaCavern = aquaCavern;
		this.caveland = caveland;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		backup = buffer.readBoolean();
		caveworld = buffer.readBoolean();
		cavern = buffer.readBoolean();
		aquaCavern = buffer.readBoolean();
		caveland = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(backup);
		buffer.writeBoolean(caveworld);
		buffer.writeBoolean(cavern);
		buffer.writeBoolean(aquaCavern);
		buffer.writeBoolean(caveland);
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
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;

			if (player.mcServer.isSinglePlayer() || player.mcServer.getConfigurationManager().func_152596_g(player.getGameProfile()))
			{
				boolean ret = CaveworldAPI.isHardcore() || CaveworldAPI.isCaveborn();

				if (message.caveworld)
				{
					CaveUtils.regenerateDimension(CaveworldAPI.getDimension(), message.backup, ret);
				}

				if (message.cavern)
				{
					CaveUtils.regenerateDimension(CaveworldAPI.getCavernDimension(), message.backup, ret);
				}

				if (message.aquaCavern)
				{
					CaveUtils.regenerateDimension(CaveworldAPI.getAquaCavernDimension(), message.backup, ret);
				}

				if (message.caveland)
				{
					CaveUtils.regenerateDimension(CaveworldAPI.getCavelandDimension(), message.backup, ret);
				}
			}
			else return new ProgressNotify(3);
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