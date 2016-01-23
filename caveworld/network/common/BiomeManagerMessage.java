/*
 * Caveworld
 *
 * Copyright (c) 2016 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package caveworld.network.common;

import java.util.concurrent.RecursiveAction;

import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveBiome;
import caveworld.api.ICaveBiomeManager;
import caveworld.core.CavernBiomeManager;
import caveworld.util.CaveUtils;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameData;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class BiomeManagerMessage implements IMessage, IMessageHandler<BiomeManagerMessage, IMessage>
{
	private int type;
	private NBTTagCompound data;

	public BiomeManagerMessage() {}

	public BiomeManagerMessage(ICaveBiomeManager manager)
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
	public IMessage onMessage(BiomeManagerMessage message, MessageContext ctx)
	{
		final ICaveBiomeManager manager = new CavernBiomeManager();

		manager.loadNBTData((NBTTagList)message.data.getTag("Entries"));

		switch (message.type)
		{
			case 1:
				CaveworldAPI.biomeCavernManager = manager;
				break;
			default:
				CaveworldAPI.biomeManager = manager;
				break;
		}

		if (ctx.side.isServer())
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					Configuration config = manager.getConfig();
					ConfigCategory category;

					for (ICaveBiome entry : manager.getCaveBiomes())
					{
						category = config.getCategory(Integer.toString(entry.getBiome().biomeID));
						category.get("genWeight").set(entry.getGenWeight());
						category.get("terrainBlock").set(GameData.getBlockRegistry().getNameForObject(entry.getTerrainBlock().getBlock()));
						category.get("terrainBlockMetadata").set(entry.getTerrainBlock().getMetadata());
						category.get("topBlock").set(GameData.getBlockRegistry().getNameForObject(entry.getTopBlock().getBlock()));
						category.get("topBlockMetadata").set(entry.getTopBlock().getMetadata());
					}

					if (config.hasChanged())
					{
						config.save();
					}
				}
			});
		}

		return null;
	}
}