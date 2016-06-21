package caveworld.client.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import caveworld.api.CaveworldAPI;
import caveworld.config.Config;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class BiomesCavernEntry extends CategoryEntry
{
	public BiomesCavernEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiBiomesEntry(owningScreen, CaveworldAPI.biomeCavernManager);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		try
		{
			FileUtils.forceDelete(new File(Config.biomesCavernCfg.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		CaveworldAPI.clearCavernBiomes();

		Config.biomesCavernCfg = null;
		Config.syncBiomesCavernCfg();

		if (childScreen instanceof GuiBiomesEntry)
		{
			GuiBiomesEntry gui = (GuiBiomesEntry)childScreen;

			if (gui.biomeList != null)
			{
				gui.biomeList.biomes.clear();
				gui.biomeList.biomes.addAll(CaveworldAPI.getCavernBiomes());
				gui.biomeList.contents.clear();
				gui.biomeList.contents.addAll(gui.biomeList.biomes);
			}
		}
	}
}