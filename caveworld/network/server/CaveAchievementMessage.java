/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.server;

import caveworld.core.CaveAchievementList;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;

public class CaveAchievementMessage implements IMessage, IMessageHandler<CaveAchievementMessage, IMessage>
{
	private int index;

	public CaveAchievementMessage() {}

	public CaveAchievementMessage(Achievement achievement)
	{
		this.index = CaveAchievementList.getAchievementIndex(achievement);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		index = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(index);
	}

	@Override
	public IMessage onMessage(CaveAchievementMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		Achievement achievement = CaveAchievementList.getAchievement(message.index);

		if (achievement != null && player.func_147099_x().canUnlockAchievement(achievement))
		{
			player.triggerAchievement(achievement);
		}

		return null;
	}
}