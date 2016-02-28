/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.client;

import caveworld.api.BlockEntry;
import caveworld.core.CaverManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

public class LastMineMessage implements IMessage, IMessageHandler<LastMineMessage, IMessage>
{
	private String name;
	private int meta;
	private int point;

	public LastMineMessage() {}

	public LastMineMessage(Block block, int meta, int point)
	{
		this.name = GameData.getBlockRegistry().getNameForObject(block);
		this.meta = meta;
		this.point = point;
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		name = ByteBufUtils.readUTF8String(buffer);
		meta = buffer.readInt();
		point = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, name);
		buffer.writeInt(meta);
		buffer.writeInt(point);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(LastMineMessage message, MessageContext ctx)
	{
		Block block = Block.getBlockFromName(message.name);

		if (block != null && block != Blocks.air)
		{
			CaverManager.lastMine = new BlockEntry(block, message.meta);
			CaverManager.lastMinePoint = message.point;
			CaverManager.mineHighlightStart = Minecraft.getSystemTime();
		}

		return null;
	}
}